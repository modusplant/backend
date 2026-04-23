package kr.modusplant.domains.notification.framework.out.jpa.repository;

import kr.modusplant.domains.notification.common.util.domain.aggregate.NotificationTestUtils;
import kr.modusplant.domains.notification.usecase.record.NotificationPreview;
import kr.modusplant.framework.jpa.entity.CommPostEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.jpa.repository.CommPostJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

import static kr.modusplant.shared.persistence.common.util.constant.CommNotificationConstant.TEST_NOTIFICATION_POST_PREVIEW;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

class PostInfoRepositoryJpaAdapterTest implements NotificationTestUtils {
    private final CommPostJpaRepository postJpaRepository = Mockito.mock(CommPostJpaRepository.class);
    private final PostInfoRepositoryJpaAdapter postInfoRepositoryJpaAdapter = new PostInfoRepositoryJpaAdapter(postJpaRepository);

    @Nested
    @DisplayName("getAuthorIdByPostId 테스트")
    class GetAuthorIdTests {

        @Test
        @DisplayName("게시글 작성자의 UUID를 반환한다")
        void testGetAuthorId_givenValidPostId_willReturnAuthorUuid() {
            // given
            CommPostEntity postEntity = Mockito.mock(CommPostEntity.class);
            SiteMemberEntity memberEntity = Mockito.mock(SiteMemberEntity.class);

            given(postJpaRepository.findByUlid(testPostId.getValue())).willReturn(Optional.of(postEntity));
            given(postEntity.getAuthMember()).willReturn(memberEntity);
            given(memberEntity.getUuid()).willReturn(MEMBER_BASIC_USER_UUID);

            // when
            UUID result = postInfoRepositoryJpaAdapter.getAuthorIdByPostId(testPostId);

            // then
            assertThat(result).isEqualTo(MEMBER_BASIC_USER_UUID);
        }
    }

    @Nested
    @DisplayName("getNotificationPreviewByPostId 테스트")
    class GetNotificationPreviewTests {
        @Test
        @DisplayName("게시글 제목을 포함한 프리뷰를 반환한다")
        void testGetNotificationPreview_givenValidPostId_willReturnPreview() {
            // given
            CommPostEntity postEntity = Mockito.mock(CommPostEntity.class);
            SiteMemberEntity authorEntity = Mockito.mock(SiteMemberEntity.class);

            given(postJpaRepository.findByUlid(testPostId.getValue())).willReturn(Optional.of(postEntity));
            given(postEntity.getAuthMember()).willReturn(authorEntity);
            given(authorEntity.getUuid()).willReturn(MEMBER_BASIC_USER_UUID);
            given(postEntity.getTitle()).willReturn(TEST_NOTIFICATION_POST_PREVIEW);

            // when
            NotificationPreview preview = postInfoRepositoryJpaAdapter.getNotificationPreviewByPostId(testPostId);

            // then
            assertThat(preview.authorUuid()).isEqualTo(MEMBER_BASIC_USER_UUID);
            assertThat(preview.contentPreview()).isEqualTo(TEST_NOTIFICATION_POST_PREVIEW);
        }
    }
}