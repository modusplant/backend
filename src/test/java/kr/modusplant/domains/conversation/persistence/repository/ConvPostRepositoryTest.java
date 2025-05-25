package kr.modusplant.domains.conversation.persistence.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kr.modusplant.domains.conversation.common.util.entity.ConvPostEntityTestUtils;
import kr.modusplant.domains.group.common.util.entity.PlantGroupEntityTestUtils;
import kr.modusplant.domains.group.persistence.entity.PlantGroupEntity;
import kr.modusplant.domains.group.persistence.repository.PlantGroupRepository;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.domains.conversation.persistence.entity.ConvPostEntity;
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RepositoryOnlyContext
class ConvPostRepositoryTest implements ConvPostEntityTestUtils, PlantGroupEntityTestUtils, SiteMemberEntityTestUtils {
    private final ConvPostRepository convPostRepository;
    private final PlantGroupRepository plantGroupRepository;
    private final SiteMemberRepository siteMemberRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    ConvPostRepositoryTest(ConvPostRepository convPostRepository, PlantGroupRepository plantGroupRepository, SiteMemberRepository siteMemberRepository) {
        this.convPostRepository = convPostRepository;
        this.plantGroupRepository = plantGroupRepository;
        this.siteMemberRepository = siteMemberRepository;
    }

    private PlantGroupEntity testPlantGroup;
    private SiteMemberEntity testSiteMember;

    @BeforeEach
    void setUp() {
        testPlantGroup = plantGroupRepository.save(createPlantGroupEntity());
        testSiteMember = siteMemberRepository.save(createMemberBasicUserEntity());
    }

    @Test
    @DisplayName("ULID로 팁 게시글 찾기")
    void findByUlidTest() {
        // given
        ConvPostEntity convPostEntity = createConvPostEntityBuilder()
                .group(testPlantGroup)
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
    @DisplayName("전체 팁 게시글 찾기(최신순)")
    void findAllByOrderByCreatedAtDescTest() {
        // given
        List<ConvPostEntity> convPosts = IntStream.range(0, 10)
                .mapToObj(i -> createConvPostEntityBuilder()
                        .group(testPlantGroup)
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
    @DisplayName("삭제되지 않은 모든 팁 게시글 찾기(최신순)")
    void findAllByIsDeletedFalseOrderByCreatedAtDescTest() {
        // given
        List<ConvPostEntity> convPosts = IntStream.range(0, 5)
                .mapToObj(i -> createConvPostEntityBuilder()
                        .group(testPlantGroup)
                        .authMember(testSiteMember)
                        .createMember(testSiteMember)
                        .build()
                ).collect(Collectors.toList());
        convPosts.get(0).updateIsDeleted(true);
        convPostRepository.saveAll(convPosts);

        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));

        // when
        Page<ConvPostEntity> result = convPostRepository.findAllByIsDeletedFalseOrderByCreatedAtDesc(pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(4); // 삭제된 1건 제외
        assertThat(result.getContent().stream().allMatch(post -> !post.getIsDeleted())).isTrue();

        List<ConvPostEntity> content = result.getContent();
        for (int i = 0; i < content.size() - 1; i++) {
            assertThat(content.get(i).getCreatedAt())
                    .isAfterOrEqualTo(content.get(i + 1).getCreatedAt());
        }
    }

    @Test
    @DisplayName("식물 그룹으로 삭제되지 않은 모든 팁 게시글 찾기(최신순)")
    void findByGroupAndIsDeletedFalseOrderByCreatedAtDescTest() {
        // given
        PlantGroupEntity testOtherGroup = plantGroupRepository.save(createOtherGroupEntity());
        List<ConvPostEntity> convPosts = IntStream.range(0, 5)
                .mapToObj(i -> createConvPostEntityBuilder()
                        .group(i % 2 == 0 ? testPlantGroup : testOtherGroup)
                        .authMember(testSiteMember)
                        .createMember(testSiteMember)
                        .build()
                ).collect(Collectors.toList());
        convPosts.get(0).updateIsDeleted(true);
        convPostRepository.saveAll(convPosts);

        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));

        // when
        Page<ConvPostEntity> result = convPostRepository.findByGroupAndIsDeletedFalseOrderByCreatedAtDesc(testPlantGroup, pageable);

        // then
        // i = 0, 2, 4 → testPlantGroup로 생성됨 (0번은 삭제됨)
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent().stream().allMatch(post -> post.getGroup().equals(testPlantGroup) && !post.getIsDeleted())).isTrue();

        List<ConvPostEntity> content = result.getContent();
        for (int i = 0; i < content.size() - 1; i++) {
            assertThat(content.get(i).getCreatedAt())
                    .isAfterOrEqualTo(content.get(i + 1).getCreatedAt());
        }
    }

    @Test
    @DisplayName("인가 회원으로 삭제되지 않은 모든 팁 게시글 찾기(최신순)")
    void findByAuthMemberAndIsDeletedFalseOrderByCreatedAtDescTest() {
        // given
        SiteMemberEntity testSiteMember2 = siteMemberRepository.save(createMemberGoogleUserEntity());
        List<ConvPostEntity> convPosts = IntStream.range(0, 5)
                .mapToObj(i -> createConvPostEntityBuilder()
                        .group(testPlantGroup)
                        .authMember(i % 2 == 0 ? testSiteMember : testSiteMember2)
                        .createMember(i % 2 == 0 ? testSiteMember : testSiteMember2)
                        .build()
                ).collect(Collectors.toList());
        convPosts.get(0).updateIsDeleted(true);
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
                        .group(testPlantGroup)
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
                        .group(testPlantGroup)
                        .authMember(testSiteMember)
                        .createMember(testSiteMember)
                        .build()
        );

        // then
        assertThat(convPostRepository.findByUpdatedAt(convPostEntity.getUpdatedAt()).getFirst()).isEqualTo(convPostEntity);
    }

    @Test
    @DisplayName("ULID로 팁 게시글 삭제")
    void deleteByUlidTest() {
        // given
        ConvPostEntity convPostEntity = convPostRepository.save(
                createConvPostEntityBuilder()
                        .group(testPlantGroup)
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
    @DisplayName("ULID로 팁 게시글 확인")
    void existsByUlidTest() {
        // when
        ConvPostEntity convPostEntity = convPostRepository.save(
                createConvPostEntityBuilder()
                        .group(testPlantGroup)
                        .authMember(testSiteMember)
                        .createMember(testSiteMember)
                        .build()
        );

        // then
        assertThat(convPostRepository.existsByUlid(convPostEntity.getUlid())).isEqualTo(true);
    }

    @Test
    @DisplayName("제목+본문 검색어로 게시글 목록 찾기")
    void searchByTitleOrContentTest() {
        // given
        ConvPostEntity convPostEntity = convPostRepository.save(
                createConvPostEntityBuilder()
                        .group(testPlantGroup)
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
        assertThat(result1.getContent().get(0).getContent().get(1).has("src")).isEqualTo(true);
    }

    @Test
    @DisplayName("현재 조회수보다 업데이트할 조회수가 더 크면 조회수 수정 성공")
    void updateViewCountSuccessTest() {
        // given
        ConvPostEntity convPostEntity = convPostRepository.save(
                createConvPostEntityBuilder()
                        .group(testPlantGroup)
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
                        .group(testPlantGroup)
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