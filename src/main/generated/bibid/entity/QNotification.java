package bibid.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QNotification is a Querydsl query type for Notification
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNotification extends EntityPathBase<Notification> {

    private static final long serialVersionUID = 1150386846L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QNotification notification = new QNotification("notification");

    public final EnumPath<NotificationType> alertCategory = createEnum("alertCategory", NotificationType.class);

    public final StringPath alertContent = createString("alertContent");

    public final DateTimePath<java.time.LocalDateTime> alertDate = createDateTime("alertDate", java.time.LocalDateTime.class);

    public final StringPath alertTitle = createString("alertTitle");

    public final BooleanPath isSent = createBoolean("isSent");

    public final BooleanPath isViewed = createBoolean("isViewed");

    public final QMember member;

    public final NumberPath<Long> notificationIndex = createNumber("notificationIndex", Long.class);

    public final NumberPath<Long> referenceIndex = createNumber("referenceIndex", Long.class);

    public QNotification(String variable) {
        this(Notification.class, forVariable(variable), INITS);
    }

    public QNotification(Path<? extends Notification> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QNotification(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QNotification(PathMetadata metadata, PathInits inits) {
        this(Notification.class, metadata, inits);
    }

    public QNotification(Class<? extends Notification> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member"), inits.get("member")) : null;
    }

}

