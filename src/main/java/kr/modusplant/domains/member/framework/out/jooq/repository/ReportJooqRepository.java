package kr.modusplant.domains.member.framework.out.jooq.repository;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Nonnull;
import kr.modusplant.domains.member.domain.enums.ReportStatus;
import kr.modusplant.domains.member.domain.vo.ReportId;
import kr.modusplant.domains.member.framework.out.jooq.record.ProposalOrBugReportAdminPageRecord;
import kr.modusplant.domains.member.usecase.model.read.ProposalOrBugReportAdminPageReadModel;
import kr.modusplant.shared.framework.aws.service.AmazonS3Service;
import kr.modusplant.shared.framework.jooq.converter.JsonbJsonNodeConverter;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

import static kr.modusplant.jooq.Tables.*;
import static org.jooq.impl.DSL.*;

@Repository
@RequiredArgsConstructor
public class ReportJooqRepository {
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

    public @Nonnull ProposalOrBugReportAdminPageRecord getProposalOrBugReportAdminPageRecord(ReportId reportId) {
        return Objects.requireNonNull(dsl.select(
                        PROP_BUG_REP.ULID,
                        PROP_BUG_REP.TITLE,
                        PROP_BUG_REP.CONTENT,
                        PROP_BUG_REP.IMAGE,
                        PROP_BUG_REP.IMAGE_NUMBER,
                        PROP_BUG_REP.CHECKED_AT,
                        PROP_BUG_REP.CREATED_AT,
                        when(PROP_BUG_REP.CHECKED_AT.isNull(), val(PROP_BUG_REP.CREATED_AT))
                                .otherwise(val(PROP_BUG_REP.CHECKED_AT))
                                .as("displayTime"),
                        SITE_MEMBER.NICKNAME,
                        when(PROP_BUG_REP.CHECKED_AT.isNull(), val(ReportStatus.UNCHECKED.getValue()))
                                .otherwise(val(ReportStatus.CHECKED.getValue()))
                                .as("status")
                )
                .from(PROP_BUG_REP)
                .leftJoin(SITE_MEMBER)
                .on(PROP_BUG_REP.MEMB_UUID.eq(SITE_MEMBER.UUID))
                .where(PROP_BUG_REP.ULID.eq(reportId.getValue()))
                .fetchOne(record -> new ProposalOrBugReportAdminPageRecord(
                        record.get(PROP_BUG_REP.ULID),
                        record.get(PROP_BUG_REP.TITLE),
                        record.get(PROP_BUG_REP.CONTENT),
                        record.get(PROP_BUG_REP.IMAGE),
                        record.get(PROP_BUG_REP.IMAGE_NUMBER),
                        record.get(PROP_BUG_REP.CHECKED_AT),
                        record.get(PROP_BUG_REP.CREATED_AT),
                        record.get("displayTime", LocalDateTime.class),
                        record.get(SITE_MEMBER.NICKNAME),
                        record.get("status", String.class)
                )));
    }

    public @Nonnull ProposalOrBugReportAdminPageReadModel getProposalOrBugReportAdminPageReadModel(
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
                record.displayTime(),
                record.nickname(),
                record.status()
        );
    }
}
