package kr.modusplant.domains.post.framework.out.jooq.repository;

import kr.modusplant.domains.post.common.helper.PostTestDataHelper;
import kr.modusplant.domains.post.domain.exception.EmptyValueException;
import kr.modusplant.domains.post.domain.vo.PostId;
import kr.modusplant.domains.post.usecase.enums.SearchOption;
import kr.modusplant.domains.post.usecase.record.PostDetailDataReadModel;
import kr.modusplant.domains.post.usecase.record.PostDetailReadModel;
import kr.modusplant.domains.post.usecase.record.PostSummaryReadModel;
import kr.modusplant.domains.post.usecase.record.PostSummaryWithSearchInfoReadModel;
import kr.modusplant.jooq.tables.records.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

import static kr.modusplant.domains.post.common.constant.PostDoubleConstant.TEST_POST_MAX_WORD_SIMILARITY_0_6;
import static kr.modusplant.domains.post.common.constant.PostIntegerConstant.TEST_POST_IMPORTANCE_COMMENT_CONTENT;
import static kr.modusplant.domains.post.common.constant.PostJsonNodeConstant.*;
import static kr.modusplant.domains.post.usecase.enums.SearchOption.TITLE_CONTENT;
import static kr.modusplant.domains.post.usecase.enums.SearchOption.TITLE_CONTENT_COMMENT;
import static kr.modusplant.shared.persistence.common.util.constant.CommPostConstant.TEST_COMM_POST_PUBLISHED_AT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings("FieldCanBeLocal")
@SpringBootTest
@Transactional
@Rollback
@TestPropertySource(properties = {
        "spring.jpa.properties.hibernate.jdbc.time_zone=Asia/Seoul"
})
class PostQueryJooqRepositoryIntegrationTest {

    @Autowired
    private PostQueryJooqRepository postQueryJooqRepository;

    @Autowired
    private PostTestDataHelper testDataHelper;

    private SiteMemberRecord testMember1, testMember2;
    private SiteMemberProfRecord testMemberProfile1, testMemberProfile2;
    private CommPriCateRecord testPrimaryCategory1, testPrimaryCategory2;
    private CommSecoCateRecord testSecondaryCategory1, testSecondaryCategory2, testSecondaryCategory3;
    private CommPostRecord testPost1,testPost2,testPost3,testPost4,testPost5;

