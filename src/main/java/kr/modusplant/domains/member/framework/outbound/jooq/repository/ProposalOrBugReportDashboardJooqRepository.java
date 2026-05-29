package kr.modusplant.domains.member.framework.outbound.jooq.repository;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import kr.modusplant.domains.member.domain.enums.ProposalOrBugReportStatus;
import kr.modusplant.domains.member.framework.outbound.jooq.record.ProposalOrBugReportDashboardRecord;
import kr.modusplant.domains.member.usecase.model.read.ProposalOrBugReportDashboardReadModel;
import kr.modusplant.shared.framework.aws.service.AmazonS3Service;
import kr.modusplant.shared.framework.jooq.converter.JsonbJsonNodeConverter;
import kr.modusplant.shared.framework.jpa.exception.NotFoundEntityException;
import lombok.RequiredArgsConstructor;
import org.jooq.*;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.NOT_FOUND_PROPOSAL_OR_BUG_REPORT;
import static kr.modusplant.jooq.Tables.PROP_BUG_REP;
import static kr.modusplant.jooq.Tables.SITE_MEMBER;
import static org.jooq.impl.DSL.*;

@Repository
@RequiredArgsConstructor
public class ProposalOrBugReportDashboardJooqRepository {
    private final DSLContext dsl;
    private final AmazonS3Service amazonS3Service;
    private final JsonbJsonNodeConverter jsonbJsonNodeConverter = new JsonbJsonNodeConverter();

    public @Nonnull ProposalOrBugReportDashboardRecord getRecordByReportId(String reportId) {
        return dsl.select(getDashboardFields())
                .from(PROP_BUG_REP)
                .leftJoin(SITE_MEMBER)
                .on(PROP_BUG_REP.MEMB_UUID.eq(SITE_MEMBER.UUID))
                .where(PROP_BUG_REP.ULID.eq(reportId))
                .fetchOptional(getDashboardRecordMapper())
                .orElseThrow(() -> new NotFoundEntityException(NOT_FOUND_PROPOSAL_OR_BUG_REPORT, "proposalOrBugReport"));
    }
    
    public @Nonnull List<ProposalOrBugReportDashboardRecord> getRecordsByPageSizeAndStatusAndReportId(
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

    public @Nonnull ProposalOrBugReportDashboardReadModel getReadModelByRecord(
            ProposalOrBugReportDashboardRecord record) {
        JsonNode imageJsonNode = jsonbJsonNodeConverter.from(record.image());
        List<String> srcStringList = null;
        if (imageJsonNode != null && imageJsonNode.isArray()) {
            srcStringList = StreamSupport.stream(imageJsonNode.spliterator(), false)
                    .filter(node -> node.hasNonNull("src"))
                    .map(node -> node.get("src").asText())
                    .toList();
        }
        List<byte[]> imageByteList = new ArrayList<>();
        if (srcStringList != null) {
            for (String src : srcStringList) {
                imageByteList.add(amazonS3Service.downloadFile(src));
            }
        }
        return new ProposalOrBugReportDashboardReadModel(
                record.ulid(),
                record.title(),
                record.content(),
                imageByteList,
                record.checkedAt(),
                record.createdAt(),
                record.displayTimestamp(),
                record.nickname(),
                record.status()
        );
    }

    public @Nonnull List<ProposalOrBugReportDashboardReadModel> getReadModelsByRecords(
            List<ProposalOrBugReportDashboardRecord> records) {
        List<ProposalOrBugReportDashboardReadModel> readModels = new ArrayList<>();
        for (ProposalOrBugReportDashboardRecord record : records) {
            readModels.add(getReadModelByRecord(record));
        }
        return readModels;
    }

    private Field<?> [] getDashboardFields() {
        return new Field<?>[]{
                PROP_BUG_REP.ULID,
                PROP_BUG_REP.TITLE,
                PROP_BUG_REP.CONTENT,
                PROP_BUG_REP.IMAGE,
                PROP_BUG_REP.IMAGE_NUMBER,
                PROP_BUG_REP.CHECKED_AT,
                PROP_BUG_REP.CREATED_AT,
                getDisplayTimeFieldFromPropBugRep(),
                SITE_MEMBER.NICKNAME,
                getStatusFieldFromPropBugRep()
        };
    }

    private @Nonnull RecordMapper<Record, ProposalOrBugReportDashboardRecord> getDashboardRecordMapper() {
        return record -> new ProposalOrBugReportDashboardRecord(
                record.get(PROP_BUG_REP.ULID),
                record.get(PROP_BUG_REP.TITLE),
                record.get(PROP_BUG_REP.CONTENT),
                record.get(PROP_BUG_REP.IMAGE),
                record.get(PROP_BUG_REP.IMAGE_NUMBER),
                record.get(PROP_BUG_REP.CHECKED_AT),
                record.get(PROP_BUG_REP.CREATED_AT),
                record.get(getDisplayTimeFieldFromPropBugRep()),
                record.get(SITE_MEMBER.NICKNAME),
                record.get(getStatusFieldFromPropBugRep())
        );
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
