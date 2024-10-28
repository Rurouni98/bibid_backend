package bibid.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAuction is a Querydsl query type for Auction
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAuction extends EntityPathBase<Auction> {

    private static final long serialVersionUID = 1907843248L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAuction auction = new QAuction("auction");

    public final QAuctionDetail auctionDetail;

    public final ListPath<AuctionImage, QAuctionImage> auctionImageList = this.<AuctionImage, QAuctionImage>createList("auctionImageList", AuctionImage.class, QAuctionImage.class, PathInits.DIRECT2);

    public final NumberPath<Long> auctionIndex = createNumber("auctionIndex", Long.class);

    public final ListPath<AuctionInfo, QAuctionInfo> auctionInfoList = this.<AuctionInfo, QAuctionInfo>createList("auctionInfoList", AuctionInfo.class, QAuctionInfo.class, PathInits.DIRECT2);

    public final StringPath auctionStatus = createString("auctionStatus");

    public final StringPath auctionType = createString("auctionType");

    public final BooleanPath autoReauctionEnabled = createBoolean("autoReauctionEnabled");

    public final NumberPath<Long> bidIncrement = createNumber("bidIncrement", Long.class);

    public final StringPath category = createString("category");

    public final QChatRoom chatRoom;

    public final DateTimePath<java.time.LocalDateTime> endingLocalDateTime = createDateTime("endingLocalDateTime", java.time.LocalDateTime.class);

    public final BooleanPath instantPurchaseEnabled = createBoolean("instantPurchaseEnabled");

    public final NumberPath<Long> instantPurchasePrice = createNumber("instantPurchasePrice", Long.class);

    public final QLiveStationChannel liveStationChannel;

    public final QMember member;

    public final DateTimePath<java.time.LocalDateTime> moddate = createDateTime("moddate", java.time.LocalDateTime.class);

    public final StringPath productDescription = createString("productDescription");

    public final StringPath productName = createString("productName");

    public final NumberPath<Long> reauctionStartingPrice = createNumber("reauctionStartingPrice", Long.class);

    public final DateTimePath<java.time.LocalDateTime> regdate = createDateTime("regdate", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> startingLocalDateTime = createDateTime("startingLocalDateTime", java.time.LocalDateTime.class);

    public final NumberPath<Long> startingPrice = createNumber("startingPrice", Long.class);

    public final StringPath subcategory = createString("subcategory");

    public final NumberPath<Long> viewCnt = createNumber("viewCnt", Long.class);

    public QAuction(String variable) {
        this(Auction.class, forVariable(variable), INITS);
    }

    public QAuction(Path<? extends Auction> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAuction(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAuction(PathMetadata metadata, PathInits inits) {
        this(Auction.class, metadata, inits);
    }

    public QAuction(Class<? extends Auction> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.auctionDetail = inits.isInitialized("auctionDetail") ? new QAuctionDetail(forProperty("auctionDetail"), inits.get("auctionDetail")) : null;
        this.chatRoom = inits.isInitialized("chatRoom") ? new QChatRoom(forProperty("chatRoom"), inits.get("chatRoom")) : null;
        this.liveStationChannel = inits.isInitialized("liveStationChannel") ? new QLiveStationChannel(forProperty("liveStationChannel")) : null;
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member"), inits.get("member")) : null;
    }

}

