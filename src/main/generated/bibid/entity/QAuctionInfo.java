package bibid.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAuctionInfo is a Querydsl query type for AuctionInfo
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAuctionInfo extends EntityPathBase<AuctionInfo> {

    private static final long serialVersionUID = 282747262L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAuctionInfo auctionInfo = new QAuctionInfo("auctionInfo");

    public final QAuction auction;

    public final NumberPath<Long> auctionInfoIndex = createNumber("auctionInfoIndex", Long.class);

    public final NumberPath<Long> bidAmount = createNumber("bidAmount", Long.class);

    public final QMember bidder;

    public final StringPath bidderNickname = createString("bidderNickname");

    public final DateTimePath<java.time.LocalDateTime> bidTime = createDateTime("bidTime", java.time.LocalDateTime.class);

    public QAuctionInfo(String variable) {
        this(AuctionInfo.class, forVariable(variable), INITS);
    }

    public QAuctionInfo(Path<? extends AuctionInfo> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAuctionInfo(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAuctionInfo(PathMetadata metadata, PathInits inits) {
        this(AuctionInfo.class, metadata, inits);
    }

    public QAuctionInfo(Class<? extends AuctionInfo> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.auction = inits.isInitialized("auction") ? new QAuction(forProperty("auction"), inits.get("auction")) : null;
        this.bidder = inits.isInitialized("bidder") ? new QMember(forProperty("bidder"), inits.get("bidder")) : null;
    }

}

