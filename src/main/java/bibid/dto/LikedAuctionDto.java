package bibid.dto;

import bibid.entity.Auction;
import bibid.entity.LikedAuction;
import bibid.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikedAuctionDto {
    private Long likedAuctionIndex;
    private Long auctionIndex;
    private Long memberIndex;

    public LikedAuction toEntity(Auction auction, Member member) {
        return LikedAuction.builder()
                .likedAuctionIndex(this.likedAuctionIndex)
                .auction(auction)
                .member(member)
                .build();
    }
}
