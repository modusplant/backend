package kr.modusplant.domains.comment.framework.out.persistence.jpa.repository.supers;

import kr.modusplant.domains.comment.usecase.response.CommentResponse;
import kr.modusplant.jooq.tables.CommComment;
import kr.modusplant.jooq.tables.CommPost;
import kr.modusplant.jooq.tables.SiteMember;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CommentJooqRepository {

    private final DSLContext dsl;
//    private final CommPost commPost;
//    private final CommComment commComment;
//    private final SiteMember siteMember;
//
//    public List<CommentResponse> findByPostUlid(String postId) {
//        return dsl.select(commPost.ULID, commComment.PATH, siteMember.NICKNAME,
//                commComment.CONTENT, commComment.IS_DELETED, commComment.CREATED_AT)
//                .from(commPost, commComment, siteMember)
//                .join(siteMember).on(commComment.AUTH_MEMB_UUID.eq(siteMember.UUID))
//                .where(commComment.POST_ULID.eq(postId))
//                .orderBy(commComment.CREATED_AT.asc())
//                .fetch(record -> new CommentResponse(
//                    record.get(commComment.POST_ULID), record.get(commComment.PATH), record.get(siteMember.NICKNAME),
//                    record.get(commComment.CONTENT), record.get(commComment.IS_DELETED), record.get(commComment.CREATED_AT).toString()
//                ));
//    }
//
//    public List<CommentResponse> findByAuthMemberUuid(UUID authMemberUuid) {
//        return dsl.select(commPost.ULID, commComment.PATH, siteMember.NICKNAME,
//                        commComment.CONTENT, commComment.IS_DELETED, commComment.CREATED_AT)
//                .from(commPost, commComment, siteMember)
//                .join(siteMember).on(commComment.AUTH_MEMB_UUID.eq(siteMember.UUID))
//                .where(commComment.AUTH_MEMB_UUID.eq(authMemberUuid))
//                .orderBy(commComment.CREATED_AT.asc())
//                .fetch(record -> new CommentResponse(
//                        record.get(commComment.POST_ULID), record.get(commComment.PATH), record.get(siteMember.NICKNAME),
//                        record.get(commComment.CONTENT), record.get(commComment.IS_DELETED), record.get(commComment.CREATED_AT).toString()
//                ));
//    }
}
