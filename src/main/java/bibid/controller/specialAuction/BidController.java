package bibid.controller.specialAuction;

import bibid.dto.AuctionInfoDto;
import bibid.entity.*;
import bibid.repository.account.AccountRepository;
import bibid.repository.auction.AuctionRepository;
import bibid.repository.specialAuction.AuctionInfoRepository;
import bibid.service.specialAuction.RedisBidService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.Acceleration;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Set;

@Controller
@RequiredArgsConstructor
@Slf4j
public class BidController {
    private final AuctionRepository auctionRepository;
    private final AuctionInfoRepository auctionInfoRepository;
    private final UserDetailsService userDetailsService;
    private final RedisBidService redisBidService;
    private final AccountRepository accountRepository;

    @MessageMapping("/auction.bid/{auctionIndex}")
    @SendTo("/topic/auction/{auctionIndex}")
    public AuctionInfoDto bid(@DestinationVariable Long auctionIndex, @Payload AuctionInfoDto auctionInfoDto, Principal principal) {

        // principal이 사용자 이름(String)일 수 있음
        if (principal == null) {
            throw new IllegalStateException("현재 인증된 사용자를 찾을 수 없습니다.");
        }

        // 사용자 이름을 principal에서 가져옴
        String username = principal.getName();

        // UserDetailsService를 사용하여 사용자 정보 로드
        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);
        Member bidder = userDetails.getMember();

        // 경매 정보 조회
        Auction auction = auctionRepository.findById(auctionIndex)
                .orElseThrow(() -> new IllegalArgumentException("Invalid auction ID"));

        // 경매 상태 확인
        if (!"방송중".equals(auction.getAuctionStatus())) {
            throw new IllegalStateException("이 경매는 현재 입찰할 수 없습니다.");
        }

        // 계좌 정보 조회
        Account bidderAccount = accountRepository.findByMember_MemberIndex(bidder.getMemberIndex())
                .orElseThrow(() -> new RuntimeException("계좌 정보를 찾을 수 없습니다."));

        int currentBalance = Integer.parseInt(bidderAccount.getUserMoney());
        int bidAmount = auctionInfoDto.getBidAmount().intValue();

        // 계좌 금액 확인: 잔액이 입찰 금액보다 적으면 예외 발생
        if (currentBalance < bidAmount) {
            throw new RuntimeException("잔액이 부족하여 입찰할 수 없습니다.");
        }

        // 새로운 입찰을 Redis에 기록
        redisBidService.placeBid(auctionIndex, auctionInfoDto.getBidAmount(), bidder.getNickname());
        log.info("Bid placed in Redis: auctionId={}, amount={}, user={}", auctionIndex, auctionInfoDto.getBidAmount(), bidder.getNickname());

//        // 최고 입찰가 업데이트 후 프론트로 전달
        Double highestBid = redisBidService.getHighestBid(auctionIndex);
        auctionInfoDto.setBidAmount(highestBid.longValue());
        auctionInfoDto.setBidderNickname(bidder.getNickname());

        // DB에 저장 (필요한 경우 주기적으로 배치 처리)
        AuctionInfo auctionInfo = auctionInfoDto.toEntity(auction, bidder);
        auctionInfo.setBidTime(LocalDateTime.now());
        auctionInfoRepository.save(auctionInfo);
        log.info("Bid saved to DB: {}", auctionInfo);

        // 저장된 입찰 정보를 브로드캐스트
        return auctionInfoDto;
    }
}
