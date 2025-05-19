package bibid.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QLiveStationChannel is a Querydsl query type for LiveStationChannel
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLiveStationChannel extends EntityPathBase<LiveStationChannel> {

    private static final long serialVersionUID = 859908270L;

    public static final QLiveStationChannel liveStationChannel = new QLiveStationChannel("liveStationChannel");

    public final BooleanPath isAllocated = createBoolean("isAllocated");

    public final BooleanPath isAvailable = createBoolean("isAvailable");

    public final NumberPath<Long> liveStationChannelIndex = createNumber("liveStationChannelIndex", Long.class);

    public final StringPath youtubeStreamKey = createString("youtubeStreamKey");

    public final StringPath youtubeStreamUrl = createString("youtubeStreamUrl");

    public final StringPath youtubeWatchUrl = createString("youtubeWatchUrl");

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

