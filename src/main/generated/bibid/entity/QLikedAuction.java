package bibid.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLikedAuction is a Querydsl query type for LikedAuction
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLikedAuction extends EntityPathBase<LikedAuction> {

    private static final long serialVersionUID = -616358007L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLikedAuction likedAuction = new QLikedAuction("likedAuction");

    public final QAuction auction;

    public final NumberPath<Long> likedAuctionIndex = createNumber("likedAuctionIndex", Long.class);

    public final QMember member;

    public QLikedAuction(String variable) {
        this(LikedAuction.class, forVariable(variable), INITS);
    }

    public QLikedAuction(Path<? extends LikedAuction> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLikedAuction(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLikedAuction(PathMetadata metadata, PathInits inits) {
        this(LikedAuction.class, metadata, inits);
    }

    public QLikedAuction(Class<? extends LikedAuction> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.auction = inits.isInitialized("auction") ? new QAuction(forProperty("auction"), inits.get("auction")) : null;
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member"), inits.get("member")) : null;
    }

}

