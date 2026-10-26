package kr.modusplant.domains.member.framework.outbound.jpa.repository;

import kr.modusplant.domains.member.common.util.framework.outbound.jpa.entity.PostAbuseReportEntityTestUtils;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.PostAbuseReportEntity;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.PostEntity;
import kr.modusplant.domains.post.framework.outbound.jpa.mapper.PrimaryCategoryJpaRepository;
import kr.modusplant.domains.post.framework.outbound.jpa.mapper.SecondaryCategoryJpaRepository;
import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryOnlyContext
class PostAbuseReportJpaRepositoryTest implements PostAbuseReportEntityTestUtils {
    private final TestEntityManager entityManager;
    private final PostAbuseReportJpaRepository postAbuseReportJpaRepository;
    private final PrimaryCategoryJpaRepository primaryCategoryJpaRepository;
    private final SecondaryCategoryJpaRepository secondaryCategoryJpaRepository;

    private PostEntity postEntity;

    @Autowired
    PostAbuseReportJpaRepositoryTest(TestEntityManager entityManager,
                                     PostAbuseReportJpaRepository postAbuseReportJpaRepository,
                                     PrimaryCategoryJpaRepository primaryCategoryJpaRepository,
                                     SecondaryCategoryJpaRepository secondaryCategoryJpaRepository) {
        this.entityManager = entityManager;
        this.postAbuseReportJpaRepository = postAbuseReportJpaRepository;
        this.primaryCategoryJpaRepository = primaryCategoryJpaRepository;
        this.secondaryCategoryJpaRepository = secondaryCategoryJpaRepository;
    }

    @BeforeEach
    void setUp() {
        MemberEntity memberEntity = entityManager.persist(createMemberBasicUserEntity());
        postEntity = entityManager.persist(
                createPublishedPostEntityBuilder()
                        .primaryCategory(primaryCategoryJpaRepository.findById(1).orElseThrow())
                        .secondaryCategory(secondaryCategoryJpaRepository.findById(1).orElseThrow())
                        .authMember(memberEntity)
                        .build());
        entityManager.persist(createPostAbuseReportEntityBuilder().member(memberEntity).post(postEntity).build());
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("저장된 게시글 id로 게시글 신고 삭제 시 활동 수행")
    void testDeleteByPostId_givenSavedPostId_willProcessAction() {
        // given
        String postId = postEntity.getUlid();
        assertThat(postAbuseReportJpaRepository.findAll())
                .extracting(PostAbuseReportEntity::getPostId)
                .contains(postId);

        // when
        postAbuseReportJpaRepository.deleteByPostId(postId);
        entityManager.flush();
        entityManager.clear();

        // then
        assertThat(postAbuseReportJpaRepository.findAll())
                .extracting(PostAbuseReportEntity::getPostId)
                .doesNotContain(postId);
    }
}
