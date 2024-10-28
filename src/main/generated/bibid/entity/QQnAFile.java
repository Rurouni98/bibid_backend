package bibid.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QQnAFile is a Querydsl query type for QnAFile
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QQnAFile extends EntityPathBase<QnAFile> {

    private static final long serialVersionUID = -1305141299L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QQnAFile qnAFile = new QQnAFile("qnAFile");

    public final StringPath fileName = createString("fileName");

    public final StringPath fileOriginName = createString("fileOriginName");

    public final QMember member;

    public final QQnA qna;

    public final NumberPath<Long> qnaFileIndex = createNumber("qnaFileIndex", Long.class);

    public QQnAFile(String variable) {
        this(QnAFile.class, forVariable(variable), INITS);
    }

    public QQnAFile(Path<? extends QnAFile> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QQnAFile(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QQnAFile(PathMetadata metadata, PathInits inits) {
        this(QnAFile.class, metadata, inits);
    }

    public QQnAFile(Class<? extends QnAFile> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member"), inits.get("member")) : null;
        this.qna = inits.isInitialized("qna") ? new QQnA(forProperty("qna"), inits.get("qna")) : null;
    }

}

