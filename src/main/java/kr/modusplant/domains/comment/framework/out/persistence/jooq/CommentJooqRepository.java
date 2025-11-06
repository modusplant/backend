package kr.modusplant.domains.comment.framework.out.persistence.jooq;

import kr.modusplant.domains.comment.domain.vo.Author;
import kr.modusplant.domains.comment.domain.vo.PostId;
import kr.modusplant.domains.comment.usecase.port.repository.CommentReadRepository;
import kr.modusplant.domains.comment.usecase.response.CommentOfAuthorResponse;
import kr.modusplant.domains.comment.usecase.response.CommentOfPostResponse;
import kr.modusplant.jooq.tables.CommComment;
import kr.modusplant.jooq.tables.CommPost;
import kr.modusplant.jooq.tables.SiteMember;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.jooq.impl.DSL.count;

@Repository
@RequiredArgsConstructor
public class CommentJooqRepository implements CommentReadRepository {

    private final DSLContext dsl;
    private final CommPost commPost = CommPost.COMM_POST;
    private final CommComment commComment = CommComment.COMM_COMMENT;
    private final SiteMember siteMember = SiteMember.SITE_MEMBER;

    public List<CommentOfPostResponse> findByPost(PostId postId) {
        return dsl.select(siteMember.NICKNAME, commComment.PATH, commComment.CONTENT,
                        commComment.LIKE_COUNT, commComment.CREATED_AT, commComment.IS_DELETED)
                .from(commComment)
                .join(commPost).on(commComment.POST_ULID.eq(commPost.ULID))
                .join(siteMember).on(commPost.AUTH_MEMB_UUID.eq(siteMember.UUID))
                .where(commComment.POST_ULID.eq(postId.getId()))
                .orderBy(commComment.CREATED_AT.desc())
                .fetchInto(CommentOfPostResponse.class);
//                .fetch(record -> new CommentOfPostResponse(
//                        record.get(siteMember.NICKNAME), record.get(commComment.PATH),
//                        record.get(commComment.CONTENT), record.get(commComment.LIKE_COUNT),
//                        record.get(commComment.CREATED_AT).toString(), record.get(commComment.IS_DELETED)
//                        ));
    }

    public List<CommentOfAuthorResponse> findByAuthor(Author author) {
        return dsl.select(commComment.CONTENT, commComment.CREATED_AT,
                commPost.TITLE, count(commComment.POST_ULID))
                .from(commComment)
                .join(siteMember).on(commComment.AUTH_MEMB_UUID.eq(siteMember.UUID))
                .join(commPost).on(commComment.POST_ULID.eq(commPost.ULID))
                .where(commComment.AUTH_MEMB_UUID.eq(author.getMemberUuid()))
                .orderBy(commComment.CREATED_AT.desc())
                .fetchInto(CommentOfAuthorResponse.class);
//                .fetch(record -> new CommentOfAuthorResponse(
//                        record.get(commComment.CONTENT), record.get(commComment.CREATED_AT).toString(),
//                        record.get(commPost.TITLE), record.get(count(commComment.POST_ULID))
//                ));
    }
}
