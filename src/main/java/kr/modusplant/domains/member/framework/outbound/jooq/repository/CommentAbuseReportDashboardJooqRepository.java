package kr.modusplant.domains.member.framework.outbound.jooq.repository;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import kr.modusplant.domains.member.domain.enums.AbuseReportStatus;
import kr.modusplant.domains.member.usecase.model.read.CommentAbuseReportDashboardReadModel;
import kr.modusplant.shared.framework.jpa.exception.NotFoundEntityException;
import lombok.RequiredArgsConstructor;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.NOT_FOUND_COMMENT_ABUSE_REPORT;
import static kr.modusplant.jooq.Tables.*;
import static org.jooq.impl.DSL.noCondition;
import static org.jooq.impl.DSL.val;

@Repository
@RequiredArgsConstructor
public class CommentAbuseReportDashboardJooqRepository {
    private final DSLContext dsl;

    public @Nonnull CommentAbuseReportDashboardReadModel getReadModelByPostUlidAndPath(String postUlid, String path) {
        return dsl.select(getDashboardFields())
                .from(COMM_COMMENT_ABUSE_REPORT_DASHBOARD)
                .leftJoin(COMM_COMMENT)
                .on(COMM_COMMENT_ABUSE_REPORT_DASHBOARD.POST_ULID.eq(COMM_COMMENT.POST_ULID)
                        .and(COMM_COMMENT_ABUSE_REPORT_DASHBOARD.PATH.eq(COMM_COMMENT.PATH)))
                .leftJoin(SITE_MEMBER)
                .on(COMM_COMMENT.AUTH_MEMB_UUID.eq(SITE_MEMBER.UUID))
                .where(COMM_COMMENT_ABUSE_REPORT_DASHBOARD.POST_ULID.eq(postUlid)
                        .and(COMM_COMMENT_ABUSE_REPORT_DASHBOARD.PATH.eq(path)))
                .fetchOptional(getDashboardReadModelMapper())
                .orElseThrow(() -> new NotFoundEntityException(NOT_FOUND_COMMENT_ABUSE_REPORT, "commentAbuseReport"));
    }

    public @Nonnull List<CommentAbuseReportDashboardReadModel> getReadModelsByPageSizeAndStatusAndCursor(
            Integer reportPageSize,
            @Nullable String status,
            @Nullable String lastPostUlid,
            @Nullable String lastPath) {

        Condition cursorCondition = (lastPostUlid != null && lastPath != null) ?
                COMM_COMMENT_ABUSE_REPORT_DASHBOARD.POST_ULID.lt(lastPostUlid)
                        .or(COMM_COMMENT_ABUSE_REPORT_DASHBOARD.POST_ULID.eq(lastPostUlid)
                                .and(COMM_COMMENT_ABUSE_REPORT_DASHBOARD.PATH.lt(lastPath)))
                : noCondition();
        Condition statusCondition = status != null ?
                COMM_COMMENT_ABUSE_REPORT_DASHBOARD.STATUS.eq(status) : noCondition();

        return dsl.select(getDashboardFields())
                .from(COMM_COMMENT_ABUSE_REPORT_DASHBOARD)
                .leftJoin(COMM_COMMENT)
                .on(COMM_COMMENT_ABUSE_REPORT_DASHBOARD.POST_ULID.eq(COMM_COMMENT.POST_ULID)
                        .and(COMM_COMMENT_ABUSE_REPORT_DASHBOARD.PATH.eq(COMM_COMMENT.PATH)))
                .leftJoin(SITE_MEMBER)
                .on(COMM_COMMENT.AUTH_MEMB_UUID.eq(SITE_MEMBER.UUID))
                .where(cursorCondition)
                .and(statusCondition)
                .orderBy(COMM_COMMENT_ABUSE_REPORT_DASHBOARD.POST_ULID.desc(),
                        COMM_COMMENT_ABUSE_REPORT_DASHBOARD.PATH.desc())
                .limit(reportPageSize)
                .fetch(getDashboardReadModelMapper());
    }

    private Field<?>[] getDashboardFields() {
        return new Field<?>[]{
                COMM_COMMENT_ABUSE_REPORT_DASHBOARD.POST_ULID,
                COMM_COMMENT_ABUSE_REPORT_DASHBOARD.PATH,
                COMM_COMMENT.CONTENT,
                COMM_COMMENT_ABUSE_REPORT_DASHBOARD.REPORT_COUNT,
                COMM_COMMENT_ABUSE_REPORT_DASHBOARD.STATUS,
                getStatusValueFieldFromCommCommentAbuseReportDashboard(),
                COMM_COMMENT_ABUSE_REPORT_DASHBOARD.FIRST_REPORTED_AT,
                COMM_COMMENT_ABUSE_REPORT_DASHBOARD.LAST_REPORTED_AT,
                getDisplayTimestampField(),
                SITE_MEMBER.NICKNAME
        };
    }

    private @Nonnull RecordMapper<Record, CommentAbuseReportDashboardReadModel> getDashboardReadModelMapper() {
        return record -> new CommentAbuseReportDashboardReadModel(
                record.get(COMM_COMMENT_ABUSE_REPORT_DASHBOARD.POST_ULID),
                record.get(COMM_COMMENT_ABUSE_REPORT_DASHBOARD.PATH),
                record.get(COMM_COMMENT.CONTENT),
                record.get(COMM_COMMENT_ABUSE_REPORT_DASHBOARD.REPORT_COUNT),
                record.get(COMM_COMMENT_ABUSE_REPORT_DASHBOARD.STATUS),
                record.get(getStatusValueFieldFromCommCommentAbuseReportDashboard()),
                record.get(COMM_COMMENT_ABUSE_REPORT_DASHBOARD.FIRST_REPORTED_AT),
                record.get(COMM_COMMENT_ABUSE_REPORT_DASHBOARD.LAST_REPORTED_AT),
                record.get(getDisplayTimestampField()),
                record.get(SITE_MEMBER.NICKNAME)
        );
    }

    private @Nonnull Field<LocalDateTime> getDisplayTimestampField() {
        return COMM_COMMENT_ABUSE_REPORT_DASHBOARD.LAST_REPORTED_AT.as("displayTimestamp");
    }

    private @Nonnull Field<String> getStatusValueFieldFromCommCommentAbuseReportDashboard() {
        return DSL.case_(COMM_COMMENT_ABUSE_REPORT_DASHBOARD.STATUS)
                .when(AbuseReportStatus.UNCHECKED.name(), val(AbuseReportStatus.UNCHECKED.getValue()))
                .when(AbuseReportStatus.DISMISSED.name(), val(AbuseReportStatus.DISMISSED.getValue()))
                .otherwise(val(AbuseReportStatus.BLINDED.getValue()))
                .as("statusValue");
    }
}
