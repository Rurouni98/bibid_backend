package bibid.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QQnA is a Querydsl query type for QnA
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QQnA extends EntityPathBase<QnA> {

    private static final long serialVersionUID = -377456975L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QQnA qnA = new QQnA("qnA");

    public final QAuction auction;

    public final QMember member;

    public final StringPath qnaContent = createString("qnaContent");

    public final NumberPath<Long> qnaIndex = createNumber("qnaIndex", Long.class);

    public final StringPath qnaTitle = createString("qnaTitle");

    public final DateTimePath<java.time.LocalDateTime> regDate = createDateTime("regDate", java.time.LocalDateTime.class);

    public QQnA(String variable) {
        this(QnA.class, forVariable(variable), INITS);
    }

    public QQnA(Path<? extends QnA> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QQnA(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QQnA(PathMetadata metadata, PathInits inits) {
        this(QnA.class, metadata, inits);
    }

    public QQnA(Class<? extends QnA> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.auction = inits.isInitialized("auction") ? new QAuction(forProperty("auction"), inits.get("auction")) : null;
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member"), inits.get("member")) : null;
    }

}

