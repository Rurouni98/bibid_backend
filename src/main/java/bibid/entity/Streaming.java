package bibid.entity;

import bibid.dto.StreamingDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(
        name = "streamingSeqGenerator",
        sequenceName = "STREAMING_SEQ",
        initialValue = 1,
        allocationSize = 1
)
public class Streaming {

    @Id
    @GeneratedValue( strategy = GenerationType.SEQUENCE,
            generator = "streamingSeqGenerator")
    private Long streamingIndex; // 스트리밍 ID

    @OneToOne
    @JoinColumn(name = "auctionIndex", nullable = true) // 외래 키: 경매 ID
    private Auction auction;

    private List<String> streamUrlList; // 스트리밍 URL 리스트

    private String channelId;
    private String channelName;
    private String channelStatus;

    private int cdnInstanceNo;
    private String cdnStatus;

    private String publishUrl;
    private String streamKey;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public StreamingDto toDto(){
        return StreamingDto.builder()
                .streamingIndex(this.streamingIndex)
                .auctionIndex(this.auction.getAuctionIndex())
                .streamUrlList(this.streamUrlList)
                .channelId(this.channelId)
                .channelName(this.channelName)
                .channelStatus(this.channelStatus)
                .cdnInstanceNo(this.cdnInstanceNo)
                .cdnStatus(this.cdnStatus)
                .publishUrl(this.publishUrl)
                .streamKey(this.streamKey)
                .startTime(this.startTime)
                .endTime(this.endTime)
                .build();
    }

}
