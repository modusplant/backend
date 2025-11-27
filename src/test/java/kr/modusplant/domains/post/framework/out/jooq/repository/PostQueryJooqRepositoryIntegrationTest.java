package kr.modusplant.domains.post.framework.out.jooq.repository;

import kr.modusplant.domains.post.common.helper.PostTestDataHelper;
import kr.modusplant.domains.post.domain.exception.EmptyCategoryIdException;
import kr.modusplant.domains.post.domain.vo.PostId;
import kr.modusplant.domains.post.usecase.record.PostDetailReadModel;
import kr.modusplant.domains.post.usecase.record.PostSummaryReadModel;
import kr.modusplant.jooq.tables.records.CommPostRecord;
import kr.modusplant.jooq.tables.records.CommPriCateRecord;
import kr.modusplant.jooq.tables.records.CommSecoCateRecord;
import kr.modusplant.jooq.tables.records.SiteMemberRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static kr.modusplant.domains.post.common.constant.PostJsonNodeConstant.TEST_POST_CONTENT;
import static kr.modusplant.domains.post.common.constant.PostJsonNodeConstant.TEST_POST_CONTENT_TEXT_AND_IMAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@Rollback
class PostQueryJooqRepositoryIntegrationTest {

    @Autowired
    private PostQueryJooqRepository postQueryJooqRepository;

    @Autowired
    private PostTestDataHelper testDataHelper;


    private SiteMemberRecord testMember1, testMember2;
    private CommPriCateRecord testPrimaryCategory1, testPrimaryCategory2;
    private CommSecoCateRecord testSecondaryCategory1, testSecondaryCategory2, testSecondaryCategory3;
    private CommPostRecord testPost1,testPost2,testPost3,testPost4,testPost5;

    @BeforeEach
    void setUp() {
        testMember1 = testDataHelper.insertTestMember("TestMember1");
        testMember2 = testDataHelper.insertTestMember("TestMember2");
        testPrimaryCategory1 = testDataHelper.insertTestPrimaryCategory("testPrimaryCategory1",100);
        testPrimaryCategory2 = testDataHelper.insertTestPrimaryCategory("testPrimaryCategory2",101);
        testSecondaryCategory1 = testDataHelper.insertTestSecondaryCategory(testPrimaryCategory1,"testSecondaryCategory1",100);
        testSecondaryCategory2 = testDataHelper.insertTestSecondaryCategory(testPrimaryCategory1,"testSecondaryCategory2",101);
        testSecondaryCategory3 = testDataHelper.insertTestSecondaryCategory(testPrimaryCategory2,"testSecondaryCategory3",102);
        testPost1 = testDataHelper.insertTestPublishedPost(testPrimaryCategory1,testSecondaryCategory1,testMember1,"title1",TEST_POST_CONTENT);
        testPost2 = testDataHelper.insertTestPublishedPost(testPrimaryCategory1,testSecondaryCategory2,testMember1,"title2",TEST_POST_CONTENT_TEXT_AND_IMAGE);
        testPost3 = testDataHelper.insertTestDraftPost(testPrimaryCategory1,testSecondaryCategory1,testMember1,"title3",TEST_POST_CONTENT);
        testPost4 = testDataHelper.insertTestPublishedPost(testPrimaryCategory2,testSecondaryCategory3,testMember1,"title4",TEST_POST_CONTENT);
        testPost5 = testDataHelper.insertTestPublishedPost(testPrimaryCategory1,testSecondaryCategory1,testMember2,"title5",TEST_POST_CONTENT);
        testDataHelper.insertTestComment(testPost1,"1",testMember2,"content1",false);
        testDataHelper.insertTestComment(testPost1,"1.1",testMember1,"content2",true);
        testDataHelper.insertTestComment(testPost1,"1.2",testMember2,"content3",false);
        testDataHelper.insertTestComment(testPost2,"1",testMember2,"content1",false);
        testDataHelper.insertTestPostLike(testPost1,testMember2);
        testDataHelper.insertTestPostLike(testPost4,testMember2);
        testDataHelper.insertTestPostLike(testPost5,testMember1);
        testDataHelper.insertTestPostBookmark(testPost1,testMember2);
        testDataHelper.insertTestPostBookmark(testPost2,testMember2);
        testDataHelper.insertTestPostBookmark(testPost5,testMember1);
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

        // then
        assertThat(firstPage).hasSize(size+1);
        assertThat(firstPage.getLast()).isEqualTo(secondPage.getFirst());
        assertThat(firstPage.get(0).ulid()).isEqualTo(testPost5.getUlid());
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

        assertThat(firstPostResult.commentCount()).isEqualTo(0);
        assertThat(firstPostResult.isLiked()).isTrue();
        assertThat(firstPostResult.isBookmarked()).isFalse();
        assertThat(secondPostResult.commentCount()).isEqualTo(2);
        assertThat(secondPostResult.isLiked()).isTrue();
        assertThat(secondPostResult.isBookmarked()).isTrue();
    }

