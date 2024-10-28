package bibid.service.specialAuction.impl;

import bibid.dto.AuctionDto;
import bibid.entity.Auction;
import bibid.entity.AuctionType;
import bibid.service.livestation.LiveStationPoolManager;
import bibid.repository.specialAuction.SpecialAuctionRepository;
import bibid.service.specialAuction.SpecialAuctionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
@Slf4j
public class SpecialAuctionServiceImpl implements SpecialAuctionService {


    private final SpecialAuctionRepository specialAuctionRepository;

    @Override
    public Page<AuctionDto> findAuctionsByType(String auctionType, Pageable pageable) {

        AuctionType auctionTypeEnum;

        try {
            auctionTypeEnum = AuctionType.valueOf(auctionType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("올바르지 않은 경매 타입입니다.");
        }

        String koreanAuctionType = auctionTypeEnum.getKoreanName();
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);

        Page<AuctionDto> auctionDtoPage = specialAuctionRepository.findAuctionsByType(koreanAuctionType, oneDayAgo, pageable)
                .map(Auction::toDto);

        return auctionDtoPage;
    }

}
