package kr.modusplant.domains.member.framework.out.jpa.repository;

import kr.modusplant.framework.jpa.entity.common.util.CommCommentAbuRepEntityTestUtils;
import kr.modusplant.framework.jpa.entity.common.util.CommPostAbuRepEntityTestUtils;
import kr.modusplant.framework.jpa.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportIdTestUtils.testReportId;
import static kr.modusplant.domains.member.common.util.domain.vo.TargetCommentIdTestUtils.testTargetCommentId;
import static kr.modusplant.domains.member.common.util.domain.vo.TargetPostIdTestUtils.testTargetPostId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class ReportRepositoryJpaAdapterTest implements CommPostAbuRepEntityTestUtils, CommCommentAbuRepEntityTestUtils {
    private final PropBugRepJpaRepository propBugRepJpaRepository = Mockito.mock(PropBugRepJpaRepository.class);
    private final CommPostJpaRepository postJpaRepository = Mockito.mock(CommPostJpaRepository.class);
    private final CommPostAbuRepJpaRepository postAbuRepJpaRepository = Mockito.mock(CommPostAbuRepJpaRepository.class);
    private final CommCommentJpaRepository commentJpaRepository = Mockito.mock(CommCommentJpaRepository.class);
    private final CommCommentAbuRepJpaRepository commentAbuRepJpaRepository = Mockito.mock(CommCommentAbuRepJpaRepository.class);
    private final ReportRepositoryJpaAdapter reportRepositoryJpaAdapter = new ReportRepositoryJpaAdapter(
            propBugRepJpaRepository, postJpaRepository, postAbuRepJpaRepository, commentJpaRepository, commentAbuRepJpaRepository);

    @Test
    @DisplayName("보고서 식별자가 존재할 때 isIdExist로 보고서 존재 여부 반환")
    void testIsIdExist_givenExistedReportId_willReturnResponse() {
        // given
        given(propBugRepJpaRepository.existsByUlid(any())).willReturn(true);

        // when
        boolean isReportIdExist = reportRepositoryJpaAdapter.isIdExist(testReportId);

        // then
        assertThat(isReportIdExist).isTrue();
    }

    @Test
    @DisplayName("보고서 식별자가 존재하지 않을 때 isIdExist로 보고서 존재 여부 반환")
    void testIsIdExist_givenNotFoundReportId_willReturnResponse() {
        // given
        given(propBugRepJpaRepository.existsByUlid(any())).willReturn(false);

        // when
        boolean isReportIdExist = reportRepositoryJpaAdapter.isIdExist(testReportId);

        // then
        assertThat(isReportIdExist).isFalse();
    }

    @Test
    @DisplayName("신고가 존재할 때 isMemberAbusePost로 게시글 신고 여부 반환")
    void testIsMemberAbusePost_givenExistedAbuse_willReturnResponse() {
        // given
        given(postJpaRepository.findByUlid(any())).willReturn(Optional.of(createCommPostEntityBuilder().build()));
        given(postAbuRepJpaRepository.findByMemberIdAndPost(any(), any())).willReturn(Optional.of(createCommPostAbuRepEntityBuilder().build()));

        // when
        boolean isMemberAbusePost = reportRepositoryJpaAdapter.isMemberAbusePost(testMemberId, testTargetPostId);

        // then
        assertThat(isMemberAbusePost).isTrue();
    }

    @Test
    @DisplayName("신고가 존재하지 않을 때 isMemberAbusePost로 게시글 신고 여부 반환")
    void testIsMemberAbusePost_givenNotFoundAbuse_willReturnResponse() {
        // given
        given(postJpaRepository.findByUlid(any())).willReturn(Optional.of(createCommPostEntityBuilder().build()));
        given(postAbuRepJpaRepository.findByMemberIdAndPost(any(), any())).willReturn(Optional.empty());

        // when
        boolean isMemberAbusePost = reportRepositoryJpaAdapter.isMemberAbusePost(testMemberId, testTargetPostId);

        // then
        assertThat(isMemberAbusePost).isFalse();
    }

    @Test
    @DisplayName("신고가 존재할 때 isMemberAbuseComment로 댓글 신고 여부 반환")
    void testIsMemberAbuseComment_givenExistedAbuse_willReturnResponse() {
        // given
        given(commentJpaRepository.findByPostUlidAndPath(any(), any()))
                .willReturn(Optional.of(createCommCommentEntityBuilder().build()));
        given(commentAbuRepJpaRepository.findByMemberIdAndComment(any(), any()))
                .willReturn(Optional.of(createCommCommentAbuRepEntityBuilder().build()));

        // when
        boolean isMemberAbusePost = reportRepositoryJpaAdapter.isMemberAbuseComment(testMemberId, testTargetCommentId);

        // then
        assertThat(isMemberAbusePost).isTrue();
    }

    @Test
    @DisplayName("신고가 존재하지 않을 때 isMemberAbuseComment로 댓글 신고 여부 반환")
    void testIsMemberAbuseComment_givenNotFoundAbuse_willReturnResponse() {
        // given
        given(commentJpaRepository.findByPostUlidAndPath(any(), any()))
                .willReturn(Optional.of(createCommCommentEntityBuilder().build()));
        given(postAbuRepJpaRepository.findByMemberIdAndPost(any(), any())).willReturn(Optional.empty());

        // when
        boolean isMemberAbusePost = reportRepositoryJpaAdapter.isMemberAbuseComment(testMemberId, testTargetCommentId);

        // then
        assertThat(isMemberAbusePost).isFalse();
    }
}