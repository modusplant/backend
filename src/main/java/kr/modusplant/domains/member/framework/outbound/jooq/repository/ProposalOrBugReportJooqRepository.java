package kr.modusplant.domains.member.framework.outbound.jooq.repository;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import kr.modusplant.domains.member.domain.enums.ProposalOrBugReportStatus;
import kr.modusplant.domains.member.framework.outbound.jooq.record.ProposalOrBugReportAdminPageRecord;
import kr.modusplant.domains.member.usecase.model.read.ProposalOrBugReportAdminPageReadModel;
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
import static kr.modusplant.jooq.Tables.*;
import static org.jooq.impl.DSL.*;

@Repository
@RequiredArgsConstructor
public class ProposalOrBugReportJooqRepository {
    private final DSLContext dsl;
    private final AmazonS3Service amazonS3Service;
    private final JsonbJsonNodeConverter jsonbJsonNodeConverter = new JsonbJsonNodeConverter();

    public @Nonnull List<String> getImageFileKeysFromReportId(String reportId) {
        return dsl.select(
                        field("jsonb_array_elements({0}) ->> 'src'", String.class, PROP_BUG_REP.IMAGE)
                )
                .from(PROP_BUG_REP)
                .where(PROP_BUG_REP.ULID.eq(reportId))
                .fetchInto(String.class);
    }

    public @Nonnull ProposalOrBugReportAdminPageRecord getAdminPageRecordByReportId(String reportId) {
        return dsl.select(getProposalOrBugReportAdminPageFields())
                .from(PROP_BUG_REP)
                .leftJoin(SITE_MEMBER)
                .on(PROP_BUG_REP.MEMB_UUID.eq(SITE_MEMBER.UUID))
                .where(PROP_BUG_REP.ULID.eq(reportId))
                .fetchOptional(getProposalOrBugReportAdminPageRecordMapper())
                .orElseThrow(() -> new NotFoundEntityException(NOT_FOUND_PROPOSAL_OR_BUG_REPORT, "proposalOrBugReport"));
    }
    
    public @Nonnull List<ProposalOrBugReportAdminPageRecord> getAdminPageRecordsByPageSizeAndStatus(
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

        return dsl.select(getProposalOrBugReportAdminPageFields())
                .from(PROP_BUG_REP)
                .leftJoin(SITE_MEMBER)
                .on(PROP_BUG_REP.MEMB_UUID.eq(SITE_MEMBER.UUID))
                .where(ulidCondition)
                .and(statusCondition)
                .orderBy(PROP_BUG_REP.ULID.desc())
                .limit(reportPageSize)
                .fetch(getProposalOrBugReportAdminPageRecordMapper());
    }

    public @Nonnull ProposalOrBugReportAdminPageReadModel getAdminPageReadModel(
            ProposalOrBugReportAdminPageRecord record) {
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
        return new ProposalOrBugReportAdminPageReadModel(
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

    public @Nonnull List<ProposalOrBugReportAdminPageReadModel> getProposalOrBugReportAdminPageReadModels(
            List<ProposalOrBugReportAdminPageRecord> records) {
        List<ProposalOrBugReportAdminPageReadModel> readModels = new ArrayList<>();
        for (ProposalOrBugReportAdminPageRecord record : records) {
            readModels.add(getAdminPageReadModel(record));
        }
        return readModels;
    }

    public void archiveByReportId(String reportId) {
        dsl.insertInto(PROP_BUG_REP_ARCHIVE,
                        PROP_BUG_REP_ARCHIVE.ULID,
                        PROP_BUG_REP_ARCHIVE.MEMB_UUID,
                        PROP_BUG_REP_ARCHIVE.TITLE,
                        PROP_BUG_REP_ARCHIVE.CONTENT,
                        PROP_BUG_REP_ARCHIVE.CREATED_AT,
                        PROP_BUG_REP_ARCHIVE.CHECKED_AT,
                        PROP_BUG_REP_ARCHIVE.ARCHIVED_AT
                )
                .select(
                        select(
                                PROP_BUG_REP.ULID,
                                PROP_BUG_REP.MEMB_UUID,
                                PROP_BUG_REP.TITLE,
                                PROP_BUG_REP.CONTENT,
                                PROP_BUG_REP.CREATED_AT,
                                PROP_BUG_REP.CHECKED_AT,
                                val(LocalDateTime.now())
                        )
                                .from(PROP_BUG_REP)
                                .where(PROP_BUG_REP.ULID.eq(reportId))
                ).execute();
    }

    private static Field<?> [] getProposalOrBugReportAdminPageFields() {
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

    private static @Nonnull RecordMapper<Record, ProposalOrBugReportAdminPageRecord> getProposalOrBugReportAdminPageRecordMapper() {
        return record -> new ProposalOrBugReportAdminPageRecord(
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

    private static @Nonnull Field<LocalDateTime> getDisplayTimeFieldFromPropBugRep() {
        return coalesce(PROP_BUG_REP.CHECKED_AT, PROP_BUG_REP.CREATED_AT).as("displayTimestamp");
    }

    private static @Nonnull Field<String> getStatusFieldFromPropBugRep() {
        return when(PROP_BUG_REP.CHECKED_AT.isNull(), val(ProposalOrBugReportStatus.UNCHECKED.getValue()))
                .otherwise(val(ProposalOrBugReportStatus.CHECKED.getValue()))
                .as("status");
    }
}
