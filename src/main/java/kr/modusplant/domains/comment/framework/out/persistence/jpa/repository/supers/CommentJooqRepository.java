package kr.modusplant.domains.comment.framework.out.persistence.jpa.repository.supers;

import kr.modusplant.domains.comment.usecase.response.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CommentJooqRepository {

    private final DSLContext dsl;

//    public List<CommentResponse> findByPostUlid(String postId) {
//        return dsl.select(CommPost.ULID, CommComment.PATH, SiteMember.NICKNAME,
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
