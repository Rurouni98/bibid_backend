package bibid.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLiveStationChannel is a Querydsl query type for LiveStationChannel
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLiveStationChannel extends EntityPathBase<LiveStationChannel> {

    private static final long serialVersionUID = 859908270L;

    public static final QLiveStationChannel liveStationChannel = new QLiveStationChannel("liveStationChannel");

    public final NumberPath<Integer> cdnInstanceNo = createNumber("cdnInstanceNo", Integer.class);

    public final StringPath cdnStatusName = createString("cdnStatusName");

    public final StringPath channelId = createString("channelId");

    public final StringPath channelStatus = createString("channelStatus");

    public final BooleanPath isAllocated = createBoolean("isAllocated");

    public final BooleanPath isAvailable = createBoolean("isAvailable");

    public final NumberPath<Long> liveStationChannelIndex = createNumber("liveStationChannelIndex", Long.class);

    public final StringPath publishUrl = createString("publishUrl");

    public final ListPath<LiveStationServiceUrl, QLiveStationServiceUrl> serviceUrlList = this.<LiveStationServiceUrl, QLiveStationServiceUrl>createList("serviceUrlList", LiveStationServiceUrl.class, QLiveStationServiceUrl.class, PathInits.DIRECT2);

    public final StringPath streamKey = createString("streamKey");

    public QLiveStationChannel(String variable) {
        super(LiveStationChannel.class, forVariable(variable));
    }

    public QLiveStationChannel(Path<? extends LiveStationChannel> path) {
        super(path.getType(), path.getMetadata());
    }

    public QLiveStationChannel(PathMetadata metadata) {
        super(LiveStationChannel.class, metadata);
    }

}

