package kr.modusplant.domains.comment.framework.outbound.jooq.repository;

import jakarta.annotation.Nullable;
import kr.modusplant.domains.comment.domain.vo.CommentAuthorId;
import kr.modusplant.domains.comment.domain.vo.CommentPageSize;
import kr.modusplant.domains.comment.domain.vo.CommentPath;
import kr.modusplant.domains.comment.domain.vo.ImmediateParentPostId;
import kr.modusplant.domains.comment.domain.vo.nullobject.EmptyCommentAuthorId;
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

@Repository
@RequiredArgsConstructor
public class CommentJooqRepository implements CommentQueryRepository {

    private final DSLContext dsl;
    private final CommPost commPost = CommPost.COMM_POST;
    private final CommComment commComment = CommComment.COMM_COMMENT;
    private final CommComment replyToComment = CommComment.COMM_COMMENT.as("reply_to_comment");
    private final SiteMember siteMember = SiteMember.SITE_MEMBER;
    private final SiteMember replyToMember = SiteMember.SITE_MEMBER.as("reply_to_member");
    private final CommCommentLike commentLike = CommCommentLike.COMM_COMMENT_LIKE;
    private final SiteMemberProf memberProf = SiteMemberProf.SITE_MEMBER_PROF;

    private final Field<Boolean> isLiked = DSL.when(commentLike.MEMB_UUID.isNotNull(), true).otherwise(false);

    public List<CommentOfPostReadModel> findByPost(ImmediateParentPostId immediateParentPostId, CommentAuthorId commentAuthorId) {
        return dsl.select(getCommentOfPostFields())
                .from(getCommentOfPostTable(commentAuthorId))
                .where(commComment.POST_ULID.eq(immediateParentPostId.getValue()))
                .orderBy(getCommentOfPostOrder())
                .fetch(this::toCommentOfPostReadModel);
    }

