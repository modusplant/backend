package kr.modusplant.domains.comment.framework.outbound.jooq.repository;

import jakarta.annotation.Nullable;
import kr.modusplant.domains.comment.domain.vo.Author;
import kr.modusplant.domains.comment.domain.vo.CommentPageSize;
import kr.modusplant.domains.comment.domain.vo.CommentPath;
import kr.modusplant.domains.comment.domain.vo.PostId;
import kr.modusplant.domains.comment.usecase.model.CommentOfAuthorReadModel;
import kr.modusplant.domains.comment.usecase.model.CommentOfPostReadModel;
import kr.modusplant.domains.comment.usecase.port.repository.CommentQueryRepository;
import kr.modusplant.jooq.tables.*;
import lombok.RequiredArgsConstructor;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.jooq.impl.DSL.count;

@Repository
@RequiredArgsConstructor
public class CommentJooqRepository implements CommentQueryRepository {

    private final DSLContext dsl;
    private final CommPost commPost = CommPost.COMM_POST;
    private final CommComment commComment = CommComment.COMM_COMMENT;
    private final CommComment rootComment = CommComment.COMM_COMMENT.as("root_comment");
    private final CommComment replyToComment = CommComment.COMM_COMMENT.as("reply_to_comment");
    private final SiteMember siteMember = SiteMember.SITE_MEMBER;
    private final SiteMember replyToMember = SiteMember.SITE_MEMBER.as("reply_to_member");
    private final CommCommentLike commentLike = CommCommentLike.COMM_COMMENT_LIKE;
    private final SiteMemberProf memberProf = SiteMemberProf.SITE_MEMBER_PROF;

    private final Field<Boolean> isLiked = DSL.when(commentLike.MEMB_UUID.isNotNull(), true).otherwise(false);
    private final Field<Boolean> isReply = DSL.field(commComment.PATH.like("%.%"));

    public List<CommentOfPostReadModel> findByPost(PostId postId, Author nullableAuthor) {
        return dsl.select(getCommentOfPostFields())
                .from(getCommentOfPostTable(nullableAuthor))
                .where(commComment.POST_ULID.eq(postId.getValue()))
                .orderBy(getCommentOfPostOrder())
                .fetch(this::toCommentOfPostReadModel);
    }

    @Override
    public List<CommentOfPostReadModel> findByPost(
            PostId postId, @Nullable CommentPath lastPath, CommentPageSize pageSize, Author nullableAuthor) {
        SelectSeekStepN<Record> orderedComments = dsl.select(getCommentOfPostFields())
                .from(getCommentOfPostTable(nullableAuthor))
                .where(commComment.POST_ULID.eq(postId.getValue()))
                .orderBy(getCommentOfPostOrder());

        if (lastPath == null) { // 첫 댓글부터 조회
            return orderedComments
                    .limit(pageSize.getValue() + 1)
                    .fetch(this::toCommentOfPostReadModel);
        }

        // 특정 lastPath 댓글의 정렬 키 이후부터 조회
        Record lastCommentSortKey = dsl.select(rootComment.PATH, isReply, commComment.PATH)
                .from(commComment)
                .join(rootComment).on(getRootCommentJoinCondition())
                .where(commComment.POST_ULID.eq(postId.getValue()))
                .and(commComment.PATH.eq(lastPath.getValue()))
                .fetchOne();
        if (lastCommentSortKey == null) {
            return List.of();
        }
        return orderedComments
                .seek(lastCommentSortKey.intoArray())
                .limit(pageSize.getValue() + 1)
                .fetch(this::toCommentOfPostReadModel);
    }

    private List<SelectField<?>> getCommentOfPostFields() {
        return List.of(
                memberProf.IMAGE_PATH,
                siteMember.NICKNAME,
                commComment.PATH,
                commComment.CONTENT,
                commComment.LIKE_COUNT,
                isLiked,
                commComment.CREATED_AT,
                commComment.IS_DELETED,
                commComment.EDITED_AT,
                commComment.REPLY_TO_PATH,
                replyToMember.NICKNAME);
    }

