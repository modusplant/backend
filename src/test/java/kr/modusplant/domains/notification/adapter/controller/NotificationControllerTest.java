package kr.modusplant.domains.notification.adapter.controller;

import kr.modusplant.domains.notification.adapter.mapper.NotificationMapperImpl;
import kr.modusplant.domains.notification.common.util.domain.aggregate.NotificationTestUtils;
import kr.modusplant.domains.notification.common.util.usecase.record.NotificationReadModelTestUtils;
import kr.modusplant.domains.notification.common.util.usecase.response.NotificationResponseTestUtils;
import kr.modusplant.domains.notification.framework.out.messaging.FcmSender;
import kr.modusplant.domains.notification.usecase.port.mapper.NotificationMapper;
import kr.modusplant.domains.notification.usecase.port.repository.*;
import kr.modusplant.domains.notification.usecase.record.NotificationPreview;
import kr.modusplant.domains.notification.usecase.record.NotificationReadModel;
import kr.modusplant.domains.notification.usecase.response.CursorPageResponse;
import kr.modusplant.domains.notification.usecase.response.NotificationResponse;
import kr.modusplant.shared.event.common.util.CommentLikeNotificationEventTestUtils;
import kr.modusplant.shared.event.common.util.CommentNotificationEventTestUtils;
import kr.modusplant.shared.event.common.util.PostLikeNotificationEventTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.UUID;

