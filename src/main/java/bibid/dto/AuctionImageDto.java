package bibid.dto;

import bibid.entity.Auction;
import bibid.entity.AuctionImage;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuctionImageDto {
    private Long auctionImageIndex;
    private Long auctionIndex;
    private String filename;
    private String filepath;
    private String fileoriginname;
    private String filetype;
    private String filestatus;
    private String newfilename;
    private Long filesize;
    private boolean isThumbnail;

    public AuctionImage toEntity(Auction auction) {
        return AuctionImage.builder()
                .auctionImageIndex(this.auctionImageIndex)
                .auction(auction)
                .filename(this.filename)
                .filepath(this.filepath)
                .fileoriginname(this.fileoriginname)
                .filetype(this.filetype)
                .filestatus(this.filestatus)
                .newfilename(this.newfilename)
                .filesize(this.filesize)
                .isThumbnail(this.isThumbnail)
                .build();
    }


}
