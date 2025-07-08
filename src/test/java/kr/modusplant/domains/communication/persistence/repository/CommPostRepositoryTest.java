package kr.modusplant.domains.communication.persistence.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kr.modusplant.domains.communication.common.util.entity.CommPostEntityTestUtils;
import kr.modusplant.domains.communication.common.util.entity.CommSecondaryCategoryEntityTestUtils;
import kr.modusplant.domains.communication.persistence.entity.CommPostEntity;
import kr.modusplant.domains.communication.persistence.entity.CommSecondaryCategoryEntity;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.global.context.RepositoryOnlyContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RepositoryOnlyContext
class CommPostRepositoryTest implements CommPostEntityTestUtils, CommSecondaryCategoryEntityTestUtils, SiteMemberEntityTestUtils {
    private final CommPostRepository commPostRepository;
    private final CommSecondaryCategoryRepository commCategoryRepository;
    private final SiteMemberRepository siteMemberRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    CommPostRepositoryTest(CommPostRepository commPostRepository, CommSecondaryCategoryRepository commCategoryRepository, SiteMemberRepository siteMemberRepository) {
        this.commPostRepository = commPostRepository;
        this.commCategoryRepository = commCategoryRepository;
        this.siteMemberRepository = siteMemberRepository;
    }

    private CommSecondaryCategoryEntity testCommCategory;
    private SiteMemberEntity testSiteMember;

    @BeforeEach
    void setUp() {
        testCommCategory = commCategoryRepository.save(createTestCommSecondaryCategoryEntity());
        testSiteMember = siteMemberRepository.save(createMemberBasicUserEntity());
    }

    @Test
    @DisplayName("ULID로 컨텐츠 게시글 찾기")
    void findByUlidTest() {
        // given
        CommPostEntity commPostEntity = createCommPostEntityBuilder()
                .category(testCommCategory)
                .authMember(testSiteMember)
                .createMember(testSiteMember)
                .build();

        // when
        commPostRepository.save(commPostEntity);
        System.out.println(commPostEntity);

        // then
        assertThat(commPostRepository.findByUlid(commPostEntity.getUlid()).orElseThrow()).isEqualTo(commPostEntity);
    }

    @Test
    @DisplayName("전체 컨텐츠 게시글 찾기(최신순)")
    void findAllByOrderByCreatedAtDescTest() {
        // given
        List<CommPostEntity> commPosts = IntStream.range(0, 10)
                .mapToObj(i -> createCommPostEntityBuilder()
                        .category(testCommCategory)
                        .authMember(testSiteMember)
                        .createMember(testSiteMember)
                        .build()
                ).collect(Collectors.toList());
        commPostRepository.saveAll(commPosts);

        Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createdAt"));

        // when
        Page<CommPostEntity> result = commPostRepository.findAllByOrderByCreatedAtDesc(pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(10);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getTotalPages()).isEqualTo(4);

        List<CommPostEntity> content = result.getContent();
        for (int i = 0; i < content.size() - 1; i++) {
            assertThat(content.get(i).getCreatedAt())
                    .isAfterOrEqualTo(content.get(i + 1).getCreatedAt());
        }
    }

