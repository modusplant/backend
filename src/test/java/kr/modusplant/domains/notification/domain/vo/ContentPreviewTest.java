package kr.modusplant.domains.notification.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.notification.common.constant.NotificationConstant.TEST_NOTIFICATION_POST_PREVIEW;
import static org.junit.jupiter.api.Assertions.*;

class ContentPreviewTest {

    @Nested
    @DisplayName("create 테스트")
    class CreateTests {
        @Test
        @DisplayName("100자 이하의 본문은 그대로 유지된다")
        void testCreate_givenShortContent_willReturnOriginal() {
            ContentPreview preview = ContentPreview.create(TEST_NOTIFICATION_POST_PREVIEW);
            assertEquals(TEST_NOTIFICATION_POST_PREVIEW, preview.getContent());
        }

        @Test
        @DisplayName("100자를 초과하는 본문은 97자에서 자르고 '...'을 붙인다")
        void testCreate_givenLongContent_willReturnTruncatedContent() {
            // given: 110자 문자열 생성
            String longContent = "a".repeat(110);

            // when
            ContentPreview preview = ContentPreview.create(longContent);

            // then
            assertEquals(100, preview.getContent().length());
            assertTrue(preview.getContent().endsWith("..."));
            assertEquals("a".repeat(97) + "...", preview.getContent());
        }

        @Test
        @DisplayName("null 입력 시 null을 반환하거나 예외를 발생시키지 않는다 (구현체 로직 기준)")
        void testCreate_givenNull_willReturnNull() {
            ContentPreview preview = ContentPreview.create(null);
            assertNull(preview.getContent());
        }
    }

    @Nested
    @DisplayName("Equals와 HashCode 테스트")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("컨텐츠 내용이 같으면 equals는 true를 반환한다")
        void useEqual_givenSameContent_willReturnTrue() {
            ContentPreview cp1 = ContentPreview.create("Hello");
            ContentPreview cp2 = ContentPreview.create("Hello");

            assertEquals(cp1, cp2);
            assertEquals(cp1.hashCode(), cp2.hashCode());
        }

        @Test
        @DisplayName("말줄임표 처리가 된 결과가 같으면 equals는 true를 반환한다")
        void useEqual_givenTruncatedContent_willReturnTrue() {
            String longText1 = "a".repeat(110);
            String longText2 = "a".repeat(120);

            ContentPreview cp1 = ContentPreview.create(longText1);
            ContentPreview cp2 = ContentPreview.create(longText2);

            assertEquals(cp1, cp2);
        }

    }

}