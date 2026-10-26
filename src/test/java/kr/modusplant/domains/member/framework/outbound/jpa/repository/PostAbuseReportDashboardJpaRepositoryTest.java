package kr.modusplant.domains.member.framework.outbound.jpa.repository;

import kr.modusplant.domains.member.common.util.framework.outbound.jpa.entity.PostAbuseReportDashboardEntityTestUtils;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
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
class PostAbuseReportDashboardJpaRepositoryTest implements PostAbuseReportDashboardEntityTestUtils {
    private final TestEntityManager entityManager;
    private final PostAbuseReportDashboardJpaRepository postAbuseReportDashboardJpaRepository;
    private final PrimaryCategoryJpaRepository primaryCategoryJpaRepository;
    private final SecondaryCategoryJpaRepository secondaryCategoryJpaRepository;

    private PostEntity postEntity;

    @Autowired
    PostAbuseReportDashboardJpaRepositoryTest(TestEntityManager entityManager,
                                              PostAbuseReportDashboardJpaRepository postAbuseReportDashboardJpaRepository,
                                              PrimaryCategoryJpaRepository primaryCategoryJpaRepository,
                                              SecondaryCategoryJpaRepository secondaryCategoryJpaRepository) {
        this.entityManager = entityManager;
        this.postAbuseReportDashboardJpaRepository = postAbuseReportDashboardJpaRepository;
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
        entityManager.persist(createPostAbuseReportDashboardUncheckedEntityBuilder().post(postEntity).build());
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("저장된 게시글 ULID로 게시글 신고 대시보드 삭제 시 활동 수행")
    void testDeleteByPostUlid_givenSavedPostUlid_willProcessAction() {
        // given
        String postUlid = postEntity.getUlid();
        assertThat(postAbuseReportDashboardJpaRepository.existsById(postUlid)).isTrue();

        // when
        postAbuseReportDashboardJpaRepository.deleteByPostUlid(postUlid);
        entityManager.flush();
        entityManager.clear();

        // then
        assertThat(postAbuseReportDashboardJpaRepository.existsById(postUlid)).isFalse();
    }
}
