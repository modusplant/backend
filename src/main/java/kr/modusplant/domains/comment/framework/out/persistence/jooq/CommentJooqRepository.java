package kr.modusplant.domains.comment.framework.out.persistence.jooq;

import kr.modusplant.domains.comment.domain.vo.Author;
import kr.modusplant.domains.comment.domain.vo.CommentPath;
import kr.modusplant.domains.comment.domain.vo.PostId;
import kr.modusplant.domains.comment.usecase.model.CommentOfAuthorPageModel;
import kr.modusplant.domains.comment.usecase.model.CommentOfPostReadModel;
import kr.modusplant.domains.comment.usecase.port.repository.CommentReadRepository;
import kr.modusplant.jooq.tables.*;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Field;
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

    public List<CommentOfPostReadModel> findByPost(PostId postId) {

        Field<Boolean> isLiked = DSL.when(commentLike.MEMB_UUID.isNotNull(), true).otherwise(false);

        return dsl.select(memberProf.IMAGE_PATH, siteMember.NICKNAME,
                        commComment.PATH, commComment.CONTENT, commComment.LIKE_COUNT,
                        isLiked, commComment.CREATED_AT, commComment.IS_DELETED)
                .from(commComment)
                .join(commPost).on(commComment.POST_ULID.eq(commPost.ULID))
                .join(siteMember).on(commComment.AUTH_MEMB_UUID.eq(siteMember.UUID))
                .join(memberProf).on(siteMember.UUID.eq(memberProf.UUID))
                .leftJoin(commentLike).on(commComment.POST_ULID.eq(commentLike.POST_ULID)
                        .and(commComment.PATH.eq(commentLike.PATH)))
                .where(commComment.POST_ULID.eq(postId.getId()))
                .orderBy(commComment.CREATED_AT.asc())
                .fetch(record -> new CommentOfPostReadModel(
                        record.getValue(memberProf.IMAGE_PATH), record.getValue(siteMember.NICKNAME),
                        record.getValue(commComment.PATH), record.getValue(commComment.CONTENT),
                        record.getValue(commComment.LIKE_COUNT), record.getValue(isLiked),
                        record.getValue(commComment.CREATED_AT).withNano(0), record.getValue(commComment.IS_DELETED)
                ));
    }

    public PageImpl<CommentOfAuthorPageModel> findByAuthor(Author author, Pageable pageable) {

        Optional<Record1<Integer>> totalComments = dsl.selectCount()
                .from(commComment)
                .join(siteMember).on(commComment.AUTH_MEMB_UUID.eq(siteMember.UUID))
                .where(commComment.AUTH_MEMB_UUID.eq(author.getMemberUuid()))
                .fetchOptional();

        if(totalComments.isPresent()) {
            int totalComment = totalComments.get().component1();

            Field<Boolean> isLiked = DSL.when(commentLike.MEMB_UUID.isNotNull(), true).otherwise(false);

            Field<Integer> totalCommentsOfPost = dsl.select(count())
                    .from(commComment)
                    .where(commComment.POST_ULID.eq(commPost.ULID)
                            .and(commComment.IS_DELETED.eq(false)))
                    .asField();

            List<CommentOfAuthorPageModel> commentList = dsl.select(commComment.CONTENT,
                            commComment.CREATED_AT, commPost.TITLE, commPost.ULID,
                            isLiked, totalCommentsOfPost)
                    .from(commComment)
                    .join(siteMember).on(commComment.AUTH_MEMB_UUID.eq(siteMember.UUID))
                    .join(commPost).on(commComment.POST_ULID.eq(commPost.ULID))
                    .leftJoin(commentLike).on(commComment.POST_ULID.eq(commentLike.POST_ULID)
                            .and(commComment.PATH.eq(commentLike.PATH)))
                    .where(commComment.AUTH_MEMB_UUID.eq(author.getMemberUuid()))
                        .and(commComment.IS_DELETED.eq(false))
                    .groupBy(commComment.CONTENT, commComment.CREATED_AT,
                            commPost.TITLE, commPost.ULID, commentLike.MEMB_UUID)
                    .orderBy(commComment.CREATED_AT.desc())
                    .limit(pageable.getPageSize())
                    .offset(pageable.getOffset())
                    .fetch(record -> new CommentOfAuthorPageModel(
                            record.getValue(commComment.CONTENT), record.getValue(commComment.CREATED_AT).withNano(0),
                            record.getValue(commPost.TITLE), record.getValue(commPost.ULID),
                            record.getValue(isLiked), record.getValue(totalCommentsOfPost)
                    ));

            return new PageImpl<>(commentList, pageable, totalComment);
        } else {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }
    }
}
