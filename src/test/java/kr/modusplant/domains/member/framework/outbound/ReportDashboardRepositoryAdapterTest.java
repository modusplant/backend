package kr.modusplant.domains.member.framework.outbound;

import kr.modusplant.domains.member.common.util.domain.aggregate.ProposalOrBugReportTestUtils;
import kr.modusplant.domains.member.common.util.framework.outbound.jpa.entity.CommentAbuseReportEntityTestUtils;
import kr.modusplant.domains.member.common.util.framework.outbound.jpa.entity.PostAbuseReportEntityTestUtils;
import kr.modusplant.domains.member.common.util.framework.outbound.jpa.entity.ProposalBugReportEntityTestUtils;
import kr.modusplant.domains.member.domain.enums.AbuseReportStatus;
import kr.modusplant.domains.member.domain.vo.ReportPageSize;
import kr.modusplant.domains.member.framework.outbound.jooq.repository.PostAbuseReportDashboardJooqRepository;
import kr.modusplant.domains.member.framework.outbound.jooq.repository.ProposalOrBugReportDashboardJooqRepository;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.PostAbuseReportDashboardJpaRepository;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.ProposalOrBugReportJpaRepository;
import kr.modusplant.domains.member.usecase.model.read.PostAbuseReportDashboardReadModel;
import kr.modusplant.domains.post.framework.outbound.jpa.repository.PostJpaRepository;
import kr.modusplant.shared.framework.jackson.holder.ObjectMapperHolder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static kr.modusplant.domains.member.common.constant.ReportConstant.TEST_REPORT_SIZE;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportIdTestUtils.testReportId;
import static kr.modusplant.domains.member.common.util.framework.outbound.jooq.record.ProposalOrBugReportDashboardRecordTestUtils.testProposalOrBugReportDashboardCheckedRecord;
import static kr.modusplant.domains.member.common.util.usecase.model.read.PostAbuseReportDashboardReadModelTestUtils.testPostAbuseReportDashboardReadModelList;
import static kr.modusplant.domains.member.common.util.usecase.model.read.ProposalOrBugReportDashboardReadModelTestUtils.testProposalOrBugReportDashboardCheckedReadModel;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;
import static kr.modusplant.infrastructure.config.jackson.JacksonConfig.objectMapper;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class ReportDashboardRepositoryAdapterTest implements PostAbuseReportEntityTestUtils, CommentAbuseReportEntityTestUtils,
        ProposalOrBugReportTestUtils, ProposalBugReportEntityTestUtils {
    @SuppressWarnings("unused")
    private final ObjectMapperHolder objectMapperHolder = new ObjectMapperHolder(objectMapper());

    private final PostJpaRepository postJpaRepository = Mockito.mock(PostJpaRepository.class);
    private final ProposalOrBugReportJpaRepository proposalOrBugReportJpaRepository = Mockito.mock(ProposalOrBugReportJpaRepository.class);
    private final PostAbuseReportDashboardJpaRepository postAbuseReportDashboardJpaRepository = Mockito.mock(PostAbuseReportDashboardJpaRepository.class);

    private final ProposalOrBugReportDashboardJooqRepository proposalOrBugReportDashboardJooqRepository = Mockito.mock(ProposalOrBugReportDashboardJooqRepository.class);
    private final PostAbuseReportDashboardJooqRepository postAbuseReportDashboardJooqRepository = Mockito.mock(PostAbuseReportDashboardJooqRepository.class);

    private final ReportDashboardRepositoryAdapter reportDashboardRepositoryAdapter = new ReportDashboardRepositoryAdapter(
            postJpaRepository, proposalOrBugReportJpaRepository, postAbuseReportDashboardJpaRepository, proposalOrBugReportDashboardJooqRepository, postAbuseReportDashboardJooqRepository);

    @Test
    @DisplayName("유효한 파라미터로 getPostAbuseReports 호출 시 readModel 목록 반환")
    void testGetPostAbuseReports_givenValidParams_willReturnReadModelList() {
        // given
        given(postAbuseReportDashboardJooqRepository.getReadModelsByPageSizeAndStatusAndPostUlid(
                any(), any(), any())).willReturn(testPostAbuseReportDashboardReadModelList);

        // when
        List<PostAbuseReportDashboardReadModel> readModels = reportDashboardRepositoryAdapter.getPostAbuseReports(
                ReportPageSize.create(TEST_REPORT_SIZE), AbuseReportStatus.UNCHECKED, TEST_POST_ULID);

        // then
        verify(postAbuseReportDashboardJooqRepository, times(1)).getReadModelsByPageSizeAndStatusAndPostUlid(any(), any(), any());
        assertThat(readModels).isEqualTo(testPostAbuseReportDashboardReadModelList);
    }

    @Test
    @DisplayName("checkProposalOrBugReport 실행 시 확인 수행")
    void testCheckProposalOrBugReport_givenValidReportId_willCheckProposalOrBugReport() {
        // given
        given(proposalOrBugReportJpaRepository.findByUlid(any())).willReturn(Optional.of(createProposalBugReportEntityBuilder().member(createMemberBasicUserEntity()).build()));
        given(proposalOrBugReportJpaRepository.save(any())).willReturn(createProposalBugReportEntityBuilder().build());
        given(proposalOrBugReportDashboardJooqRepository.getRecordByReportId(any())).willReturn(testProposalOrBugReportDashboardCheckedRecord);
        given(proposalOrBugReportDashboardJooqRepository.getReadModelByRecord(testProposalOrBugReportDashboardCheckedRecord)).willReturn(testProposalOrBugReportDashboardCheckedReadModel);

        // when
        reportDashboardRepositoryAdapter.checkProposalOrBugReport(testReportId);

        // then
        verify(proposalOrBugReportDashboardJooqRepository, times(1)).getRecordByReportId(any());
        verify(proposalOrBugReportDashboardJooqRepository, times(1)).getReadModelByRecord(testProposalOrBugReportDashboardCheckedRecord);
    }
}