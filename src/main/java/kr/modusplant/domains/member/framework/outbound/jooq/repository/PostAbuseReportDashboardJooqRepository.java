package kr.modusplant.domains.member.framework.outbound.jooq.repository;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import kr.modusplant.domains.member.usecase.model.read.PostAbuseReportDashboardReadModel;
import kr.modusplant.shared.framework.jooq.converter.JsonbJsonNodeConverter;
import kr.modusplant.shared.framework.jpa.exception.NotFoundEntityException;
import lombok.RequiredArgsConstructor;
import org.jooq.*;
import org.jooq.Record;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.NOT_FOUND_POST_ABUSE_REPORT;
import static kr.modusplant.jooq.Tables.*;
import static org.jooq.impl.DSL.noCondition;

@Repository
@RequiredArgsConstructor
public class PostAbuseReportDashboardJooqRepository {
    private final DSLContext dsl;
    private final JsonbJsonNodeConverter jsonConverter = new JsonbJsonNodeConverter();

    public @Nonnull PostAbuseReportDashboardReadModel getReadModelByPostId(String postId) {
        return dsl.select(getDashboardFields())
                .from(COMM_POST_ABUSE_REPORT_DASHBOARD)
                .leftJoin(COMM_POST)
                .on(COMM_POST_ABUSE_REPORT_DASHBOARD.POST_ULID.eq(COMM_POST.ULID))
                .leftJoin(SITE_MEMBER)
                .on(COMM_POST.AUTH_MEMB_UUID.eq(SITE_MEMBER.UUID))
                .where(COMM_POST_ABUSE_REPORT_DASHBOARD.POST_ULID.eq(postId))
                .fetchOptional(getDashboardReadModelMapper())
                .orElseThrow(() -> new NotFoundEntityException(NOT_FOUND_POST_ABUSE_REPORT, "postAbuseReport"));
    }

    public @Nonnull List<PostAbuseReportDashboardReadModel> getReadModelsByPageSizeAndStatusAndPostUlid(
            Integer reportPageSize,
            @Nullable String status,
            @Nullable String lastPostUlid) {

        Condition ulidCondition = lastPostUlid != null ?
                COMM_POST_ABUSE_REPORT_DASHBOARD.POST_ULID.lt(lastPostUlid) : noCondition();
        Condition statusCondition = status != null ?
                COMM_POST_ABUSE_REPORT_DASHBOARD.STATUS.eq(status) : noCondition();

        return dsl.select(getDashboardFields())
                .from(COMM_POST_ABUSE_REPORT_DASHBOARD)
                .leftJoin(COMM_POST)
                .on(COMM_POST_ABUSE_REPORT_DASHBOARD.POST_ULID.eq(COMM_POST.ULID))
                .leftJoin(SITE_MEMBER)
                .on(COMM_POST.AUTH_MEMB_UUID.eq(SITE_MEMBER.UUID))
                .where(ulidCondition)
                .and(statusCondition)
                .orderBy(COMM_POST_ABUSE_REPORT_DASHBOARD.POST_ULID.desc())
                .limit(reportPageSize)
                .fetch(getDashboardReadModelMapper());
    }

    private Field<?>[] getDashboardFields() {
        return new Field<?>[]{
                COMM_POST_ABUSE_REPORT_DASHBOARD.POST_ULID,
                COMM_POST.TITLE,
                getConvertedContentFieldFromCommPost(),
                COMM_POST_ABUSE_REPORT_DASHBOARD.REPORT_COUNT,
                COMM_POST_ABUSE_REPORT_DASHBOARD.STATUS,
                COMM_POST_ABUSE_REPORT_DASHBOARD.FIRST_REPORTED_AT,
                COMM_POST_ABUSE_REPORT_DASHBOARD.LAST_REPORTED_AT,
                getDisplayTimestampFieldFromCommPostAbuseReportDashboard(),
                SITE_MEMBER.NICKNAME
        };
    }

    private @Nonnull RecordMapper<Record, PostAbuseReportDashboardReadModel> getDashboardReadModelMapper() {
        return record -> new PostAbuseReportDashboardReadModel(
                record.get(COMM_POST_ABUSE_REPORT_DASHBOARD.POST_ULID),
                record.get(COMM_POST.TITLE),
                record.get(getConvertedContentFieldFromCommPost()),
                record.get(COMM_POST_ABUSE_REPORT_DASHBOARD.REPORT_COUNT),
                record.get(COMM_POST_ABUSE_REPORT_DASHBOARD.STATUS),
                record.get(COMM_POST_ABUSE_REPORT_DASHBOARD.FIRST_REPORTED_AT),
                record.get(COMM_POST_ABUSE_REPORT_DASHBOARD.LAST_REPORTED_AT),
                record.get(getDisplayTimestampFieldFromCommPostAbuseReportDashboard()),
                record.get(SITE_MEMBER.NICKNAME)
        );
    }

    private @NonNull Field<JsonNode> getConvertedContentFieldFromCommPost() {
        return COMM_POST.CONTENT.convert(jsonConverter);
    }

    private @Nonnull Field<LocalDateTime> getDisplayTimestampFieldFromCommPostAbuseReportDashboard() {
        return COMM_POST_ABUSE_REPORT_DASHBOARD.LAST_REPORTED_AT.as("displayTimestamp");
    }
}
