package kr.modusplant.domains.search.framework.out.jooq.repository;

import kr.modusplant.domains.post.common.helper.PostTestDataHelper;
import kr.modusplant.domains.search.domain.enums.SearchPostTarget;
import kr.modusplant.domains.search.domain.vo.*;
import kr.modusplant.domains.search.usecase.model.read.SearchPostReadModel;
import kr.modusplant.domains.search.usecase.port.repository.SearchPostRepository;
import kr.modusplant.jooq.tables.records.CommPostRecord;
import kr.modusplant.jooq.tables.records.CommPriCateRecord;
import kr.modusplant.jooq.tables.records.CommSecoCateRecord;
import kr.modusplant.jooq.tables.records.SiteMemberRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static kr.modusplant.domains.post.common.constant.PostJsonNodeConstant.*;
import static kr.modusplant.domains.search.common.util.domain.vo.SearchPostPublishedAtTestUtils.testSearchPostPublishedAt;
import static kr.modusplant.domains.search.common.util.domain.vo.nullobject.EmptySearchPostIdTestUtils.testEmptySearchPostId;
import static kr.modusplant.domains.search.common.util.domain.vo.nullobject.EmptySearchPostPublishedAtTestUtils.testEmptySearchPostPublishedAt;
import static kr.modusplant.domains.search.domain.enums.SearchPostTarget.TITLE_CONTENT;
import static kr.modusplant.domains.search.domain.enums.SearchPostTarget.TITLE_CONTENT_COMMENT;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("FieldCanBeLocal")
@SpringBootTest
class SearchPostRepositoryJooqAdapterIntegrationTest {
    private final SearchPostRepository searchPostRepository;
    private final PostTestDataHelper testDataHelper;

    @Autowired
    SearchPostRepositoryJooqAdapterIntegrationTest(SearchPostRepository searchPostRepository, PostTestDataHelper testDataHelper) {
        this.searchPostRepository = searchPostRepository;
        this.testDataHelper = testDataHelper;
    }

    private SiteMemberRecord testMember1, testMember2;
    private CommPriCateRecord testPrimaryCategory1, testPrimaryCategory2;
    private CommSecoCateRecord testSecondaryCategory1, testSecondaryCategory2, testSecondaryCategory3;
    private CommPostRecord testPost1, testPost2, testPost3, testPost4, testPost5;
    private LocalDateTime mockedNow;

    @BeforeEach
    void setUp() {
        LocalDateTime baseTime = LocalDateTime.now();
        testMember1 = testDataHelper.insertTestMember("TestMember1");
        testMember2 = testDataHelper.insertTestMember("TestMember2");
        testDataHelper.insertTestMemberProfile(testMember1);
        testDataHelper.insertTestMemberProfile(testMember2);
        testPrimaryCategory1 = testDataHelper.insertTestPrimaryCategory("testPrimaryCategory1", 100);
        testPrimaryCategory2 = testDataHelper.insertTestPrimaryCategory("testPrimaryCategory2", 101);
        testSecondaryCategory1 = testDataHelper.insertTestSecondaryCategory(testPrimaryCategory1, "testSecondaryCategory1", 100);
        testSecondaryCategory2 = testDataHelper.insertTestSecondaryCategory(testPrimaryCategory1, "testSecondaryCategory2", 101);
        testSecondaryCategory3 = testDataHelper.insertTestSecondaryCategory(testPrimaryCategory2, "testSecondaryCategory3", 102);
        testPost1 = testDataHelper.insertTestPublishedPost(testPrimaryCategory1, testSecondaryCategory1, testMember1, "title1", TEST_POST_CONTENT, TEST_POST_CONTENT_THUMBNAIL_KEY, baseTime.plusDays(31));
        testPost2 = testDataHelper.insertTestPublishedPost(testPrimaryCategory1, testSecondaryCategory2, testMember1, "title2", TEST_POST_CONTENT_TEXT_AND_IMAGE, TEST_POST_CONTENT_TEXT_AND_IMAGE_THUMBNAIL_KEY, baseTime.plusDays(32));
        testPost3 = testDataHelper.insertTestDraftPost(testPrimaryCategory1, testSecondaryCategory1, testMember1, "title3", TEST_POST_CONTENT, TEST_POST_CONTENT_THUMBNAIL_KEY, baseTime.plusDays(31));
        testPost4 = testDataHelper.insertTestPublishedPost(testPrimaryCategory2, testSecondaryCategory3, testMember1, "title4", TEST_POST_CONTENT, TEST_POST_CONTENT_THUMBNAIL_KEY, baseTime.plusDays(33));
        testPost5 = testDataHelper.insertTestPublishedPost(testPrimaryCategory1, testSecondaryCategory1, testMember2, "title5", TEST_POST_CONTENT, TEST_POST_CONTENT_THUMBNAIL_KEY, baseTime.plusDays(34));
        testDataHelper.insertTestComment(testPost1, "1", testMember2, "content1", false);
        testDataHelper.insertTestComment(testPost1, "1.1", testMember1, "content2", true);
        testDataHelper.insertTestComment(testPost1, "1.2", testMember2, "content3", false);
        testDataHelper.insertTestComment(testPost2, "1", testMember2, "content1", false);
        testDataHelper.insertTestPostLike(testPost1, testMember2, baseTime.plusDays(35));
        testDataHelper.insertTestPostLike(testPost4, testMember2, baseTime.plusDays(36));
        testDataHelper.insertTestPostLike(testPost5, testMember1, baseTime.plusDays(37));
        testDataHelper.insertTestPostBookmark(testPost1, testMember2, baseTime.plusDays(35));
        testDataHelper.insertTestPostBookmark(testPost2, testMember2, baseTime.plusDays(36));
        testDataHelper.insertTestPostBookmark(testPost5, testMember1, baseTime.plusDays(37));
        mockedNow = baseTime.plusDays(40);
    }

