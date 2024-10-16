package bibid.entity;


import bibid.dto.AuctionImageDto;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(
        name = "auctionImageSeqGenerator",
        sequenceName = "AUCTION_IMAGE_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Builder
public class AuctionImage {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "auctionImageSeqGenerator"
    )
    private Long auctionImageIndex;

    @ManyToOne
    @JoinColumn(name = "auctionIndex")
    @JsonBackReference
    private Auction auction;

    private String filename;
    private String filepath;
    private String fileoriginname;
    private String filetype;
    private Long filesize;

    @Transient
    private String filestatus;
    @Transient
    private String newfilename;

    private boolean isThumbnail;

    public AuctionImageDto toDto() {
        return AuctionImageDto.builder()
                .auctionImageIndex(this.auctionImageIndex)
                .auctionIndex(this.auction.getAuctionIndex())
                .filename(this.filename)
                .filepath(this.filepath)
                .filetype(this.filetype)
                .fileoriginname(this.fileoriginname)
                .filestatus(this.filestatus)
                .newfilename(this.newfilename)
                .filesize(this.filesize)
                .isThumbnail(this.isThumbnail)
                .build();
    }

}
