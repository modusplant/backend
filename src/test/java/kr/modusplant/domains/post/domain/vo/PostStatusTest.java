package kr.modusplant.domains.post.domain.vo;

import kr.modusplant.domains.post.common.util.domain.aggregate.PostTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PostStatusTest implements PostTestUtils {

    @Nested
    @DisplayName("PostStatus 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("draft()로 PostStatus 반환")
        void testDraft_givenNothing_willReturnPostStatus() {
            // when
            PostStatus postStatus = PostStatus.draft();

            // then
            assertNotNull(postStatus);
            assertTrue(postStatus.isDraft());
            assertFalse(postStatus.isPublished());
        }

        @Test
        @DisplayName("published()로 PostStatus 반환")
        void testPublished_givenNothing_willReturnPostStatus() {
            // when
            PostStatus postStatus = PostStatus.published();

            // then
            assertNotNull(postStatus);
            assertFalse(postStatus.isDraft());
            assertTrue(postStatus.isPublished());
        }
    }

    @Nested
    @DisplayName("PostStatus 상태 확인 테스트")
    class StatusCheckTests {

        @Test
        @DisplayName("draft 상태에서 거짓 반환")
        void testIsPublished_givenDraftStatus_willReturnFalse() {
            // given
            PostStatus draftStatus = PostStatus.draft();

            // when & then
            assertFalse(draftStatus.isPublished());
        }

        @Test
        @DisplayName("published 상태에서 참 반환")
        void testIsPublished_givenPublishedStatus_willReturnTrue() {
            // given
            PostStatus publishedStatus = PostStatus.published();

            // when & then
            assertTrue(publishedStatus.isPublished());
        }

        @Test
        @DisplayName("draft 상태에서 참 반환")
        void testIsDraft_givenDraftStatus_willReturnTrue() {
            // given
            PostStatus draftStatus = PostStatus.draft();

            // when & then
            assertTrue(draftStatus.isDraft());
        }

        @Test
        @DisplayName("published 상태에서 거짓 반환")
        void testIsDraft_givenPublishedStatus_willReturnFalse() {
            // given
            PostStatus publishedStatus = PostStatus.published();

            // when & then
            assertFalse(publishedStatus.isDraft());
        }
    }


    @Nested
    @DisplayName("Equals와 HashCode 테스트")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("같은 객체로 참 반환")
        void testEquals_givenSameObject_willReturnTrue() {
            // given
            PostStatus draftStatus = PostStatus.draft();
            PostStatus publishedStatus = PostStatus.published();

            // when & then
            assertEquals(draftStatus, draftStatus);
            assertEquals(publishedStatus, publishedStatus);
        }

        @Test
        @DisplayName("다른 클래스 인스턴스로 거짓 반환")
        void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
            // given
            PostStatus draftStatus = PostStatus.draft();

            // when & then
            assertNotEquals(draftStatus, testPostId);
        }

        @Test
        @DisplayName("다른 프로퍼티 인스턴스로 거짓 반환")
        void testEquals_givenObjectContainingDifferentProperty_willReturnFalse() {
            // given
            PostStatus draftStatus = PostStatus.draft();
            PostStatus publishedStatus = PostStatus.published();

            // when & then
            assertNotEquals(draftStatus, publishedStatus);
        }
    }
}