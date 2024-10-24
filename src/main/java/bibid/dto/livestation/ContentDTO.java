package bibid.dto.livestation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContentDTO {
    @JsonProperty("channelName")
    private String channelName;

    @JsonProperty("channelId")
    private String channelId;

    @JsonProperty("cdn")
    private CdnDTO cdn;

    @JsonProperty("qualitySetId")
    private long qualitySetId;

    @JsonProperty("useDvr")
    private boolean useDvr;

    @JsonProperty("immediateOnAir")
    private boolean immediateOnAir;

    @JsonProperty("timemachineMin")
    private int timemachineMin;

    @JsonProperty("envType")
    private String envType;

    @JsonProperty("outputProtocol")
    private String outputProtocol;

    @JsonProperty("uploadPath")
    private String uploadPath;

    @JsonProperty("record")
    private RecordDTO record;

    @JsonProperty("origin")
    private OriginDTO origin;

    @JsonProperty("backupStreamKey")
    private String backupStreamKey;

    @JsonProperty("isStreamFailOver")
    private boolean isStreamFailOver;

    @JsonProperty("instanceNo")
    private int instanceNo;

    @JsonProperty("qualitySetName")
    private String qualitySetName;

    @JsonProperty("channelStatus") // CREATING, READY, PUBLISH, DELETED,BLOCK, FORCE_BLOCK, FORCE_DELETED
    private String channelStatus;

    @JsonProperty("isRecording")
    private boolean isRecording;

    @JsonProperty("useDVR")
    private boolean useDVR;

    @JsonProperty("publishUrl")
    private String publishUrl;

    @JsonProperty("globalPublishUrl")
    private String globalPublishUrl;

    @JsonProperty("streamKey")
    private String streamKey;

    @JsonProperty("totalPublishSeconds")
    private int totalPublishSeconds;

    @JsonProperty("createdTime")
    private long createdTime;

    @JsonProperty("recentPublishStartTime")
    private long recentPublishStartTime;

    @JsonProperty("alertOn")
    private boolean alertOn;

    @JsonProperty("alertOptions")
    private AlertOptionsDTO alertOptions;

    @JsonProperty("callbackEndpoint")
    private String callbackEndpoint;

    @JsonProperty("name")
    private String name;

    @JsonProperty("url")
    private String url;

    @JsonProperty("resolution")
    private String resolution;

    @JsonProperty("videoBitrate")
    private String videoBitrate;

    @JsonProperty("audioBitrate")
    private String audioBitrate;
}
