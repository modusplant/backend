package kr.modusplant.domains.comment.framework.out.persistence.jooq;

import kr.modusplant.domains.comment.domain.vo.Author;
import kr.modusplant.domains.comment.domain.vo.PostId;
import kr.modusplant.domains.comment.usecase.port.repository.CommentReadRepository;
import kr.modusplant.domains.comment.usecase.response.CommentResponse;
import kr.modusplant.jooq.tables.CommComment;
import kr.modusplant.jooq.tables.CommPost;
import kr.modusplant.jooq.tables.SiteMember;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentJooqRepository implements CommentReadRepository {

    private final DSLContext dsl;
    private final CommPost commPost = CommPost.COMM_POST;
    private final CommComment commComment = CommComment.COMM_COMMENT;
    private final SiteMember siteMember = SiteMember.SITE_MEMBER;

    public List<CommentResponse> findByPost(PostId postId) {
        return dsl.select(commPost.ULID, commComment.PATH, siteMember.NICKNAME,
                commComment.CONTENT, commComment.IS_DELETED, commComment.CREATED_AT)
                .from(commPost, commComment, siteMember)
                .join(siteMember).on(commComment.AUTH_MEMB_UUID.eq(siteMember.UUID))
                .where(commComment.POST_ULID.eq(postId.getId()))
                .orderBy(commComment.CREATED_AT.asc())
                .fetch(record -> new CommentResponse(
                    record.get(commPost.ULID), record.get(commComment.PATH), record.get(siteMember.NICKNAME),
                    record.get(commComment.CONTENT), record.get(commComment.IS_DELETED), record.get(commComment.CREATED_AT).toString()
                ));
    }

    public List<CommentResponse> findByAuthor(Author author) {
        return dsl.select(commPost.ULID, commComment.PATH, siteMember.NICKNAME,
                        commComment.CONTENT, commComment.IS_DELETED, commComment.CREATED_AT)
                .from(commPost, commComment, siteMember)
                .join(siteMember).on(commComment.AUTH_MEMB_UUID.eq(siteMember.UUID))
                .where(commComment.AUTH_MEMB_UUID.eq(author.getMemberUuid()))
                .orderBy(commComment.CREATED_AT.asc())
                .fetch(record -> new CommentResponse(
                        record.get(commPost.ULID), record.get(commComment.PATH), record.get(siteMember.NICKNAME),
                        record.get(commComment.CONTENT), record.get(commComment.IS_DELETED), record.get(commComment.CREATED_AT).toString()
                ));
    }
}