    @Override
    public List<CommentOfPostReadModel> findByPost(
            ImmediateParentPostId immediateParentPostId, @Nullable CommentPath lastPath, CommentPageSize pageSize, CommentAuthorId commentAuthorId) {
        SelectSeekStepN<Record> orderedComments = dsl.select(getCommentOfPostFields())
                .from(getCommentOfPostTable(commentAuthorId))
                .where(commComment.POST_ULID.eq(immediateParentPostId.getValue()))
                .orderBy(getCommentOfPostOrder());

        if (lastPath == null) { // 첫 댓글부터 조회
            return orderedComments
                    .limit(pageSize.getValue() + 1)
                    .fetch(this::toCommentOfPostReadModel);
        }

        // 특정 lastPath 댓글의 정렬 키 이후부터 조회
        Record lastCommentSortKey = dsl.select(commComment.PATH_1_DEPTH_VALUE, commComment.PATH_2_DEPTH_VALUE)
                .from(commComment)
                .where(commComment.POST_ULID.eq(immediateParentPostId.getValue()))
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
                replyToComment.PATH,
                replyToMember.NICKNAME,
                replyToComment.IS_DELETED);
    }

    private Table<?> getCommentOfPostTable(CommentAuthorId commentAuthorId) {
        Condition likeJoinCondition = commentAuthorId instanceof EmptyCommentAuthorId
                ? DSL.falseCondition()
                : commComment.POST_ULID.eq(commentLike.POST_ULID)
                .and(commComment.PATH.eq(commentLike.PATH))
                .and(commentLike.MEMB_UUID.eq(commentAuthorId.getValue()));

        return commComment
                .join(siteMember).on(commComment.AUTH_MEMB_UUID.eq(siteMember.UUID))
                .join(memberProf).on(commComment.AUTH_MEMB_UUID.eq(memberProf.UUID))
                .leftJoin(commentLike).on(likeJoinCondition)
                .leftJoin(replyToComment).on(
                        replyToComment.POST_ULID.eq(commComment.POST_ULID)
                                .and(replyToComment.PATH.eq(commComment.REPLY_TO_PATH)))
                .leftJoin(replyToMember).on(replyToComment.AUTH_MEMB_UUID.eq(replyToMember.UUID));
    }

    private List<SortField<?>> getCommentOfPostOrder() {
        return List.of(
                commComment.PATH_1_DEPTH_VALUE.asc(),
                commComment.PATH_2_DEPTH_VALUE.asc().nullsFirst()); // 루트 행 → 답글 행
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
                record.getValue(replyToComment.PATH) != null, // 답글 대상 행이 없으면 NULL → false
                record.getValue(replyToMember.NICKNAME),
                Boolean.TRUE.equals(record.getValue(replyToComment.IS_DELETED)) // 답글 대상이 없으면 NULL → false
        );
    }

    public PageImpl<CommentOfAuthorReadModel> findByAuthor(CommentAuthorId commentAuthorId, Pageable pageable) {

        Optional<Record1<Integer>> totalComments = dsl.selectCount()
                .from(commComment)
                .join(siteMember).on(commComment.AUTH_MEMB_UUID.eq(siteMember.UUID))
                .where(commComment.AUTH_MEMB_UUID.eq(commentAuthorId.getValue())
                        .and(commComment.IS_DELETED.isFalse()))
                .fetchOptional();

        if(totalComments.isPresent()) {
            int totalComment = totalComments.get().component1();

            Field<Boolean> isLiked = DSL.when(commentLike.MEMB_UUID.isNotNull(), true).otherwise(false);

            Field<Integer> totalCommentsOfPost = dsl.selectCount()
                    .from(commComment)
                    .where(commComment.POST_ULID.eq(commPost.ULID))
                    .asField();

            List<CommentOfAuthorReadModel> commentList = dsl.select(commComment.CONTENT,
                            commComment.CREATED_AT, commPost.TITLE, commPost.ULID,
                            isLiked, totalCommentsOfPost)
                    .from(commComment)
                    .join(siteMember).on(commComment.AUTH_MEMB_UUID.eq(siteMember.UUID))
                    .join(commPost).on(commComment.POST_ULID.eq(commPost.ULID))
                    .leftJoin(commentLike).on(commComment.POST_ULID.eq(commentLike.POST_ULID)
                            .and(commComment.PATH.eq(commentLike.PATH)))
                    .where(commComment.AUTH_MEMB_UUID.eq(commentAuthorId.getValue()))
                    .and(commComment.IS_DELETED.isFalse())
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
    public Optional<LocalDateTime> findLatestUpdatedAtByPost(ImmediateParentPostId immediateParentPostId) {
        return Optional.ofNullable(
                dsl.select(DSL.max(commComment.UPDATED_AT))
                        .from(commComment)
                        .where(commComment.POST_ULID.eq(immediateParentPostId.getValue()))
                        .fetchOne(DSL.max(commComment.UPDATED_AT))
        );
    }

    @Override
    public Optional<LocalDateTime> findLatestUpdatedAtByAuthor(CommentAuthorId commentAuthorId) {
        return Optional.ofNullable(
                dsl.select(DSL.max(commComment.UPDATED_AT))
                        .from(commComment)
                        .where(commComment.AUTH_MEMB_UUID.eq(commentAuthorId.getValue()))
                        .fetchOne(DSL.max(commComment.UPDATED_AT))
        );
    }

    @Override
    public int findMaximumSiblingPathOrder(ImmediateParentPostId immediateParentPostId, CommentPath path) {
        String pathValue = path.getValue();
        int dotIndex = pathValue.indexOf('.');

        // 루트 댓글이면 루트 행들의 1-Depth 값, 대댓글이면 같은 루트 댓글 아래 행들의 2-Depth 값 중 최댓값
        Field<Integer> maximumSiblingOrder;
        Condition siblingCondition;
        if (dotIndex < 0) {
            maximumSiblingOrder = DSL.max(commComment.PATH_1_DEPTH_VALUE);
            siblingCondition = commComment.PATH_2_DEPTH_VALUE.isNull();
        } else {
            maximumSiblingOrder = DSL.max(commComment.PATH_2_DEPTH_VALUE);
            siblingCondition = commComment.PATH_1_DEPTH_VALUE.eq(Integer.parseInt(pathValue.substring(0, dotIndex)));
        }

        Integer maximumOrder = dsl.select(maximumSiblingOrder)
                .from(commComment)
                .where(commComment.POST_ULID.eq(immediateParentPostId.getValue()))
                .and(siblingCondition)
                .fetchOne(maximumSiblingOrder);
        return maximumOrder == null ? 0 : maximumOrder;
    }

    @Override
    public boolean isCommentExists(ImmediateParentPostId immediateParentPostId, CommentPath path) {
        return dsl.fetchExists(
                dsl.selectOne()
                        .from(commComment)
                        .where(commComment.POST_ULID.eq(immediateParentPostId.getValue()))
                        .and(commComment.PATH.eq(path.getValue()))
        );
    }

    @Override
    public boolean isCommentDeleted(ImmediateParentPostId immediateParentPostId, CommentPath path) {
        return dsl.fetchExists(
                dsl.selectOne()
                        .from(commComment)
                        .where(commComment.POST_ULID.eq(immediateParentPostId.getValue()))
                        .and(commComment.PATH.eq(path.getValue()))
                        .and(commComment.IS_DELETED.isTrue())
        );
    }

    @Override
    public boolean isCommentWrittenByAuthor(ImmediateParentPostId immediateParentPostId, CommentPath path, CommentAuthorId commentAuthorId) {
        return dsl.fetchExists(
                dsl.selectOne()
                        .from(commComment)
                        .where(commComment.POST_ULID.eq(immediateParentPostId.getValue()))
                        .and(commComment.PATH.eq(path.getValue()))
                        .and(commComment.AUTH_MEMB_UUID.eq(commentAuthorId.getValue()))
        );
    }
}