package kr.modusplant.domains.member.framework.outbound.jooq.repository;

import kr.modusplant.domains.member.framework.outbound.jooq.record.ActivitySubjectCommentIdRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Row2;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static kr.modusplant.jooq.Tables.*;
import static org.jooq.impl.DSL.*;

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

    public List<ActivitySubjectCommentIdRecord> getCommentIdsByMemberId(UUID memberId) {
        return dsl.select(COMM_COMMENT.POST_ULID, COMM_COMMENT.PATH)
                .from(COMM_COMMENT)
                .where(COMM_COMMENT.AUTH_MEMB_UUID.eq(memberId))
                .fetchInto(ActivitySubjectCommentIdRecord.class);
    }

    public void archiveAllByCommentIds(List<ActivitySubjectCommentIdRecord> activitySubjectCommentIds) {
        dsl.insertInto(COMM_COMMENT_ARCHIVE,
                        COMM_COMMENT_ARCHIVE.PATH,
                        COMM_COMMENT_ARCHIVE.POST_ULID,
                        COMM_COMMENT_ARCHIVE.AUTH_MEMB_UUID,
                        COMM_COMMENT_ARCHIVE.CONTENT,
                        COMM_COMMENT_ARCHIVE.IS_DELETED,
                        COMM_COMMENT_ARCHIVE.CREATED_AT,
                        COMM_COMMENT_ARCHIVE.UPDATED_AT,
                        COMM_COMMENT_ARCHIVE.EDITED_AT,
                        COMM_COMMENT_ARCHIVE.ARCHIVED_AT
                )
                .select(
                        select(
                                COMM_COMMENT.PATH,
                                COMM_COMMENT.POST_ULID,
                                COMM_COMMENT.AUTH_MEMB_UUID,
                                COMM_COMMENT.CONTENT,
                                COMM_COMMENT.IS_DELETED,
                                COMM_COMMENT.CREATED_AT,
                                COMM_COMMENT.UPDATED_AT,
                                COMM_COMMENT.EDITED_AT,
                                val(LocalDateTime.now())
                        )
                                .from(COMM_COMMENT)
                                .where(row(COMM_COMMENT.POST_ULID, COMM_COMMENT.PATH)
                                        .in(convertToCommentIdRow2List(activitySubjectCommentIds)))
                ).execute();
    }

    public void deleteAllAndRelatedRecordsByMemberIdAndCommentIds(
            UUID memberId, List<ActivitySubjectCommentIdRecord> activitySubjectCommentIds) {
        dsl.batch(
                dsl.deleteFrom(COMM_COMMENT_LIKE)
                        .where(row(COMM_COMMENT_LIKE.POST_ULID, COMM_COMMENT_LIKE.PATH)
                                .in(convertToCommentIdRow2List(activitySubjectCommentIds))),

                dsl.deleteFrom(COMM_COMMENT)
                        .where(COMM_COMMENT.AUTH_MEMB_UUID.eq(memberId))
        ).execute();
    }

    public void decreaseLikeCountByCommentIds(List<ActivitySubjectCommentIdRecord> activitySubjectCommentIds) {
        List<Row2<String, String>> activitySubjectCommentIdRows = convertToCommentIdRow2List(activitySubjectCommentIds);
        dsl.update(COMM_COMMENT)
                .set(COMM_COMMENT.LIKE_COUNT, COMM_COMMENT.LIKE_COUNT.minus(1))
                .set(COMM_COMMENT.UPDATED_AT, LocalDateTime.now())
                .where(
                        row(COMM_COMMENT.POST_ULID, COMM_COMMENT.PATH).in(activitySubjectCommentIdRows)
                                .and(COMM_COMMENT.LIKE_COUNT.gt(0))   // 좋아요 수가 1 이상일 때만 좋아요 수 감소
                )
                .execute();
    }

    private List<Row2<String, String>> convertToCommentIdRow2List(List<ActivitySubjectCommentIdRecord> activitySubjectCommentIds) {
        return activitySubjectCommentIds.stream()
                .map(dto -> row(dto.postUlid(), dto.path()))
                .toList();
    }
}
