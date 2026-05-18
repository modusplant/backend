package kr.modusplant.domains.member.framework.out.jpa.repository;

import kr.modusplant.domains.comment.framework.out.persistence.jpa.repository.CommentJpaRepository;
import kr.modusplant.domains.member.framework.out.jpa.entity.common.util.CommentAbuseReportEntityTestUtils;
import kr.modusplant.domains.member.framework.out.jpa.entity.common.util.PostAbuseReportEntityTestUtils;
import kr.modusplant.domains.post.framework.out.jpa.repository.PostJpaRepository;
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

class ReportRepositoryJpaAdapterTest implements PostAbuseReportEntityTestUtils, CommentAbuseReportEntityTestUtils {
    private final ProposalBugReportJpaRepository proposalBugReportJpaRepository = Mockito.mock(ProposalBugReportJpaRepository.class);
    private final PostJpaRepository postJpaRepository = Mockito.mock(PostJpaRepository.class);
    private final PostAbuseReportJpaRepository postAbuRepJpaRepository = Mockito.mock(PostAbuseReportJpaRepository.class);
    private final CommentJpaRepository commentJpaRepository = Mockito.mock(CommentJpaRepository.class);
    private final CommentAbuseReportJpaRepository commentAbuseReportJpaRepository = Mockito.mock(CommentAbuseReportJpaRepository.class);
    private final ReportRepositoryJpaAdapter reportRepositoryJpaAdapter = new ReportRepositoryJpaAdapter(
            proposalBugReportJpaRepository, postJpaRepository, postAbuRepJpaRepository, commentJpaRepository, commentAbuseReportJpaRepository);

    @Test
    @DisplayName("보고서 식별자가 존재할 때 isIdExist로 보고서 존재 여부 반환")
    void testIsIdExist_givenExistedReportId_willReturnResponse() {
        // given
        given(proposalBugReportJpaRepository.existsByUlid(any())).willReturn(true);

        // when
        boolean isReportIdExist = reportRepositoryJpaAdapter.isIdExist(testReportId);

        // then
        assertThat(isReportIdExist).isTrue();
    }

    @Test
    @DisplayName("보고서 식별자가 존재하지 않을 때 isIdExist로 보고서 존재 여부 반환")
    void testIsIdExist_givenNotFoundReportId_willReturnResponse() {
        // given
        given(proposalBugReportJpaRepository.existsByUlid(any())).willReturn(false);

        // when
        boolean isReportIdExist = reportRepositoryJpaAdapter.isIdExist(testReportId);

        // then
        assertThat(isReportIdExist).isFalse();
    }

    @Test
    @DisplayName("신고가 존재할 때 isMemberAbusePost로 게시글 신고 여부 반환")
    void testIsMemberAbusePost_givenExistedAbuse_willReturnResponse() {
        // given
        given(postJpaRepository.findByUlid(any())).willReturn(Optional.of(createPostEntityBuilder().build()));
        given(postAbuRepJpaRepository.findByMemberIdAndPost(any(), any())).willReturn(Optional.of(createPostAbuseReportEntityBuilder().build()));

        // when
        boolean isMemberAbusePost = reportRepositoryJpaAdapter.isMemberAbusePost(testMemberId, testTargetPostId);

        // then
        assertThat(isMemberAbusePost).isTrue();
    }

    @Test
    @DisplayName("신고가 존재하지 않을 때 isMemberAbusePost로 게시글 신고 여부 반환")
    void testIsMemberAbusePost_givenNotFoundAbuse_willReturnResponse() {
        // given
        given(postJpaRepository.findByUlid(any())).willReturn(Optional.of(createPostEntityBuilder().build()));
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
                .willReturn(Optional.of(createCommentEntityBuilder().build()));
        given(commentAbuseReportJpaRepository.findByMemberIdAndComment(any(), any()))
                .willReturn(Optional.of(createCommentAbuseReportEntityBuilder().build()));

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
                .willReturn(Optional.of(createCommentEntityBuilder().build()));
        given(postAbuRepJpaRepository.findByMemberIdAndPost(any(), any())).willReturn(Optional.empty());

        // when
        boolean isMemberAbusePost = reportRepositoryJpaAdapter.isMemberAbuseComment(testMemberId, testTargetCommentId);

        // then
        assertThat(isMemberAbusePost).isFalse();
    }
}