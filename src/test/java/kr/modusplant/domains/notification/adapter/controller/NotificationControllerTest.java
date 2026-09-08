package kr.modusplant.domains.notification.adapter.controller;

import kr.modusplant.domains.comment.common.util.domain.event.CommentNotificationEventTestUtils;
import kr.modusplant.domains.member.common.util.domain.event.CommentLikeNotificationEventTestUtils;
import kr.modusplant.domains.member.common.util.domain.event.PostLikeNotificationEventTestUtils;
import kr.modusplant.domains.notification.adapter.mapper.NotificationMapperImpl;
import kr.modusplant.domains.notification.common.util.domain.aggregate.NotificationTestUtils;
import kr.modusplant.domains.notification.common.util.usecase.record.NotificationReadModelTestUtils;
import kr.modusplant.domains.notification.common.util.usecase.response.NotificationResponseTestUtils;
import kr.modusplant.domains.notification.framework.outbound.messaging.FcmSender;
import kr.modusplant.domains.notification.usecase.port.mapper.NotificationMapper;
import kr.modusplant.domains.notification.usecase.port.repository.*;
import kr.modusplant.domains.notification.usecase.record.NotificationPreview;
import kr.modusplant.domains.notification.usecase.record.NotificationReadModel;
import kr.modusplant.domains.notification.usecase.response.CursorPageResponse;
import kr.modusplant.domains.notification.usecase.response.NotificationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.UUID;

