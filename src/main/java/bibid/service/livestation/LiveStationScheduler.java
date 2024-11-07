package bibid.service.livestation;

import bibid.dto.livestation.LiveStationChannelDTO;
import bibid.entity.LiveStationChannel;
import bibid.entity.LiveStationServiceUrl;
import bibid.repository.livestation.LiveStationChannelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LiveStationScheduler {

    private final LiveStationService liveStationService;
    private final LiveStationChannelRepository channelRepository;

    @Scheduled(fixedRate = 60 * 60 * 1000)  // 1시간 간격으로 실행
    public void updateChannelPool() {
        List<LiveStationChannelDTO> preCreatedChannelDTOList = liveStationService.getChannelList();

        log.info("총 {}개의 채널을 확인 중...", preCreatedChannelDTOList.size());

        for (LiveStationChannelDTO preCreatedChannelDTO : preCreatedChannelDTOList) {
            updateChannel(preCreatedChannelDTO);
        }
    }

    @Transactional
    private void updateChannel(LiveStationChannelDTO preCreatedChannelDTO) {
        String channelId = preCreatedChannelDTO.getChannelId();
        String cdnStatusName = preCreatedChannelDTO.getCdnStatusName();
        String channelStatus = preCreatedChannelDTO.getChannelStatus();

        // fetch join을 사용하여 serviceUrlList를 함께 로드
        LiveStationChannel channelEntity = channelRepository.findByChannelId(channelId)
                .orElseGet(preCreatedChannelDTO::toEntity);

        boolean isUpdated = false;

        // 기존 channelStatus와 비교하여 다를 때만 업데이트
        if (!channelEntity.getChannelStatus().equals(channelStatus)) {
            log.info("채널 상태 업데이트: {} -> {} (Channel ID: {})", channelEntity.getChannelStatus(), channelStatus, channelId);
            channelEntity.setChannelStatus(channelStatus);
            isUpdated = true;
        }

        // 기존 cdnStatusName과 비교하여 'CREATING'에서 'RUNNING'으로 변경될 때만 서비스 URL 업데이트
        if (!channelEntity.getCdnStatusName().equals(cdnStatusName)) {
            log.info("CDN 상태 업데이트: {} -> {} (Channel ID: {})", channelEntity.getCdnStatusName(), cdnStatusName, channelId);
            channelEntity.setCdnStatusName(cdnStatusName);
            isUpdated = true;

            if (cdnStatusName.equals("RUNNING")) {
                List<LiveStationServiceUrl> serviceUrlList = liveStationService.getServiceURL(channelId, "GENERAL")
                        .stream()
                        .map(liveStationUrlDTO -> LiveStationServiceUrl.builder()
                                .liveStationChannel(channelEntity)
                                .serviceUrl(liveStationUrlDTO.getUrl())
                                .build()
                        )
                        .toList();
                channelEntity.setServiceUrlList(serviceUrlList);
                log.info("서비스 URL 리스트 업데이트 완료 (Channel ID: {})", channelId);
            }
        }

        // cdnStatusName과 channelStatus를 기반으로 'AVAILABLE' 상태 업데이트
        boolean newAvailableStatus = cdnStatusName.equals("RUNNING") && channelStatus.equals("READY");
        if (channelEntity.isAvailable() != newAvailableStatus) {
            log.info("채널 사용 가능 상태 업데이트: {} -> {} (Channel ID: {})", channelEntity.isAvailable(), newAvailableStatus, channelId);
            channelEntity.setAvailable(newAvailableStatus);
            isUpdated = true;
        }

        // 변경된 내용이 있을 경우에만 저장
        if (isUpdated) {
            channelRepository.save(channelEntity);
            log.info("채널 정보 저장 완료 (Channel ID: {})", channelId);
        }
    }
}
