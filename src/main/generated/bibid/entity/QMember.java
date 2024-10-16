package bibid.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = -717781363L;

    public static final QMember member = new QMember("member1");

    public final StringPath addressDetail = createString("addressDetail");

    public final StringPath email = createString("email");

    public final BooleanPath isSeller = createBoolean("isSeller");

    public final StringPath memberAddress = createString("memberAddress");

    public final StringPath memberId = createString("memberId");

    public final NumberPath<Long> memberIndex = createNumber("memberIndex", Long.class);

    public final StringPath memberPnum = createString("memberPnum");

    public final StringPath memberPw = createString("memberPw");

    public final StringPath name = createString("name");

    public final StringPath nickname = createString("nickname");

    public final StringPath profileUrl = createString("profileUrl");

    public final StringPath role = createString("role");

    public QMember(String variable) {
        super(Member.class, forVariable(variable));
    }

    public QMember(Path<? extends Member> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMember(PathMetadata metadata) {
        super(Member.class, metadata);
    }

}

