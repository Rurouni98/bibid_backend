package bibid.service.livestation;

import bibid.dto.livestation.LiveStationChannelDTO;
import bibid.dto.livestation.LiveStationInfoDTO;
import bibid.entity.LiveStationChannel;
import bibid.entity.LiveStationServiceUrl;
import bibid.repository.livestation.LiveStationChannelRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LiveStationPoolManager {

    private final SimpMessagingTemplate messagingTemplate;
    private final LiveStationService liveStationService;
    private final LiveStationChannelRepository channelRepository;
    private final TaskScheduler taskScheduler;

    @PostConstruct
    public void initializePool() {
        List<LiveStationChannel> existingChannels = channelRepository.findAll();

        if (existingChannels.isEmpty()) {
            log.info("DB에 채널이 없습니다. API로부터 채널을 가져옵니다.");
            List<LiveStationChannelDTO> preCreatedChannelDTOList = liveStationService.getChannelList();

            for (LiveStationChannelDTO preCreatedChannelDTO : preCreatedChannelDTOList) {
                LiveStationChannel preCreatedChannel = configureChannel(preCreatedChannelDTO);
                channelRepository.save(preCreatedChannel);
                log.info("채널 저장 완료: {}", preCreatedChannel.getChannelId());
            }
        }

        // 서버 재시작 시, CDN이 아직 준비되지 않은 채널들을 다시 확인
        List<LiveStationChannel> channelsNeedingCdnUpdate = channelRepository.findAllByIsAvailableFalse();
        for (LiveStationChannel channel : channelsNeedingCdnUpdate) {
            channel.getServiceUrlList().size(); // Lazy Loading 방지
            scheduleCdnStatusCheck(channel);  // 스케줄링 작업으로 상태 확인 및 업데이트
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
            log.info("CDN 상태 'RUNNING' - 서비스 URL 리스트 저장 완료: {}", channelId);
        }

        preCreatedChannel.setAvailable(cdnStatusName.equals("RUNNING") && channelStatus.equals("READY"));

        return preCreatedChannel;
    }

    @Transactional
    public LiveStationChannel allocateChannel() {
        List<LiveStationChannel> availableChannels = channelRepository.findAllByIsAllocatedFalseAndIsAvailableTrue();

        LiveStationChannel allocatedChannel = availableChannels.isEmpty() ? createNewChannel() : availableChannels.get(0);

        allocatedChannel.getServiceUrlList().size(); // Lazy Loading 방지
        allocatedChannel.setAllocated(true);
        log.info("채널 할당: Channel ID: {}", allocatedChannel.getChannelId());

        if (!allocatedChannel.getCdnStatusName().equals("RUNNING")) {
            log.info("CDN 준비 중, 상태 업데이트 대기: Channel ID: {}", allocatedChannel.getChannelId());
            scheduleCdnStatusCheck(allocatedChannel);  // 스케줄링 작업 호출
        }

        return channelRepository.save(allocatedChannel);
    }

    @Transactional
    private LiveStationChannel createNewChannel() {
        String channelName = createNewChannelName();
        String channelId = liveStationService.createChannel(channelName);
        LiveStationInfoDTO liveStationInfoDTO = liveStationService.getChannelInfo(channelId);

        LiveStationChannel createdChannel = LiveStationChannel.builder()
                .channelId(channelId)
                .channelStatus(liveStationInfoDTO.getChannelStatus())
                .cdnInstanceNo(liveStationInfoDTO.getCdnInstanceNo())
                .cdnStatusName(liveStationInfoDTO.getCdnStatusName())
                .publishUrl(liveStationInfoDTO.getPublishUrl())
                .streamKey(liveStationInfoDTO.getStreamKey())
                .isAvailable(false)
                .isAllocated(false)
                .build();

        log.info("새로운 채널 생성 및 저장: Channel ID: {}", channelId);
        return channelRepository.save(createdChannel);
    }

    @Transactional
    public LiveStationChannel testCreateNewChannel() {
        LiveStationChannel allocatedChannel = createNewChannel();

        allocatedChannel.getServiceUrlList().size(); // Lazy Loading 방지
        allocatedChannel.setAllocated(true);
        log.info("채널 할당: Channel ID: {}", allocatedChannel.getChannelId());

        if (!allocatedChannel.getCdnStatusName().equals("RUNNING")) {
            log.info("CDN 준비 중, 상태 업데이트 대기: Channel ID: {}", allocatedChannel.getChannelId());
            scheduleCdnStatusCheck(allocatedChannel);  // 스케줄링 작업 호출
        }

        return channelRepository.save(allocatedChannel);
    }

    // 스케줄링된 작업을 관리하는 메서드
    private void scheduleCdnStatusCheck(LiveStationChannel channel) {
        final long CHECK_INTERVAL_MS = 5 * 60 * 1000;  // 5분

        taskScheduler.schedule(() -> checkCdnStatusAndUpdate(channel), new Date(System.currentTimeMillis() + CHECK_INTERVAL_MS));
    }

    // 트랜잭션이 적용된 CDN 상태 확인 및 업데이트 메서드
    @Transactional
    public void checkCdnStatusAndUpdate(LiveStationChannel channel) {
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
                log.info("CDN 상태 업데이트 및 서비스 URL 저장 완료: {}", channel.getChannelId());
            } else {
                log.info("CDN 생성 중 또는 채널 상태 대기 중: Channel ID: {}", channel.getChannelId());
                scheduleCdnStatusCheck(channel);  // 상태 업데이트가 필요하면 다시 스케줄링
            }
        } catch (Exception e) {
            log.error("CDN 상태 확인 중 오류 발생: {} (Channel ID: {})", e.getMessage(), channel.getChannelId());
        }
    }

    @Transactional
    public void releaseChannel(LiveStationChannel channel) {
        channel.setChannelStatus("READY");
        channel.setAllocated(false);
        channelRepository.save(channel);
        log.info("채널 반납 완료: Channel ID: {}", channel.getChannelId());
    }

    private String createNewChannelName() {
        String dateTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String randomString = UUID.randomUUID().toString().substring(0, 5);
        return "ls-" + dateTime + "-" + randomString;
    }
}