    @BeforeAll
    static void setTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }

    @BeforeEach
    void setUp() {
        LocalDateTime baseTime = LocalDateTime.now();
        testMember1 = testDataHelper.insertTestMember("TestMember1");
        testMember2 = testDataHelper.insertTestMember("TestMember2");
        testMemberProfile1 = testDataHelper.insertTestMemberProfile(testMember1);
        testMemberProfile2 = testDataHelper.insertTestMemberProfile(testMember2);
        testPrimaryCategory1 = testDataHelper.insertTestPrimaryCategory("testPrimaryCategory1",100);
        testPrimaryCategory2 = testDataHelper.insertTestPrimaryCategory("testPrimaryCategory2",101);
        testSecondaryCategory1 = testDataHelper.insertTestSecondaryCategory(testPrimaryCategory1,"testSecondaryCategory1",100);
        testSecondaryCategory2 = testDataHelper.insertTestSecondaryCategory(testPrimaryCategory1,"testSecondaryCategory2",101);
        testSecondaryCategory3 = testDataHelper.insertTestSecondaryCategory(testPrimaryCategory2,"testSecondaryCategory3",102);
        testPost1 = testDataHelper.insertTestPublishedPost(testPrimaryCategory1,testSecondaryCategory1,testMember1,"title1",TEST_POST_CONTENT, TEST_POST_CONTENT_THUMBNAIL_KEY,baseTime.plusDays(31));
        testPost2 = testDataHelper.insertTestPublishedPost(testPrimaryCategory1,testSecondaryCategory2,testMember1,"title2",TEST_POST_CONTENT_TEXT_AND_IMAGE, TEST_POST_CONTENT_TEXT_AND_IMAGE_THUMBNAIL_KEY,baseTime.plusDays(32));
        testPost3 = testDataHelper.insertTestDraftPost(testPrimaryCategory1,testSecondaryCategory1,testMember1,"title3",TEST_POST_CONTENT, TEST_POST_CONTENT_THUMBNAIL_KEY,baseTime.plusDays(31));
        testPost4 = testDataHelper.insertTestPublishedPost(testPrimaryCategory2,testSecondaryCategory3,testMember1,"title4",TEST_POST_CONTENT, TEST_POST_CONTENT_THUMBNAIL_KEY,baseTime.plusDays(33));
        testPost5 = testDataHelper.insertTestPublishedPost(testPrimaryCategory1,testSecondaryCategory1,testMember2,"title5",TEST_POST_CONTENT, TEST_POST_CONTENT_THUMBNAIL_KEY,baseTime.plusDays(34));
        testDataHelper.insertTestComment(testPost1,"1",testMember2,"content1",false);
        testDataHelper.insertTestComment(testPost1,"1.1",testMember1,"content2",true);
        testDataHelper.insertTestComment(testPost1,"1.2",testMember2,"content3",false);
        testDataHelper.insertTestComment(testPost2,"1",testMember2,"content1",false);
        testDataHelper.insertTestPostLike(testPost1,testMember2, baseTime.plusDays(35));
        testDataHelper.insertTestPostLike(testPost4,testMember2, baseTime.plusDays(36));
        testDataHelper.insertTestPostLike(testPost5,testMember1, baseTime.plusDays(37));
        testDataHelper.insertTestPostBookmark(testPost1,testMember2, baseTime.plusDays(35));
        testDataHelper.insertTestPostBookmark(testPost2,testMember2, baseTime.plusDays(36));
        testDataHelper.insertTestPostBookmark(testPost5,testMember1, baseTime.plusDays(37));
    }

    @AfterEach
    void tearDown() {
        testDataHelper.deleteTestPostWithRelations(testPost1, testPost2, testPost3, testPost4, testPost5);
        testDataHelper.deleteTestCategory(testPrimaryCategory1, testPrimaryCategory2);
        testDataHelper.deleteTestMember(testMember1, testMember2);
    }

    @Test
    @DisplayName("카테고리 필터 없이 전체 게시글 목록 조회")
    void testFindByCategoryWithCursor_givenNoFilter_willReturnAllPosts() {
        // when
        int size = 2;
        List<PostSummaryReadModel> firstPage = postQueryJooqRepository.findByCategoryWithCursor(null, null,testMember2.getUuid(),null,size);
        List<PostSummaryReadModel> secondPage = postQueryJooqRepository.findByCategoryWithCursor(null, null,testMember2.getUuid(),firstPage.get(size-1).ulid(),size);
        List<PostSummaryReadModel> pageWithAnonymousUser = postQueryJooqRepository.findByCategoryWithCursor(null,null,null,null,size);

        // then
        assertThat(firstPage).hasSize(size+1);
        assertThat(firstPage.getLast()).isEqualTo(secondPage.getFirst());
        assertThat(firstPage.get(0).ulid())
                .withFailMessage("ULID: " + firstPage.get(0).ulid() + ", Title: "+firstPage.get(0).title() +" PublishedAt: " + firstPage.get(0).publishedAt())
                .isEqualTo(testPost5.getUlid());
        assertThat(firstPage.get(1).ulid()).isEqualTo(testPost4.getUlid());
        assertThat(secondPage.get(0).ulid()).isEqualTo(testPost2.getUlid());
        assertThat(secondPage.get(1).ulid()).isEqualTo(testPost1.getUlid());

        PostSummaryReadModel firstPostResult = firstPage.stream()
                .filter(post -> post.ulid().equals(testPost4.getUlid()))
                .findFirst()
                .orElseThrow();
        PostSummaryReadModel secondPostResult = secondPage.stream()
                .filter(post -> post.ulid().equals(testPost1.getUlid()))
                .findFirst()
                .orElseThrow();
        PostSummaryReadModel pageWithAnonymousUserResult = pageWithAnonymousUser.stream().findFirst().orElseThrow();

        assertThat(firstPostResult.commentCount()).isEqualTo(0);
        assertThat(firstPostResult.isLiked()).isTrue();
        assertThat(firstPostResult.isBookmarked()).isFalse();
        assertThat(secondPostResult.commentCount()).isEqualTo(2);
        assertThat(secondPostResult.isLiked()).isTrue();
        assertThat(secondPostResult.isBookmarked()).isTrue();
        assertThat(pageWithAnonymousUserResult.isLiked()).isFalse();
        assertThat(pageWithAnonymousUserResult.isBookmarked()).isFalse();
    }

    @Test
    @DisplayName("1차 카테고리 및 2차 카테고리로 게시글 목록 조회")
    void testFindByCategoryWithCursor_givenCategories_willReturnFilteredPosts() {
        // when
        int size = 2;
        List<PostSummaryReadModel> firstPageByPrimaryCategory = postQueryJooqRepository.findByCategoryWithCursor(
                testPrimaryCategory1.getId(), null,testMember2.getUuid(),null,size
        );
        List<PostSummaryReadModel> secondPageByPrimaryCategory = postQueryJooqRepository.findByCategoryWithCursor(
                testPrimaryCategory1.getId(), null,testMember2.getUuid(),firstPageByPrimaryCategory.get(size-1).ulid(),size
        );

        List<PostSummaryReadModel> firstPageByCategories = postQueryJooqRepository.findByCategoryWithCursor(
                testPrimaryCategory1.getId(),List.of(testSecondaryCategory1.getId()),testMember2.getUuid(),null,size
        );
        List<PostSummaryReadModel> secondPageByCategories = postQueryJooqRepository.findByCategoryWithCursor(
                testPrimaryCategory1.getId(),List.of(testSecondaryCategory1.getId()),testMember2.getUuid(),firstPageByCategories.get(size-1).ulid(),size
        );

        // then
        assertThat(firstPageByPrimaryCategory).hasSize(size+1);
        assertThat(firstPageByPrimaryCategory.getLast()).isEqualTo(secondPageByPrimaryCategory.getFirst());
        assertThat(firstPageByPrimaryCategory.get(0).ulid()).isEqualTo(testPost5.getUlid());
        assertThat(firstPageByPrimaryCategory.get(1).ulid()).isEqualTo(testPost2.getUlid());
        assertThat(secondPageByPrimaryCategory.getFirst().ulid()).isEqualTo(testPost1.getUlid());

        assertThat(firstPageByCategories).hasSize(size);
        assertThat(secondPageByCategories).isEmpty();
        assertThat(firstPageByCategories.get(0).ulid()).isEqualTo(testPost5.getUlid());
        assertThat(firstPageByCategories.get(1).ulid()).isEqualTo(testPost1.getUlid());

        // when & then
        assertThatThrownBy(() ->
                postQueryJooqRepository.findByCategoryWithCursor(
                        null, List.of(testSecondaryCategory1.getId()), testMember2.getUuid(), null, size
                )
        ).isInstanceOf(EmptyValueException.class);
    }

    @Test
    @DisplayName("키워드 없이 게시글 목록 검색 - 최신순")
    void testSearchByKeywordWithLatest_givenNoKeyword_willReturnEmptyList() {
        // given & when
        int size = 2;
        List<PostSummaryWithSearchInfoReadModel> firstPageWithNullKeyword = postQueryJooqRepository.searchByKeywordWithLatest(
                TITLE_CONTENT, null, null, null, 
                testMember2.getUuid(), null, null, size);

        List<PostSummaryWithSearchInfoReadModel> firstPageWithEmptyKeyword = postQueryJooqRepository.searchByKeywordWithLatest(
                TITLE_CONTENT, "", null, null,
                testMember2.getUuid(), null, null, size);


        // then
        assertThat(firstPageWithNullKeyword).isEmpty();
        assertThat(firstPageWithEmptyKeyword).isEmpty();
    }

    @Test
    @DisplayName("키워드 없이 게시글 목록 검색 - 정확도순")
    void testSearchByKeywordWithRelevance_givenNoKeyword_willReturnEmptyList() {
        // when
        int size = 2;
        List<PostSummaryWithSearchInfoReadModel> firstPageWithNullKeyword = postQueryJooqRepository.searchByKeywordWithRelevance(
                TITLE_CONTENT, null, null, null,
                testMember2.getUuid(), null, 
                TEST_POST_IMPORTANCE_COMMENT_CONTENT, TEST_POST_MAX_WORD_SIMILARITY_0_6, 
                TEST_COMM_POST_PUBLISHED_AT, size);

        List<PostSummaryWithSearchInfoReadModel> firstPageWithEmptyKeyword = postQueryJooqRepository.searchByKeywordWithRelevance(
                TITLE_CONTENT, "", null, null,
                testMember2.getUuid(), null,
                TEST_POST_IMPORTANCE_COMMENT_CONTENT, TEST_POST_MAX_WORD_SIMILARITY_0_6,
                TEST_COMM_POST_PUBLISHED_AT, size);

        // then
        assertThat(firstPageWithNullKeyword).isEmpty();
        assertThat(firstPageWithEmptyKeyword).isEmpty();
    }

    @Test
    @DisplayName("키워드로 게시글 목록 검색 - 최신순")
    void testSearchByKeywordWithLatest_givenKeyword_willReturnFilteredPosts() {
        // when
        int size = 2;
        String keyword = "Hello";

        List<PostSummaryWithSearchInfoReadModel> firstPageByKeyword =
                postQueryJooqRepository.searchByKeywordWithLatest(
                        TITLE_CONTENT, keyword, null, null, testMember2.getUuid(), 
                        null, null, size);
        List<PostSummaryWithSearchInfoReadModel> secondPageByKeyword =
                postQueryJooqRepository.searchByKeywordWithLatest(
                        TITLE_CONTENT, keyword, null, null, testMember2.getUuid(),
                        firstPageByKeyword.get(size - 1).ulid(), firstPageByKeyword.get(size - 1).publishedAt(), size);

        List<PostSummaryWithSearchInfoReadModel> pageByBackslash = postQueryJooqRepository.searchByKeywordWithLatest(
                TITLE_CONTENT, "\\".repeat(10), null, null, testMember2.getUuid(),
                null, null, size);
        List<PostSummaryWithSearchInfoReadModel> pageByPercent = postQueryJooqRepository.searchByKeywordWithLatest(
                TITLE_CONTENT, "%".repeat(10), null, null, testMember2.getUuid(),
                null, null, size);
        List<PostSummaryWithSearchInfoReadModel> pageByUnderscore = postQueryJooqRepository.searchByKeywordWithLatest(
                TITLE_CONTENT, "_".repeat(10), null, null, testMember2.getUuid(),
                null, null, size);

        // then
        // 결과 존재 여부 확인
        assertThat(firstPageByKeyword).hasSize(size + 1);
        assertThat(firstPageByKeyword.getLast()).isEqualTo(secondPageByKeyword.getFirst());
        assertThat(firstPageByKeyword.getFirst().ulid()).isEqualTo(testPost5.getUlid());
        assertThat(firstPageByKeyword.get(1).ulid()).isEqualTo(testPost4.getUlid());
        assertThat(secondPageByKeyword.getFirst().ulid()).isEqualTo(testPost1.getUlid());

        // 최신순 정렬 여부 확인
        assertThat(firstPageByKeyword).extracting("ulid").isSortedAccordingTo(Collections.reverseOrder());
        assertThat(secondPageByKeyword).extracting("ulid").isSortedAccordingTo(Collections.reverseOrder());

        assertThat(firstPageByKeyword).extracting("publishedAt").isSortedAccordingTo(Collections.reverseOrder());
        assertThat(secondPageByKeyword).extracting("publishedAt").isSortedAccordingTo(Collections.reverseOrder());
        assertThat(firstPageByKeyword.getLast().publishedAt()).isAfterOrEqualTo(secondPageByKeyword.getLast().publishedAt());

        // 이스케이핑 처리되어야 하는 키워드에 대한 정상 작동 여부 확인
        assertThat(pageByBackslash).isEmpty();
        assertThat(pageByPercent).isEmpty();
        assertThat(pageByUnderscore).isEmpty();
    }

    @Test
    @DisplayName("키워드로 제목에서 게시글 검색 - 최신순 (SearchOption.TITLE)")
    void testSearchByKeywordWithLatest_givenTitleOption_willSearchTitleOnly() {
        // given & when
        int size = 10;
        List<PostSummaryWithSearchInfoReadModel> result = postQueryJooqRepository.searchByKeywordWithLatest(
                SearchOption.TITLE, "title1", null, null, testMember2.getUuid(),
                null, TEST_COMM_POST_PUBLISHED_AT, size);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().ulid()).isEqualTo(testPost1.getUlid());
    }

    @Test
    @DisplayName("키워드로 제목, 컨텐츠, 댓글 컨텐츠에서 게시글 검색 - 최신순 (SearchOption.TITLE_CONTENT_COMMENT)")
    void testSearchByKeywordWithLatest_givenCommentOption_willSearchCommentContent() {
        // given & when
        // testPost1에 "content3" 댓글이 있음 (is_deleted = false)
        // testPost1에 "content2" 댓글이 있음 (is_deleted = true) → 검색 제외
        int size = 10;
        List<PostSummaryWithSearchInfoReadModel> resultByContent3 = postQueryJooqRepository.searchByKeywordWithLatest(
                TITLE_CONTENT_COMMENT, "content3", null, null,
                testMember2.getUuid(), null, TEST_COMM_POST_PUBLISHED_AT, size);

        List<PostSummaryWithSearchInfoReadModel> resultByContent2 = postQueryJooqRepository.searchByKeywordWithLatest(
                TITLE_CONTENT_COMMENT, "content2", null, null,
                testMember2.getUuid(), null, TEST_COMM_POST_PUBLISHED_AT, size);

        // then
        // content3은 is_deleted = false → testPost1에서 검색됨
        assertThat(resultByContent3).hasSize(1);
        assertThat(resultByContent3.getFirst().ulid()).isEqualTo(testPost1.getUlid());

        // content2는 is_deleted = true → 검색 안됨
        assertThat(resultByContent2).isEmpty();
    }

    @Test
    @DisplayName("키워드로 제목, 컨텐츠에서 게시글 검색 - 최신순 (키워드와 카테고리 필터 조합)")
    void testSearchByKeywordWithLatest_givenCategoryAndKeyword_willReturnFilteredResponse() {
        // given & when
        int size = 10;
        List<PostSummaryWithSearchInfoReadModel> result = postQueryJooqRepository.searchByKeywordWithLatest(
                TITLE_CONTENT, "Hello", testPrimaryCategory1.getId(), List.of(testSecondaryCategory1.getId()),
                testMember2.getUuid(), null, null, size);

        // then
        // testPrimaryCategory1 + testSecondaryCategory1에 속한 게시글만 반환
        assertThat(result).allMatch(post ->
                post.primaryCategory().equals(testPrimaryCategory1.getCategory()));
        assertThat(result).allMatch(post ->
                post.secondaryCategory().equals(testSecondaryCategory1.getCategory()));
    }

    @Test
    @DisplayName("키워드로 게시글 목록 검색 - 정확도순")
    void testSearchByKeywordWithRelevance_givenNullImportanceAndMaxWordSimilarity_willReturnByImportanceAndMaxWordSimilarity() {
        // given & when
        int size = 10;
        List<PostSummaryWithSearchInfoReadModel> result = postQueryJooqRepository.searchByKeywordWithRelevance(
                TITLE_CONTENT, "hello", null, null, testMember2.getUuid(),
                null, null, null, null, size);

        // then
        // 결과가 존재하고 importance 기준으로 정렬되어 있음을 확인
        assertThat(result).isNotEmpty();
        assertThat(result).extracting("importance").isSortedAccordingTo(Collections.reverseOrder());
    }

    @Test
    @DisplayName("키워드로 게시글 목록 검색 - 정확도순 (페이지네이션)")
    void testSearchByKeywordWithRelevance_givenSequentialRequests_willReturnPaginatedResponse() {
        // given & when
        int size = 1;
        List<PostSummaryWithSearchInfoReadModel> firstPage = postQueryJooqRepository.searchByKeywordWithRelevance(
                TITLE_CONTENT, "Hello", null, null, testMember2.getUuid(),
                null, null, null, null, size);
        PostSummaryWithSearchInfoReadModel firstPost = firstPage.getFirst();
        List<PostSummaryWithSearchInfoReadModel> secondPage = postQueryJooqRepository.searchByKeywordWithRelevance(
                TITLE_CONTENT, "Hello", null, null, testMember2.getUuid(),
                firstPost.ulid(), firstPost.importance(), firstPost.maxWordSimilarity(), firstPost.publishedAt(), size);

        // then
        // 첫 페이지 마지막 = 두 번째 페이지 첫 번째 (커서 기준 연속성 확인)
        assertThat(firstPage.getLast()).isEqualTo(secondPage.getFirst());
        // 두 페이지에 중복된 ulid 없음
        assertThat(firstPage.getFirst().ulid()).isNotEqualTo(secondPage.getFirst().ulid());
    }

    @Test
    @DisplayName("PostId로 특정 게시글 조회")
    void testFindPostDetailByPostId_givenPostId_willReturnPost() {
        // when
        Optional<PostDetailReadModel> result = postQueryJooqRepository.findPostDetailByPostId(
                PostId.create(testPost1.getUlid()), testMember2.getUuid()
        );

        // then
        assertThat(result).isPresent();
        assertThat(result.get().ulid()).isEqualTo(testPost1.getUlid());
        assertThat(result.get().imagePath()).isEqualTo(testMemberProfile1.getImagePath());
    }

    @Test
    @DisplayName("PostId로 특정 게시글 데이터 조회")
    void testFindPostDetailDataByPostId_givenPostId_willReturnPost() {
        // when
        Optional<PostDetailDataReadModel> result = postQueryJooqRepository.findPostDetailDataByPostId(PostId.create(testPost1.getUlid()));

        // then
        assertThat(result).isPresent();
        assertThat(result.get().ulid()).isEqualTo(testPost1.getUlid());
    }
}