package bibid.controller.specialAuction;

import bibid.dto.AuctionInfoDto;
import bibid.entity.Auction;
import bibid.entity.AuctionInfo;
import bibid.entity.CustomUserDetails;
import bibid.entity.Member;
import bibid.repository.auction.AuctionRepository;
import bibid.repository.specialAuction.AuctionInfoRepository;
import bibid.service.impl.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@Slf4j
public class BidController {
    private final AuctionRepository auctionRepository;
    private final AuctionInfoRepository auctionInfoRepository;
    private final UserDetailsService userDetailsService;

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

        // 입찰 정보를 DB에 저장
        AuctionInfo auctionInfo = auctionInfoDto.toEntity(auction, bidder);
        auctionInfo.setBidTime(LocalDateTime.now());
        AuctionInfo savedAuctionInfo = auctionInfoRepository.save(auctionInfo);
        log.info("Saved auction info: {}", savedAuctionInfo);

        return savedAuctionInfo.toDto(); // 저장된 입찰 정보 반환
    }
}