    private Table<?> getCommentOfPostTable(Author nullableAuthor) {
        Condition likeJoinCondition = nullableAuthor != null
                ? commComment.POST_ULID.eq(commentLike.POST_ULID)
                .and(commComment.PATH.eq(commentLike.PATH))
                .and(commentLike.MEMB_UUID.eq(nullableAuthor.getUuid()))
                : DSL.falseCondition();

        return commComment
                .join(siteMember).on(commComment.AUTH_MEMB_UUID.eq(siteMember.UUID))
                .join(memberProf).on(commComment.AUTH_MEMB_UUID.eq(memberProf.UUID))
                .leftJoin(commentLike).on(likeJoinCondition)
                .leftJoin(replyToComment).on(
                        replyToComment.POST_ULID.eq(commComment.POST_ULID)
                                .and(replyToComment.PATH.eq(commComment.REPLY_TO_PATH)))
                .leftJoin(replyToMember).on(replyToComment.AUTH_MEMB_UUID.eq(replyToMember.UUID))
                .leftJoin(rootComment).on(getRootCommentJoinCondition());
    }

    private Condition getRootCommentJoinCondition() {
        return rootComment.POST_ULID.eq(commComment.POST_ULID)
                .and(rootComment.PATH.notLike("%.%"))
                .and(commComment.PATH.eq(rootComment.PATH)
                        .or(commComment.PATH.like(rootComment.PATH.concat(".%"))));
    }

    private List<SortField<?>> getCommentOfPostOrder() {
        return List.of(
                rootComment.PATH.asc(),
                isReply.asc(), // 루트 행 → 답글 행
                commComment.PATH.asc());
    }

    private CommentOfPostReadModel toCommentOfPostReadModel(Record record) {
        return new CommentOfPostReadModel(
                record.getValue(memberProf.IMAGE_PATH),
                record.getValue(siteMember.NICKNAME),
                record.getValue(commComment.PATH),
                record.getValue(commComment.CONTENT),
                record.getValue(commComment.LIKE_COUNT),
                record.getValue(isLiked),
                record.getValue(commComment.CREATED_AT).withNano(0),
                record.getValue(commComment.EDITED_AT) == null ? null : record.getValue(commComment.EDITED_AT).withNano(0),
                record.getValue(commComment.IS_DELETED),
                record.getValue(commComment.REPLY_TO_PATH),
                record.getValue(replyToMember.NICKNAME)
        );
    }

    public PageImpl<CommentOfAuthorReadModel> findByAuthor(Author author, Pageable pageable) {

        Optional<Record1<Integer>> totalComments = dsl.selectCount()
                .from(commComment)
                .join(siteMember).on(commComment.AUTH_MEMB_UUID.eq(siteMember.UUID))
                .where(commComment.AUTH_MEMB_UUID.eq(author.getUuid()))
                .fetchOptional();

        if(totalComments.isPresent()) {
            int totalComment = totalComments.get().component1();

            Field<Boolean> isLiked = DSL.when(commentLike.MEMB_UUID.isNotNull(), true).otherwise(false);

            Field<Integer> totalCommentsOfPost = dsl.select(count())
                    .from(commComment)
                    .where(commComment.POST_ULID.eq(commPost.ULID)
                            .and(commComment.IS_DELETED.eq(false)))
                    .asField();

            List<CommentOfAuthorReadModel> commentList = dsl.select(commComment.CONTENT,
                            commComment.CREATED_AT, commPost.TITLE, commPost.ULID,
                            isLiked, totalCommentsOfPost)
                    .from(commComment)
                    .join(siteMember).on(commComment.AUTH_MEMB_UUID.eq(siteMember.UUID))
                    .join(commPost).on(commComment.POST_ULID.eq(commPost.ULID))
                    .leftJoin(commentLike).on(commComment.POST_ULID.eq(commentLike.POST_ULID)
                            .and(commComment.PATH.eq(commentLike.PATH)))
                    .where(commComment.AUTH_MEMB_UUID.eq(author.getUuid()))
                    .and(commComment.IS_DELETED.eq(false))
                    .groupBy(commComment.CONTENT, commComment.CREATED_AT,
                            commPost.TITLE, commPost.ULID, commentLike.MEMB_UUID)
                    .orderBy(commComment.CREATED_AT.desc())
                    .limit(pageable.getPageSize())
                    .offset(pageable.getOffset())
                    .fetch(record -> new CommentOfAuthorReadModel(
                            record.getValue(commComment.CONTENT), record.getValue(commComment.CREATED_AT).withNano(0),
                            record.getValue(commPost.TITLE), record.getValue(commPost.ULID),
                            record.getValue(isLiked), record.getValue(totalCommentsOfPost)
                    ));

            return new PageImpl<>(commentList, pageable, totalComment);
        } else {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }
    }

