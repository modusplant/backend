package kr.modusplant.domains.member.framework.out.jpa.repository;

import kr.modusplant.framework.out.jpa.repository.CommCommentJpaRepository;
import kr.modusplant.framework.out.jpa.repository.CommCommentLikeJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static kr.modusplant.domains.member.common.constant.MemberStringConstant.TEST_TARGET_POST_ID_STRING;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.member.common.util.domain.vo.TargetCommentIdTestUtils.testTargetCommentId;
import static kr.modusplant.shared.persistence.common.util.constant.CommCommentConstant.TEST_COMM_COMMENT_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

class TargetCommentIdRepositoryJpaAdapterTest {
    CommCommentJpaRepository commPostJpaRepository = Mockito.mock(CommCommentJpaRepository.class);
    CommCommentLikeJpaRepository commPostLikeJpaRepository = Mockito.mock(CommCommentLikeJpaRepository.class);
    TargetCommentIdRepositoryJpaAdapter targetPostIdRepositoryJpaAdapter = new TargetCommentIdRepositoryJpaAdapter(commPostJpaRepository, commPostLikeJpaRepository);

    @Test
    @DisplayName("isIdExist로 true 반환")
    void testIsIdExist_givenIdThatExists_willReturnTrue() {
        // given & when
        given(commPostJpaRepository.existsByPostUlidAndPath(TEST_TARGET_POST_ID_STRING, TEST_COMM_COMMENT_PATH)).willReturn(true);

        // when & then
        assertThat(targetPostIdRepositoryJpaAdapter.isIdExist(testTargetCommentId)).isEqualTo(true);
    }

    @Test
    @DisplayName("isIdExist로 false 반환")
    void testIsIdExist_givenIdThatIsNotExist_willReturnFalse() {
        // given & when
        given(commPostJpaRepository.existsByPostUlidAndPath(TEST_TARGET_POST_ID_STRING, TEST_COMM_COMMENT_PATH)).willReturn(false);

        // when & then
        assertThat(targetPostIdRepositoryJpaAdapter.isIdExist(testTargetCommentId)).isEqualTo(false);
    }

    @Test
    @DisplayName("isLiked로 true 반환")
    void testIsLiked_givenIdThatExists_willReturnTrue() {
        // given & when
        given(commPostLikeJpaRepository.existsByPostIdAndPathAndMemberId(TEST_TARGET_POST_ID_STRING, TEST_COMM_COMMENT_PATH, testMemberId.getValue())).willReturn(true);

        // when & then
        assertThat(targetPostIdRepositoryJpaAdapter.isLiked(testMemberId, testTargetCommentId)).isEqualTo(true);
    }

    @Test
    @DisplayName("isLiked로 false 반환")
    void testIsLiked_givenIdThatIsNotExist_willReturnFalse() {
        // given & when
        given(commPostLikeJpaRepository.existsByPostIdAndPathAndMemberId(TEST_TARGET_POST_ID_STRING, TEST_COMM_COMMENT_PATH, testMemberId.getValue())).willReturn(false);

        // when & then
        assertThat(targetPostIdRepositoryJpaAdapter.isLiked(testMemberId, testTargetCommentId)).isEqualTo(false);
    }

    @Test
    @DisplayName("isUnliked로 true 반환")
    void testIsUnliked_givenIdThatExists_willReturnTrue() {
        // given & when
        given(commPostLikeJpaRepository.existsByPostIdAndPathAndMemberId(TEST_TARGET_POST_ID_STRING, TEST_COMM_COMMENT_PATH, testMemberId.getValue())).willReturn(false);

        // when & then
        assertThat(targetPostIdRepositoryJpaAdapter.isUnliked(testMemberId, testTargetCommentId)).isEqualTo(true);
    }

    @Test
    @DisplayName("isUnliked로 false 반환")
    void testIsUnliked_givenIdThatIsNotExist_willReturnFalse() {
        // given & when
        given(commPostLikeJpaRepository.existsByPostIdAndPathAndMemberId(TEST_TARGET_POST_ID_STRING, TEST_COMM_COMMENT_PATH, testMemberId.getValue())).willReturn(true);

        // when & then
        assertThat(targetPostIdRepositoryJpaAdapter.isUnliked(testMemberId, testTargetCommentId)).isEqualTo(false);
    }
}