import static kr.modusplant.shared.persistence.common.util.constant.CommNotificationConstant.TEST_NOTIFICATION_RECIPIENT_ID;
import static kr.modusplant.shared.persistence.common.util.constant.CommNotificationConstant.TEST_NOTIFICATION_ULID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class NotificationControllerTest implements NotificationTestUtils, NotificationReadModelTestUtils, NotificationResponseTestUtils, PostLikeNotificationEventTestUtils, CommentNotificationEventTestUtils, CommentLikeNotificationEventTestUtils {
    private final NotificationMapper notificationMapper = new NotificationMapperImpl();
    private final NotificationRepository notificationRepository = Mockito.mock(NotificationRepository.class);
    private final NotificationQueryRepository notificationQueryRepository = Mockito.mock(NotificationQueryRepository.class);
    private final PostInfoRepository postInfoRepository = Mockito.mock(PostInfoRepository.class);
    private final CommentInfoRepository commentInfoRepository = Mockito.mock(CommentInfoRepository.class);
    private final MemberInfoRepository memberInfoRepository = Mockito.mock(MemberInfoRepository.class);
    private final FcmSender fcmSender = Mockito.mock(FcmSender.class);
    private final NotificationController notificationController = new NotificationController(
            notificationMapper, notificationRepository, notificationQueryRepository, postInfoRepository, commentInfoRepository, memberInfoRepository, fcmSender
    );

    @Nested
    @DisplayName("알림함 로직 테스트")
    class NotificationsTests {

        @Test
        @DisplayName("알림 목록 조회")
        void testGetNotifications_givenStatusAndCursor_willReturnCursorPageResponse() {
            // given
            int size = 10;
            List<NotificationReadModel> readModels = List.of(TEST_POST_LIKED_READ_NOTIFICATION_UNREAD_MODEL);
            given(notificationQueryRepository.findByStatusWithCursor(testNotificationStatusUnread.getStatus(),testRecipientId.getValue(),TEST_NOTIFICATION_ULID,size)).willReturn(readModels);

            // when
            CursorPageResponse<NotificationResponse> result = notificationController.getNotifications(testNotificationStatusUnread.getStatus(), testRecipientId.getValue(), TEST_NOTIFICATION_ULID, size);

            // then
            assertThat(result).isNotNull();
            assertThat(result.notifications()).hasSize(1);
            assertThat(result.notifications().get(0).ulid()).isEqualTo(TEST_POST_LIKED_READ_NOTIFICATION_UNREAD_MODEL.ulid());
            assertThat(result.nextUlid()).isNull();
            assertThat(result.hasNext()).isFalse();

            verify(notificationQueryRepository).findByStatusWithCursor(testNotificationStatusUnread.getStatus(),testRecipientId.getValue(),TEST_NOTIFICATION_ULID,size);
        }

        @Test
        @DisplayName("알림 단건 읽음 처리")
        void testReadNotification_givenNotificationIdAndCurrentMemberUuid_willCallMarkAsRead() {

            // when
            notificationController.readNotification(testNotificationId.getValue(), testRecipientId.getValue());

            // then
            verify(notificationRepository).markAsRead(testNotificationId,testRecipientId);
        }

        @Test
        @DisplayName("알림 전체 읽음 처리")
        void testReadAllNotifications_givenCurrentMemberUuid_willCallMarkAllAsRead() {
            // when
            notificationController.readAllNotifications(testRecipientId.getValue());

            // then
            verify(notificationRepository).markAllAsRead(testRecipientId);
        }

        @Test
        @DisplayName("읽지 않은 알림 개수 조회")
        void testCountUnreadNotifications_givenCurrentMemberUuid_willReturnCount() {
            // given
            Long expectedCount = 7L;
            given(notificationRepository.countByRecipientIdAndStatus(testRecipientId, testNotificationStatusUnread)).willReturn(expectedCount);

            // when
            Long result = notificationController.countUnreadNotifications(testRecipientId.getValue());

            // then
            assertThat(result).isEqualTo(expectedCount);
            verify(notificationRepository).countByRecipientIdAndStatus(testRecipientId, testNotificationStatusUnread);
        }
    }

    @Nested
    @DisplayName("알림 생성 테스트")
    class CreateNotificationTests {

        @Test
        @DisplayName("다른 사용자가 좋아요를 누르면 알림을 저장하고 FCM을 전송한다")
        void createPostLikeNotification_Success() {
            // given
            NotificationPreview preview = new NotificationPreview(TEST_NOTIFICATION_RECIPIENT_ID, "게시글 제목");

            given(postInfoRepository.getNotificationPreviewByPostId(any())).willReturn(preview);
            given(memberInfoRepository.getNicknameByUuid(testPostLikeNotificationEvent.getActorId())).willReturn("ActorNickname");
            given(notificationRepository.saveWithLimit(any(), anyInt())).willAnswer(invocation -> invocation.getArgument(0));

            // when
            notificationController.createPostLikeNotification(testPostLikeNotificationEvent);

            // then
            verify(notificationRepository, times(1)).saveWithLimit(any(), eq(50));
            verify(fcmSender, times(1)).sendAsync(any());
        }

        @Test
        @DisplayName("작성자 본인이 좋아요를 누르면 알림을 생성하지 않는다")
        void createPostLikeNotification_SelfAction_NoNotification() {
            // given
            NotificationPreview preview = new NotificationPreview(testPostLikeNotificationEvent.getActorId(), "내 게시글 제목");

            given(postInfoRepository.getNotificationPreviewByPostId(any())).willReturn(preview);

            // when
            notificationController.createPostLikeNotification(testPostLikeNotificationEvent);

            // then
            verify(notificationRepository, never()).saveWithLimit(any(), anyInt());
            verify(fcmSender, never()).sendAsync(any());
        }

        @Test
        @DisplayName("게시글 작성자 정보가 없으면(null) 알림을 생성하지 않는다")
        void createPostLikeNotification_NoAuthor_NoNotification() {
            // given
            NotificationPreview preview = new NotificationPreview(null, "게시글 제목");

            given(postInfoRepository.getNotificationPreviewByPostId(any())).willReturn(preview);

            // when
            notificationController.createPostLikeNotification(testPostLikeNotificationEvent);

            // then
            verify(notificationRepository, never()).saveWithLimit(any(), anyInt());
            verify(fcmSender, never()).sendAsync(any());
        }

        @Test
        @DisplayName("다른 사용자의 댓글에 좋아요를 누르면 알림을 저장하고 FCM을 전송한다")
        void createCommentLikeNotification_Success() {
            // given
            NotificationPreview preview = new NotificationPreview(TEST_NOTIFICATION_RECIPIENT_ID, "댓글 내용 프리뷰");

            given(commentInfoRepository.getNotificationPreviewByPostIdAndCommentPath(any(), any())).willReturn(preview);
            given(memberInfoRepository.getNicknameByUuid(testCommentLikeNotificationEvent.getActorId())).willReturn("ActorNickname");
            given(notificationRepository.saveWithLimit(any(), anyInt())).willAnswer(invocation -> invocation.getArgument(0));

            // when
            notificationController.createCommentLikeNotification(testCommentLikeNotificationEvent);

            // then
            verify(notificationRepository, times(1)).saveWithLimit(any(), eq(50));
            verify(fcmSender, times(1)).sendAsync(any());
        }

        @Test
        @DisplayName("본인의 댓글에 좋아요를 누르면 알림을 생성하지 않는다")
        void createCommentLikeNotification_SelfAction_NoNotification() {
            // given
            NotificationPreview preview = new NotificationPreview(testCommentLikeNotificationEvent.getActorId(), "내 댓글 내용");

            given(commentInfoRepository.getNotificationPreviewByPostIdAndCommentPath(any(), any())).willReturn(preview);

            // when
            notificationController.createCommentLikeNotification(testCommentLikeNotificationEvent);

            // then
            verify(notificationRepository, never()).saveWithLimit(any(), anyInt());
            verify(fcmSender, never()).sendAsync(any());
        }

        @Test
        @DisplayName("댓글 작성자 정보가 없으면(null) 알림을 생성하지 않는다")
        void createCommentLikeNotification_NoAuthor_NoNotification() {
            // given
            NotificationPreview preview = new NotificationPreview(null, "댓글 내용");

            given(commentInfoRepository.getNotificationPreviewByPostIdAndCommentPath(any(), any())).willReturn(preview);

            // when
            notificationController.createCommentLikeNotification(testCommentLikeNotificationEvent);

            // then
            verify(notificationRepository, never()).saveWithLimit(any(), anyInt());
        }

        @Test
        @DisplayName("일반 댓글 추가 시 게시글 작성자에게 알림이 전송된다")
        void createCommentNotification_NormalComment() {
            // given
            given(memberInfoRepository.getNicknameByUuid(testCommentNotificationEvent.getActorId())).willReturn("ActorName");
            given(postInfoRepository.getAuthorIdByPostId(any())).willReturn(TEST_NOTIFICATION_RECIPIENT_ID);
            given(notificationRepository.saveWithLimit(any(), anyInt())).willAnswer(invocation -> invocation.getArgument(0));

            // when
            notificationController.createCommentNotification(testCommentNotificationEvent);

            // then
            verify(notificationRepository, times(1)).saveWithLimit(any(), eq(50));
            verify(fcmSender, times(1)).sendAsync(any());
        }

        @Test
        @DisplayName("본인 게시글에 본인이 댓글을 달면 알림이 생성되지 않는다")
        void createCommentNotification_SelfComment_NoNotification() {
            // given
            UUID sameMember = testCommentNotificationEvent.getActorId();

            given(memberInfoRepository.getNicknameByUuid(sameMember)).willReturn("MyNickname");
            given(postInfoRepository.getAuthorIdByPostId(any())).willReturn(sameMember);

            // when
            notificationController.createCommentNotification(testCommentNotificationEvent);

            // then
            // 알림 저장 및 전송이 일어나지 않아야 함
            verify(notificationRepository, never()).saveWithLimit(any(), anyInt());
            verify(fcmSender, never()).sendAsync(any());
        }

        @Test
        @DisplayName("대댓글 추가 시 상위 댓글 작성자와 게시글 작성자 모두에게 알림이 전송된다")
        void createCommentNotification_ReplyToOther() {
            // given
            UUID postAuthorId = UUID.randomUUID();
            UUID parentCommentAuthorId = UUID.randomUUID();

            given(memberInfoRepository.getNicknameByUuid(testCommentReplyNotificationEvent.getActorId())).willReturn("ActorName");
            given(postInfoRepository.getAuthorIdByPostId(any())).willReturn(postAuthorId);
            given(commentInfoRepository.getAuthorIdByPostIdAndCommentPath(any(), any())).willReturn(parentCommentAuthorId);
            given(notificationRepository.saveWithLimit(any(), anyInt())).willAnswer(invocation -> invocation.getArgument(0));

            // when
            notificationController.createCommentNotification(testCommentReplyNotificationEvent);

            // then
            verify(notificationRepository, times(2)).saveWithLimit(any(), eq(50));
            verify(fcmSender, times(2)).sendAsync(any());
        }

        @Test
        @DisplayName("게시글 작성자가 상위 댓글 작성자일 경우 대댓글 알림은 1번만 전송된다")
        void createCommentNotification_ReplyWhenPostAuthorIsParentAuthor() {
            // given
            UUID postAuthorId = TEST_NOTIFICATION_RECIPIENT_ID;
            UUID parentCommentAuthorId = TEST_NOTIFICATION_RECIPIENT_ID; // 게시글 작성자 == 댓글 작성자

            given(memberInfoRepository.getNicknameByUuid(testCommentReplyNotificationEvent.getActorId())).willReturn("ActorName");
            given(postInfoRepository.getAuthorIdByPostId(any())).willReturn(postAuthorId);
            given(commentInfoRepository.getAuthorIdByPostIdAndCommentPath(any(), any())).willReturn(parentCommentAuthorId);
            given(notificationRepository.saveWithLimit(any(), anyInt())).willAnswer(invocation -> invocation.getArgument(0));

            // when
            notificationController.createCommentNotification(testCommentReplyNotificationEvent);

            // then
            verify(notificationRepository, times(1)).saveWithLimit(any(), eq(50));
            verify(fcmSender, times(1)).sendAsync(any());
        }

        @Test
        @DisplayName("본인 댓글에 본인이 대댓글을 달면 상위 댓글 작성자 알림이 생성되지 않는다")
        void createCommentNotification_SelfReply_NoNotification() {
            // given
            UUID sameMember = testCommentReplyNotificationEvent.getActorId();

            given(memberInfoRepository.getNicknameByUuid(sameMember)).willReturn("MyNickname");
            given(commentInfoRepository.getAuthorIdByPostIdAndCommentPath(any(), any())).willReturn(sameMember);
            given(postInfoRepository.getAuthorIdByPostId(any())).willReturn(UUID.randomUUID());
            given(notificationRepository.saveWithLimit(any(), anyInt())).willAnswer(invocation -> invocation.getArgument(0));

            // when
            notificationController.createCommentNotification(testCommentReplyNotificationEvent);

            // then
            verify(notificationRepository, times(1)).saveWithLimit(any(), anyInt());
            verify(fcmSender, times(1)).sendAsync(any());
        }
    }


}