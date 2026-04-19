package kr.modusplant.domains.member.framework.out.jpa.repository;

import kr.modusplant.framework.jpa.entity.common.util.CommCommentAbuRepEntityTestUtils;
import kr.modusplant.framework.jpa.entity.common.util.CommPostAbuRepEntityTestUtils;
import kr.modusplant.framework.jpa.repository.CommCommentAbuRepJpaRepository;
import kr.modusplant.framework.jpa.repository.CommCommentJpaRepository;
import kr.modusplant.framework.jpa.repository.CommPostAbuRepJpaRepository;
import kr.modusplant.framework.jpa.repository.CommPostJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.member.common.util.domain.vo.TargetCommentIdTestUtils.testTargetCommentId;
import static kr.modusplant.domains.member.common.util.domain.vo.TargetPostIdTestUtils.testTargetPostId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class AbuseRepositoryJpaAdapterTest implements CommPostAbuRepEntityTestUtils, CommCommentAbuRepEntityTestUtils {
    private final CommPostJpaRepository postJpaRepository = Mockito.mock(CommPostJpaRepository.class);
    private final CommPostAbuRepJpaRepository postAbuRepJpaRepository = Mockito.mock(CommPostAbuRepJpaRepository.class);
    private final CommCommentJpaRepository commentJpaRepository = Mockito.mock(CommCommentJpaRepository.class);
    private final CommCommentAbuRepJpaRepository commentAbuRepJpaRepository = Mockito.mock(CommCommentAbuRepJpaRepository.class);
    private final AbuseRepositoryJpaAdapter abuseRepositoryJpaAdapter = new AbuseRepositoryJpaAdapter(postJpaRepository, postAbuRepJpaRepository, commentJpaRepository, commentAbuRepJpaRepository);

    @Test
    @DisplayName("신고가 존재할 때 isMemberAbusePost로 게시글 신고 여부 반환")
    void testIsMemberAbusePost_givenExistedAbuse_willReturnResponse() {
        // given
        given(postJpaRepository.findByUlid(any())).willReturn(Optional.of(createCommPostEntityBuilder().build()));
        given(postAbuRepJpaRepository.findByMemberIdAndPost(any(), any())).willReturn(Optional.of(createCommPostAbuRepEntityBuilder().build()));

        // when
        boolean isMemberAbusePost = abuseRepositoryJpaAdapter.isMemberAbusePost(testMemberId, testTargetPostId);

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
        boolean isMemberAbusePost = abuseRepositoryJpaAdapter.isMemberAbusePost(testMemberId, testTargetPostId);

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
        boolean isMemberAbusePost = abuseRepositoryJpaAdapter.isMemberAbuseComment(testMemberId, testTargetCommentId);

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
        boolean isMemberAbusePost = abuseRepositoryJpaAdapter.isMemberAbuseComment(testMemberId, testTargetCommentId);

        // then
        assertThat(isMemberAbusePost).isFalse();
    }
}