import static kr.modusplant.domains.notification.common.constant.NotificationConstant.TEST_NOTIFICATION_RECIPIENT_ID;
import static kr.modusplant.domains.notification.common.constant.NotificationConstant.TEST_NOTIFICATION_ULID;
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
        @DisplayName("상태와 커서로 CursorPageResponse 반환")
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
            assertThat(result.notifications().getFirst().ulid()).isEqualTo(TEST_POST_LIKED_READ_NOTIFICATION_UNREAD_MODEL.ulid());
            assertThat(result.nextUlid()).isNull();
            assertThat(result.hasNext()).isFalse();

            verify(notificationQueryRepository).findByStatusWithCursor(testNotificationStatusUnread.getStatus(),testRecipientId.getValue(),TEST_NOTIFICATION_ULID,size);
        }

        @Test
        @DisplayName("알림 단건 읽음 처리 활동 수행")
        void testReadNotification_givenNotificationIdAndMemberUuid_willProcessAction() {

            // when
            notificationController.readNotification(testNotificationId.getValue(), testRecipientId.getValue());

            // then
            verify(notificationRepository).markAsRead(testNotificationId,testRecipientId);
        }

        @Test
        @DisplayName("알림 전체 읽음 처리 활동 수행")
        void testReadAllNotifications_givenMemberUuid_willProcessAction() {
            // when
            notificationController.readAllNotifications(testRecipientId.getValue());

            // then
            verify(notificationRepository).markAllAsRead(testRecipientId);
        }

        @Test
        @DisplayName("읽지 않은 알림 개수 Long 반환")
        void testCountUnreadNotifications_givenMemberUuid_willReturnLong() {
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
        @DisplayName("타인 좋아요 시 알림 저장 및 FCM 전송 활동 수행")
        void testCreatePostLikeNotification_givenOtherUserLike_willProcessAction() {
            // given
            NotificationPreview preview = new NotificationPreview(TEST_NOTIFICATION_RECIPIENT_ID, "게시글 제목");

            given(postInfoRepository.getNotificationPreviewByPostId(any())).willReturn(preview);
            given(memberInfoRepository.getNicknameByUuid(testPostLikeEvent.getMemberId())).willReturn("ActorNickname");
            given(notificationRepository.saveWithLimit(any(), anyInt())).willAnswer(invocation -> invocation.getArgument(0));

            // when
            notificationController.createPostLikeNotification(testPostLikeEvent);

            // then
            verify(notificationRepository, times(1)).saveWithLimit(any(), eq(50));
            verify(fcmSender, times(1)).sendAsync(any());
        }

        @Test
        @DisplayName("본인 좋아요 시 알림 미생성 활동 수행")
        void testCreatePostLikeNotification_givenSelfLike_willProcessAction() {
            // given
            NotificationPreview preview = new NotificationPreview(testPostLikeEvent.getMemberId(), "내 게시글 제목");

            given(postInfoRepository.getNotificationPreviewByPostId(any())).willReturn(preview);

            // when
            notificationController.createPostLikeNotification(testPostLikeEvent);

            // then
            verify(notificationRepository, never()).saveWithLimit(any(), anyInt());
            verify(fcmSender, never()).sendAsync(any());
        }

        @Test
        @DisplayName("게시글 작성자 null일 때 알림 미생성 활동 수행")
        void testCreatePostLikeNotification_givenNullAuthor_willProcessAction() {
            // given
            NotificationPreview preview = new NotificationPreview(null, "게시글 제목");

            given(postInfoRepository.getNotificationPreviewByPostId(any())).willReturn(preview);

            // when
            notificationController.createPostLikeNotification(testPostLikeEvent);

            // then
            verify(notificationRepository, never()).saveWithLimit(any(), anyInt());
            verify(fcmSender, never()).sendAsync(any());
        }

        @Test
        @DisplayName("타인 댓글 좋아요 시 알림 저장 및 FCM 전송 활동 수행")
        void testCreateCommentLikeNotification_givenOtherUserLike_willProcessAction() {
            // given
            NotificationPreview preview = new NotificationPreview(TEST_NOTIFICATION_RECIPIENT_ID, "댓글 내용 프리뷰");

            given(commentInfoRepository.getNotificationPreviewByPostIdAndCommentPath(any(), any())).willReturn(preview);
            given(memberInfoRepository.getNicknameByUuid(testCommentLikeEvent.getMemberId())).willReturn("ActorNickname");
            given(notificationRepository.saveWithLimit(any(), anyInt())).willAnswer(invocation -> invocation.getArgument(0));

            // when
            notificationController.createCommentLikeNotification(testCommentLikeEvent);

            // then
            verify(notificationRepository, times(1)).saveWithLimit(any(), eq(50));
            verify(fcmSender, times(1)).sendAsync(any());
        }

        @Test
        @DisplayName("본인 댓글 좋아요 시 알림 미생성 활동 수행")
        void testCreateCommentLikeNotification_givenSelfLike_willProcessAction() {
            // given
            NotificationPreview preview = new NotificationPreview(testCommentLikeEvent.getMemberId(), "내 댓글 내용");

            given(commentInfoRepository.getNotificationPreviewByPostIdAndCommentPath(any(), any())).willReturn(preview);

            // when
            notificationController.createCommentLikeNotification(testCommentLikeEvent);

            // then
            verify(notificationRepository, never()).saveWithLimit(any(), anyInt());
            verify(fcmSender, never()).sendAsync(any());
        }

        @Test
        @DisplayName("댓글 작성자 null일 때 알림 미생성 활동 수행")
        void testCreateCommentLikeNotification_givenNullAuthor_willProcessAction() {
            // given
            NotificationPreview preview = new NotificationPreview(null, "댓글 내용");

            given(commentInfoRepository.getNotificationPreviewByPostIdAndCommentPath(any(), any())).willReturn(preview);

            // when
            notificationController.createCommentLikeNotification(testCommentLikeEvent);

            // then
            verify(notificationRepository, never()).saveWithLimit(any(), anyInt());
        }

        @Test
        @DisplayName("일반 댓글 시 게시글 작성자 알림 전송 활동 수행")
        void testCreateCommentNotification_givenNormalComment_willProcessAction() {
            // given
            given(memberInfoRepository.getNicknameByUuid(testCommentRegisterEvent.getAuthorId())).willReturn("ActorName");
            given(postInfoRepository.getAuthorIdByPostId(any())).willReturn(TEST_NOTIFICATION_RECIPIENT_ID);
            given(notificationRepository.saveWithLimit(any(), anyInt())).willAnswer(invocation -> invocation.getArgument(0));

            // when
            notificationController.createCommentNotification(testCommentRegisterEvent);

            // then
            verify(notificationRepository, times(1)).saveWithLimit(any(), eq(50));
            verify(fcmSender, times(1)).sendAsync(any());
        }

        @Test
        @DisplayName("본인 게시글에 본인 댓글 시 알림 미생성 활동 수행")
        void testCreateCommentNotification_givenSelfComment_willProcessAction() {
            // given
            UUID sameMember = testCommentRegisterEvent.getAuthorId();

            given(memberInfoRepository.getNicknameByUuid(sameMember)).willReturn("MyNickname");
            given(postInfoRepository.getAuthorIdByPostId(any())).willReturn(sameMember);

            // when
            notificationController.createCommentNotification(testCommentRegisterEvent);

            // then
            // 알림 저장 및 전송이 일어나지 않아야 함
            verify(notificationRepository, never()).saveWithLimit(any(), anyInt());
            verify(fcmSender, never()).sendAsync(any());
        }

        @Test
        @DisplayName("대댓글 시 상위 댓글·게시글 작성자 알림 전송 활동 수행")
        void testCreateCommentNotification_givenReplyToOther_willProcessAction() {
            // given
            UUID postAuthorId = UUID.randomUUID();
            UUID parentCommentAuthorId = UUID.randomUUID();

            given(memberInfoRepository.getNicknameByUuid(testCommentReplyNotificationEvent.getAuthorId())).willReturn("ActorName");
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
        @DisplayName("게시글 작성자가 상위 댓글 작성자일 때 알림 1회 전송 활동 수행")
        void testCreateCommentNotification_givenPostAuthorIsParentAuthor_willProcessAction() {
            // given
            given(memberInfoRepository.getNicknameByUuid(testCommentReplyNotificationEvent.getAuthorId())).willReturn("ActorName");
            given(postInfoRepository.getAuthorIdByPostId(any())).willReturn(TEST_NOTIFICATION_RECIPIENT_ID);
            given(commentInfoRepository.getAuthorIdByPostIdAndCommentPath(any(), any())).willReturn(TEST_NOTIFICATION_RECIPIENT_ID);
            given(notificationRepository.saveWithLimit(any(), anyInt())).willAnswer(invocation -> invocation.getArgument(0));

            // when
            notificationController.createCommentNotification(testCommentReplyNotificationEvent);

            // then
            verify(notificationRepository, times(1)).saveWithLimit(any(), eq(50));
            verify(fcmSender, times(1)).sendAsync(any());
        }

        @Test
        @DisplayName("본인 댓글에 본인 대댓글 시 상위 작성자 알림 미생성 활동 수행")
        void testCreateCommentNotification_givenSelfReply_willProcessAction() {
            // given
            UUID sameMember = testCommentReplyNotificationEvent.getAuthorId();

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