    @Test
    @DisplayName("삭제되지 않은 모든 컨텐츠 게시글 찾기(최신순)")
    void findByIsDeletedFalseOrderByCreatedAtDescTest() {
        // given
        List<CommPostEntity> commPosts = IntStream.range(0, 5)
                .mapToObj(i -> createCommPostEntityBuilder()
                        .category(testCommCategory)
                        .authMember(testSiteMember)
                        .createMember(testSiteMember)
                        .build()
                ).collect(Collectors.toList());
        commPosts.getFirst().updateIsDeleted(true);
        commPostRepository.saveAll(commPosts);

        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));

        // when
        Page<CommPostEntity> result = commPostRepository.findByIsDeletedFalseOrderByCreatedAtDesc(pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(4); // 삭제된 1건 제외
        assertThat(result.getContent().stream().noneMatch(CommPostEntity::getIsDeleted)).isTrue();

        List<CommPostEntity> content = result.getContent();
        for (int i = 0; i < content.size() - 1; i++) {
            assertThat(content.get(i).getCreatedAt())
                    .isAfterOrEqualTo(content.get(i + 1).getCreatedAt());
        }
    }

    @Test
    @DisplayName("카테고리로 삭제되지 않은 모든 컨텐츠 게시글 찾기(최신순)")
    void findByCategoryAndIsDeletedFalseOrderByCreatedAtDescTest() {
        // given
        CommSecondaryCategoryEntity testOtherGroup = commCategoryRepository.save(
                CommSecondaryCategoryEntity.builder().order(3).category("기타").build());
        List<CommPostEntity> commPosts = IntStream.range(0, 5)
                .mapToObj(i -> createCommPostEntityBuilder()
                        .category(i % 2 == 0 ? testCommCategory : testOtherGroup)
                        .authMember(testSiteMember)
                        .createMember(testSiteMember)
                        .build()
                ).collect(Collectors.toList());
        commPosts.getFirst().updateIsDeleted(true);
        commPostRepository.saveAll(commPosts);

        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));

        // when
        Page<CommPostEntity> result = commPostRepository.findByCategoryAndIsDeletedFalseOrderByCreatedAtDesc(testCommCategory, pageable);

        // then
        // i = 0, 2, 4 → testCommCategory로 생성됨 (0번은 삭제됨)
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent().stream().allMatch(post -> post.getCategory().equals(testCommCategory) && !post.getIsDeleted())).isTrue();

        List<CommPostEntity> content = result.getContent();
        for (int i = 0; i < content.size() - 1; i++) {
            assertThat(content.get(i).getCreatedAt())
                    .isAfterOrEqualTo(content.get(i + 1).getCreatedAt());
        }
    }

    @Test
    @DisplayName("인가 회원으로 삭제되지 않은 모든 컨텐츠 게시글 찾기(최신순)")
    void findByAuthMemberAndIsDeletedFalseOrderByCreatedAtDescTest() {
        // given
        SiteMemberEntity testSiteMember2 = siteMemberRepository.save(createMemberGoogleUserEntity());
        List<CommPostEntity> commPosts = IntStream.range(0, 5)
                .mapToObj(i -> createCommPostEntityBuilder()
                        .category(testCommCategory)
                        .authMember(i % 2 == 0 ? testSiteMember : testSiteMember2)
                        .createMember(i % 2 == 0 ? testSiteMember : testSiteMember2)
                        .build()
                ).collect(Collectors.toList());
        commPosts.getFirst().updateIsDeleted(true);
        commPostRepository.saveAll(commPosts);

        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));

        // when
        Page<CommPostEntity> result = commPostRepository.findByAuthMemberAndIsDeletedFalseOrderByCreatedAtDesc(testSiteMember,pageable);

        // then
        // i = 0, 2, 4 → testSiteMember로 생성됨 (0번은 삭제됨)
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent().stream().allMatch(post -> post.getAuthMember().equals(testSiteMember) && !post.getIsDeleted())).isTrue();

        List<CommPostEntity> content = result.getContent();
        for (int i = 0; i < content.size() - 1; i++) {
            assertThat(content.get(i).getCreatedAt())
                    .isAfterOrEqualTo(content.get(i + 1).getCreatedAt());
        }
    }


    @Test
    @DisplayName("createdAt으로 회원 찾기")
    void findByCreatedAtTest() {
        // when
        CommPostEntity commPostEntity = commPostRepository.save(
                createCommPostEntityBuilder()
                        .category(testCommCategory)
                        .authMember(testSiteMember)
                        .createMember(testSiteMember)
                        .build()
        );

        // then
        assertThat(commPostRepository.findByCreatedAt(commPostEntity.getCreatedAt()).getFirst()).isEqualTo(commPostEntity);
    }

    @Test
    @DisplayName("updatedAt으로 회원 찾기")
    void findByUpdatedAtTest() {
        // when
        CommPostEntity commPostEntity = commPostRepository.save(
                createCommPostEntityBuilder()
                        .category(testCommCategory)
                        .authMember(testSiteMember)
                        .createMember(testSiteMember)
                        .build()
        );

        // then
        assertThat(commPostRepository.findByUpdatedAt(commPostEntity.getUpdatedAt()).getFirst()).isEqualTo(commPostEntity);
    }

    @Test
    @DisplayName("ULID로 컨텐츠 게시글 삭제")
    void deleteByUlidTest() {
        // given
        CommPostEntity commPostEntity = commPostRepository.save(
                createCommPostEntityBuilder()
                        .category(testCommCategory)
                        .authMember(testSiteMember)
                        .createMember(testSiteMember)
                        .build()
        );
        String ulid = commPostEntity.getUlid();

        // when
        commPostRepository.deleteByUlid(ulid);

        // then
        assertThat(commPostRepository.findByUlid(ulid)).isEmpty();
    }

    @Test
    @DisplayName("ULID로 컨텐츠 게시글 확인")
    void existsByUlidTest() {
        // when
        CommPostEntity commPostEntity = commPostRepository.save(
                createCommPostEntityBuilder()
                        .category(testCommCategory)
                        .authMember(testSiteMember)
                        .createMember(testSiteMember)
                        .build()
        );

        // then
        assertThat(commPostRepository.existsByUlid(commPostEntity.getUlid())).isEqualTo(true);
    }

    @Test
    @DisplayName("ulid로 삭제되지 않은 게시글 조회")
    void findByUlidAndIsDeletedFalseTest() {
        // given
        CommPostEntity commPostEntity1 = commPostRepository.save(
                createCommPostEntityBuilder()
                        .category(testCommCategory)
                        .authMember(testSiteMember)
                        .createMember(testSiteMember)
                        .build()
        );
        CommPostEntity commPostEntity2 = commPostRepository.save(
                createCommPostEntityBuilder()
                        .category(testCommCategory)
                        .authMember(testSiteMember)
                        .createMember(testSiteMember)
                        .isDeleted(true)
                        .build()
        );

        // when
        Optional<CommPostEntity> found = commPostRepository.findByUlidAndIsDeletedFalse(commPostEntity1.getUlid());
        Optional<CommPostEntity> notFound = commPostRepository.findByUlidAndIsDeletedFalse(commPostEntity2.getUlid());

        // then
        assertThat(found).isPresent();
        assertThat(found.orElseThrow().getUlid()).isEqualTo(commPostEntity1.getUlid());
        assertThat(notFound).isEmpty();
    }

    @Test
    @DisplayName("제목+본문 검색어로 게시글 목록 찾기")
    void searchByTitleOrContentTest() {
        // given
        commPostRepository.save(
                createCommPostEntityBuilder()
                        .category(testCommCategory)
                        .authMember(testSiteMember)
                        .createMember(testSiteMember)
                        .build());
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<CommPostEntity> result1 = commPostRepository.searchByTitleOrContent("물",pageable);
        Page<CommPostEntity> result2 = commPostRepository.searchByTitleOrContent("this",pageable);
        Page<CommPostEntity> result3 = commPostRepository.searchByTitleOrContent("erd",pageable);

        // then
        assertThat(result1.getTotalElements()).isEqualTo(1);
        assertThat(result2.getTotalElements()).isEqualTo(1);
        assertThat(result3.getTotalElements()).isEqualTo(0);
        assertThat(result1.getContent().getFirst().getContent().get(1).has("src")).isEqualTo(true);
    }

    @Test
    @DisplayName("현재 조회수보다 업데이트할 조회수가 더 크면 조회수 수정 성공")
    void updateViewCountSuccessTest() {
        // given
        CommPostEntity commPostEntity = commPostRepository.save(
                createCommPostEntityBuilder()
                        .category(testCommCategory)
                        .authMember(testSiteMember)
                        .createMember(testSiteMember)
                        .viewCount(10L)
                        .build()
        );

        // when
        int updatedCount = commPostRepository.updateViewCount(commPostEntity.getUlid(),20L);

        // then
        assertThat(updatedCount).isEqualTo(1);
        entityManager.clear();
        CommPostEntity result = commPostRepository.findByUlid(commPostEntity.getUlid()).orElseThrow();
        assertThat(result.getViewCount()).isEqualTo(20L);
    }

    @Test
    @DisplayName("현재 조회수보다 업데이트할 조회수가 더 작거나 같으면 조회수 수정 실패")
    void updateViewCountFailTest() {
        // given
        CommPostEntity commPostEntity = commPostRepository.save(
                createCommPostEntityBuilder()
                        .category(testCommCategory)
                        .authMember(testSiteMember)
                        .createMember(testSiteMember)
                        .viewCount(10L)
                        .build()
        );

        // when
        int updatedCount = commPostRepository.updateViewCount(commPostEntity.getUlid(),5L);

        // then
        assertThat(updatedCount).isEqualTo(0);
        entityManager.clear();
        CommPostEntity result = commPostRepository.findByUlid(commPostEntity.getUlid()).orElseThrow();
        assertThat(result.getViewCount()).isEqualTo(10L);
    }

}