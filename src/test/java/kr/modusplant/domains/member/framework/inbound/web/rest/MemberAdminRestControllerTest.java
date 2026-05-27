package kr.modusplant.domains.member.framework.inbound.web.rest;

import kr.modusplant.domains.member.adapter.controller.MemberAdminController;
import kr.modusplant.domains.member.domain.enums.ProposalOrBugReportStatus;
import kr.modusplant.domains.member.usecase.model.read.ProposalOrBugReportAdminPageReadModel;
import kr.modusplant.shared.framework.jackson.holder.ObjectMapperHolder;
import kr.modusplant.shared.framework.jackson.http.response.DataResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Objects;

import static kr.modusplant.domains.member.common.constant.ReportConstant.TEST_REPORT_SIZE;
import static kr.modusplant.domains.member.common.constant.ReportConstant.TEST_REPORT_ULID;
import static kr.modusplant.domains.member.common.util.usecase.model.read.ProposalOrBugReportAdminPageReadModelTestUtils.testProposalOrBugReportAdminPageCheckedReadModel;
import static kr.modusplant.domains.member.common.util.usecase.model.read.ProposalOrBugReportAdminPageReadModelTestUtils.testProposalOrBugReportAdminPageCheckedReadModelList;
import static kr.modusplant.domains.member.common.util.usecase.record.ProposalOrBugReportCheckRecordTestUtils.testProposalOrBugReportCheckRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.ProposalOrBugReportRecordGetTestUtils.testProposalOrBugReportGetRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.ProposalOrBugReportRemoveRecordTestUtils.testProposalOrBugReportRemoveRecord;
import static kr.modusplant.infrastructure.config.jackson.JacksonConfig.objectMapper;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

class MemberAdminRestControllerTest {
    @SuppressWarnings("unused")
    private final ObjectMapperHolder objectMapperHolder = new ObjectMapperHolder(objectMapper());
    private final MemberAdminController memberAdminController = Mockito.mock(MemberAdminController.class);
    private final MemberAdminRestController memberAdminRestController = new MemberAdminRestController(memberAdminController);

    @Test
    @DisplayName("getProposalOrBugReport로 응답 반환")
    void testGetProposalOrBugReport_givenValidRequest_willReturnResponse() {
        // given
        given(memberAdminController.getProposalOrBug(testProposalOrBugReportGetRecord)).willReturn(testProposalOrBugReportAdminPageCheckedReadModelList);

        // when
        ResponseEntity<DataResponse<List<ProposalOrBugReportAdminPageReadModel>>> responseEntity = memberAdminRestController.getProposalOrBugReport(ProposalOrBugReportStatus.CHECKED, TEST_REPORT_ULID, TEST_REPORT_SIZE);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(responseEntity.getBody()).toString()).isEqualTo(DataResponse.ok(testProposalOrBugReportAdminPageCheckedReadModelList).toString());
    }

    @Test
    @DisplayName("checkProposalOrBugReport로 응답 반환")
    void testCheckProposalOrBugReport_givenValidRequest_willReturnResponse() {
        // given
        given(memberAdminController.checkProposalOrBug(testProposalOrBugReportCheckRecord)).willReturn(testProposalOrBugReportAdminPageCheckedReadModel);

        // when
        ResponseEntity<DataResponse<ProposalOrBugReportAdminPageReadModel>> responseEntity = memberAdminRestController.checkProposalOrBugReport(TEST_REPORT_ULID);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(responseEntity.getBody()).toString()).isEqualTo(DataResponse.ok(testProposalOrBugReportAdminPageCheckedReadModel).toString());
    }

    @Test
    @DisplayName("deleteProposalOrBugReport로 응답 반환")
    void testDeleteProposalOrBugReport_givenValidRequest_willReturnResponse() {
        // given
        willDoNothing().given(memberAdminController).removeProposalOrBug(testProposalOrBugReportRemoveRecord);

        // when
        ResponseEntity<DataResponse<Void>> responseEntity = memberAdminRestController.removeProposalOrBugReport(TEST_REPORT_ULID);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(responseEntity.getBody()).toString()).isEqualTo(DataResponse.ok().toString());
    }
}