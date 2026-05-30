package kr.modusplant.domains.member.framework.outbound;

import kr.modusplant.domains.member.common.util.domain.aggregate.ProposalOrBugReportTestUtils;
import kr.modusplant.domains.member.common.util.framework.outbound.jpa.entity.CommentAbuseReportEntityTestUtils;
import kr.modusplant.domains.member.common.util.framework.outbound.jpa.entity.PostAbuseReportDashboardEntityTestUtils;
import kr.modusplant.domains.member.common.util.framework.outbound.jpa.entity.PostAbuseReportEntityTestUtils;
import kr.modusplant.domains.member.common.util.framework.outbound.jpa.entity.ProposalBugReportEntityTestUtils;
import kr.modusplant.domains.member.domain.enums.AbuseReportStatus;
import kr.modusplant.domains.member.domain.vo.ActivitySubjectPostId;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.PostAbuseReportDashboardEntity;
import kr.modusplant.domains.member.domain.vo.ReportPageSize;
import kr.modusplant.domains.member.framework.outbound.jooq.repository.PostAbuseReportDashboardJooqRepository;
import kr.modusplant.domains.member.framework.outbound.jooq.repository.ProposalOrBugReportDashboardJooqRepository;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.PostAbuseReportDashboardJpaRepository;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.ProposalOrBugReportJpaRepository;
import kr.modusplant.domains.member.usecase.model.read.PostAbuseReportDashboardReadModel;
import kr.modusplant.domains.post.framework.outbound.jpa.repository.PostJpaRepository;
import kr.modusplant.shared.framework.jackson.holder.ObjectMapperHolder;
import kr.modusplant.shared.framework.jpa.exception.NotFoundEntityException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static kr.modusplant.domains.member.common.constant.ReportConstant.TEST_REPORT_SIZE;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportIdTestUtils.testReportId;
import static kr.modusplant.domains.member.common.util.usecase.model.read.PostAbuseReportDashboardReadModelTestUtils.testPostAbuseReportDashboardReadModel;
import static kr.modusplant.domains.member.common.util.usecase.model.read.PostAbuseReportDashboardReadModelTestUtils.testPostAbuseReportDashboardReadModelList;
import static kr.modusplant.domains.member.common.util.usecase.model.read.ProposalOrBugReportDashboardReadModelTestUtils.testProposalOrBugReportDashboardCheckedReadModel;
import static kr.modusplant.domains.member.common.util.domain.vo.ActivitySubjectPostIdTestUtils.testActivitySubjectPostId;
import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.NOT_FOUND_POST_ABUSE_REPORT;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;
import static kr.modusplant.infrastructure.config.jackson.JacksonConfig.objectMapper;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class ReportDashboardRepositoryAdapterTest implements PostAbuseReportEntityTestUtils, CommentAbuseReportEntityTestUtils,
        ProposalOrBugReportTestUtils, ProposalBugReportEntityTestUtils, PostAbuseReportDashboardEntityTestUtils {
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
        given(proposalOrBugReportDashboardJooqRepository.getReadModelByReportId(any())).willReturn(testProposalOrBugReportDashboardCheckedReadModel);

        // when
        reportDashboardRepositoryAdapter.checkProposalOrBugReport(testReportId);

        // then
        verify(proposalOrBugReportDashboardJooqRepository, times(1)).getReadModelByReportId(any());
    }

    @Test
    @DisplayName("DISMISSED 상태의 엔티티가 존재할 때 isDismissedInPostAbuseReportDashboard로 true 반환")
    void testIsDismissedInPostAbuseReportDashboard_givenDismissedEntity_willReturnTrue() {
        // given
        PostAbuseReportDashboardEntity dismissedEntity = createPostAbuseReportDashboardDismissedEntityBuilder().build();
        given(postAbuseReportDashboardJpaRepository.findById(TEST_POST_ULID)).willReturn(Optional.of(dismissedEntity));

        // when
        boolean result = reportDashboardRepositoryAdapter.isDismissedInPostAbuseReportDashboard(ActivitySubjectPostId.create(TEST_POST_ULID));

        // then
        verify(postAbuseReportDashboardJpaRepository, times(1)).findById(TEST_POST_ULID);
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("UNCHECKED 상태의 엔티티가 존재할 때 isDismissedInPostAbuseReportDashboard로 false 반환")
    void testIsDismissedInPostAbuseReportDashboard_givenUncheckedEntity_willReturnFalse() {
        // given
        PostAbuseReportDashboardEntity uncheckedEntity = createPostAbuseReportDashboardUncheckedEntityBuilder().build();
        given(postAbuseReportDashboardJpaRepository.findById(TEST_POST_ULID)).willReturn(Optional.of(uncheckedEntity));

        // when
        boolean result = reportDashboardRepositoryAdapter.isDismissedInPostAbuseReportDashboard(ActivitySubjectPostId.create(TEST_POST_ULID));

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("엔티티가 존재하지 않을 때 isDismissedInPostAbuseReportDashboard로 false 반환")
    void testIsDismissedInPostAbuseReportDashboard_givenNotFoundEntity_willReturnFalse() {
        // given
        given(postAbuseReportDashboardJpaRepository.findById(TEST_POST_ULID)).willReturn(Optional.empty());

        // when
        boolean result = reportDashboardRepositoryAdapter.isDismissedInPostAbuseReportDashboard(ActivitySubjectPostId.create(TEST_POST_ULID));

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("엔티티가 존재할 때 dismissPostAbuseReport로 반려 수행 후 읽기 모델 반환")
    void testDismissPostAbuseReport_givenFoundEntity_willDismissAndReturnReadModel() {
        // given
        PostAbuseReportDashboardEntity uncheckedEntity = createPostAbuseReportDashboardUncheckedEntityBuilder().build();
        given(postAbuseReportDashboardJpaRepository.findById(TEST_POST_ULID)).willReturn(Optional.of(uncheckedEntity));
        given(postAbuseReportDashboardJpaRepository.save(any())).willReturn(uncheckedEntity);
        given(postAbuseReportDashboardJooqRepository.getReadModelByPostId(TEST_POST_ULID)).willReturn(testPostAbuseReportDashboardReadModel);

        // when
        PostAbuseReportDashboardReadModel readModel = reportDashboardRepositoryAdapter.dismissPostAbuseReport(ActivitySubjectPostId.create(TEST_POST_ULID));

        // then
        verify(postAbuseReportDashboardJpaRepository, times(1)).save(any());
        verify(postAbuseReportDashboardJooqRepository, times(1)).getReadModelByPostId(TEST_POST_ULID);
        assertThat(readModel).isEqualTo(testPostAbuseReportDashboardReadModel);
    }

    @Test
    @DisplayName("엔티티가 존재하지 않을 때 dismissPostAbuseReport로 예외 반환")
    void testDismissPostAbuseReport_givenNotFoundEntity_willThrowException() {
        // given
        given(postAbuseReportDashboardJpaRepository.findById(TEST_POST_ULID)).willReturn(Optional.empty());

        // when
        NotFoundEntityException notFoundEntityException = assertThrows(
                NotFoundEntityException.class,
                () -> reportDashboardRepositoryAdapter.dismissPostAbuseReport(ActivitySubjectPostId.create(TEST_POST_ULID)));

        // then
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_POST_ABUSE_REPORT);
    }

    @Test
    @DisplayName("엔티티가 존재할 때 approvePostAbuseReport로 수리 수행 후 읽기 모델 반환")
    void testApprovePostAbuseReport_givenFoundEntity_willApproveAndReturnReadModel() {
        // given
        PostAbuseReportDashboardEntity uncheckedEntity = createPostAbuseReportDashboardUncheckedEntityBuilder().build();
        given(postAbuseReportDashboardJpaRepository.findById(TEST_POST_ULID)).willReturn(Optional.of(uncheckedEntity));
        given(postAbuseReportDashboardJpaRepository.save(any())).willReturn(uncheckedEntity);
        given(postAbuseReportDashboardJooqRepository.getReadModelByPostId(TEST_POST_ULID)).willReturn(testPostAbuseReportDashboardReadModel);

        // when
        PostAbuseReportDashboardReadModel readModel = reportDashboardRepositoryAdapter.approvePostAbuseReport(testActivitySubjectPostId);

        // then
        verify(postAbuseReportDashboardJpaRepository, times(1)).save(any());
        verify(postAbuseReportDashboardJooqRepository, times(1)).getReadModelByPostId(TEST_POST_ULID);
        assertThat(readModel).isEqualTo(testPostAbuseReportDashboardReadModel);
    }

    @Test
    @DisplayName("엔티티가 존재하지 않을 때 approvePostAbuseReport로 예외 반환")
    void testApprovePostAbuseReport_givenNotFoundEntity_willThrowException() {
        // given
        given(postAbuseReportDashboardJpaRepository.findById(TEST_POST_ULID)).willReturn(Optional.empty());

        // when
        NotFoundEntityException notFoundEntityException = assertThrows(
                NotFoundEntityException.class,
                () -> reportDashboardRepositoryAdapter.approvePostAbuseReport(testActivitySubjectPostId));

        // then
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_POST_ABUSE_REPORT);
    }

    @Test
    @DisplayName("BLINDED 상태의 엔티티가 존재할 때 isApprovedInPostAbuseReportDashboard로 true 반환")
    void testIsApprovedInPostAbuseReportDashboard_givenBlindedEntity_willReturnTrue() {
        // given
        PostAbuseReportDashboardEntity blindedEntity = createPostAbuseReportDashboardBlindedEntityBuilder().build();
        given(postAbuseReportDashboardJpaRepository.findById(TEST_POST_ULID)).willReturn(Optional.of(blindedEntity));

        // when
        boolean result = reportDashboardRepositoryAdapter.isApprovedInPostAbuseReportDashboard(testActivitySubjectPostId);

        // then
        verify(postAbuseReportDashboardJpaRepository, times(1)).findById(TEST_POST_ULID);
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("UNCHECKED 상태의 엔티티가 존재할 때 isApprovedInPostAbuseReportDashboard로 false 반환")
    void testIsApprovedInPostAbuseReportDashboard_givenUncheckedEntity_willReturnFalse() {
        // given
        PostAbuseReportDashboardEntity uncheckedEntity = createPostAbuseReportDashboardUncheckedEntityBuilder().build();
        given(postAbuseReportDashboardJpaRepository.findById(TEST_POST_ULID)).willReturn(Optional.of(uncheckedEntity));

        // when
        boolean result = reportDashboardRepositoryAdapter.isApprovedInPostAbuseReportDashboard(testActivitySubjectPostId);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("엔티티가 존재하지 않을 때 isApprovedInPostAbuseReportDashboard로 false 반환")
    void testIsApprovedInPostAbuseReportDashboard_givenNotFoundEntity_willReturnFalse() {
        // given
        given(postAbuseReportDashboardJpaRepository.findById(TEST_POST_ULID)).willReturn(Optional.empty());

        // when
        boolean result = reportDashboardRepositoryAdapter.isApprovedInPostAbuseReportDashboard(testActivitySubjectPostId);

        // then
        assertThat(result).isFalse();
    }
}