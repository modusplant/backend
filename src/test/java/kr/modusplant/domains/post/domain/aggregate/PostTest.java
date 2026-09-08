package kr.modusplant.domains.post.domain.aggregate;

import kr.modusplant.domains.post.common.util.domain.aggregate.PostTestUtils;
import kr.modusplant.domains.post.domain.exception.EmptyValueException;
import kr.modusplant.domains.post.domain.exception.InvalidValueException;
import kr.modusplant.domains.post.domain.vo.LikeCount;
import kr.modusplant.domains.post.domain.vo.PostContent;
import kr.modusplant.domains.post.domain.vo.PostStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.post.common.constant.PostJsonNodeConstant.TEST_POST_CONTENT;
import static kr.modusplant.domains.post.common.constant.PostJsonNodeConstant.TEST_POST_CONTENT_THUMBNAIL_KEY;
import static org.junit.jupiter.api.Assertions.*;

class PostTest implements PostTestUtils {
    @Nested
    @DisplayName("Post 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("유효한 파라미터로 Post 반환")
        void testCreate_givenValidParameter_willReturnPost() {
            // then
            assertNotNull(createPublishedPost());
            assertEquals(testPostId, createPublishedPost().getPostId());
            assertEquals(testAuthorId, createPublishedPost().getAuthorId());
            assertEquals(testPrimaryCategoryId, createPublishedPost().getPrimaryCategoryId());
            assertEquals(testSecondaryCategoryId, createPublishedPost().getSecondaryCategoryId());
            assertEquals(testPostContent, createPublishedPost().getPostContent());
            assertEquals(testLikeCount, createPublishedPost().getLikeCount());
            assertEquals(PostStatus.published(), createPublishedPost().getStatus());
        }

        @Test
        @DisplayName("파라미터가 null일 때 예외 반환")
        void testCreate_givenNullParameter_willThrowException() {
            assertThrows(EmptyValueException.class, () ->
                    Post.create(null, testAuthorId, testPrimaryCategoryId,
                            testSecondaryCategoryId, testPostContent, testLikeCount, PostStatus.published()));

            assertThrows(EmptyValueException.class, () ->
                    Post.create(testPostId, null, testPrimaryCategoryId,
                            testSecondaryCategoryId, testPostContent, testLikeCount, PostStatus.published()));

            assertThrows(EmptyValueException.class, () ->
                    Post.create(testPostId, testAuthorId, null,
                            testSecondaryCategoryId, testPostContent, testLikeCount, PostStatus.published()));

            assertThrows(EmptyValueException.class, () ->
                    Post.create(testPostId, testAuthorId, testPrimaryCategoryId,
                            testSecondaryCategoryId, null, testLikeCount, PostStatus.published()));

            assertThrows(EmptyValueException.class, () ->
                    Post.create(testPostId, testAuthorId, testPrimaryCategoryId,
                            testSecondaryCategoryId, testPostContent, null, PostStatus.published()));

            assertThrows(EmptyValueException.class, () ->
                    Post.create(testPostId, testAuthorId, testPrimaryCategoryId,
                            testSecondaryCategoryId, testPostContent, testLikeCount, null));

        }
    }

    @Nested
    @DisplayName("Draft Post 생성 테스트")
    class CreateDraftTests {
        @Test
        @DisplayName("유효한 파라미터로 Draft Post 반환")
        void testCreateDraft_givenValidParameter_willReturnPost() {
            // when
            Post post = Post.createDraft(testAuthorId, testPrimaryCategoryId, testSecondaryCategoryId, testPostContent);

            // then
            assertNotNull(post);
            assertEquals(testAuthorId, post.getAuthorId());
            assertEquals(testPrimaryCategoryId, post.getPrimaryCategoryId());
            assertEquals(testSecondaryCategoryId, post.getSecondaryCategoryId());
            assertEquals(testPostContent, post.getPostContent());
            assertEquals(LikeCount.zero(), post.getLikeCount());
            assertEquals(PostStatus.draft(), post.getStatus());
        }

