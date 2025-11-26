package kr.modusplant.domains.post.domain.aggregate;

import kr.modusplant.domains.post.common.util.domain.aggregate.PostTestUtils;
import kr.modusplant.domains.post.domain.exception.*;
import kr.modusplant.domains.post.domain.vo.LikeCount;
import kr.modusplant.domains.post.domain.vo.PostContent;
import kr.modusplant.domains.post.domain.vo.PostId;
import kr.modusplant.domains.post.domain.vo.PostStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.post.common.constant.PostJsonNodeConstant.TEST_POST_CONTENT;
import static org.junit.jupiter.api.Assertions.*;

class PostTest implements PostTestUtils {
    @Nested
    @DisplayName("Post 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("모든 파라미터가 유효할 때 Post를 성공적으로 생성한다")
        void testCreate_givenValidParameter_willReturnPost() {
            // then
            assertNotNull(createPublishedPost());
            assertEquals(testPostId, createPublishedPost().getPostId());
            assertEquals(testAuthorId, createPublishedPost().getAuthorId());
            assertEquals(testAuthorId, createPublishedPost().getCreateAuthorId()); // authorId와 동일해야 함
            assertEquals(testPrimaryCategoryId, createPublishedPost().getPrimaryCategoryId());
            assertEquals(testSecondaryCategoryId, createPublishedPost().getSecondaryCategoryId());
            assertEquals(testPostContent, createPublishedPost().getPostContent());
            assertEquals(testLikeCount, createPublishedPost().getLikeCount());
            assertEquals(PostStatus.published(), createPublishedPost().getStatus());
        }

        @Test
        @DisplayName("Post의 파라미터가 null일 때 Exception을 발생시킨다.")
        void testCreate_givenNullParameter_willThrowException() {
            assertThrows(EmptyPostIdException.class, () ->
                    Post.create(null, testAuthorId, testPrimaryCategoryId,
                            testSecondaryCategoryId, testPostContent, testLikeCount, PostStatus.published()));

            assertThrows(EmptyAuthorIdException.class, () ->
                    Post.create(testPostId, null, testPrimaryCategoryId,
                            testSecondaryCategoryId, testPostContent, testLikeCount, PostStatus.published()));

            assertThrows(EmptyCategoryIdException.class, () ->
                    Post.create(testPostId, testAuthorId, null,
                            testSecondaryCategoryId, testPostContent, testLikeCount, PostStatus.published()));

            assertThrows(EmptyCategoryIdException.class, () ->
                    Post.create(testPostId, testAuthorId, testPrimaryCategoryId,
                            null, testPostContent, testLikeCount, PostStatus.published()));

            assertThrows(EmptyPostContentException.class, () ->
                    Post.create(testPostId, testAuthorId, testPrimaryCategoryId,
                            testSecondaryCategoryId, null, testLikeCount, PostStatus.published()));

            assertThrows(EmptyLikeCountException.class, () ->
                    Post.create(testPostId, testAuthorId, testPrimaryCategoryId,
                            testSecondaryCategoryId, testPostContent, null, PostStatus.published()));

            assertThrows(EmptyPostStatusException.class, () ->
                    Post.create(testPostId, testAuthorId, testPrimaryCategoryId,
                            testSecondaryCategoryId, testPostContent, testLikeCount, null));

        }
    }

    @Nested
    @DisplayName("Draft Post 생성 테스트")
    class CreateDraftTests {
        @Test
        @DisplayName("유효한 파라미터로 Draft Post를 성공적으로 생성한다")
        void testCreateDraft_givenValidParameter_willReturnPost() {
            // when
            Post post = Post.createDraft(testAuthorId, testPrimaryCategoryId, testSecondaryCategoryId, testPostContent);

            // then
            assertNotNull(post);
            assertNotNull(post.getPostId()); // PostId.generate()로 생성됨
            assertEquals(testAuthorId, post.getAuthorId());
            assertEquals(testAuthorId, post.getCreateAuthorId());
            assertEquals(testPrimaryCategoryId, post.getPrimaryCategoryId());
            assertEquals(testSecondaryCategoryId, post.getSecondaryCategoryId());
            assertEquals(testPostContent, post.getPostContent());
            assertEquals(LikeCount.zero(), post.getLikeCount());
            assertEquals(PostStatus.draft(), post.getStatus());
        }

