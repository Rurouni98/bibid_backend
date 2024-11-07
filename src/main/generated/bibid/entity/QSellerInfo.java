package bibid.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSellerInfo is a Querydsl query type for SellerInfo
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSellerInfo extends EntityPathBase<SellerInfo> {

    private static final long serialVersionUID = 750787392L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSellerInfo sellerInfo = new QSellerInfo("sellerInfo");

    public final StringPath businessClassification = createString("businessClassification");

    public final StringPath businessLocation = createString("businessLocation");

    public final StringPath businessName = createString("businessName");

    public final StringPath businessRegistrationNum = createString("businessRegistrationNum");

    public final StringPath exponent = createString("exponent");

    public final QMember member;

    public final StringPath salesCnt = createString("salesCnt");

    public final StringPath salesDeclaration = createString("salesDeclaration");

    public final NumberPath<Long> sellerInfoIndex = createNumber("sellerInfoIndex", Long.class);

    public QSellerInfo(String variable) {
        this(SellerInfo.class, forVariable(variable), INITS);
    }

    public QSellerInfo(Path<? extends SellerInfo> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSellerInfo(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSellerInfo(PathMetadata metadata, PathInits inits) {
        this(SellerInfo.class, metadata, inits);
    }

    public QSellerInfo(Class<? extends SellerInfo> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member"), inits.get("member")) : null;
    }

}