        @Test
        @DisplayName("파라미터가 null일 때 예외 반환")
        void testCreateDraft_givenNullParameter_willThrowException() {
            assertThrows(EmptyValueException.class, () ->
                    Post.createDraft( null, testPrimaryCategoryId, testSecondaryCategoryId, testPostContent));
            assertThrows(EmptyValueException.class, () ->
                    Post.createDraft(testAuthorId, null, testSecondaryCategoryId, testPostContent));
            assertThrows(EmptyValueException.class, () ->
                    Post.createDraft( testAuthorId, testPrimaryCategoryId, testSecondaryCategoryId, null));
        }
    }

    @Nested
    @DisplayName("Published Post 생성 테스트")
    class CreatePublishedTests {
        @Test
        @DisplayName("유효한 파라미터로 Published Post 반환")
        void testCreatePublished_givenValidParameter_willReturnPost() {
            // when
            Post post = Post.createPublished(testAuthorId, testPrimaryCategoryId, testSecondaryCategoryId, testPostContent);

            // then
            assertNotNull(post);
            assertEquals(testAuthorId, post.getAuthorId());
            assertEquals(testPrimaryCategoryId, post.getPrimaryCategoryId());
            assertEquals(testSecondaryCategoryId, post.getSecondaryCategoryId());
            assertEquals(testPostContent, post.getPostContent());
            assertEquals(LikeCount.zero(), post.getLikeCount());
            assertEquals(PostStatus.published(), post.getStatus());
        }

        @Test
        @DisplayName("파라미터가 null일 때 예외 반환")
        void testCreatePublished_givenNullParameter_willThrowException() {
            assertThrows(EmptyValueException.class, () ->
                    Post.createPublished( null, testPrimaryCategoryId, testSecondaryCategoryId, testPostContent));

            assertThrows(EmptyValueException.class, () ->
                    Post.createPublished( testAuthorId, null, testSecondaryCategoryId, testPostContent));

            assertThrows(EmptyValueException.class, () ->
                    Post.createPublished( testAuthorId, testPrimaryCategoryId, null, testPostContent));

            assertThrows(EmptyValueException.class, () ->
                    Post.createPublished( testAuthorId, testPrimaryCategoryId, testSecondaryCategoryId, null));
        }
    }

    @Nested
    @DisplayName("Post 업데이트 테스트")
    class UpdateTests {
        @Test
        @DisplayName("유효한 파라미터로 수정 활동 수행")
        void testUpdate_givenValidParameter_willProcessAction() {
            // given
            Post post = createDraftPost();
            PostContent postContent = PostContent.create("title",TEST_POST_CONTENT,TEST_POST_CONTENT_THUMBNAIL_KEY);

            // when
            post.update(testAuthorId2, testPrimaryCategoryId2, testSecondaryCategoryId2, postContent, PostStatus.published());

            // then
            assertEquals(testAuthorId2, post.getAuthorId());
            assertEquals(testPrimaryCategoryId2, post.getPrimaryCategoryId());
            assertEquals(testSecondaryCategoryId2, post.getSecondaryCategoryId());
            assertEquals(postContent, post.getPostContent());
            assertEquals(PostStatus.published(), post.getStatus());
        }

