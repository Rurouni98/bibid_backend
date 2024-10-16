package bibid.repository.custom.specialAuction;

import bibid.dto.SpecialAuctionListMapping;

import java.util.List;

public interface SpecialAuctionRepositoryCustom {

    List<SpecialAuctionListMapping> findAllByAuctionType(String auctionType);
}