        @Test
        @DisplayName("Post의 파라미터가 null일 때 Exception을 발생시킨다.")
        void testCreateDraft_givenNullParameter_willThrowException() {
            assertThrows(EmptyAuthorIdException.class, () ->
                    Post.createDraft( null, testPrimaryCategoryId, testSecondaryCategoryId, testPostContent));

            assertThrows(EmptyCategoryIdException.class, () ->
                    Post.createDraft( testAuthorId, null, testSecondaryCategoryId, testPostContent));

            assertThrows(EmptyCategoryIdException.class, () ->
                    Post.createDraft( testAuthorId, testPrimaryCategoryId, null, testPostContent));

            assertThrows(EmptyPostContentException.class, () ->
                    Post.createDraft( testAuthorId, testPrimaryCategoryId, testSecondaryCategoryId, null));
        }
    }

    @Nested
    @DisplayName("Published Post 생성 테스트")
    class CreatePublishedTests {
        @Test
        @DisplayName("유효한 파라미터로 Draft Post를 성공적으로 생성한다")
        void testCreatePublished_givenValidParameter_willReturnPost() {
            // when
            Post post = Post.createPublished(testAuthorId, testPrimaryCategoryId, testSecondaryCategoryId, testPostContent);

            // then
            assertNotNull(post);
            assertNotNull(post.getPostId()); // PostId.generate()로 생성됨
            assertEquals(testAuthorId, post.getAuthorId());
            assertEquals(testAuthorId, post.getCreateAuthorId());
            assertEquals(testPrimaryCategoryId, post.getPrimaryCategoryId());
            assertEquals(testSecondaryCategoryId, post.getSecondaryCategoryId());
            assertEquals(testPostContent, post.getPostContent());
            assertEquals(LikeCount.zero(), post.getLikeCount());
            assertEquals(PostStatus.published(), post.getStatus());
        }

        @Test
        @DisplayName("Post의 파라미터가 null일 때 Exception을 발생시킨다.")
        void testCreatePublished_givenNullParameter_willThrowException() {
            assertThrows(EmptyAuthorIdException.class, () ->
                    Post.createPublished( null, testPrimaryCategoryId, testSecondaryCategoryId, testPostContent));

            assertThrows(EmptyCategoryIdException.class, () ->
                    Post.createPublished( testAuthorId, null, testSecondaryCategoryId, testPostContent));

            assertThrows(EmptyCategoryIdException.class, () ->
                    Post.createPublished( testAuthorId, testPrimaryCategoryId, null, testPostContent));

            assertThrows(EmptyPostContentException.class, () ->
                    Post.createPublished( testAuthorId, testPrimaryCategoryId, testSecondaryCategoryId, null));
        }
    }

    @Nested
    @DisplayName("Post 업데이트 테스트")
    class UpdateTests {
        @Test
        @DisplayName("유효한 파라미터로 Post를 성공적으로 업데이트한다")
        void testUpdate_givenValidParameter_willReturnPost() {
            // given
            Post post = createDraftPost();
            PostContent postContent = PostContent.create("title",TEST_POST_CONTENT);

            // when
            post.update(testAuthorId2, testPrimaryCategoryId2, testSecondaryCategoryId2, postContent, PostStatus.published());

            // then
            assertEquals(testAuthorId2, post.getAuthorId());
            assertEquals(testPrimaryCategoryId2, post.getPrimaryCategoryId());
            assertEquals(testSecondaryCategoryId2, post.getSecondaryCategoryId());
            assertEquals(postContent, post.getPostContent());
            assertEquals(PostStatus.published(), post.getStatus());
            assertEquals(testAuthorId, post.getCreateAuthorId());
        }

        @Test
        @DisplayName("Post의 파라미터가 null일 때 Exception을 발생시킨다.")
        void testUpdate_givenNullParameter_willThrowException() {
            // given
            Post post = createDraftPost();
            PostContent postContent = PostContent.create("title",TEST_POST_CONTENT);

            // when & then
            assertThrows(EmptyAuthorIdException.class, () ->
                    post.update(null, testPrimaryCategoryId2, testSecondaryCategoryId2, postContent, PostStatus.published()));

            assertThrows(EmptyCategoryIdException.class, () ->
                    post.update(testAuthorId2, null, testSecondaryCategoryId2, postContent, PostStatus.published()));

            assertThrows(EmptyCategoryIdException.class, () ->
                    post.update(testAuthorId2, testPrimaryCategoryId2, null, postContent, PostStatus.published()));

            assertThrows(EmptyPostContentException.class, () ->
                    post.update(testAuthorId2, testPrimaryCategoryId2, testSecondaryCategoryId2, null, PostStatus.published()));

            assertThrows(EmptyPostStatusException.class, () ->
                    post.update(testAuthorId2, testPrimaryCategoryId2, testSecondaryCategoryId2, postContent, null));
        }

