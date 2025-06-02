package kr.modusplant.domains.communication.conversation.persistence.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kr.modusplant.domains.communication.conversation.common.util.entity.ConvCategoryEntityTestUtils;
import kr.modusplant.domains.communication.conversation.common.util.entity.ConvPostEntityTestUtils;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvCategoryEntity;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvPostEntity;
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
class ConvPostRepositoryTest implements ConvPostEntityTestUtils, ConvCategoryEntityTestUtils, SiteMemberEntityTestUtils {
    private final ConvPostRepository convPostRepository;
    private final ConvCategoryRepository convCategoryRepository;
    private final SiteMemberRepository siteMemberRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    ConvPostRepositoryTest(ConvPostRepository convPostRepository, ConvCategoryRepository convCategoryRepository, SiteMemberRepository siteMemberRepository) {
        this.convPostRepository = convPostRepository;
        this.convCategoryRepository = convCategoryRepository;
        this.siteMemberRepository = siteMemberRepository;
    }

    private ConvCategoryEntity testConvCategory;
    private SiteMemberEntity testSiteMember;

    @BeforeEach
    void setUp() {
        testConvCategory = convCategoryRepository.save(createTestConvCategoryEntity());
        testSiteMember = siteMemberRepository.save(createMemberBasicUserEntity());
    }

    @Test
    @DisplayName("ULID로 대화 게시글 찾기")
    void findByUlidTest() {
        // given
        ConvPostEntity convPostEntity = createConvPostEntityBuilder()
                .category(testConvCategory)
                .authMember(testSiteMember)
                .createMember(testSiteMember)
                .build();

        // when
        convPostRepository.save(convPostEntity);
        System.out.println(convPostEntity);

        // then
        assertThat(convPostRepository.findByUlid(convPostEntity.getUlid()).orElseThrow()).isEqualTo(convPostEntity);
    }

    @Test
    @DisplayName("전체 대화 게시글 찾기(최신순)")
    void findAllByOrderByCreatedAtDescTest() {
        // given
        List<ConvPostEntity> convPosts = IntStream.range(0, 10)
                .mapToObj(i -> createConvPostEntityBuilder()
                        .category(testConvCategory)
                        .authMember(testSiteMember)
                        .createMember(testSiteMember)
                        .build()
                ).collect(Collectors.toList());
        convPostRepository.saveAll(convPosts);

        Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createdAt"));

        // when
        Page<ConvPostEntity> result = convPostRepository.findAllByOrderByCreatedAtDesc(pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(10);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getTotalPages()).isEqualTo(4);

        List<ConvPostEntity> content = result.getContent();
        for (int i = 0; i < content.size() - 1; i++) {
            assertThat(content.get(i).getCreatedAt())
                    .isAfterOrEqualTo(content.get(i + 1).getCreatedAt());
        }
    }

