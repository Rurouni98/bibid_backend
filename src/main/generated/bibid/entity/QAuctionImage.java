package bibid.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAuctionImage is a Querydsl query type for AuctionImage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAuctionImage extends EntityPathBase<AuctionImage> {

    private static final long serialVersionUID = 175195787L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAuctionImage auctionImage = new QAuctionImage("auctionImage");

    public final QAuction auction;

    public final NumberPath<Long> auctionImageIndex = createNumber("auctionImageIndex", Long.class);

    public final StringPath filename = createString("filename");

    public final StringPath fileoriginname = createString("fileoriginname");

    public final StringPath filepath = createString("filepath");

    public final NumberPath<Long> filesize = createNumber("filesize", Long.class);

    public final StringPath filetype = createString("filetype");

    public final BooleanPath isThumbnail = createBoolean("isThumbnail");

    public QAuctionImage(String variable) {
        this(AuctionImage.class, forVariable(variable), INITS);
    }

    public QAuctionImage(Path<? extends AuctionImage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAuctionImage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAuctionImage(PathMetadata metadata, PathInits inits) {
        this(AuctionImage.class, metadata, inits);
    }

    public QAuctionImage(Class<? extends AuctionImage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.auction = inits.isInitialized("auction") ? new QAuction(forProperty("auction"), inits.get("auction")) : null;
    }

}