    @AfterEach
    void tearDown() {
        testDataHelper.deleteTestPostWithRelations(testPost1, testPost2, testPost3, testPost4, testPost5);
        testDataHelper.deleteTestCategory(testPrimaryCategory1, testPrimaryCategory2);
        testDataHelper.deleteTestMember(testMember1, testMember2);
    }

    @Test
    @DisplayName("키워드로 게시글 목록 검색 - 최신순")
    void testSearchByKeywordWithLatest_givenKeyword_willReturnFilteredPosts() {
        // given & when
        int size = 1;
        SearchKeyword keyword = SearchKeyword.create("Hello");

        List<SearchPostReadModel> firstPageByKeyword =
                searchPostRepository.searchByKeywordWithLatest(
                        keyword, TITLE_CONTENT, null, null,
                        testEmptySearchPostId, testEmptySearchPostPublishedAt, size, testMember2.getUuid());

        SearchPostReadModel firstPage = firstPageByKeyword.getFirst();
        SearchPostReadModel lastPage = firstPageByKeyword.getLast();
        SearchPostId cursorId = SearchPostId.create(firstPage.ulid());
        SearchPostPublishedAt cursorPublishedAt;
        try (MockedStatic<LocalDateTime> mockedTime = Mockito.mockStatic(LocalDateTime.class)) {
            mockedTime.when(LocalDateTime::now).thenReturn(mockedNow);
            cursorPublishedAt = SearchPostPublishedAt.create(firstPage.publishedAt());
        }

        List<SearchPostReadModel> secondPageByKeyword =
                searchPostRepository.searchByKeywordWithLatest(
                        keyword, TITLE_CONTENT, null, null,
                        cursorId, cursorPublishedAt, size, testMember2.getUuid());

        List<SearchPostReadModel> pageByBackslash = searchPostRepository.searchByKeywordWithLatest(
                SearchKeyword.create("\\".repeat(10)), TITLE_CONTENT, null, null,
                testEmptySearchPostId, testEmptySearchPostPublishedAt, size, testMember2.getUuid());

        List<SearchPostReadModel> pageByPercent = searchPostRepository.searchByKeywordWithLatest(
                SearchKeyword.create("%".repeat(10)), TITLE_CONTENT, null, null,
                testEmptySearchPostId, testEmptySearchPostPublishedAt, size, testMember2.getUuid());

        List<SearchPostReadModel> pageByUnderscore = searchPostRepository.searchByKeywordWithLatest(
                SearchKeyword.create("_".repeat(10)), TITLE_CONTENT, null, null,
                testEmptySearchPostId, testEmptySearchPostPublishedAt, size, testMember2.getUuid());

        // then
        // 결과 존재 여부 확인
        assertThat(firstPageByKeyword).hasSize(size + 1);
        assertThat(lastPage).isEqualTo(secondPageByKeyword.getFirst());
        assertThat(firstPage.ulid()).isEqualTo(testPost5.getUlid());
        assertThat(lastPage.ulid()).isEqualTo(testPost4.getUlid());

        // 최신순 정렬 여부 확인
        assertThat(firstPageByKeyword).extracting("ulid").isSortedAccordingTo(Collections.reverseOrder());
        assertThat(secondPageByKeyword).extracting("ulid").isSortedAccordingTo(Collections.reverseOrder());

        assertThat(firstPageByKeyword).extracting("publishedAt").isSortedAccordingTo(Collections.reverseOrder());
        assertThat(secondPageByKeyword).extracting("publishedAt").isSortedAccordingTo(Collections.reverseOrder());
        assertThat(lastPage.publishedAt()).isAfterOrEqualTo(secondPageByKeyword.getLast().publishedAt());

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
        List<SearchPostReadModel> result = searchPostRepository.searchByKeywordWithLatest(
                SearchKeyword.create("title1"), SearchPostTarget.TITLE, null, null,
                testEmptySearchPostId, testSearchPostPublishedAt, size, testMember2.getUuid());

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
        List<SearchPostReadModel> resultByContent3 = searchPostRepository.searchByKeywordWithLatest(
                SearchKeyword.create("content3"), TITLE_CONTENT_COMMENT, null, null,
                testEmptySearchPostId, testSearchPostPublishedAt, size, testMember2.getUuid());

        List<SearchPostReadModel> resultByContent2 = searchPostRepository.searchByKeywordWithLatest(
                SearchKeyword.create("content2"), TITLE_CONTENT_COMMENT, null, null,
                testEmptySearchPostId, testSearchPostPublishedAt, size, testMember2.getUuid());

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
        List<SearchPostReadModel> result = searchPostRepository.searchByKeywordWithLatest(
                SearchKeyword.create("Hello"), TITLE_CONTENT, testPrimaryCategory1.getId(), List.of(testSecondaryCategory1.getId()),
                testEmptySearchPostId, testEmptySearchPostPublishedAt, size, testMember2.getUuid());

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
        List<SearchPostReadModel> result = searchPostRepository.searchByKeywordWithRelevance(
                SearchKeyword.create("hello"), TITLE_CONTENT, null, null,
                testEmptySearchPostId, testEmptySearchPostPublishedAt,
                SearchPostImportance.createEmpty(), SearchKeywordSimilarity.createEmpty(),
                size, testMember2.getUuid());

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
        SearchKeyword keyword = SearchKeyword.create("Hello");

        List<SearchPostReadModel> firstPage = searchPostRepository.searchByKeywordWithRelevance(
                keyword, TITLE_CONTENT, null, null,
                testEmptySearchPostId, testEmptySearchPostPublishedAt,
                SearchPostImportance.createEmpty(), SearchKeywordSimilarity.createEmpty(),
                size, testMember2.getUuid());

        SearchPostReadModel firstPost = firstPage.getFirst();
        SearchPostId cursorId = SearchPostId.create(firstPost.ulid());
        SearchPostImportance cursorImportance = SearchPostImportance.create(firstPost.importance());
        SearchKeywordSimilarity cursorSimilarity = SearchKeywordSimilarity.create(firstPost.maxWordSimilarity());
        SearchPostPublishedAt cursorPublishedAt;
        try (MockedStatic<LocalDateTime> mockedTime = Mockito.mockStatic(LocalDateTime.class)) {
            mockedTime.when(LocalDateTime::now).thenReturn(mockedNow);
            cursorPublishedAt = SearchPostPublishedAt.create(firstPost.publishedAt());
        }

        List<SearchPostReadModel> secondPage = searchPostRepository.searchByKeywordWithRelevance(
                keyword, TITLE_CONTENT, null, null,
                cursorId, cursorPublishedAt, cursorImportance, cursorSimilarity,
                size, testMember2.getUuid());

        // then
        // 첫 페이지 마지막 = 두 번째 페이지 첫 번째 (커서 기준 연속성 확인)
        assertThat(firstPage.getLast()).isEqualTo(secondPage.getFirst());
        // 두 페이지에 중복된 ulid 없음
        assertThat(firstPage.getFirst().ulid()).isNotEqualTo(secondPage.getFirst().ulid());
    }
}
