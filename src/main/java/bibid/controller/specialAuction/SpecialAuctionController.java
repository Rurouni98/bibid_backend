package bibid.controller.specialAuction;

import bibid.dto.AuctionDto;
import bibid.dto.ResponseDto;
import bibid.entity.Auction;
import bibid.dto.livestation.LiveStationChannelDTO;
import bibid.entity.CustomUserDetails;
import bibid.entity.LiveStationChannel;
import bibid.entity.Member;
import bibid.repository.livestation.LiveStationChannelRepository;
import bibid.service.livestation.LiveStationPoolManager;
import bibid.repository.specialAuction.SpecialAuctionRepository;
import bibid.service.specialAuction.SpecialAuctionService;
import bibid.service.specialAuction.impl.SpecialAuctionScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/specialAuction")
@RequiredArgsConstructor
@Slf4j
public class SpecialAuctionController {
//
//    private final SpecialAuctionService specialAuctionService;
//    private final SpecialAuctionRepository specialAuctionRepository;
//    private final LiveStationChannelRepository channelRepository;
//    private final LiveStationPoolManager liveStationPoolManager;
//    private final SpecialAuctionScheduler specialAuctionScheduler;
//
//    @GetMapping("/list")
//    public ResponseEntity<?> getAuctionsByType(
//            @RequestParam("auctionType") String auctionType,
//            @PageableDefault(page = 0, size = 100, sort = "moddate", direction = Sort.Direction.DESC) Pageable pageable) {
//
//        // 응답 데이터를 담을 Map 선언
//        ResponseDto<AuctionDto> responseDto = new ResponseDto<>();
//
//        try {
//
//            Page<AuctionDto> auctionDtoList = specialAuctionService.findAuctionsByType(auctionType, pageable);
//
//            if (auctionDtoList.isEmpty()) {
//                log.info("No auctions found for auctionType: {}", auctionType);
//            } else {
//                log.info("Found auctions: {}", auctionDtoList.getContent());
//            }
//
//            responseDto.setPageItems(auctionDtoList);
//            responseDto.setStatusCode(HttpStatus.OK.value());
//            responseDto.setStatusMessage("ok");
//
//            return ResponseEntity.ok(responseDto);
//        } catch(Exception e) {
//            log.error("getAuctions error: {}", e.getMessage());
//            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
//            responseDto.setStatusMessage(e.getMessage());
//            return ResponseEntity.internalServerError().body(responseDto);
//        }
//    }
//
//    // 라이브 종료 요청
//    @PostMapping("/endLive/{auctionIndex}")
//    public ResponseEntity<?> endLive(@PathVariable Long auctionIndex) {
//        Auction auction = specialAuctionRepository.findById(auctionIndex)
//                .orElseThrow(() -> new RuntimeException("해당 옥션은 없습니다."));
//
//        // 상태 초기화
//        auction.setAuctionStatus("종료됨"); // 또는 필요 시 "대기중", "종료" 등으로 구분
//        specialAuctionRepository.save(auction);
//
//        log.info("라이브 방송 종료됨 - auctionIndex={}", auctionIndex);
//
//        return ResponseEntity.ok("YouTube 라이브 방송이 종료되었습니다.");
//    }
//
//
//    // 라이브 시작
//    @PostMapping("/startLive/{auctionIndex}")
//    public ResponseEntity<?> startLive(@PathVariable Long auctionIndex) {
//        Auction auction = specialAuctionRepository.findById(auctionIndex)
//                .orElseThrow(() -> new RuntimeException("해당 옥션은 없습니다."));
//
//        LiveStationChannel channel = auction.getLiveStationChannel();
//        if (channel == null) {
//            return ResponseEntity.badRequest().body("연결된 YouTube 채널이 없습니다.");
//        }
//
//        // 상태 변경
//        auction.setAuctionStatus("방송중");
//        specialAuctionRepository.save(auction);
//
//        log.info("라이브 방송 시작됨 - auctionIndex={}, streamKey={}", auctionIndex, channel.getYoutubeStreamKey());
//
//        return ResponseEntity.ok("YouTube 라이브 방송이 시작되었습니다.");
//    }
//
//    // 채널 정보 요청
//    @GetMapping("/channelInfo/{auctionIndex}")
//    public ResponseEntity<LiveStationChannelDTO> getChannelInfo(@PathVariable Long auctionIndex) {
//
//        Auction auction = specialAuctionRepository.findById(auctionIndex)
//                .orElseThrow(() -> new RuntimeException("해당 경매를 찾을 수 없습니다."));
//
//        LiveStationChannel channel = auction.getLiveStationChannel();
//
//        if (channel == null) {
//            throw new RuntimeException("경매에 연결된 YouTube 채널 정보가 없습니다.");
//        }
//
//        LiveStationChannelDTO dto = channel.toDto();
//
//        log.info("채널 정보 요청 - auctionIndex={}, youtubeWatchUrl={}", auctionIndex, dto.getYoutubeWatchUrl());
//
//        return ResponseEntity.ok(dto);
//    }
//
//
//    @PostMapping("/registerAlarm/{auctionIndex}")
//    public ResponseEntity<ResponseDto<String>> registerAuctionAlarm(
//            @PathVariable Long auctionIndex,
//            @AuthenticationPrincipal CustomUserDetails userDetails) {
//
//        ResponseDto<String> responseDto = new ResponseDto<>();
//        Member member = userDetails.getMember();
//        Auction auction = specialAuctionRepository.findById(auctionIndex).orElseThrow(
//                () -> new RuntimeException("해당 옥션은 없습니다.")
//        );
//
//        if (auction == null) {
//            responseDto.setStatusCode(HttpStatus.NOT_FOUND.value());
//            responseDto.setStatusMessage("경매를 찾을 수 없습니다.");
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDto);
//        }
//
//        boolean isRegistered = specialAuctionScheduler.registerAlarmForUser(auction, member.getMemberIndex());
//
//        if (isRegistered) {
//            responseDto.setStatusCode(HttpStatus.OK.value());
//            responseDto.setStatusMessage("알림 신청이 완료되었습니다.");
//            responseDto.setItem("알림 신청 성공");
//            return ResponseEntity.ok(responseDto);
//        } else {
//            responseDto.setStatusCode(HttpStatus.CONFLICT.value());
//            responseDto.setStatusMessage("이미 알림이 등록되어 있습니다.");
//            responseDto.setItem("알림 등록 중복");
//            return ResponseEntity.status(HttpStatus.CONFLICT).body(responseDto);
//        }
//    }
}
