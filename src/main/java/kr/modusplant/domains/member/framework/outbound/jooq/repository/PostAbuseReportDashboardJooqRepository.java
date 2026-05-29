package kr.modusplant.domains.member.framework.outbound.jooq.repository;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import kr.modusplant.domains.member.framework.outbound.jooq.record.PostAbuseReportDashboardRecord;
import kr.modusplant.domains.member.usecase.model.read.PostAbuseReportDashboardReadModel;
import kr.modusplant.shared.framework.jooq.converter.JsonbJsonNodeConverter;
import lombok.RequiredArgsConstructor;
import org.jooq.*;
import org.jooq.Record;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static kr.modusplant.jooq.Tables.*;
import static org.jooq.impl.DSL.noCondition;

@Repository
@RequiredArgsConstructor
public class PostAbuseReportDashboardJooqRepository {
    private final DSLContext dsl;
    private final JsonbJsonNodeConverter jsonConverter = new JsonbJsonNodeConverter();

    public @Nonnull List<PostAbuseReportDashboardRecord> getDashboardsByPageSizeAndStatus(
            Integer reportPageSize,
            @Nullable String status,
            @Nullable String lastPostUlid) {

        Condition ulidCondition = lastPostUlid != null ?
                COMM_POST_ABUSE_REPORT_DASHBOARD.POST_ULID.lt(lastPostUlid) : noCondition();
        Condition statusCondition = status != null ?
                COMM_POST_ABUSE_REPORT_DASHBOARD.STATUS.eq(status) : noCondition();

        return dsl.select(getPostAbuseReportDashboardFields())
                .from(COMM_POST_ABUSE_REPORT_DASHBOARD)
                .leftJoin(COMM_POST)
                .on(COMM_POST_ABUSE_REPORT_DASHBOARD.POST_ULID.eq(COMM_POST.ULID))
                .leftJoin(SITE_MEMBER)
                .on(COMM_POST.AUTH_MEMB_UUID.eq(SITE_MEMBER.UUID))
                .where(ulidCondition)
                .and(statusCondition)
                .orderBy(COMM_POST_ABUSE_REPORT_DASHBOARD.POST_ULID.desc())
                .limit(reportPageSize)
                .fetch(getPostAbuseReportDashboardRecordMapper());
    }

    public @Nonnull List<PostAbuseReportDashboardReadModel> getPostAbuseReportDashboardReadModels(
            List<PostAbuseReportDashboardRecord> records) {
        return records.stream()
                .map(this::toDashboardReadModel)
                .toList();
    }

    private Field<?>[] getPostAbuseReportDashboardFields() {
        return new Field<?>[]{
                COMM_POST_ABUSE_REPORT_DASHBOARD.POST_ULID,
                COMM_POST.TITLE,
                getConvertedContentField(),
                COMM_POST_ABUSE_REPORT_DASHBOARD.REPORT_COUNT,
                COMM_POST_ABUSE_REPORT_DASHBOARD.STATUS,
                COMM_POST_ABUSE_REPORT_DASHBOARD.FIRST_REPORTED_AT,
                COMM_POST_ABUSE_REPORT_DASHBOARD.LAST_REPORTED_AT,
                getDisplayTimestampField(),
                SITE_MEMBER.NICKNAME
        };
    }

    private @Nonnull RecordMapper<Record, PostAbuseReportDashboardRecord> getPostAbuseReportDashboardRecordMapper() {
        return record -> new PostAbuseReportDashboardRecord(
                record.get(COMM_POST_ABUSE_REPORT_DASHBOARD.POST_ULID),
                record.get(COMM_POST.TITLE),
                record.get(getConvertedContentField()),
                record.get(COMM_POST_ABUSE_REPORT_DASHBOARD.REPORT_COUNT),
                record.get(COMM_POST_ABUSE_REPORT_DASHBOARD.STATUS),
                record.get(COMM_POST_ABUSE_REPORT_DASHBOARD.FIRST_REPORTED_AT),
                record.get(COMM_POST_ABUSE_REPORT_DASHBOARD.LAST_REPORTED_AT),
                record.get(getDisplayTimestampField()),
                record.get(SITE_MEMBER.NICKNAME)
        );
    }

    private @NonNull Field<JsonNode> getConvertedContentField() {
        return COMM_POST.CONTENT.convert(jsonConverter);
    }

    private @Nonnull Field<LocalDateTime> getDisplayTimestampField() {
        return COMM_POST_ABUSE_REPORT_DASHBOARD.LAST_REPORTED_AT.as("displayTimestamp");
    }

    private PostAbuseReportDashboardReadModel toDashboardReadModel(PostAbuseReportDashboardRecord record) {
        return new PostAbuseReportDashboardReadModel(
                record.ulid(),
                record.title(),
                record.content(),
                record.reportCount(),
                record.status(),
                record.firstReportedAt(),
                record.lastReportedAt(),
                record.displayTimestamp(),
                record.nickname()
        );
    }
}
