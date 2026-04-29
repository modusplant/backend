package kr.modusplant.domains.notification.framework.out.jpa.repository;

import kr.modusplant.domains.notification.common.util.domain.aggregate.NotificationTestUtils;
import kr.modusplant.domains.notification.usecase.record.NotificationPreview;
import kr.modusplant.framework.jpa.entity.CommCommentEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.framework.jpa.repository.CommCommentJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class CommentInfoRepositoryJpaAdapterTest implements NotificationTestUtils {
    private final CommCommentJpaRepository commentJpaRepository = Mockito.mock(CommCommentJpaRepository.class);
    private final CommentInfoRepositoryJpaAdapter commentInfoRepositoryJpaAdapter = new CommentInfoRepositoryJpaAdapter(commentJpaRepository);

    @Nested
    @DisplayName("getAuthorIdByPostIdAndCommentPath 테스트")
    class GetAuthorIdTests {
        @Test
        @DisplayName("댓글이 존재하면 작성자의 UUID를 반환한다")
        void testGetAuthorId_givenValidParams_willReturnAuthorUuid() {
            // given
            CommCommentEntity commentEntity = Mockito.mock(CommCommentEntity.class);
            SiteMemberEntity memberEntity = Mockito.mock(SiteMemberEntity.class);

            given(commentJpaRepository.findByPostUlidAndPath(any(), any())).willReturn(Optional.of(commentEntity));
            given(commentEntity.getAuthMember()).willReturn(memberEntity);
            given(memberEntity.getUuid()).willReturn(MEMBER_BASIC_USER_UUID);

            // when
            UUID result = commentInfoRepositoryJpaAdapter.getAuthorIdByPostIdAndCommentPath(testPostId, testCommentPath);

            // then
            assertThat(result).isEqualTo(MEMBER_BASIC_USER_UUID);
        }

        @Test
        @DisplayName("댓글이 존재하지 않으면 NotFoundEntityException이 발생한다")
        void testGetAuthorId_givenNonExistentComment_willThrowException() {
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
        @DisplayName("댓글 정보를 바탕으로 알림 프리뷰를 생성한다")
        void testGetNotificationPreview_givenValidParams_willReturnPreview() {
            // given
            CommCommentEntity commentEntity = Mockito.mock(CommCommentEntity.class);
            SiteMemberEntity memberEntity = Mockito.mock(SiteMemberEntity.class);
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