        @Test
        @DisplayName("파라미터가 null일 때 예외 반환")
        void testUpdate_givenNullParameter_willThrowException() {
            // given
            Post post = createDraftPost();
            PostContent postContent = PostContent.create("title",TEST_POST_CONTENT, TEST_POST_CONTENT_THUMBNAIL_KEY);

            // when & then
            assertThrows(EmptyValueException.class, () ->
                    post.update(null, testPrimaryCategoryId2, testSecondaryCategoryId2, postContent, PostStatus.published()));

            assertThrows(EmptyValueException.class, () ->
                    post.update(testAuthorId2, null, testSecondaryCategoryId2, postContent, PostStatus.published()));

            assertThrows(EmptyValueException.class, () ->
                    post.update(testAuthorId2, testPrimaryCategoryId2, null, postContent, PostStatus.published()));

            assertThrows(EmptyValueException.class, () ->
                    post.update(testAuthorId2, testPrimaryCategoryId2, testSecondaryCategoryId2, null, PostStatus.published()));

            assertThrows(EmptyValueException.class, () ->
                    post.update(testAuthorId2, testPrimaryCategoryId2, testSecondaryCategoryId2, postContent, null));
        }

        @Test
        @DisplayName("발행글을 임시저장으로 수정 시 예외 반환")
        void testUpdate_givenPublishedPostToDraftPost_willThrowException() {
            // given
            Post post = createPublishedPost();
            PostContent postContent = PostContent.create("title",TEST_POST_CONTENT, TEST_POST_CONTENT_THUMBNAIL_KEY);

            // when & then
            assertThrows(InvalidValueException.class, () ->
                    post.updateDraft(testAuthorId2, testPrimaryCategoryId2, testSecondaryCategoryId2, postContent));
        }
    }

    @Nested
    @DisplayName("Post 임시저장 업데이트 테스트")
    class UpdateDraftTests {
        @Test
        @DisplayName("유효한 파라미터로 수정 활동 수행")
        void testUpdateDraft_givenValidParameter_willProcessAction() {
            // given
            Post post = createDraftPost();
            PostContent postContent = testPostContent;

            // when
            post.updateDraft(testAuthorId2, testPrimaryCategoryId2, null, postContent);

            // then
            assertEquals(testAuthorId2, post.getAuthorId());
            assertEquals(testPrimaryCategoryId2, post.getPrimaryCategoryId());
            assertNull(post.getSecondaryCategoryId());
            assertEquals(postContent, post.getPostContent());
            assertEquals(PostStatus.draft(), post.getStatus());
        }

        @Test
        @DisplayName("파라미터가 null일 때 예외 반환")
        void testUpdateDraft_givenNullParameter_willThrowException() {
            // given
            Post post = createDraftPost();
            PostContent postContent = testPostContent;

            // when & then
            assertThrows(EmptyValueException.class, () ->
                    post.updateDraft(null, testPrimaryCategoryId2, testSecondaryCategoryId2, postContent));

            assertThrows(EmptyValueException.class, () ->
                    post.updateDraft(testAuthorId2, null, testSecondaryCategoryId2, postContent));

            assertThrows(EmptyValueException.class, () ->
                    post.updateDraft(testAuthorId2, testPrimaryCategoryId2, testSecondaryCategoryId2, null));
        }

        @Test
        @DisplayName("발행된 게시글일 때 예외 반환")
        void testUpdateDraft_givenInvalidStatus_willThrowException() {
            // given
            Post post = createPublishedPost();
            PostContent postContent = testPostContent;

            // when & then
            assertThrows(InvalidValueException.class, () ->
                    post.updateDraft(testAuthorId2, testPrimaryCategoryId2, testSecondaryCategoryId2, postContent));
        }
    }


    @Nested
    @DisplayName("AuthorId 업데이트 테스트")
    class UpdateAuthorIdTests {

        @Test
        @DisplayName("유효한 AuthorId로 수정 활동 수행")
        void testUpdateAuthorId_givenValidParameter_willProcessAction() {
            // given
            Post post = createPublishedPost();

            // when
            post.updateAuthorId(testAuthorId2);

            // then
            assertEquals(testAuthorId2, post.getAuthorId());
        }

        @Test
        @DisplayName("AuthorId가 null일 때 예외 반환")
        void testUpdateAuthorId_givenNullParameter_willThrowException() {
            // given
            Post post = createPublishedPost();

            // when & then
            assertThrows(EmptyValueException.class, () ->
                    post.updateAuthorId(null));
        }
    }

