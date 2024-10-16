package bibid.entity;

import bibid.dto.ChatDto;
import bibid.dto.LikedAuctionDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@SequenceGenerator(
        name = "likedAuctionSeqGenerator",
        sequenceName = "LIKED_AUCTION_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LikedAuction {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "likedAuctionSeqGenerator"
    )
    private Long likedAuctionIndex;

    @ManyToOne
    @JoinColumn(name = "auctionIndex") // 외래 키: 스트리밍 ID
    private Auction auction;

    @ManyToOne
    @JoinColumn(name = "memberIndex") // 외래 키: 스트리밍 ID
    private Member member;

    public LikedAuctionDto toDto() {
        return LikedAuctionDto.builder()
                .likedAuctionIndex(this.likedAuctionIndex)
                .auctionIndex(this.auction.getAuctionIndex())
                .memberIndex(this.member.getMemberIndex())
                .build();
    }
}
