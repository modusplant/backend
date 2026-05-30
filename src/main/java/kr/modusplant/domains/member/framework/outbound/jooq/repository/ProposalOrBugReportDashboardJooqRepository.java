package kr.modusplant.domains.member.framework.outbound.jooq.repository;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import kr.modusplant.domains.member.domain.enums.ProposalOrBugReportStatus;
import kr.modusplant.domains.member.usecase.model.read.ProposalOrBugReportDashboardReadModel;
import kr.modusplant.shared.framework.jooq.converter.JsonbJsonNodeConverter;
import kr.modusplant.shared.framework.jpa.exception.NotFoundEntityException;
import lombok.RequiredArgsConstructor;
import org.jooq.*;
import org.jooq.Record;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.NOT_FOUND_PROPOSAL_OR_BUG_REPORT;
import static kr.modusplant.jooq.Tables.PROP_BUG_REP;
import static kr.modusplant.jooq.Tables.SITE_MEMBER;
import static org.jooq.impl.DSL.*;

@Repository
@RequiredArgsConstructor
public class ProposalOrBugReportDashboardJooqRepository {
    private final DSLContext dsl;
    private final JsonbJsonNodeConverter jsonbJsonNodeConverter = new JsonbJsonNodeConverter();

    public @Nonnull ProposalOrBugReportDashboardReadModel getReadModelByReportId(String reportId) {
        return dsl.select(getDashboardFields())
                .from(PROP_BUG_REP)
                .leftJoin(SITE_MEMBER)
                .on(PROP_BUG_REP.MEMB_UUID.eq(SITE_MEMBER.UUID))
                .where(PROP_BUG_REP.ULID.eq(reportId))
                .fetchOptional(getDashboardRecordMapper())
                .orElseThrow(() -> new NotFoundEntityException(NOT_FOUND_PROPOSAL_OR_BUG_REPORT, "proposalOrBugReport"));
    }
    
    public @Nonnull List<ProposalOrBugReportDashboardReadModel> getReadModelsByPageSizeAndStatusAndReportId(
            Integer reportPageSize,
            String proposalOrBugReportStatus,
            @Nullable String lastReportId) {
        Field<String> statusName = when(PROP_BUG_REP.CHECKED_AT.isNull(), val(ProposalOrBugReportStatus.UNCHECKED.name()))
                .otherwise(val(ProposalOrBugReportStatus.CHECKED.name()))
                .as("status");
        Condition ulidCondition = lastReportId != null ?
                PROP_BUG_REP.ULID.lt(lastReportId) : noCondition();
        Condition statusCondition = proposalOrBugReportStatus != null ?
                statusName.eq(proposalOrBugReportStatus) : noCondition();

        return dsl.select(getDashboardFields())
                .from(PROP_BUG_REP)
                .leftJoin(SITE_MEMBER)
                .on(PROP_BUG_REP.MEMB_UUID.eq(SITE_MEMBER.UUID))
                .where(ulidCondition)
                .and(statusCondition)
                .orderBy(PROP_BUG_REP.ULID.desc())
                .limit(reportPageSize)
                .fetch(getDashboardRecordMapper());
    }

    private Field<?> [] getDashboardFields() {
        return new Field<?>[]{
                PROP_BUG_REP.ULID,
                PROP_BUG_REP.TITLE,
                PROP_BUG_REP.CONTENT,
                getImageFromPropBugRep(),
                PROP_BUG_REP.CHECKED_AT,
                PROP_BUG_REP.CREATED_AT,
                getDisplayTimeFieldFromPropBugRep(),
                SITE_MEMBER.NICKNAME,
                getStatusFieldFromPropBugRep()
        };
    }

    private @Nonnull RecordMapper<Record, ProposalOrBugReportDashboardReadModel> getDashboardRecordMapper() {
        return record -> new ProposalOrBugReportDashboardReadModel(
                record.get(PROP_BUG_REP.ULID),
                record.get(PROP_BUG_REP.TITLE),
                record.get(PROP_BUG_REP.CONTENT),
                record.get(getImageFromPropBugRep()),
                record.get(PROP_BUG_REP.CHECKED_AT),
                record.get(PROP_BUG_REP.CREATED_AT),
                record.get(getDisplayTimeFieldFromPropBugRep()),
                record.get(SITE_MEMBER.NICKNAME),
                record.get(getStatusFieldFromPropBugRep())
        );
    }

    private @NonNull Field<JsonNode> getImageFromPropBugRep() {
        return PROP_BUG_REP.IMAGE.convert(jsonbJsonNodeConverter).as("image");
    }

    private @Nonnull Field<LocalDateTime> getDisplayTimeFieldFromPropBugRep() {
        return coalesce(PROP_BUG_REP.CHECKED_AT, PROP_BUG_REP.CREATED_AT).as("displayTimestamp");
    }

    private @Nonnull Field<String> getStatusFieldFromPropBugRep() {
        return when(PROP_BUG_REP.CHECKED_AT.isNull(), val(ProposalOrBugReportStatus.UNCHECKED.getValue()))
                .otherwise(val(ProposalOrBugReportStatus.CHECKED.getValue()))
                .as("status");
    }
}
