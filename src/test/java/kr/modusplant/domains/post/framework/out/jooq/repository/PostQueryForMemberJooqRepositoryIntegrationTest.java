package kr.modusplant.domains.post.framework.out.jooq.repository;

import kr.modusplant.domains.post.common.helper.PostTestDataHelper;
import kr.modusplant.domains.post.domain.vo.AuthorId;
import kr.modusplant.domains.post.domain.vo.PostId;
import kr.modusplant.domains.post.usecase.record.DraftPostReadModel;
import kr.modusplant.domains.post.usecase.record.PostSummaryReadModel;
import kr.modusplant.jooq.tables.records.CommPostRecord;
import kr.modusplant.jooq.tables.records.CommPriCateRecord;
import kr.modusplant.jooq.tables.records.CommSecoCateRecord;
import kr.modusplant.jooq.tables.records.SiteMemberRecord;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import static kr.modusplant.domains.post.common.constant.PostJsonNodeConstant.TEST_POST_CONTENT;
import static kr.modusplant.domains.post.common.constant.PostJsonNodeConstant.TEST_POST_CONTENT_TEXT_AND_IMAGE;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback
@TestPropertySource(properties = {
        "spring.jpa.properties.hibernate.jdbc.time_zone=Asia/Seoul"
})
class PostQueryForMemberJooqRepositoryIntegrationTest {

    @Autowired
    private PostQueryForMemberJooqRepository postQueryForMemberJooqRepository;

    @Autowired
    private PostTestDataHelper testDataHelper;

    private SiteMemberRecord testMember1, testMember2;
    private CommPriCateRecord testPrimaryCategory1, testPrimaryCategory2;
    private CommSecoCateRecord testSecondaryCategory1, testSecondaryCategory2, testSecondaryCategory3;
    private CommPostRecord testPost1, testPost2, testPost3, testPost4, testPost5;

