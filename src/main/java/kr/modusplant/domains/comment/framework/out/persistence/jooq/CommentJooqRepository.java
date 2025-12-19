package kr.modusplant.domains.comment.framework.out.persistence.jooq;

import kr.modusplant.domains.comment.domain.vo.Author;
import kr.modusplant.domains.comment.domain.vo.CommentPath;
import kr.modusplant.domains.comment.domain.vo.PostId;
import kr.modusplant.domains.comment.usecase.model.CommentOfAuthorPageModel;
import kr.modusplant.domains.comment.usecase.port.repository.CommentReadRepository;
import kr.modusplant.domains.comment.usecase.response.CommentOfPostResponse;
import kr.modusplant.jooq.tables.*;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.impl.DSL;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.jooq.impl.DSL.count;

@Repository
@RequiredArgsConstructor
public class CommentJooqRepository implements CommentReadRepository {

    private final DSLContext dsl;
    private final CommPost commPost = CommPost.COMM_POST;
    private final CommComment commComment = CommComment.COMM_COMMENT;
    private final SiteMember siteMember = SiteMember.SITE_MEMBER;
    private final CommCommentLike commentLike = CommCommentLike.COMM_COMMENT_LIKE;
    private final SiteMemberProf memberProf = SiteMemberProf.SITE_MEMBER_PROF;

    @Override
    public boolean existsByPostAndPath(PostId postId, CommentPath path) {
        return dsl.fetchExists(
                dsl.selectOne()
                        .from(commComment)
                        .where(commComment.POST_ULID.eq(postId.getId()))
                        .and(commComment.PATH.eq(path.getPath()))
        );
    }

    @Override
    public int countPostComment(PostId postId) {
        return dsl.selectCount()
                .from(commComment)
                .where(commComment.POST_ULID.eq(postId.getId()))
                .fetchOptional()
                .map(Record1::value1)
                .orElse(0);
    }

    public List<CommentOfPostResponse> findByPost(PostId postId) {
        return dsl.select(memberProf.IMAGE_PATH.as("profileImagePath"), siteMember.NICKNAME,
                        commComment.PATH, commComment.CONTENT, commComment.LIKE_COUNT,
                        DSL.when(commentLike.MEMB_UUID.isNotNull(), true).as("isLiked"),
                        commComment.CREATED_AT, commComment.IS_DELETED)
                .from(commComment)
                .join(commPost).on(commComment.POST_ULID.eq(commPost.ULID))
                .join(siteMember).on(commPost.AUTH_MEMB_UUID.eq(siteMember.UUID))
                .join(memberProf).on(siteMember.UUID.eq(memberProf.UUID))
                .leftJoin(commentLike).on(commComment.POST_ULID.eq(commentLike.POST_ULID)
                        .and(commComment.PATH.eq(commentLike.PATH)))
                .where(commComment.POST_ULID.eq(postId.getId()))
                .orderBy(commComment.CREATED_AT.desc())
                .fetchInto(CommentOfPostResponse.class);
    }

    public PageImpl<CommentOfAuthorPageModel> findByAuthor(Author author, Pageable pageable) {

        Optional<Record1<Integer>> totalComments = dsl.selectCount()
                .from(commComment)
                .join(siteMember).on(commComment.AUTH_MEMB_UUID.eq(siteMember.UUID))
                .where(commComment.AUTH_MEMB_UUID.eq(author.getMemberUuid()))
                .fetchOptional();

        if(totalComments.isPresent()) {
            int totalComment = totalComments.get().component1();

            List<CommentOfAuthorPageModel> commentList = dsl.select(commComment.CONTENT,
                            commComment.CREATED_AT, commPost.TITLE, commPost.ULID.as("postId"),
                            DSL.when(commentLike.MEMB_UUID.isNotNull(), true),
                            count(commComment.POST_ULID))
                    .from(commComment)
                    .join(siteMember).on(commComment.AUTH_MEMB_UUID.eq(siteMember.UUID))
                    .join(commPost).on(commComment.POST_ULID.eq(commPost.ULID))
                    .leftJoin(commentLike).on(commComment.POST_ULID.eq(commentLike.POST_ULID)
                            .and(commComment.PATH.eq(commentLike.PATH)))
                    .where(commComment.AUTH_MEMB_UUID.eq(author.getMemberUuid()))
                    .groupBy(commComment.CONTENT, commComment.CREATED_AT,
                            commPost.TITLE, commPost.ULID, commentLike.MEMB_UUID)
                    .orderBy(commComment.CREATED_AT.desc())
                    .limit(pageable.getPageSize())
                    .offset(pageable.getOffset())
                    .fetchInto(CommentOfAuthorPageModel.class);

            return new PageImpl<>(commentList, pageable, totalComment);
        } else {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }
    }
}
