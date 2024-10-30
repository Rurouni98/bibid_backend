package bibid.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAuctionDetail is a Querydsl query type for AuctionDetail
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAuctionDetail extends EntityPathBase<AuctionDetail> {

    private static final long serialVersionUID = 986128673L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAuctionDetail auctionDetail = new QAuctionDetail("auctionDetail");

    public final QAuction auction;

    public final NumberPath<Long> auctionDetailIndex = createNumber("auctionDetailIndex", Long.class);

    public final StringPath costResponsibility = createString("costResponsibility");

    public final StringPath shippingMethod = createString("shippingMethod");

    public final NumberPath<Long> winnerIndex = createNumber("winnerIndex", Long.class);

    public final StringPath winnerNickname = createString("winnerNickname");

    public final NumberPath<Long> winningBid = createNumber("winningBid", Long.class);

    public QAuctionDetail(String variable) {
        this(AuctionDetail.class, forVariable(variable), INITS);
    }

    public QAuctionDetail(Path<? extends AuctionDetail> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAuctionDetail(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAuctionDetail(PathMetadata metadata, PathInits inits) {
        this(AuctionDetail.class, metadata, inits);
    }

    public QAuctionDetail(Class<? extends AuctionDetail> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.auction = inits.isInitialized("auction") ? new QAuction(forProperty("auction"), inits.get("auction")) : null;
    }

}

