package kr.modusplant.domains.member.framework.in.web.rest;

import kr.modusplant.domains.member.adapter.controller.MemberAdminController;
import kr.modusplant.framework.jackson.holder.ObjectMapperHolder;
import kr.modusplant.framework.jackson.http.response.DataResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static kr.modusplant.domains.member.common.util.usecase.record.ProposalOrBugReportRemoveRecordTestUtils.testProposalOrBugReportRemoveRecord;
import static kr.modusplant.infrastructure.config.jackson.JacksonConfig.objectMapper;
import static kr.modusplant.shared.persistence.common.util.constant.ReportConstant.TEST_REPORT_ULID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.willDoNothing;

class MemberAdminRestControllerTest {
    @SuppressWarnings("unused")
    private final ObjectMapperHolder objectMapperHolder = new ObjectMapperHolder(objectMapper());
    private final MemberAdminController memberAdminController = Mockito.mock(MemberAdminController.class);
    private final MemberAdminRestController memberAdminRestController = new MemberAdminRestController(memberAdminController);

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