        @Test
        @DisplayName("Post의 파라미터가 null일 때 Exception을 발생시킨다.")
        void testUpdate_givenPublishedPostToDraftPost_willThrowException() {
            // given
            Post post = createPublishedPost();
            PostContent postContent = PostContent.create("title",TEST_POST_CONTENT);

            // when & then
            assertThrows(InvalidPostStatusException.class, () ->
                    post.update(testAuthorId2, testPrimaryCategoryId2, testSecondaryCategoryId2, postContent, PostStatus.draft()));
        }
    }

    @Nested
    @DisplayName("AuthorId 업데이트 테스트")
    class UpdateAuthorIdTests {

        @Test
        @DisplayName("유효한 AuthorId로 성공적으로 업데이트한다")
        void testUpdateAuthorId_givenValidParameter_willReturnPost() {
            // given
            Post post = createPublishedPost();

            // when
            post.updateAuthorId(testAuthorId2);

            // then
            assertEquals(testAuthorId2, post.getAuthorId());
            assertEquals(testAuthorId, post.getCreateAuthorId());
        }

        @Test
        @DisplayName("AuthorId가 null일 때 EmptyAuthorIdException을 발생시킨다")
        void testUpdateAuthorId_givenNullParameter_willThrowException() {
            // given
            Post post = createPublishedPost();

            // when & then
            assertThrows(EmptyAuthorIdException.class, () ->
                    post.updateAuthorId(null));
        }
    }

    @Nested
    @DisplayName("Content 업데이트 테스트")
    class UpdateContentTests {

        @Test
        @DisplayName("새로운 Content로 성공적으로 업데이트한다")
        void testUpdateContent_givenValidParameter_willReturnPost() {
            // given
            Post post = createDraftPost();
            PostContent postContent = PostContent.create("title",TEST_POST_CONTENT);

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
        @DisplayName("Draft 상태의 Post를 성공적으로 발행한다")
        void testPublish_givenNothing_willChangeStatus() {
            // given
            Post post = createDraftPost();

            // when
            post.publish();

            // then
            assertEquals(PostStatus.published(), post.getStatus());
        }

        @Test
        @DisplayName("이미 발행된 Post를 발행하려 할 때 InvalidPostStatusException을 발생시킨다")
        void testPublish_givenPublishedPost_willChangeStatus() {
            // given
            Post post = createPublishedPost();

            // when & then
            assertThrows(InvalidPostStatusException.class, post::publish);
        }
    }

    @Nested
    @DisplayName("Like 테스트")
    class LikeTests {

        @Test
        @DisplayName("발행된 Post에 Like를 성공적으로 추가한다")
        void testLike_givenPublishedPost_willSucceed() {
            // given
            Post post = createPublishedPost();
            LikeCount originalLikeCount = post.getLikeCount();

            // when
            post.like();

            // then
            assertEquals(originalLikeCount.increment(), post.getLikeCount());
        }

        @Test
        @DisplayName("Draft 상태의 Post에 Like를 추가하려 할 때 InvalidPostStatusException을 발생시킨다")
        void testLike_givenDraftPost_willThrowException() {
            // given
            Post post = createDraftPost();

            // when & then
            assertThrows(InvalidPostStatusException.class, post::like);
        }
    }

    @Nested
    @DisplayName("Unlike 테스트")
    class UnlikeTests {

        @Test
        @DisplayName("발행된 Post에서 Like를 성공적으로 제거한다")
        void testUnlike_givenPublishedPost_willSucceed() {
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
        @DisplayName("Draft 상태의 Post에서 Like를 제거하려 할 때 InvalidPostStatusException을 발생시킨다")
        void testunlike_givenDraftPost_willThrowException() {
            // given
            Post post = createDraftPost();

            // when & then
            assertThrows(InvalidPostStatusException.class, post::unlike);
        }
    }

    @Nested
    @DisplayName("Equals와 HashCode 테스트")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("같은 객체에 대한 equals 호출")
        void useEqual_givenSameObject_willReturnTrue() {
            // when & then
            assertEquals(createDraftPost(), createDraftPost());
        }

        @Test
        @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
        void useEqual_givenObjectOfDifferentClass_willReturnFalse() {
            assertNotEquals(createDraftPost(), testPostId);
        }

        @Test
        @DisplayName("다른 프로퍼티를 갖는 인스턴스에 대한 equals 호출")
        void useEqual_givenObjectContainingDifferentProperty_willReturnFalse() {
            assertNotEquals(
                    createPublishedPost(),
                    Post.create(PostId.generate(),testAuthorId, testPrimaryCategoryId, testSecondaryCategoryId, testPostContent,testLikeCount, PostStatus.published())
            );
        }

    }


}
