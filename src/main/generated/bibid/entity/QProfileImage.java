package bibid.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProfileImage is a Querydsl query type for ProfileImage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProfileImage extends EntityPathBase<ProfileImage> {

    private static final long serialVersionUID = 1758513893L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProfileImage profileImage = new QProfileImage("profileImage");

    public final StringPath filepath = createString("filepath");

    public final NumberPath<Long> filesize = createNumber("filesize", Long.class);

    public final StringPath filestatus = createString("filestatus");

    public final StringPath filetype = createString("filetype");

    public final QMember member;

    public final StringPath newfilename = createString("newfilename");

    public final StringPath originalname = createString("originalname");

    public final NumberPath<Long> profileImageIndex = createNumber("profileImageIndex", Long.class);

    public QProfileImage(String variable) {
        this(ProfileImage.class, forVariable(variable), INITS);
    }

    public QProfileImage(Path<? extends ProfileImage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProfileImage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProfileImage(PathMetadata metadata, PathInits inits) {
        this(ProfileImage.class, metadata, inits);
    }

    public QProfileImage(Class<? extends ProfileImage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member"), inits.get("member")) : null;
    }

}

