package kr.modusplant.domains.notification.framework.outbound.jpa.repository;

import kr.modusplant.domains.comment.framework.outbound.jpa.entity.CommentEntity;
import kr.modusplant.domains.comment.framework.outbound.jpa.repository.CommentJpaRepository;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.domains.notification.common.util.domain.aggregate.NotificationTestUtils;
import kr.modusplant.domains.notification.usecase.record.NotificationPreview;
import kr.modusplant.shared.framework.jpa.exception.NotFoundEntityException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class CommentInfoRepositoryJpaAdapterTest implements NotificationTestUtils {
    private final CommentJpaRepository commentJpaRepository = Mockito.mock(CommentJpaRepository.class);
    private final CommentInfoRepositoryJpaAdapter commentInfoRepositoryJpaAdapter = new CommentInfoRepositoryJpaAdapter(commentJpaRepository);

    @Nested
    @DisplayName("getAuthorIdByPostIdAndCommentPath 테스트")
    class GetAuthorIdTests {
        @Test
        @DisplayName("댓글이 존재할 때 UUID 반환")
        void testGetAuthorIdByPostIdAndCommentPath_givenExistingComment_willReturnUuid() {
            // given
            CommentEntity commentEntity = Mockito.mock(CommentEntity.class);
            MemberEntity memberEntity = Mockito.mock(MemberEntity.class);

            given(commentJpaRepository.findByPostUlidAndPath(any(), any())).willReturn(Optional.of(commentEntity));
            given(commentEntity.getAuthMember()).willReturn(memberEntity);
            given(memberEntity.getUuid()).willReturn(MEMBER_BASIC_USER_UUID);

            // when
            UUID result = commentInfoRepositoryJpaAdapter.getAuthorIdByPostIdAndCommentPath(testPostId, testCommentPath);

            // then
            assertThat(result).isEqualTo(MEMBER_BASIC_USER_UUID);
        }

        @Test
        @DisplayName("댓글이 존재하지 않을 때 예외 반환")
        void testGetAuthorIdByPostIdAndCommentPath_givenNonExistentComment_willThrowException() {
            // given
            given(commentJpaRepository.findByPostUlidAndPath(any(), any())).willReturn(Optional.empty());

            // when & then
            assertThrows(NotFoundEntityException.class, () ->
                    commentInfoRepositoryJpaAdapter.getAuthorIdByPostIdAndCommentPath(testPostId, testCommentPath));
        }
    }

    @Nested
    @DisplayName("getNotificationPreviewByPostIdAndCommentPath 테스트")
    class GetNotificationPreviewTests {
        @Test
        @DisplayName("유효한 파라미터로 읽기 모델 반환")
        void testGetNotificationPreviewByPostIdAndCommentPath_givenValidParams_willReturnReadModel() {
            // given
            CommentEntity commentEntity = Mockito.mock(CommentEntity.class);
            MemberEntity memberEntity = Mockito.mock(MemberEntity.class);
            String content = "댓글 내용입니다.";

            given(commentJpaRepository.findByPostUlidAndPath(any(), any())).willReturn(Optional.of(commentEntity));
            given(commentEntity.getAuthMember()).willReturn(memberEntity);
            given(memberEntity.getUuid()).willReturn(MEMBER_BASIC_USER_UUID);
            given(commentEntity.getContent()).willReturn(content);

            // when
            NotificationPreview preview = commentInfoRepositoryJpaAdapter.getNotificationPreviewByPostIdAndCommentPath(testPostId, testCommentPath);

            // then
            assertThat(preview.authorUuid()).isEqualTo(MEMBER_BASIC_USER_UUID);
            assertThat(preview.contentPreview()).isEqualTo(content);
        }
    }
}