package bibid.service.livestation;

import bibid.dto.livestation.LiveStationChannelDTO;
import bibid.dto.livestation.LiveStationInfoDTO;
import bibid.entity.LiveStationChannel;
import bibid.entity.LiveStationServiceUrl;
import bibid.repository.livestation.LiveStationChannelRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LiveStationPoolManager {

    private final SimpMessagingTemplate messagingTemplate;
    private final LiveStationService liveStationService;
    private final LiveStationChannelRepository channelRepository;
//    private final RedisTemplate<String, LiveStationChannel> redisTemplate; // Redis 사용
    private final TaskScheduler taskScheduler;

    @PostConstruct
    public void initializePool() {

        List<LiveStationChannel> existingChannels = channelRepository.findAll();

        if (existingChannels.isEmpty()) {
            System.out.println("DB에 채널이 없습니다. API로부터 채널을 가져옵니다.");
            List<LiveStationChannelDTO> preCreatedChannelDTOList = liveStationService.getChannelList();

            for (LiveStationChannelDTO preCreatedChannelDTO : preCreatedChannelDTOList) {
                LiveStationChannel preCreatedChannel = configureChannel(preCreatedChannelDTO);
                channelRepository.save(preCreatedChannel);
            }
        }

        // 서버 재시작 시, CDN이 아직 준비되지 않은 채널들을 다시 확인
        List<LiveStationChannel> channelsNeedingCdnUpdate = channelRepository.findAllByIsAvailableFalse();
        for (LiveStationChannel channel : channelsNeedingCdnUpdate) {
            checkCdnStatusAndUpdate(channel);  // CDN 상태를 다시 확인하여 업데이트 작업 재시작
        }

    }

    private LiveStationChannel configureChannel(LiveStationChannelDTO preCreatedChannelDTO) {
        String channelId = preCreatedChannelDTO.getChannelId();
        String cdnStatusName = preCreatedChannelDTO.getCdnStatusName();
        String channelStatus = preCreatedChannelDTO.getChannelStatus();

        LiveStationChannel preCreatedChannel = preCreatedChannelDTO.toEntity();

        if (cdnStatusName.equals("RUNNING")) {
            List<LiveStationServiceUrl> serviceUrlList = liveStationService.getServiceURL(channelId, "GENERAL")
                    .stream()
                    .map(liveStationUrlDTO -> LiveStationServiceUrl.builder()
                            .liveStationChannel(preCreatedChannel)
                            .serviceUrl(liveStationUrlDTO.getUrl())
                            .build()
                    )
                    .toList();
            preCreatedChannel.setServiceUrlList(serviceUrlList);
        }

        preCreatedChannel.setAvailable(cdnStatusName.equals("RUNNING") && channelStatus.equals("READY"));

        return preCreatedChannel;
    }


    @Transactional
    public LiveStationChannel allocateChannel() {

        LiveStationChannel allocatedChannel = channelRepository.findFirstByIsAvailableTrue()
                .orElseGet(() -> {
                    System.out.println("사용 가능한 채널이 없으므로 새 채널을 생성합니다.");
                    return createNewChannel();
                });

        allocatedChannel.setChannelStatus("PUBLISH");
        allocatedChannel.setAvailable(false);

        if (!allocatedChannel.getCdnStatusName().equals("RUNNING")) {
            System.out.println("CDN 준비 중, 상태 업데이트 대기: " + allocatedChannel.getChannelId());
            checkCdnStatusAndUpdate(allocatedChannel);
        }

        return  channelRepository.save(allocatedChannel);
    }

    private LiveStationChannel createNewChannel() {

        String channelId = liveStationService.createChannel("새로운 채널 이름");
        LiveStationInfoDTO liveStationInfoDTO = liveStationService.getChannelInfo(channelId);

        LiveStationChannel createdChannel = LiveStationChannel.builder()
                .channelId(channelId)
                .channelStatus(liveStationInfoDTO.getChannelStatus())
                .cdnInstanceNo(liveStationInfoDTO.getCdnInstanceNo())
                .cdnStatusName(liveStationInfoDTO.getCdnStatusName())
                .publishUrl(liveStationInfoDTO.getPublishUrl())
                .streamKey(liveStationInfoDTO.getStreamKey())
                .isAvailable(false)
                .build();

        return channelRepository.save(createdChannel);
    }

    private void checkCdnStatusAndUpdate(LiveStationChannel channel) {

        final long CHECK_INTERVAL_MS = 5 * 60 * 1000;  // 5분

        taskScheduler.schedule(() -> {
            try {
                LiveStationInfoDTO channelInfo = liveStationService.getChannelInfo(channel.getChannelId());
                String cdnStatusName = channelInfo.getCdnStatusName();
                String channelStatus = channelInfo.getChannelStatus();

                if ("RUNNING".equals(cdnStatusName) && "PUBLISH".equals(channelStatus)) {
                    List<LiveStationServiceUrl> serviceUrlList = liveStationService.getServiceURL(channel.getChannelId(), "GENERAL")
                            .stream()
                            .map(liveStationUrlDTO -> LiveStationServiceUrl.builder()
                                    .liveStationChannel(channel)
                                    .serviceUrl(liveStationUrlDTO.getUrl())
                                    .build()
                            )
                            .toList();
                    channel.setServiceUrlList(serviceUrlList);
                    channel.setCdnStatusName("RUNNING");
                    channelRepository.save(channel);

                    messagingTemplate.convertAndSend("/topic/cdn-updates", channel.getServiceUrlList());
                    System.out.println("CDN 상태 업데이트 및 서비스 URL 저장 완료: " + channel.getChannelId());

                    System.out.println("CDN 상태 업데이트 및 서비스 URL 저장 완료: " + channel.getChannelId());

                } else {
                    System.out.println("CDN 생성 중 또는 채널 상태 대기 중: " + channel.getChannelId());
                    taskScheduler.schedule(() -> checkCdnStatusAndUpdate(channel), new Date(System.currentTimeMillis() + CHECK_INTERVAL_MS));
                }
            } catch (Exception e) {
                System.err.println("CDN 상태 확인 중 오류 발생: " + e.getMessage());
            }
        }, new Date(System.currentTimeMillis() + CHECK_INTERVAL_MS));  // 5분 후 첫 확인
    }

    public void releaseChannel(LiveStationChannel channel) {

        channel.setChannelStatus("READY");
        channel.setAvailable(true);

        channelRepository.save(channel);
    }

}