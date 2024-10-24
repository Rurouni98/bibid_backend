package bibid.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLiveStationServiceUrl is a Querydsl query type for LiveStationServiceUrl
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLiveStationServiceUrl extends EntityPathBase<LiveStationServiceUrl> {

    private static final long serialVersionUID = -1746518289L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLiveStationServiceUrl liveStationServiceUrl = new QLiveStationServiceUrl("liveStationServiceUrl");

    public final QLiveStationChannel liveStationChannel;

    public final NumberPath<Long> LiveStationServiceUrlIndex = createNumber("LiveStationServiceUrlIndex", Long.class);

    public final StringPath serviceUrl = createString("serviceUrl");

    public QLiveStationServiceUrl(String variable) {
        this(LiveStationServiceUrl.class, forVariable(variable), INITS);
    }

    public QLiveStationServiceUrl(Path<? extends LiveStationServiceUrl> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLiveStationServiceUrl(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLiveStationServiceUrl(PathMetadata metadata, PathInits inits) {
        this(LiveStationServiceUrl.class, metadata, inits);
    }

    public QLiveStationServiceUrl(Class<? extends LiveStationServiceUrl> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.liveStationChannel = inits.isInitialized("liveStationChannel") ? new QLiveStationChannel(forProperty("liveStationChannel")) : null;
    }

}