    @BeforeAll
    static void setTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }

    @BeforeEach
    void setUp() {
        LocalDateTime baseTime = LocalDateTime.now();
        testMember1 = testDataHelper.insertTestMember("TestMember1");
        testMember2 = testDataHelper.insertTestMember("TestMember2");
        testPrimaryCategory1 = testDataHelper.insertTestPrimaryCategory("testPrimaryCategory1",100);
        testPrimaryCategory2 = testDataHelper.insertTestPrimaryCategory("testPrimaryCategory2",101);
        testSecondaryCategory1 = testDataHelper.insertTestSecondaryCategory(testPrimaryCategory1,"testSecondaryCategory1",100);
        testSecondaryCategory2 = testDataHelper.insertTestSecondaryCategory(testPrimaryCategory1,"testSecondaryCategory2",101);
        testSecondaryCategory3 = testDataHelper.insertTestSecondaryCategory(testPrimaryCategory2,"testSecondaryCategory3",102);
        testPost1 = testDataHelper.insertTestPublishedPost(testPrimaryCategory1,testSecondaryCategory1,testMember1,"title1",TEST_POST_CONTENT,baseTime.plusDays(31));
        testPost2 = testDataHelper.insertTestPublishedPost(testPrimaryCategory1,testSecondaryCategory2,testMember1,"title2",TEST_POST_CONTENT_TEXT_AND_IMAGE,baseTime.plusDays(32));
        testPost3 = testDataHelper.insertTestDraftPost(testPrimaryCategory1,testSecondaryCategory1,testMember1,"title3",TEST_POST_CONTENT);
        testPost4 = testDataHelper.insertTestPublishedPost(testPrimaryCategory2,testSecondaryCategory3,testMember1,"title4",TEST_POST_CONTENT,baseTime.plusDays(33));
        testPost5 = testDataHelper.insertTestPublishedPost(testPrimaryCategory1,testSecondaryCategory1,testMember2,"title5",TEST_POST_CONTENT,baseTime.plusDays(34));
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
    @DisplayName("작성자의 발행된 게시글 목록을 오프셋 기반으로 조회")
    void testFindPublishedByAuthMemberWithOffset_givenAuthor_willReturnPublishedPosts() {
        // given
        AuthorId authorId = AuthorId.fromUuid(testMember1.getUuid());
        int page = 0;
        int size = 2;

        // when
        Page<PostSummaryReadModel> firstPage = postQueryForMemberJooqRepository.findPublishedByAuthMemberWithOffset(authorId, page, size);
        Page<PostSummaryReadModel> secondPage = postQueryForMemberJooqRepository.findPublishedByAuthMemberWithOffset(authorId, page + 1, size);

        // then
        assertThat(firstPage.getTotalElements()).isEqualTo(3);
        assertThat(firstPage.getTotalPages()).isEqualTo(2);
        assertThat(firstPage.getContent()).hasSize(2);
        assertThat(firstPage.getContent().get(0).ulid()).isEqualTo(testPost4.getUlid());
        assertThat(firstPage.getContent().get(1).ulid()).isEqualTo(testPost2.getUlid());
        assertThat(secondPage.getTotalElements()).isEqualTo(3);
        assertThat(secondPage.getTotalPages()).isEqualTo(2);
        assertThat(secondPage.getContent()).hasSize(1);
        assertThat(secondPage.getContent().get(0).ulid()).isEqualTo(testPost1.getUlid());

        assertThat(firstPage.getContent().get(0).publishedAt()).isNotNull();
        assertThat(firstPage.getContent().get(1).publishedAt()).isNotNull();
        assertThat(secondPage.getContent().get(0).publishedAt()).isNotNull();

        // 댓글 수, 좋아요, 북마크 검증
        PostSummaryReadModel post4Result = firstPage.getContent().get(0);
        assertThat(post4Result.commentCount()).isEqualTo(0);
        assertThat(post4Result.isLiked()).isFalse();
        assertThat(post4Result.isBookmarked()).isFalse();
        PostSummaryReadModel post1Result = secondPage.getContent().get(0);
        assertThat(post1Result.commentCount()).isEqualTo(2);
        assertThat(post1Result.isLiked()).isFalse();
        assertThat(post1Result.isBookmarked()).isFalse();
    }

    @Test
    @DisplayName("작성자가 발행한 게시글이 없을 때 빈 페이지 반환")
    void testFindPublishedByAuthMemberWithOffset_givenNoPublishedPosts_willReturnEmptyPage() {
        // given
        SiteMemberRecord newMember = testDataHelper.insertTestMember("New member");
        AuthorId authorId = AuthorId.fromUuid(newMember.getUuid());
        int page = 0;
        int size = 2;

        // when
        Page<PostSummaryReadModel> firstPage = postQueryForMemberJooqRepository.findPublishedByAuthMemberWithOffset(authorId, page, size);

        // then
        assertThat(firstPage.getTotalElements()).isEqualTo(0);
        assertThat(firstPage.getTotalPages()).isEqualTo(0);
        assertThat(firstPage.getContent()).isEmpty();

        testDataHelper.deleteTestMember(newMember);
    }

    @Test
    @DisplayName("작성자의 임시저장 게시글 목록을 오프셋 기반으로 조회")
    void testFindDraftByAuthMemberWithOffset_givenAuthor_willReturnDraftPosts() {
        // given
        AuthorId authorId = AuthorId.fromUuid(testMember1.getUuid());
        int page = 0;
        int size = 2;

        // when
        Page<DraftPostReadModel> firstPage = postQueryForMemberJooqRepository.findDraftByAuthMemberWithOffset(authorId, page, size);

        // then
        assertThat(firstPage.getTotalElements()).isEqualTo(1);
        assertThat(firstPage.getTotalPages()).isEqualTo(1);
        assertThat(firstPage.getContent()).hasSize(1);
        assertThat(firstPage.getContent().get(0).ulid()).isEqualTo(testPost3.getUlid());
    }

    @Test
    @DisplayName("작성자가 임시저장한 게시글이 없을 때 빈 페이지 반환")
    void testFindDraftByAuthMemberWithOffset_givenNoDraftPosts_willReturnEmptyPage() {
        // given
        AuthorId authorId = AuthorId.fromUuid(testMember2.getUuid());
        int page = 0;
        int size = 2;

        // when
        Page<DraftPostReadModel> firstPage = postQueryForMemberJooqRepository.findDraftByAuthMemberWithOffset(authorId, page, size);

        // then
        assertThat(firstPage.getTotalElements()).isEqualTo(0);
        assertThat(firstPage.getTotalPages()).isEqualTo(0);
        assertThat(firstPage.getContent()).isEmpty();
    }

    @Test
    @DisplayName("게시글 id 목록으로 게시글 목록 오프셋 기반으로 조회")
    void testFindByIds_givenIds_willReturnPosts() {
        // given
        List<PostId> postIds = List.of(
                PostId.create(testPost4.getUlid()),
                PostId.create(testPost1.getUlid()),
                PostId.create(testPost2.getUlid())
        );

        // when
        List<PostSummaryReadModel> results = postQueryForMemberJooqRepository.findByIds(postIds,testMember2.getUuid());

        // then
        PostSummaryReadModel post4 = results.get(0);
        PostSummaryReadModel post1 = results.get(1);
        PostSummaryReadModel post2 = results.get(2);

        assertThat(results).hasSize(3);
        assertThat(post4.ulid()).isEqualTo(testPost4.getUlid());
        assertThat(post1.ulid()).isEqualTo(testPost1.getUlid());
        assertThat(post2.ulid()).isEqualTo(testPost2.getUlid());

        // 좋아요, 북마크, 댓글 수 확인
        assertThat(post4.isLiked()).isTrue();
        assertThat(post4.isBookmarked()).isFalse();
        assertThat(post4.commentCount()).isEqualTo(0);
        assertThat(post1.isLiked()).isTrue();
        assertThat(post1.isBookmarked()).isTrue();
        assertThat(post1.commentCount()).isEqualTo(2);
        assertThat(post2.isLiked()).isFalse();
        assertThat(post2.isBookmarked()).isTrue();
        assertThat(post2.commentCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("빈 게시글 id 목록으로 게시글 목록 조회 시 empty list 반환")
    void testFindByIds_givenEmptyIdList_willReturnEmptyPostList() {
        // given
        UUID currentMemberUuid = testMember1.getUuid();

        // when
        List<PostSummaryReadModel> result1 = postQueryForMemberJooqRepository.findByIds(List.of(),currentMemberUuid);
        List<PostSummaryReadModel> result2 = postQueryForMemberJooqRepository.findByIds(null,currentMemberUuid);

        // then
        assertThat(result1).isEmpty();
        assertThat(result2).isEmpty();
    }

    @Test
    @DisplayName("회원이 좋아요한 게시글 목록을 오프셋 기반으로 조회")
    void testFindLikedByMemberWithOffset_givenMember_willReturnLikedPosts() {
        // given
        UUID currentMemberUuid = testMember2.getUuid();
        int page = 0;
        int size = 2;

        // when
        Page<PostSummaryReadModel> firstPage = postQueryForMemberJooqRepository.findLikedByMemberWithOffset(currentMemberUuid, page, size);

        // then
        assertThat(firstPage.getTotalElements()).isEqualTo(2);
        assertThat(firstPage.getTotalPages()).isEqualTo(1);
        assertThat(firstPage.getContent()).hasSize(2);
        assertThat(firstPage.getContent().get(0).ulid()).isEqualTo(testPost4.getUlid());
        assertThat(firstPage.getContent().get(1).ulid()).isEqualTo(testPost1.getUlid());

        // 좋아요
        List<String> postUlids = firstPage.getContent().stream()
                .map(PostSummaryReadModel::ulid)
                .toList();
        assertThat(postUlids).containsExactly(testPost4.getUlid(),testPost1.getUlid());
        assertThat(firstPage.getContent()).allMatch(PostSummaryReadModel::isLiked);

        // 댓글수 & 북마크
        PostSummaryReadModel post4Result = firstPage.getContent().get(0);
        assertThat(post4Result.commentCount()).isEqualTo(0);
        assertThat(post4Result.isBookmarked()).isFalse();
        PostSummaryReadModel post1Result = firstPage.getContent().get(1);
        assertThat(post1Result.commentCount()).isEqualTo(2);
        assertThat(post1Result.isBookmarked()).isTrue();
    }

    @Test
    @DisplayName("회원이 좋아요한 게시글이 없을 때 빈 페이지 반환")
    void testFindLikedByMemberWithOffset_givenNoLikedPosts_willReturnEmptyPage() {
        // given
        SiteMemberRecord newMember = testDataHelper.insertTestMember("New member");
        int page = 0;
        int size = 2;

        // when
        Page<PostSummaryReadModel> firstPage = postQueryForMemberJooqRepository.findLikedByMemberWithOffset(newMember.getUuid(), page, size);

        // then
        assertThat(firstPage.getTotalElements()).isEqualTo(0);
        assertThat(firstPage.getTotalPages()).isEqualTo(0);
        assertThat(firstPage.getContent()).isEmpty();

        testDataHelper.deleteTestMember(newMember);
    }

    @Test
    @DisplayName("회원이 북마크한 게시글 목록을 오프셋 기반으로 조회")
    void testFindBookmarkedByMemberWithOffset_givenMember_willReturnBookmarkedPosts() {
        // given
        UUID currentMemberUuid = testMember2.getUuid();
        int page = 0;
        int size = 2;

        // when
        Page<PostSummaryReadModel> firstPage = postQueryForMemberJooqRepository.findBookmarkedByMemberWithOffset(currentMemberUuid, page, size);

        // then
        assertThat(firstPage.getTotalElements()).isEqualTo(2);
        assertThat(firstPage.getTotalPages()).isEqualTo(1);
        assertThat(firstPage.getContent()).hasSize(2);
        assertThat(firstPage.getContent().get(0).ulid()).isEqualTo(testPost2.getUlid());
        assertThat(firstPage.getContent().get(1).ulid()).isEqualTo(testPost1.getUlid());

        // 북마크
        List<String> postUlids = firstPage.getContent().stream()
                .map(PostSummaryReadModel::ulid)
                .toList();
        assertThat(postUlids).containsExactly(testPost2.getUlid(),testPost1.getUlid());
        assertThat(firstPage.getContent()).allMatch(PostSummaryReadModel::isBookmarked);

        // 댓글수 & 좋아요
        PostSummaryReadModel post2Result = firstPage.getContent().get(0);
        assertThat(post2Result.commentCount()).isEqualTo(1);
        assertThat(post2Result.isLiked()).isFalse();
        PostSummaryReadModel post1Result = firstPage.getContent().get(1);
        assertThat(post1Result.commentCount()).isEqualTo(2);
        assertThat(post1Result.isLiked()).isTrue();
    }

    @Test
    @DisplayName("회원이 북마크한 게시글이 없을 때 빈 페이지 반환")
    void testFindBookmarkedByMemberWithOffset_givenNoBookmarkedPosts_willReturnEmptyPage() {
        // given
        SiteMemberRecord newMember = testDataHelper.insertTestMember("New member");
        int page = 0;
        int size = 2;

        // when
        Page<PostSummaryReadModel> firstPage = postQueryForMemberJooqRepository.findBookmarkedByMemberWithOffset(newMember.getUuid(), page, size);

        // then
        assertThat(firstPage.getTotalElements()).isEqualTo(0);
        assertThat(firstPage.getTotalPages()).isEqualTo(0);
        assertThat(firstPage.getContent()).isEmpty();

        testDataHelper.deleteTestMember(newMember);
    }

}