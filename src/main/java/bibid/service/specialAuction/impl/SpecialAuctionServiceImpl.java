package bibid.service.specialAuction.impl;

import bibid.dto.AuctionDto;
import bibid.entity.Auction;
import bibid.entity.Streaming;
import bibid.livestation.dto.LiveStationInfoDTO;
import bibid.livestation.dto.LiveStationUrlDTO;
import bibid.livestation.service.LiveStationService;
import bibid.repository.specialAuction.SpecialAuctionRepository;
import bibid.repository.specialAuction.StreamingRepository;
import bibid.service.specialAuction.ChatRoomService;
import bibid.service.specialAuction.SpecialAuctionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j


public class SpecialAuctionServiceImpl implements SpecialAuctionService {


    private final SpecialAuctionRepository specialAuctionRepository;
    private final ChatRoomService chatRoomService;
    private final LiveStationService liveStationService;
    private final StreamingRepository streamingRepository;

    public enum AuctionType {
        REALTIME("실시간 경매"),
        BLIND("블라인드 경매");

        private final String koreanName;

        AuctionType(String koreanName) {
            this.koreanName = koreanName;
        }

        public String getKoreanName() {
            return koreanName;
        }
    }

//    // 경매 시작 시간이 30분 이내인 경매들 찾기 (찾아서 채팅방 생성)
//    @Transactional
//    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void checkAuctionStart() {
        log.info("checkAuctionStart() 실행됨");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirtyMinutesLater = now.plusMinutes(3000);

        try {
            // 경매 시작 시간이  이내인 경매들을 찾음
            List<Auction> upcomingAuctions = specialAuctionRepository.findAuctionsStartingWithinThirtyMinutes(now, thirtyMinutesLater);

            for (Auction auction : upcomingAuctions) {
                // 1. 채팅방이 생성되지 않은 경우 채팅방 생성
                if (!auction.isChatRoomCreated()) {
                    chatRoomService.createChatRoom(auction.getAuctionIndex()); // 채팅방 생성
                    auction.setChatRoomCreated(true); // 채팅방 생성 상태 업데이트
                }

                // 2. 스트리밍이 생성되지 않은 경우 스트리밍 채널 생성
                if (!auction.isStreamingCreated()) {
                    String UUIDProductName = auction.getProductName() + "-" + UUID.randomUUID().toString();
                    String channelId = liveStationService.createChannel(UUIDProductName); // LiveStation 채널 생성
                    LiveStationInfoDTO liveStationInfoDTO = liveStationService.getChannelInfo(channelId);
                    List<LiveStationUrlDTO> liveStationUrlDTOList = liveStationService.getServiceURL(channelId, "GENERAL");

                    Streaming streaming = Streaming.builder()
                            .channelId(channelId)
                            .startTime(auction.getStartingLocalDateTime())
                            .endTime(auction.getEndingLocalDateTime().plusMinutes(30)) // 스트리밍 종료 시간 설정
                            .auction(auction)
                            .channelName(liveStationInfoDTO.getChannelName())
                            .cdnInstanceNo(liveStationInfoDTO.getCdnInstanceNo())
                            .cdnStatus(liveStationInfoDTO.getCdnStatus())
                            .publishUrl(liveStationInfoDTO.getPublishUrl())
                            .channelStatus(liveStationInfoDTO.getChannelStatus())
                            .streamKey(liveStationInfoDTO.getStreamKey())
                            .streamUrl(liveStationUrlDTOList.get(0).getUrl())
                            .build();

                    streamingRepository.save(streaming); // 스트리밍 정보 저장
                    auction.setStreamingCreated(true); // 스트리밍 생성 상태 업데이트
                }
            }
        } catch (Exception e) {
            log.error("경매 시작 스케줄링 중 오류 발생: ", e);
        }
    }

    // 경매 타입과 종료 시간이 현재 이후인 (즉, 아직 경매가 끝나지 않은) 경매 목록을 페이징 처리하여 조회
    @Override
    public Page<AuctionDto> findAuctionsByType(String auctionType, Pageable pageable) {
        String koreanAuctionType = "";

        // 문자열 비교는 .equals()로 처리
        if ("realtime".equals(auctionType)) {
            koreanAuctionType = "실시간 경매";
        } else if ("blind".equals(auctionType)) {
            koreanAuctionType = "블라인드 경매";
        }

        // 로깅 - 경매 타입 확인
        log.info("Requested auctionType: {}", auctionType);
        log.info("Converted koreanAuctionType: {}", koreanAuctionType);

        LocalDateTime currentTime = LocalDateTime.now();

        // 로깅 - 현재 시간 확인
        log.info("Current time for filtering auctions: {}", currentTime);

        Page<AuctionDto> auctionDtoPage = specialAuctionRepository.findAuctionsByType(koreanAuctionType, currentTime, pageable)
                .map(Auction::toDto);

        // 로깅 - 쿼리 결과 확인
        log.info("Found auctions: {}", auctionDtoPage.getContent());
        log.info("Total elements: {}, Total pages: {}", auctionDtoPage.getTotalElements(), auctionDtoPage.getTotalPages());

        return auctionDtoPage;
    }
}