    @Override
    public Optional<LocalDateTime> findLatestUpdatedAtByPost(PostId postId) {
        return Optional.ofNullable(
                dsl.select(DSL.max(commComment.UPDATED_AT))
                        .from(commComment)
                        .where(commComment.POST_ULID.eq(postId.getValue()))
                        .fetchOne(DSL.max(commComment.UPDATED_AT))
        );
    }

    @Override
    public Optional<LocalDateTime> findLatestUpdatedAtByAuthor(Author author) {
        return Optional.ofNullable(
                dsl.select(DSL.max(commComment.UPDATED_AT))
                        .from(commComment)
                        .where(commComment.AUTH_MEMB_UUID.eq(author.getUuid()))
                        .fetchOne(DSL.max(commComment.UPDATED_AT))
        );
    }

    @Override
    public int findMaximumSiblingPathOrder(PostId postId, CommentPath path) {
        String pathValue = path.getValue();
        int lastDotIndex = pathValue.lastIndexOf('.');
        String parentPath = lastDotIndex < 0 ? "" : pathValue.substring(0, lastDotIndex);

        Condition siblingCondition = parentPath.isEmpty()
                ? commComment.PATH.notLike("%.%")
                : commComment.PATH.like(parentPath + ".%").and(commComment.PATH.notLike(parentPath + ".%.%"));

        List<String> siblingPathOrders = dsl.select(commComment.PATH)
                .from(commComment)
                .where(commComment.POST_ULID.eq(postId.getValue()))
                .and(siblingCondition)
                .fetch(commComment.PATH);

        int maxIndex = 0;
        for (String siblingPath : siblingPathOrders) {
            String lastSegment = siblingPath.substring(siblingPath.lastIndexOf('.') + 1);
            maxIndex = Math.max(maxIndex, Integer.parseInt(lastSegment));
        }
        return maxIndex;
    }

    @Override
    public int countPostComment(PostId postId) {
        return dsl.selectCount()
                .from(commComment)
                .where(commComment.POST_ULID.eq(postId.getValue()))
                .fetchOptional()
                .map(Record1::value1)
                .orElse(0);
    }

    @Override
    public boolean isCommentExists(PostId postId, CommentPath path) {
        return dsl.fetchExists(
                dsl.selectOne()
                        .from(commComment)
                        .where(commComment.POST_ULID.eq(postId.getValue()))
                        .and(commComment.PATH.eq(path.getValue()))
        );
    }

    @Override
    public boolean isCommentDeleted(PostId postId, CommentPath path) {
        return dsl.fetchExists(
                dsl.selectOne()
                        .from(commComment)
                        .where(commComment.POST_ULID.eq(postId.getValue()))
                        .and(commComment.PATH.eq(path.getValue()))
                        .and(commComment.IS_DELETED.eq(true))
        );
    }

    @Override
    public boolean isPostPublished(PostId postIdVO) {
        String postId = postIdVO.getValue();
        return postId != null && Boolean.TRUE.equals(
                dsl.select(commPost.IS_PUBLISHED)
                        .from(commPost)
                        .where(commPost.ULID.eq(postId))
                        .fetchOne(commPost.IS_PUBLISHED)
        );
    }
}