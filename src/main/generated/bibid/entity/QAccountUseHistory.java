package bibid.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAccountUseHistory is a Querydsl query type for AccountUseHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAccountUseHistory extends EntityPathBase<AccountUseHistory> {

    private static final long serialVersionUID = -991809209L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAccountUseHistory accountUseHistory = new QAccountUseHistory("accountUseHistory");

    public final QAccount account;

    public final NumberPath<Long> accountUseHistoryIndex = createNumber("accountUseHistoryIndex", Long.class);

    public final StringPath afterBalance = createString("afterBalance");

    public final QAuction auction;

    public final StringPath beforeBalance = createString("beforeBalance");

    public final StringPath changeAccount = createString("changeAccount");

    public final DateTimePath<java.time.LocalDateTime> createdTime = createDateTime("createdTime", java.time.LocalDateTime.class);

    public final QMember member;

    public final StringPath useType = createString("useType");

    public QAccountUseHistory(String variable) {
        this(AccountUseHistory.class, forVariable(variable), INITS);
    }

    public QAccountUseHistory(Path<? extends AccountUseHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAccountUseHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAccountUseHistory(PathMetadata metadata, PathInits inits) {
        this(AccountUseHistory.class, metadata, inits);
    }

    public QAccountUseHistory(Class<? extends AccountUseHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.account = inits.isInitialized("account") ? new QAccount(forProperty("account"), inits.get("account")) : null;
        this.auction = inits.isInitialized("auction") ? new QAuction(forProperty("auction"), inits.get("auction")) : null;
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member"), inits.get("member")) : null;
    }

}

