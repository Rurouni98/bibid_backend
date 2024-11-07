package bibid.service.specialAuction;

import bibid.dto.AuctionDto;
import bibid.entity.Auction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SpecialAuctionService {
    // 경매 타입에 따른 경매 목록을 페이징 처리하여 반환
    Page<AuctionDto> findAuctionsByType(String auctionType, Pageable pageable);
}