    @Test
    @DisplayName("삭제되지 않은 모든 대화 게시글 찾기(최신순)")
    void findByIsDeletedFalseOrderByCreatedAtDescTest() {
        // given
        List<ConvPostEntity> convPosts = IntStream.range(0, 5)
                .mapToObj(i -> createConvPostEntityBuilder()
                        .category(testConvCategory)
                        .authMember(testSiteMember)
                        .createMember(testSiteMember)
                        .build()
                ).collect(Collectors.toList());
        convPosts.getFirst().updateIsDeleted(true);
        convPostRepository.saveAll(convPosts);

        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));

        // when
        Page<ConvPostEntity> result = convPostRepository.findByIsDeletedFalseOrderByCreatedAtDesc(pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(4); // 삭제된 1건 제외
        assertThat(result.getContent().stream().noneMatch(ConvPostEntity::getIsDeleted)).isTrue();

        List<ConvPostEntity> content = result.getContent();
        for (int i = 0; i < content.size() - 1; i++) {
            assertThat(content.get(i).getCreatedAt())
                    .isAfterOrEqualTo(content.get(i + 1).getCreatedAt());
        }
    }

    @Test
    @DisplayName("카테고리로 삭제되지 않은 모든 대화 게시글 찾기(최신순)")
    void findByCategoryAndIsDeletedFalseOrderByCreatedAtDescTest() {
        // given
        ConvCategoryEntity testOtherGroup = convCategoryRepository.save(
                ConvCategoryEntity.builder().order(2).category("기타").build());
        List<ConvPostEntity> convPosts = IntStream.range(0, 5)
                .mapToObj(i -> createConvPostEntityBuilder()
                        .category(i % 2 == 0 ? testConvCategory : testOtherGroup)
                        .authMember(testSiteMember)
                        .createMember(testSiteMember)
                        .build()
                ).collect(Collectors.toList());
        convPosts.getFirst().updateIsDeleted(true);
        convPostRepository.saveAll(convPosts);

        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));

        // when
        Page<ConvPostEntity> result = convPostRepository.findByCategoryAndIsDeletedFalseOrderByCreatedAtDesc(testConvCategory, pageable);

        // then
        // i = 0, 2, 4 → testConvCategory로 생성됨 (0번은 삭제됨)
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent().stream().allMatch(post -> post.getCategory().equals(testConvCategory) && !post.getIsDeleted())).isTrue();

        List<ConvPostEntity> content = result.getContent();
        for (int i = 0; i < content.size() - 1; i++) {
            assertThat(content.get(i).getCreatedAt())
                    .isAfterOrEqualTo(content.get(i + 1).getCreatedAt());
        }
    }

    @Test
    @DisplayName("인가 회원으로 삭제되지 않은 모든 대화 게시글 찾기(최신순)")
    void findByAuthMemberAndIsDeletedFalseOrderByCreatedAtDescTest() {
        // given
        SiteMemberEntity testSiteMember2 = siteMemberRepository.save(createMemberGoogleUserEntity());
        List<ConvPostEntity> convPosts = IntStream.range(0, 5)
                .mapToObj(i -> createConvPostEntityBuilder()
                        .category(testConvCategory)
                        .authMember(i % 2 == 0 ? testSiteMember : testSiteMember2)
                        .createMember(i % 2 == 0 ? testSiteMember : testSiteMember2)
                        .build()
                ).collect(Collectors.toList());
        convPosts.getFirst().updateIsDeleted(true);
        convPostRepository.saveAll(convPosts);

        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));

        // when
        Page<ConvPostEntity> result = convPostRepository.findByAuthMemberAndIsDeletedFalseOrderByCreatedAtDesc(testSiteMember,pageable);

        // then
        // i = 0, 2, 4 → testSiteMember로 생성됨 (0번은 삭제됨)
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent().stream().allMatch(post -> post.getAuthMember().equals(testSiteMember) && !post.getIsDeleted())).isTrue();

        List<ConvPostEntity> content = result.getContent();
        for (int i = 0; i < content.size() - 1; i++) {
            assertThat(content.get(i).getCreatedAt())
                    .isAfterOrEqualTo(content.get(i + 1).getCreatedAt());
        }
    }


    @Test
    @DisplayName("createdAt으로 회원 찾기")
    void findByCreatedAtTest() {
        // when
        ConvPostEntity convPostEntity = convPostRepository.save(
                createConvPostEntityBuilder()
                        .category(testConvCategory)
                        .authMember(testSiteMember)
                        .createMember(testSiteMember)
                        .build()
        );

        // then
        assertThat(convPostRepository.findByCreatedAt(convPostEntity.getCreatedAt()).getFirst()).isEqualTo(convPostEntity);
    }

    @Test
    @DisplayName("updatedAt으로 회원 찾기")
    void findByUpdatedAtTest() {
        // when
        ConvPostEntity convPostEntity = convPostRepository.save(
                createConvPostEntityBuilder()
                        .category(testConvCategory)
                        .authMember(testSiteMember)
                        .createMember(testSiteMember)
                        .build()
        );

        // then
        assertThat(convPostRepository.findByUpdatedAt(convPostEntity.getUpdatedAt()).getFirst()).isEqualTo(convPostEntity);
    }

    @Test
    @DisplayName("ULID로 대화 게시글 삭제")
    void deleteByUlidTest() {
        // given
        ConvPostEntity convPostEntity = convPostRepository.save(
                createConvPostEntityBuilder()
                        .category(testConvCategory)
                        .authMember(testSiteMember)
                        .createMember(testSiteMember)
                        .build()
        );
        String ulid = convPostEntity.getUlid();

        // when
        convPostRepository.deleteByUlid(ulid);

        // then
        assertThat(convPostRepository.findByUlid(ulid)).isEmpty();
    }

    @Test
    @DisplayName("ULID로 대화 게시글 확인")
    void existsByUlidTest() {
        // when
        ConvPostEntity convPostEntity = convPostRepository.save(
                createConvPostEntityBuilder()
                        .category(testConvCategory)
                        .authMember(testSiteMember)
                        .createMember(testSiteMember)
                        .build()
        );

        // then
        assertThat(convPostRepository.existsByUlid(convPostEntity.getUlid())).isEqualTo(true);
    }

    @Test
    @DisplayName("ulid로 삭제되지 않은 게시글 조회")
    void findByUlidAndIsDeletedFalseTest() {
        // given
        ConvPostEntity convPostEntity1 = convPostRepository.save(
                createConvPostEntityBuilder()
                        .category(testConvCategory)
                        .authMember(testSiteMember)
                        .createMember(testSiteMember)
                        .build()
        );
        ConvPostEntity convPostEntity2 = convPostRepository.save(
                createConvPostEntityBuilder()
                        .category(testConvCategory)
                        .authMember(testSiteMember)
                        .createMember(testSiteMember)
                        .isDeleted(true)
                        .build()
        );

        // when
        Optional<ConvPostEntity> found = convPostRepository.findByUlidAndIsDeletedFalse(convPostEntity1.getUlid());
        Optional<ConvPostEntity> notFound = convPostRepository.findByUlidAndIsDeletedFalse(convPostEntity2.getUlid());

        // then
        assertThat(found).isPresent();
        assertThat(found.orElseThrow().getUlid()).isEqualTo(convPostEntity1.getUlid());
        assertThat(notFound).isEmpty();
    }

    @Test
    @DisplayName("제목+본문 검색어로 게시글 목록 찾기")
    void searchByTitleOrContentTest() {
        // given
        convPostRepository.save(
                createConvPostEntityBuilder()
                        .category(testConvCategory)
                        .authMember(testSiteMember)
                        .createMember(testSiteMember)
                        .build());
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<ConvPostEntity> result1 = convPostRepository.searchByTitleOrContent("물",pageable);
        Page<ConvPostEntity> result2 = convPostRepository.searchByTitleOrContent("this",pageable);
        Page<ConvPostEntity> result3 = convPostRepository.searchByTitleOrContent("erd",pageable);

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
        ConvPostEntity convPostEntity = convPostRepository.save(
                createConvPostEntityBuilder()
                        .category(testConvCategory)
                        .authMember(testSiteMember)
                        .createMember(testSiteMember)
                        .viewCount(10L)
                        .build()
        );

        // when
        int updatedCount = convPostRepository.updateViewCount(convPostEntity.getUlid(),20L);

        // then
        assertThat(updatedCount).isEqualTo(1);
        entityManager.clear();
        ConvPostEntity result = convPostRepository.findByUlid(convPostEntity.getUlid()).orElseThrow();
        assertThat(result.getViewCount()).isEqualTo(20L);
    }

    @Test
    @DisplayName("현재 조회수보다 업데이트할 조회수가 더 작거나 같으면 조회수 수정 실패")
    void updateViewCountFailTest() {
        // given
        ConvPostEntity convPostEntity = convPostRepository.save(
                createConvPostEntityBuilder()
                        .category(testConvCategory)
                        .authMember(testSiteMember)
                        .createMember(testSiteMember)
                        .viewCount(10L)
                        .build()
        );

        // when
        int updatedCount = convPostRepository.updateViewCount(convPostEntity.getUlid(),5L);

        // then
        assertThat(updatedCount).isEqualTo(0);
        entityManager.clear();
        ConvPostEntity result = convPostRepository.findByUlid(convPostEntity.getUlid()).orElseThrow();
        assertThat(result.getViewCount()).isEqualTo(10L);
    }

}