    @Nested
    @DisplayName("Content 업데이트 테스트")
    class UpdateContentTests {

        @Test
        @DisplayName("새 Content로 수정 활동 수행")
        void testUpdateContent_givenValidParameter_willProcessAction() {
            // given
            Post post = createDraftPost();
            PostContent postContent = PostContent.create("title",TEST_POST_CONTENT, TEST_POST_CONTENT_THUMBNAIL_KEY);

            // when
            post.updateContent(postContent);

            // then
            assertEquals(postContent, post.getPostContent());
        }
    }

    @Nested
    @DisplayName("Post 발행 테스트")
    class PublishTests {

        @Test
        @DisplayName("Draft 게시글 발행 활동 수행")
        void testPublish_givenDraftPost_willProcessAction() {
            // given
            Post post = createDraftPost();

            // when
            post.publish();

            // then
            assertEquals(PostStatus.published(), post.getStatus());
        }

        @Test
        @DisplayName("유효하지 않은 Draft일 때 예외 반환")
        void testPublish_givenInvalidPost_willThrowException() {
            // given
            Post post = createDraftPostWithEmptyValue();

            // when & then
            assertThrows(EmptyValueException.class, post::publish);
        }

        @Test
        @DisplayName("이미 발행된 게시글일 때 예외 반환")
        void testPublish_givenPublishedPost_willThrowException() {
            // given
            Post post = createPublishedPost();

            // when & then
            assertThrows(InvalidValueException.class, post::publish);
        }
    }

    @Nested
    @DisplayName("Like 테스트")
    class LikeTests {

        @Test
        @DisplayName("발행 게시글 좋아요 활동 수행")
        void testLike_givenPublishedPost_willProcessAction() {
            // given
            Post post = createPublishedPost();
            LikeCount originalLikeCount = post.getLikeCount();

            // when
            post.like();

            // then
            assertEquals(originalLikeCount.increment(), post.getLikeCount());
        }

        @Test
        @DisplayName("Draft 게시글 좋아요 시 예외 반환")
        void testLike_givenDraftPost_willThrowException() {
            // given
            Post post = createDraftPost();

            // when & then
            assertThrows(InvalidValueException.class, post::like);
        }
    }

    @Nested
    @DisplayName("Unlike 테스트")
    class UnlikeTests {

        @Test
        @DisplayName("발행 게시글 좋아요 취소 활동 수행")
        void testUnlike_givenPublishedPost_willProcessAction() {
            // given
            Post post = createPublishedPost();
            post.like(); // 먼저 like를 추가
            LikeCount likedCount = post.getLikeCount();

            // when
            post.unlike();

            // then
            assertEquals(likedCount.decrement(), post.getLikeCount());
        }

        @Test
        @DisplayName("Draft 게시글 좋아요 취소 시 예외 반환")
        void testUnlike_givenDraftPost_willThrowException() {
            // given
            Post post = createDraftPost();

            // when & then
            assertThrows(InvalidValueException.class, post::unlike);
        }
    }

    @Nested
    @DisplayName("Equals와 HashCode 테스트")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("같은 객체로 참 반환")
        void testEquals_givenSameObject_willReturnTrue() {
            // when & then
            assertEquals(createDraftPost(), createDraftPost());
        }

        @Test
        @DisplayName("다른 클래스 인스턴스로 거짓 반환")
        void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
            assertNotEquals(createDraftPost(), testPostId);
        }

        @Test
        @DisplayName("다른 프로퍼티 인스턴스로 거짓 반환")
        void testEquals_givenObjectContainingDifferentProperty_willReturnFalse() {
            assertNotEquals(
                    createPublishedPost(),
                    Post.create(testPostId2,testAuthorId, testPrimaryCategoryId, testSecondaryCategoryId, testPostContent,testLikeCount, PostStatus.published())
            );
        }

    }


}
