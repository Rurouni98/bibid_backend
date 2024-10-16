package bibid.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QStreaming is a Querydsl query type for Streaming
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStreaming extends EntityPathBase<Streaming> {

    private static final long serialVersionUID = -440159345L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QStreaming streaming = new QStreaming("streaming");

    public final QAuction auction;

    public final NumberPath<Integer> cdnInstanceNo = createNumber("cdnInstanceNo", Integer.class);

    public final StringPath cdnStatus = createString("cdnStatus");

    public final StringPath channelId = createString("channelId");

    public final StringPath channelName = createString("channelName");

    public final StringPath channelStatus = createString("channelStatus");

    public final DateTimePath<java.time.LocalDateTime> endTime = createDateTime("endTime", java.time.LocalDateTime.class);

    public final StringPath publishUrl = createString("publishUrl");

    public final DateTimePath<java.time.LocalDateTime> startTime = createDateTime("startTime", java.time.LocalDateTime.class);

    public final NumberPath<Long> streamingIndex = createNumber("streamingIndex", Long.class);

    public final StringPath streamKey = createString("streamKey");

    public final StringPath streamUrl = createString("streamUrl");

    public QStreaming(String variable) {
        this(Streaming.class, forVariable(variable), INITS);
    }

    public QStreaming(Path<? extends Streaming> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QStreaming(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QStreaming(PathMetadata metadata, PathInits inits) {
        this(Streaming.class, metadata, inits);
    }

    public QStreaming(Class<? extends Streaming> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.auction = inits.isInitialized("auction") ? new QAuction(forProperty("auction"), inits.get("auction")) : null;
    }

}

