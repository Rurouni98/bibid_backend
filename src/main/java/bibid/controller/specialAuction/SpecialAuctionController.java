package bibid.controller.specialAuction;

import bibid.dto.AuctionDto;
import bibid.dto.ResponseDto;
import bibid.entity.Auction;
import bibid.dto.livestation.LiveStationChannelDTO;
import bibid.entity.LiveStationChannel;
import bibid.repository.livestation.LiveStationChannelRepository;
import bibid.service.livestation.LiveStationPoolManager;
import bibid.repository.specialAuction.SpecialAuctionRepository;
import bibid.service.specialAuction.SpecialAuctionService;
import bibid.service.specialAuction.impl.SpecialAuctionScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/specialAuction")
@RequiredArgsConstructor
@Slf4j
public class SpecialAuctionController {

    private final SpecialAuctionService specialAuctionService;
    private final SpecialAuctionRepository specialAuctionRepository;
    private final SpecialAuctionScheduler specialAuctionScheduler;
    private final LiveStationChannelRepository channelRepository;
    private final LiveStationPoolManager liveStationPoolManager;

    @GetMapping("/list")
    public ResponseEntity<?> getAuctionsByType(
            @RequestParam("auctionType") String auctionType,
            @PageableDefault(page = 0, size = 100) Pageable pageable) {

        // 응답 데이터를 담을 Map 선언
        ResponseDto<AuctionDto> responseDto = new ResponseDto<>();

        try {

            Page<AuctionDto> auctionDtoList = specialAuctionService.findAuctionsByType(auctionType, pageable);

            if (auctionDtoList.isEmpty()) {
                log.info("No auctions found for auctionType: {}", auctionType);
            } else {
                log.info("Found auctions: {}", auctionDtoList.getContent());
            }

            responseDto.setPageItems(auctionDtoList);
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("ok");

            return ResponseEntity.ok(responseDto);
        } catch(Exception e) {
            log.error("getAuctions error: {}", e.getMessage());
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage(e.getMessage());
            return ResponseEntity.internalServerError().body(responseDto);
        }
    }

    // 라이브 종료 요청
    @PostMapping("/endLive/{auctionIndex}")
    public ResponseEntity<?> endLive(@PathVariable Long auctionIndex) {

        Auction auction = specialAuctionRepository.findById(auctionIndex).orElseThrow(
                () -> new RuntimeException("해당 옥션은 없습니다.")
        );

        LiveStationChannel channel = auction.getLiveStationChannel();

        if (channel != null) {
            liveStationPoolManager.releaseChannel(channel);

            auction.setAuctionStatus("경매종료");
            auction.setLiveStationChannel(null);
            specialAuctionRepository.save(auction);
            log.info("경매 종료로 채널 반납: auctionIndex : {} ", auctionIndex);
        }

        return ResponseEntity.ok("라이브가 종료되었습니다.");
    }

    // 라이브 시작
    @PostMapping("/startLive/{auctionIndex}")
    public ResponseEntity<?> startLive(@PathVariable Long auctionIndex) {

        Auction auction = specialAuctionRepository.findById(auctionIndex).orElseThrow(
                () -> new RuntimeException("해당 옥션은 없습니다.")
        );

        LiveStationChannel channel = auction.getLiveStationChannel();

        auction.setAuctionStatus("방송중");
        specialAuctionRepository.save(auction);

        channel.setChannelStatus("PUBLISH");
        channelRepository.save(channel);

        return ResponseEntity.ok("라이브가 시작되었습니다.");
    }

    // 채널 정보 요청
    @GetMapping("/channelInfo/{auctionIndex}")
    public ResponseEntity<?> getChannelInfo(@PathVariable Long auctionIndex) {

        ResponseDto<LiveStationChannelDTO> responseDto = new ResponseDto<>();

        try {

            Auction auction = specialAuctionRepository.findById(auctionIndex).orElseThrow(
                    () -> new RuntimeException("해당 옥션은 없습니다.")
            );

            LiveStationChannelDTO channelDTO = auction.getLiveStationChannel().toDto();

            responseDto.setItem(channelDTO);
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatusMessage("ok");

            return ResponseEntity.ok(responseDto);
        } catch(Exception e) {

            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage(e.getMessage());
            return ResponseEntity.internalServerError().body(responseDto);
        }
    }

    // 알림 등록 엔드포인트
    @GetMapping("/registerAlarm/{auctionIndex}")
    public ResponseEntity<?> registerAlarm(@PathVariable Long auctionIndex) {
        ResponseDto<String> responseDto = new ResponseDto<>();

        try {
            Auction auction = specialAuctionRepository.findById(auctionIndex)
                    .orElseThrow(() -> new RuntimeException("해당 옥션은 없습니다."));

            // 알림 등록
            boolean isScheduled = specialAuctionScheduler.registerAlarm(auction);

            if (isScheduled) {
                responseDto.setStatusCode(HttpStatus.OK.value());
                responseDto.setStatusMessage("알림이 성공적으로 등록되었습니다.");
            } else {
                responseDto.setStatusCode(HttpStatus.CONFLICT.value());
                responseDto.setStatusMessage("이미 등록된 알림입니다.");
            }

            return ResponseEntity.ok(responseDto);

        } catch (Exception e) {
            log.error("알림 등록 오류: {}", e.getMessage());
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatusMessage("알림 등록 중 오류 발생: " + e.getMessage());
            return ResponseEntity.internalServerError().body(responseDto);
        }
    }



}
