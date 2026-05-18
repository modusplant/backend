package kr.modusplant.domains.member.adapter.controller;

import kr.modusplant.domains.member.adapter.helper.MemberValidationHelper;
import kr.modusplant.framework.jackson.holder.ObjectMapperHolder;
import kr.modusplant.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.framework.jpa.generator.UlidIdGenerator;
import kr.modusplant.shared.event.ProposalOrBugReportRemoveEvent;
import kr.modusplant.shared.generator.UlidGeneratorHolder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;

import static kr.modusplant.domains.member.common.util.usecase.record.ProposalOrBugReportRemoveRecordTestUtils.testProposalOrBugReportRemoveRecord;
import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.NOT_FOUND_REPORT_ID;
import static kr.modusplant.infrastructure.config.jackson.JacksonConfig.objectMapper;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class MemberAdminControllerTest {
    @SuppressWarnings("unused")
    private final ObjectMapperHolder objectMapperHolder = new ObjectMapperHolder(objectMapper());
    @SuppressWarnings("unused")
    private final UlidGeneratorHolder ulidGeneratorHolder = new UlidGeneratorHolder(new UlidIdGenerator());

    private final MemberValidationHelper memberValidationHelper = Mockito.mock(MemberValidationHelper.class);
    private final ApplicationEventPublisher applicationEventPublisher = Mockito.mock(ApplicationEventPublisher.class);

    private final MemberAdminController memberAdminController = new MemberAdminController(memberValidationHelper, applicationEventPublisher);

    private final NotFoundEntityException notFoundEntityExceptionForReport = new NotFoundEntityException(NOT_FOUND_REPORT_ID, "reportId");

    @Test
    @DisplayName("removeProposalOrBug로 건의 및 버그 제보 제거")
    void testRemoveProposalOrBug_givenValidRecord_willRemoveProposalOrBugReport() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfReportExists(any());
        willDoNothing().given(applicationEventPublisher).publishEvent(any(ProposalOrBugReportRemoveEvent.class));

        // when
        memberAdminController.removeProposalOrBug(testProposalOrBugReportRemoveRecord);

        // then
        verify(applicationEventPublisher, times(1)).publishEvent(any(ProposalOrBugReportRemoveEvent.class));
    }

    @Test
    @DisplayName("존재하지 않는 보고서로 인해 removeProposalOrBug로 건의 및 버그 제보 제거 실패")
    void testRemoveProposalOrBug_givenNotFoundReportId_willThrowException() {
        // given
        willThrow(notFoundEntityExceptionForReport).given(memberValidationHelper).validateIfReportExists(any());

        // when
        NotFoundEntityException notFoundEntityException = assertThrows(NotFoundEntityException.class,
                () -> memberAdminController.removeProposalOrBug(testProposalOrBugReportRemoveRecord));

        // then
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_REPORT_ID);
    }
}