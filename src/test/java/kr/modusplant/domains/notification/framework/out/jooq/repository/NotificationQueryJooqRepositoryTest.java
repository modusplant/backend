package kr.modusplant.domains.notification.framework.out.jooq.repository;

import jakarta.transaction.Transactional;
import kr.modusplant.domains.notification.common.helper.NotificationTestDataHelper;
import kr.modusplant.domains.notification.usecase.record.NotificationReadModel;
import kr.modusplant.domains.post.common.helper.PostTestDataHelper;
import kr.modusplant.framework.jpa.generator.UlidIdGenerator;
import kr.modusplant.jooq.tables.records.CommNotificationRecord;
import kr.modusplant.jooq.tables.records.SiteMemberRecord;
import kr.modusplant.shared.enums.NotificationActionType;
import kr.modusplant.shared.enums.NotificationStatusType;
import org.hibernate.generator.EventType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.TimeZone;

import static kr.modusplant.shared.persistence.common.util.constant.CommNotificationConstant.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback
@TestPropertySource(properties = {
        "spring.jpa.properties.hibernate.jdbc.time_zone=Asia/Seoul"
})
class NotificationQueryJooqRepositoryTest {
    @Autowired
    private NotificationQueryJooqRepository notificationQueryJooqRepository;

    @Autowired
    private PostTestDataHelper postTestDataHelper;

    @Autowired
    private NotificationTestDataHelper notificationTestDataHelper;

    private static final UlidIdGenerator generator = new UlidIdGenerator();

    private SiteMemberRecord testMember1;
    private SiteMemberRecord testMember2;

    private CommNotificationRecord testNotification1;
    private CommNotificationRecord testNotification2;
    private CommNotificationRecord testNotification3;
    private CommNotificationRecord testNotification4;

    @BeforeAll
    static void setTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }

    @BeforeEach
    void setUp() {
        testMember1 = postTestDataHelper.insertTestMember("Member1");
        testMember2 = postTestDataHelper.insertTestMember("Member2");

        LocalDateTime baseTime = LocalDateTime.now();


        testNotification1 = notificationTestDataHelper.insertTestNotification(
                generator.generate(null,null,null, EventType.INSERT),
                testMember1,
                testMember2.getUuid(),
                testMember2.getNickname(),
                NotificationActionType.POST_LIKED,
                NotificationStatusType.UNREAD,
                TEST_NOTIFICATION_POST_ULID,
                null,
                TEST_NOTIFICATION_POST_PREVIEW,
                baseTime.plusDays(7).plusMinutes(1)
        );

        testNotification2 = notificationTestDataHelper.insertTestNotification(
                generator.generate(null,null,null, EventType.INSERT),
                testMember1,
                testMember2.getUuid(),
                testMember2.getNickname(),
                NotificationActionType.COMMENT_ADDED,
                NotificationStatusType.READ,
                TEST_NOTIFICATION_POST_ULID,
                TEST_NOTIFICATION_COMMENT_PATH,
                "comment added preview 2",
                baseTime.plusDays(7).plusMinutes(2)
        );

        testNotification3 = notificationTestDataHelper.insertTestNotification(
                generator.generate(null,null,null, EventType.INSERT),
                testMember1,
                testMember2.getUuid(),
                testMember2.getNickname(),
                NotificationActionType.COMMENT_REPLY_ADDED,
                NotificationStatusType.UNREAD,
                TEST_NOTIFICATION_ULID,
                "1.2.3",
                "reply added preview 3",
                baseTime.plusDays(7).plusMinutes(3)
        );

        testNotification4 = notificationTestDataHelper.insertTestNotification(
                generator.generate(null,null,null, EventType.INSERT),
                testMember2,
                testMember1.getUuid(),
                testMember1.getNickname(),
                NotificationActionType.POST_LIKED,
                NotificationStatusType.UNREAD,
                "01JXEDF9SNSMAVBY8Z3P5YXK5J",
                null,
                "other member notification",
                baseTime.plusDays(7).plusMinutes(4)
        );
    }

    @AfterEach
    void tearDown() {
        notificationTestDataHelper.deleteTestNotifications(testNotification1, testNotification2, testNotification3, testNotification4);
        postTestDataHelper.deleteTestMember(testMember1, testMember2);
    }

    @Test
    @DisplayName("recipient 기준으로 알림 목록이 최신순으로 조회된다")
    void testFindByStatusWithCursor_givenNoCursor_willReturnNotificationsOrderedByLatest() {
        // when
        int size = 2;
        List<NotificationReadModel> result = notificationQueryJooqRepository.findByStatusWithCursor(null, testMember1.getUuid(), null, size);

        // then
        assertThat(result).hasSize(size + 1);
        assertThat(result.get(0).ulid()).isEqualTo(testNotification3.getUlid());
        assertThat(result.get(1).ulid()).isEqualTo(testNotification2.getUlid());
        assertThat(result.get(2).ulid()).isEqualTo(testNotification1.getUlid());
    }

    @Test
    @DisplayName("status 필터로 unread 알림만 조회된다")
    void testFindByStatusWithCursor_givenUnreadStatus_willReturnOnlyUnreadNotifications() {
        // when
        int size = 10;
        List<NotificationReadModel> result = notificationQueryJooqRepository.findByStatusWithCursor(NotificationStatusType.UNREAD, testMember1.getUuid(), null, size);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(notification -> notification.status() == NotificationStatusType.UNREAD);
        assertThat(result).extracting(NotificationReadModel::ulid).containsExactly(testNotification3.getUlid(), testNotification1.getUlid());
    }

    @Test
    @DisplayName("cursor를 기준으로 다음 페이지를 조회한다")
    void testFindByStatusWithCursor_givenCursor_willPaginateCorrectly() {
        // when
        int size = 1;
        List<NotificationReadModel> firstPage = notificationQueryJooqRepository.findByStatusWithCursor(null, testMember1.getUuid(), null, size);

        String cursorUlid = firstPage.get(size - 1).ulid();

        List<NotificationReadModel> secondPage = notificationQueryJooqRepository.findByStatusWithCursor(null, testMember1.getUuid(), cursorUlid, size);

        // then
        assertThat(firstPage).hasSize(size + 1);
        assertThat(secondPage).isNotEmpty();
        assertThat(firstPage.getFirst().ulid()).isEqualTo(testNotification3.getUlid());
        assertThat(firstPage.getLast().ulid()).isEqualTo(secondPage.getFirst().ulid());
    }

    @Test
    @DisplayName("다른 recipient의 알림은 조회되지 않는다")
    void testFindByStatusWithCursor_givenDifferentRecipient_willReturnNothing() {
        // when
        List<NotificationReadModel> result = notificationQueryJooqRepository.findByStatusWithCursor(null, testMember2.getUuid(), null, 10);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).ulid()).isEqualTo(testNotification4.getUlid());
    }
}