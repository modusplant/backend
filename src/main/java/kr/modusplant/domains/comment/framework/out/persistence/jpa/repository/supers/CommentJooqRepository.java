package kr.modusplant.domains.comment.framework.out.persistence.jpa.repository.supers;

import kr.modusplant.domains.comment.usecase.response.CommentResponse;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class CommentJooqRepository {
//    private final DSLContext dsl = DSL.using("temporary url");
//
////    @Query("SELECT c.postEntity.ulid, c.id.path, m.nickname, c.content, c.isDeleted, c.createdAt " +
////            "FROM CommCommentEntity c " +
////            "INNER JOIN c.authMember m " +
////            "WHERE c.postEntity.ulid = :postUlid " +
////            "ORDER BY c.createdAt ASC")
//    public List<CommentResponse> findByPostUlid(String postId) {
//        return dsl.select(CommComment.ULID, CommComment.PATH, SiteMember.NICKNAME,
//                CommComment.CONTENT, CommComment.IS_DELETED, CommComment.CREATED_AT)
//                .from(CommPost, CommComment, SiteMember)
//                .join(SiteMember).on(CommComment.AUTH_MEMB_UUID.eq(SiteMember.UUID))
//                .where(CommComment.POST_ULID.eq(postId))
//                .orderBy(CommComment.CREATED_AT.asc())
//                .fetch(record -> new CommentResponse(
//                    CommComment.ULID, CommComment.PATH, SiteMember.NICKNAME,
//                    CommComment.CONTENT, CommComment.IS_DELETED, CommComment.CREATED_AT
//                ));
//    }
//
////    @Query("SELECT c.postEntity.ulid, c.id.path, m.nickname, c.content, c.isDeleted, c.createdAt " +
////            "FROM CommCommentEntity c " +
////            "INNER JOIN c.authMember m " +
////            "WHERE c.authMember.uuid = :memberUuid " +
////            "ORDER BY c.createdAt ASC")
////    List<CommentResponse> findByAuthMemberUuid(@Param("memberUuid") UUID memberUuid);
//    public List<CommentResponse> findByAuthMemberUuid(UUID authMemberUuid) {
//        return dsl.select(CommPost.ULID, CommComment.PATH, SiteMember.NICKNAME,
//                        CommComment.CONTENT, CommComment.IS_DELETED, CommComment.CREATED_AT)
//                .from(CommPost, CommComment, SiteMember)
//                .join(SiteMember).on(CommComment.AUTH_MEMB_UUID.eq(SiteMember.UUID))
//                .where(CommComment.AUTH_MEMB_UUID.eq(authMemberUuid))
//                .orderBy(CommComment.CREATED_AT.asc())
//                .fetch(record -> new CommentResponse(
//                        CommComment.ULID, CommComment.PATH, SiteMember.NICKNAME,
//                        CommComment.CONTENT, CommComment.IS_DELETED, CommComment.CREATED_AT
//                ));
//    }
}
