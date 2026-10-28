package kr.modusplant.domains.member.framework.outbound.jpa.adapter;

import kr.modusplant.domains.comment.framework.outbound.jpa.entity.CommentEntity;
import kr.modusplant.domains.comment.framework.outbound.jpa.repository.CommentJpaRepository;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.CommentLikeEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.CommentLikeJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static kr.modusplant.domains.comment.common.constant.CommentConstant.TEST_COMMENT_PATH;
import static kr.modusplant.domains.member.common.util.domain.vo.ActivitySubjectCommentIdTestUtils.testActivitySubjectCommentId;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class ActivitySubjectCommentRepositoryJpaAdapterTest {
    CommentJpaRepository commentJpaRepository = Mockito.mock(CommentJpaRepository.class);
    CommentLikeJpaRepository commentLikeJpaRepository = Mockito.mock(CommentLikeJpaRepository.class);
    ActivitySubjectCommentRepositoryJpaAdapter activitySubjectCommentRepositoryJpaAdapter = new ActivitySubjectCommentRepositoryJpaAdapter(commentJpaRepository, commentLikeJpaRepository);

    @Test
    @DisplayName("isIdExist로 true 반환")
    void testIsIdExist_givenIdThatExists_willReturnTrue() {
        // given & when
        given(commentJpaRepository.existsByPostUlidAndPath(TEST_POST_ULID, TEST_COMMENT_PATH)).willReturn(true);

        // when & then
        assertThat(activitySubjectCommentRepositoryJpaAdapter.isIdExist(testActivitySubjectCommentId)).isEqualTo(true);
    }

    @Test
    @DisplayName("isIdExist로 false 반환")
    void testIsIdExist_givenIdThatIsNotExist_willReturnFalse() {
        // given & when
        given(commentJpaRepository.existsByPostUlidAndPath(TEST_POST_ULID, TEST_COMMENT_PATH)).willReturn(false);

        // when & then
        assertThat(activitySubjectCommentRepositoryJpaAdapter.isIdExist(testActivitySubjectCommentId)).isEqualTo(false);
    }

    @Test
    @DisplayName("isLiked로 true 반환")
    void testIsLiked_givenIdThatExists_willReturnTrue() {
        // given & when
        given(commentLikeJpaRepository.existsByPostIdAndPathAndMemberId(TEST_POST_ULID, TEST_COMMENT_PATH, testMemberId.getValue())).willReturn(true);

        // when & then
        assertThat(activitySubjectCommentRepositoryJpaAdapter.isLiked(testMemberId, testActivitySubjectCommentId)).isEqualTo(true);
    }

    @Test
    @DisplayName("isLiked로 false 반환")
    void testIsLiked_givenIdThatIsNotExist_willReturnFalse() {
        // given & when
        given(commentLikeJpaRepository.existsByPostIdAndPathAndMemberId(TEST_POST_ULID, TEST_COMMENT_PATH, testMemberId.getValue())).willReturn(false);

        // when & then
        assertThat(activitySubjectCommentRepositoryJpaAdapter.isLiked(testMemberId, testActivitySubjectCommentId)).isEqualTo(false);
    }

    @Test
    @DisplayName("isUnliked로 true 반환")
    void testIsUnliked_givenIdThatExists_willReturnTrue() {
        // given & when
        given(commentLikeJpaRepository.existsByPostIdAndPathAndMemberId(TEST_POST_ULID, TEST_COMMENT_PATH, testMemberId.getValue())).willReturn(false);

        // when & then
        assertThat(activitySubjectCommentRepositoryJpaAdapter.isUnliked(testMemberId, testActivitySubjectCommentId)).isEqualTo(true);
    }

    @Test
    @DisplayName("isUnliked로 false 반환")
    void testIsUnliked_givenIdThatIsNotExist_willReturnFalse() {
        // given & when
        given(commentLikeJpaRepository.existsByPostIdAndPathAndMemberId(TEST_POST_ULID, TEST_COMMENT_PATH, testMemberId.getValue())).willReturn(true);

        // when & then
        assertThat(activitySubjectCommentRepositoryJpaAdapter.isUnliked(testMemberId, testActivitySubjectCommentId)).isEqualTo(false);
    }

    @Test
    @DisplayName("isDeleted로 true 반환")
    void testIsDeleted_givenDeletedComment_willReturnTrue() {
        // given
        CommentEntity mockComment = mock(CommentEntity.class);
        given(mockComment.getIsDeleted()).willReturn(true);
        given(commentJpaRepository.findByPostUlidAndPath(TEST_POST_ULID, TEST_COMMENT_PATH)).willReturn(Optional.of(mockComment));

        // when & then
        assertThat(activitySubjectCommentRepositoryJpaAdapter.isDeleted(testActivitySubjectCommentId)).isEqualTo(true);
    }

    @Test
    @DisplayName("isDeleted로 false 반환")
    void testIsDeleted_givenNotDeletedComment_willReturnFalse() {
        // given
        CommentEntity mockComment = mock(CommentEntity.class);
        given(mockComment.getIsDeleted()).willReturn(false);
        given(commentJpaRepository.findByPostUlidAndPath(TEST_POST_ULID, TEST_COMMENT_PATH)).willReturn(Optional.of(mockComment));

        // when & then
        assertThat(activitySubjectCommentRepositoryJpaAdapter.isDeleted(testActivitySubjectCommentId)).isEqualTo(false);
    }

    @Test
    @DisplayName("존재하지 않는 댓글로 인해 isDeleted 실행 실패")
    void testIsDeleted_givenNotFoundComment_willThrowException() {
        // given
        given(commentJpaRepository.findByPostUlidAndPath(TEST_POST_ULID, TEST_COMMENT_PATH)).willReturn(Optional.empty());

        // when & then
        assertThrows(NoSuchElementException.class, () -> activitySubjectCommentRepositoryJpaAdapter.isDeleted(testActivitySubjectCommentId));
    }

    @Test
    @DisplayName("isWrittenByMember로 true 반환")
    void testIsWrittenByMember_givenCommentWrittenByMember_willReturnTrue() {
        // given
        CommentEntity mockComment = mock(CommentEntity.class);
        MemberEntity mockAuthMember = mock(MemberEntity.class);
        given(mockAuthMember.getUuid()).willReturn(testMemberId.getValue());
        given(mockComment.getAuthMember()).willReturn(mockAuthMember);
        given(commentJpaRepository.findByPostUlidAndPath(TEST_POST_ULID, TEST_COMMENT_PATH)).willReturn(Optional.of(mockComment));

        // when & then
        assertThat(activitySubjectCommentRepositoryJpaAdapter.isWrittenByMember(testMemberId, testActivitySubjectCommentId)).isEqualTo(true);
    }

    @Test
    @DisplayName("isWrittenByMember로 false 반환")
    void testIsWrittenByMember_givenCommentWrittenByOtherMember_willReturnFalse() {
        // given
        CommentEntity mockComment = mock(CommentEntity.class);
        MemberEntity mockAuthMember = mock(MemberEntity.class);
        given(mockAuthMember.getUuid()).willReturn(UUID.randomUUID());
        given(mockComment.getAuthMember()).willReturn(mockAuthMember);
        given(commentJpaRepository.findByPostUlidAndPath(TEST_POST_ULID, TEST_COMMENT_PATH)).willReturn(Optional.of(mockComment));

        // when & then
        assertThat(activitySubjectCommentRepositoryJpaAdapter.isWrittenByMember(testMemberId, testActivitySubjectCommentId)).isEqualTo(false);
    }

    @Test
    @DisplayName("존재하지 않는 댓글로 인해 isWrittenByMember 실행 실패")
    void testIsWrittenByMember_givenNotFoundComment_willThrowException() {
        // given
        given(commentJpaRepository.findByPostUlidAndPath(TEST_POST_ULID, TEST_COMMENT_PATH)).willReturn(Optional.empty());

        // when & then
        assertThrows(NoSuchElementException.class, () -> activitySubjectCommentRepositoryJpaAdapter.isWrittenByMember(testMemberId, testActivitySubjectCommentId));
    }

    @Test
    @DisplayName("존재하는 댓글에 대해 choose 실행 시 채택 처리")
    void testChoose_givenExistedComment_willMarkAsChosen() {
        // given
        CommentEntity mockComment = mock(CommentEntity.class);
        given(commentJpaRepository.findByPostUlidAndPath(TEST_POST_ULID, TEST_COMMENT_PATH)).willReturn(Optional.of(mockComment));

        // when
        activitySubjectCommentRepositoryJpaAdapter.choose(testActivitySubjectCommentId);

        // then
        verify(mockComment, times(1)).markAsChosen();
    }

    @Test
    @DisplayName("이미 채택된 같은 댓글에 대해 choose 재실행 시 활동 수행")
    void testChoose_givenAlreadyChosenComment_willProcessAction() {
        // given
        CommentEntity chosenComment = CommentEntity.builder().isChosen(true).build();
        given(commentJpaRepository.findByPostUlidAndPath(TEST_POST_ULID, TEST_COMMENT_PATH)).willReturn(Optional.of(chosenComment));

        // when
        activitySubjectCommentRepositoryJpaAdapter.choose(testActivitySubjectCommentId);

        // then
        assertThat(chosenComment.getIsChosen()).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 댓글로 인해 choose 실행 실패")
    void testChoose_givenNotFoundComment_willThrowException() {
        // given
        given(commentJpaRepository.findByPostUlidAndPath(TEST_POST_ULID, TEST_COMMENT_PATH)).willReturn(Optional.empty());

        // when & then
        assertThrows(NoSuchElementException.class, () -> activitySubjectCommentRepositoryJpaAdapter.choose(testActivitySubjectCommentId));
    }

    @Test
    @DisplayName("존재하는 댓글에 대해 like 실행 시 좋아요 저장 및 카운트 증가")
    void testLike_givenExistedComment_willIncreaseLikeCount() {
        // given
        CommentEntity mockComment = mock(CommentEntity.class);
        given(commentJpaRepository.findByPostUlidAndPath(any(), any())).willReturn(Optional.of(mockComment));

        // when
        activitySubjectCommentRepositoryJpaAdapter.like(testMemberId, testActivitySubjectCommentId);

        // then
        verify(commentLikeJpaRepository, times(1)).save(any(CommentLikeEntity.class));
        verify(mockComment, times(1)).increaseLikeCount();
    }

    @Test
    @DisplayName("존재하지 않는 댓글로 인해 like 실행 실패")
    void testLike_givenNotFoundComment_willThrowException() {
        // given
        given(commentJpaRepository.findByPostUlidAndPath(any(), any())).willReturn(Optional.empty());

        // when
        assertThrows(NoSuchElementException.class, () -> activitySubjectCommentRepositoryJpaAdapter.like(testMemberId, testActivitySubjectCommentId));

        // then
        verify(commentLikeJpaRepository, times(1)).save(any(CommentLikeEntity.class));
    }

    @Test
    @DisplayName("존재하는 댓글에 대해 unlike 실행 시 좋아요 삭제 및 카운트 감소")
    void testUnlike_givenExistedComment_willDecreaseLikeCount() {
        // given
        CommentEntity mockComment = mock(CommentEntity.class);
        given(commentJpaRepository.findByPostUlidAndPath(any(), any())).willReturn(Optional.of(mockComment));

        // when
        activitySubjectCommentRepositoryJpaAdapter.unlike(testMemberId, testActivitySubjectCommentId);

        // then
        verify(commentLikeJpaRepository, times(1)).delete(any(CommentLikeEntity.class));
        verify(mockComment, times(1)).decreaseLikeCount();
    }

    @Test
    @DisplayName("존재하지 않는 댓글로 인해 handleCommentUnlikeEvent 실행 실패")
    void testHandleCommentUnlikeEvent_givenNotFoundComment_willThrowException() {
        // given
        given(commentJpaRepository.findByPostUlidAndPath(any(), any())).willReturn(Optional.empty());

        // when
        assertThrows(NoSuchElementException.class, () -> activitySubjectCommentRepositoryJpaAdapter.unlike(testMemberId, testActivitySubjectCommentId));

        // then
        verify(commentLikeJpaRepository, times(1)).delete(any(CommentLikeEntity.class));
    }
}