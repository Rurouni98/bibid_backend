package bibid.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QChatRoomManagement is a Querydsl query type for ChatRoomManagement
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QChatRoomManagement extends EntityPathBase<ChatRoomManagement> {

    private static final long serialVersionUID = -384103319L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QChatRoomManagement chatRoomManagement = new QChatRoomManagement("chatRoomManagement");

    public final QChatRoom chatRoom;

    public final NumberPath<Long> chatRoomManagementIndex = createNumber("chatRoomManagementIndex", Long.class);

    public final DateTimePath<java.time.LocalDateTime> eventTime = createDateTime("eventTime", java.time.LocalDateTime.class);

    public final NumberPath<Long> participantIndex = createNumber("participantIndex", Long.class);

    public final StringPath participantNickname = createString("participantNickname");

    public final StringPath type = createString("type");

    public QChatRoomManagement(String variable) {
        this(ChatRoomManagement.class, forVariable(variable), INITS);
    }

    public QChatRoomManagement(Path<? extends ChatRoomManagement> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QChatRoomManagement(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QChatRoomManagement(PathMetadata metadata, PathInits inits) {
        this(ChatRoomManagement.class, metadata, inits);
    }

    public QChatRoomManagement(Class<? extends ChatRoomManagement> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.chatRoom = inits.isInitialized("chatRoom") ? new QChatRoom(forProperty("chatRoom"), inits.get("chatRoom")) : null;
    }

}

