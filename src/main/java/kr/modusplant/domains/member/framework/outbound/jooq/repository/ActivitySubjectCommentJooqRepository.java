package kr.modusplant.domains.member.framework.outbound.jooq.repository;

import kr.modusplant.domains.member.framework.outbound.jooq.record.ActivitySubjectCommentIdRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Row2;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static kr.modusplant.jooq.Tables.COMM_COMMENT;
import static kr.modusplant.jooq.Tables.COMM_COMMENT_LIKE;
import static org.jooq.impl.DSL.row;

@Repository
@RequiredArgsConstructor
public class ActivitySubjectCommentJooqRepository {
    private final DSLContext dsl;

    public List<ActivitySubjectCommentIdRecord> getCommentIdsLikedByMemberId(UUID memberId) {
        return dsl.select(COMM_COMMENT_LIKE.POST_ULID, COMM_COMMENT_LIKE.PATH)
                .from(COMM_COMMENT_LIKE)
                .where(COMM_COMMENT_LIKE.MEMB_UUID.eq(memberId))
                .fetchInto(ActivitySubjectCommentIdRecord.class);
    }

    public void decreaseLikeCountByCommentIds(List<ActivitySubjectCommentIdRecord> activitySubjectCommentIds) {
        List<Row2<String, String>> activitySubjectCommentIdRows = activitySubjectCommentIds.stream()
                .map(dto -> row(dto.postUlid(), dto.path()))
                .toList();
        dsl.update(COMM_COMMENT)
                .set(COMM_COMMENT.LIKE_COUNT, COMM_COMMENT.LIKE_COUNT.minus(1))
                .set(COMM_COMMENT.UPDATED_AT, LocalDateTime.now())
                .where(
                        row(COMM_COMMENT.POST_ULID, COMM_COMMENT.PATH).in(activitySubjectCommentIdRows)
                                .and(COMM_COMMENT.LIKE_COUNT.gt(0))   // 좋아요 수가 1 이상일 때만 좋아요 수 감소
                )
                .execute();
    }

    public void markAsWithdrawnByMemberId(UUID memberId) {
        dsl.update(COMM_COMMENT)
                .setNull(COMM_COMMENT.AUTH_MEMB_UUID)
                .set(COMM_COMMENT.IS_DELETED, true)
                .set(COMM_COMMENT.UPDATED_AT, LocalDateTime.now())
                .where(COMM_COMMENT.AUTH_MEMB_UUID.eq(memberId)).execute();
    }
}