    @Test
    @DisplayName("1차 카테고리 및 2차 카테고리로 게시글 목록 조회")
    void testFindByCategoryWithCursor_givenCategories_willReturnFilteredPosts() {
        // when
        int size = 2;
        List<PostSummaryReadModel> firstPageByPrimaryCategory = postQueryJooqRepository.findByCategoryWithCursor(
                testPrimaryCategory1.getUuid(), null,testMember2.getUuid(),null,size
        );
        List<PostSummaryReadModel> secondPageByPrimaryCategory = postQueryJooqRepository.findByCategoryWithCursor(
                testPrimaryCategory1.getUuid(), null,testMember2.getUuid(),firstPageByPrimaryCategory.get(size-1).ulid(),size
        );

        List<PostSummaryReadModel> firstPageByCategories = postQueryJooqRepository.findByCategoryWithCursor(
                testPrimaryCategory1.getUuid(),List.of(testSecondaryCategory1.getUuid()),testMember2.getUuid(),null,size
        );
        List<PostSummaryReadModel> secondePageByCategories = postQueryJooqRepository.findByCategoryWithCursor(
                testPrimaryCategory1.getUuid(),List.of(testSecondaryCategory1.getUuid()),testMember2.getUuid(),firstPageByCategories.get(size-1).ulid(),size
        );

        // then
        assertThat(firstPageByPrimaryCategory).hasSize(size+1);
        assertThat(firstPageByPrimaryCategory.getLast()).isEqualTo(secondPageByPrimaryCategory.getFirst());
        assertThat(firstPageByPrimaryCategory.get(0).ulid()).isEqualTo(testPost5.getUlid());
        assertThat(firstPageByPrimaryCategory.get(1).ulid()).isEqualTo(testPost2.getUlid());
        assertThat(secondPageByPrimaryCategory.get(0).ulid()).isEqualTo(testPost1.getUlid());

        assertThat(firstPageByCategories).hasSize(size);
        assertThat(secondePageByCategories).isEmpty();
        assertThat(firstPageByCategories.get(0).ulid()).isEqualTo(testPost5.getUlid());
        assertThat(firstPageByCategories.get(1).ulid()).isEqualTo(testPost1.getUlid());

        // when & then
        assertThatThrownBy(() ->
                postQueryJooqRepository.findByCategoryWithCursor(
                        null, List.of(testSecondaryCategory1.getUuid()), testMember2.getUuid(), null, size
                )
        ).isInstanceOf(EmptyCategoryIdException.class);
    }

    @Test
    @DisplayName("키워드 없이 게시글 목록 조회")
    void testFindByKeywordWithCursor_givenNoKeyword_willReturnAllPosts() {
        // when
        int size = 2;
        List<PostSummaryReadModel> firstPage = postQueryJooqRepository.findByKeywordWithCursor(null,testMember2.getUuid(),null,size);
        List<PostSummaryReadModel> secondPage = postQueryJooqRepository.findByKeywordWithCursor(null,testMember2.getUuid(),firstPage.get(size-1).ulid(),size);

        // then
        assertThat(firstPage).hasSize(size+1);
        assertThat(firstPage.getLast()).isEqualTo(secondPage.getFirst());
        assertThat(firstPage.get(0).ulid()).isEqualTo(testPost5.getUlid());
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

        assertThat(firstPostResult.commentCount()).isEqualTo(0);
        assertThat(firstPostResult.isLiked()).isTrue();
        assertThat(firstPostResult.isBookmarked()).isFalse();
        assertThat(secondPostResult.commentCount()).isEqualTo(2);
        assertThat(secondPostResult.isLiked()).isTrue();
        assertThat(secondPostResult.isBookmarked()).isTrue();
    }

    @Test
    @DisplayName("키워드로 게시글 목록 조회")
    void testFindByKeywordWithCursor_givenKeyword_willReturnFilteredPosts() {
        // when
        int size = 2;
        String keyword = "Hello";
        List<PostSummaryReadModel> firstPageByKeyword = postQueryJooqRepository.findByKeywordWithCursor(keyword,testMember2.getUuid(),null,size);
        List<PostSummaryReadModel> secondPageByKeyword = postQueryJooqRepository.findByKeywordWithCursor(keyword,testMember2.getUuid(),firstPageByKeyword.get(size-1).ulid(),size);

        List<PostSummaryReadModel> firstPageByBlankKeyword = postQueryJooqRepository.findByKeywordWithCursor("",testMember2.getUuid(),null,size);
        List<PostSummaryReadModel> secondePageByBlankKeyword = postQueryJooqRepository.findByKeywordWithCursor("",testMember2.getUuid(),firstPageByBlankKeyword.get(size-1).ulid(),size);

        // then
        assertThat(firstPageByKeyword).hasSize(size+1);
        assertThat(firstPageByKeyword.getLast()).isEqualTo(secondPageByKeyword.getFirst());
        assertThat(firstPageByKeyword.get(0).ulid()).isEqualTo(testPost5.getUlid());
        assertThat(firstPageByKeyword.get(1).ulid()).isEqualTo(testPost4.getUlid());
        assertThat(secondPageByKeyword.get(0).ulid()).isEqualTo(testPost1.getUlid());

        assertThat(firstPageByBlankKeyword).hasSize(size+1);
        assertThat(firstPageByBlankKeyword.getLast()).isEqualTo(secondePageByBlankKeyword.getFirst());
        assertThat(firstPageByBlankKeyword.get(0).ulid()).isEqualTo(testPost5.getUlid());
        assertThat(firstPageByBlankKeyword.get(1).ulid()).isEqualTo(testPost4.getUlid());
        assertThat(secondePageByBlankKeyword.get(0).ulid()).isEqualTo(testPost2.getUlid());
        assertThat(secondePageByBlankKeyword.get(1).ulid()).isEqualTo(testPost1.getUlid());
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
    }


}