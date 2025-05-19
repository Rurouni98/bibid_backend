package bibid.entity;

import bibid.dto.livestation.LiveStationChannelDTO;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@SequenceGenerator(
        name = "liveStationChannelSeqGenerator",
        sequenceName = "LIVE_STATION_CHANNEL_SEQ",
        initialValue = 1,
        allocationSize = 1
)
public class LiveStationChannel {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "liveStationChannelSeqGenerator"
    )
    private Long liveStationChannelIndex;

    /**
     * YouTube RTMP 송출 주소
     * 예: rtmp://a.rtmp.youtube.com/live2
     */
    @Column(nullable = false)
    private String youtubeStreamUrl;

    /**
     * YouTube 스트림 키
     * 예: 7s1a-xxxx-xxxx-xxxx
     */
    @Column(nullable = false)
    private String youtubeStreamKey;

    /**
     * 시청자가 접속할 YouTube 라이브 주소
     * 예: https://www.youtube.com/watch?v=abcdefghijk
     */
    @Column(nullable = false)
    private String youtubeWatchUrl;

    /**
     * 해당 채널이 현재 사용 중인지 여부
     */
    private boolean isAllocated;

    /**
     * 현재 사용 가능한지 여부
     */
    private boolean isAvailable;

    /**
     * DTO 변환 메서드
     */
    public LiveStationChannelDTO toDto(){
        return LiveStationChannelDTO.builder()
                .liveStationChannelIndex(this.liveStationChannelIndex)
                .youtubeStreamUrl(this.youtubeStreamUrl)
                .youtubeStreamKey(this.youtubeStreamKey)
                .youtubeWatchUrl(this.youtubeWatchUrl)
                .isAllocated(this.isAllocated)
                .isAvailable(this.isAvailable)
                .build();
    }
}
