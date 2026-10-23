package kr.modusplant.domains.member.framework.outbound.jooq.repository;

import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Select;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

import static kr.modusplant.jooq.Tables.*;
import static org.jooq.impl.DSL.*;

@Repository
@RequiredArgsConstructor
public class ActivitySubjectCommentJooqRepository {
    private final DSLContext dsl;

    // 대상 댓글: 회원의 모든 댓글 + 발행된 게시글에 포함된 댓글
    public void archiveAllByMemberIdAndPostUlids(UUID memberId, String[] publishedPostUlids) {
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
                                .where(isTargetComment(memberId, publishedPostUlids))
                ).execute();
    }

    public void deleteRelatedRecordsByMemberIdAndPostUlids(UUID memberId, String[] publishedPostUlids) {
        dsl.batch(
                dsl.deleteFrom(COMM_COMMENT_LIKE)
                        .where(COMM_COMMENT_LIKE.POST_ULID.eq(any(publishedPostUlids))
                                .or(row(COMM_COMMENT_LIKE.POST_ULID, COMM_COMMENT_LIKE.PATH)
                                        .in(selectCommentIdsByMemberId(memberId)))),

                dsl.deleteFrom(COMM_COMMENT_ABU_REP)
                        .where(COMM_COMMENT_ABU_REP.POST_ULID.eq(any(publishedPostUlids))
                                .or(row(COMM_COMMENT_ABU_REP.POST_ULID, COMM_COMMENT_ABU_REP.PATH)
                                        .in(selectCommentIdsByMemberId(memberId)))),

                dsl.deleteFrom(COMM_COMMENT_ABUSE_REPORT_DASHBOARD)
                        .where(COMM_COMMENT_ABUSE_REPORT_DASHBOARD.POST_ULID.eq(any(publishedPostUlids))
                                .or(row(COMM_COMMENT_ABUSE_REPORT_DASHBOARD.POST_ULID, COMM_COMMENT_ABUSE_REPORT_DASHBOARD.PATH)
                                        .in(selectCommentIdsByMemberId(memberId))))
        ).execute();
    }

    public void deleteAllByMemberIdAndPostUlids(UUID memberId, String[] publishedPostUlids) {
        dsl.deleteFrom(COMM_COMMENT)
                .where(isTargetComment(memberId, publishedPostUlids))
                .execute();
    }

    public void decreaseLikeCountOfCommentsLikedByMemberId(UUID memberId) {
        dsl.update(COMM_COMMENT)
                .set(COMM_COMMENT.LIKE_COUNT, COMM_COMMENT.LIKE_COUNT.minus(1))
                .set(COMM_COMMENT.UPDATED_AT, LocalDateTime.now())
                .where(
                        row(COMM_COMMENT.POST_ULID, COMM_COMMENT.PATH).in(
                                        select(COMM_COMMENT_LIKE.POST_ULID, COMM_COMMENT_LIKE.PATH)
                                                .from(COMM_COMMENT_LIKE)
                                                .where(COMM_COMMENT_LIKE.MEMB_UUID.eq(memberId)))
                                .and(COMM_COMMENT.LIKE_COUNT.gt(0))   // 좋아요 수가 1 이상일 때만 좋아요 수 감소
                )
                .execute();
    }

    private Condition isTargetComment(UUID memberId, String[] publishedPostUlids) {
        return COMM_COMMENT.AUTH_MEMB_UUID.eq(memberId)
                .or(COMM_COMMENT.POST_ULID.eq(any(publishedPostUlids)));
    }

    private Select<Record2<String, String>> selectCommentIdsByMemberId(UUID memberId) {
        return select(COMM_COMMENT.POST_ULID, COMM_COMMENT.PATH)
                .from(COMM_COMMENT)
                .where(COMM_COMMENT.AUTH_MEMB_UUID.eq(memberId));
    }
}
