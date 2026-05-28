package kr.modusplant.domains.member.framework.outbound;

import kr.modusplant.domains.member.common.util.domain.aggregate.ProposalOrBugReportTestUtils;
import kr.modusplant.domains.member.framework.outbound.jooq.repository.ProposalOrBugReportDashboardJooqRepository;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.common.util.CommentAbuseReportEntityTestUtils;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.common.util.PostAbuseReportEntityTestUtils;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.common.util.ProposalBugReportEntityTestUtils;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.PostAbuseReportDashboardJpaRepository;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.ProposalOrBugReportJpaRepository;
import kr.modusplant.domains.post.framework.outbound.jpa.repository.PostJpaRepository;
import kr.modusplant.shared.framework.jackson.holder.ObjectMapperHolder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static kr.modusplant.domains.member.common.util.domain.vo.ReportIdTestUtils.testReportId;
import static kr.modusplant.domains.member.common.util.framework.outbound.jooq.record.ProposalOrBugReportDashboardRecordTestUtils.testProposalOrBugReportDashboardCheckedRecord;
import static kr.modusplant.domains.member.common.util.usecase.model.read.ProposalOrBugReportDashboardReadModelTestUtils.testProposalOrBugReportDashboardCheckedReadModel;
import static kr.modusplant.infrastructure.config.jackson.JacksonConfig.objectMapper;
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

    private final ReportDashboardRepositoryAdapter reportDashboardRepositoryAdapter = new ReportDashboardRepositoryAdapter(
            postJpaRepository, proposalOrBugReportJpaRepository, postAbuseReportDashboardJpaRepository, proposalOrBugReportDashboardJooqRepository);

    @Test
    @DisplayName("checkProposalOrBugReport 실행 시 확인 수행")
    void testCheckProposalOrBugReport_givenValidReportId_willCheckProposalOrBugReport() {
        // given
        given(proposalOrBugReportJpaRepository.findByUlid(any())).willReturn(Optional.of(createProposalBugReportEntityBuilder().member(createMemberBasicUserEntity()).build()));
        given(proposalOrBugReportJpaRepository.save(any())).willReturn(createProposalBugReportEntityBuilder().build());
        given(proposalOrBugReportDashboardJooqRepository.getDashboardByReportId(any())).willReturn(testProposalOrBugReportDashboardCheckedRecord);
        given(proposalOrBugReportDashboardJooqRepository.getDashboardReadModel(testProposalOrBugReportDashboardCheckedRecord)).willReturn(testProposalOrBugReportDashboardCheckedReadModel);

        // when
        reportDashboardRepositoryAdapter.checkProposalOrBugReport(testReportId);

        // then
        verify(proposalOrBugReportDashboardJooqRepository, times(1)).getDashboardByReportId(any());
        verify(proposalOrBugReportDashboardJooqRepository, times(1)).getDashboardReadModel(testProposalOrBugReportDashboardCheckedRecord);
    }
}