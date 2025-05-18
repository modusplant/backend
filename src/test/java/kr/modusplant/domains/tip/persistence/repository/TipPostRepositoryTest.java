package kr.modusplant.domains.tip.persistence.repository;

import kr.modusplant.domains.group.common.util.entity.PlantGroupEntityTestUtils;
import kr.modusplant.domains.group.persistence.entity.PlantGroupEntity;
import kr.modusplant.domains.group.persistence.repository.PlantGroupRepository;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.domains.tip.common.util.entity.TipPostEntityTestUtils;
import kr.modusplant.domains.tip.persistence.entity.TipPostEntity;
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
import static org.junit.jupiter.api.Assertions.*;

@RepositoryOnlyContext
class TipPostRepositoryTest implements TipPostEntityTestUtils, PlantGroupEntityTestUtils, SiteMemberEntityTestUtils {
    private final TipPostRepository tipPostRepository;
    private final PlantGroupRepository plantGroupRepository;
    private final SiteMemberRepository siteMemberRepository;

    @Autowired
    TipPostRepositoryTest(TipPostRepository tipPostRepository, PlantGroupRepository plantGroupRepository, SiteMemberRepository siteMemberRepository) {
        this.tipPostRepository = tipPostRepository;
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
        TipPostEntity tipPostEntity = createTipPostEntityBuilder()
                .group(testPlantGroup)
                .authMember(testSiteMember)
                .createMember(testSiteMember)
                .build();

        // when
        tipPostRepository.save(tipPostEntity);
        System.out.println(tipPostEntity);

        // then
        assertThat(tipPostRepository.findByUlid(tipPostEntity.getUlid()).orElseThrow()).isEqualTo(tipPostEntity);
    }

    @Test
    @DisplayName("전체 팁 게시글 찾기(최신순)")
    void findAllByOrderByCreatedAtDescTest() {
        // given
        List<TipPostEntity> tipPosts = IntStream.range(0, 10)
                .mapToObj(i -> createTipPostEntityBuilder()
                        .group(testPlantGroup)
                        .authMember(testSiteMember)
                        .createMember(testSiteMember)
                        .build()
                ).collect(Collectors.toList());
        tipPostRepository.saveAll(tipPosts);

        Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createdAt"));

        // when
        Page<TipPostEntity> result = tipPostRepository.findAllByOrderByCreatedAtDesc(pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(10);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getTotalPages()).isEqualTo(4);

        List<TipPostEntity> content = result.getContent();
        for (int i = 0; i < content.size() - 1; i++) {
            assertThat(content.get(i).getCreatedAt())
                    .isAfterOrEqualTo(content.get(i + 1).getCreatedAt());
        }
    }

    @Test
    @DisplayName("삭제되지 않은 모든 팁 게시글 찾기(최신순)")
    void findAllByIsDeletedFalseOrderByCreatedAtDescTest() {
        // given
        List<TipPostEntity> tipPosts = IntStream.range(0, 5)
                .mapToObj(i -> createTipPostEntityBuilder()
                        .group(testPlantGroup)
                        .authMember(testSiteMember)
                        .createMember(testSiteMember)
                        .build()
                ).collect(Collectors.toList());
        tipPosts.get(0).updateIsDeleted(true);
        tipPostRepository.saveAll(tipPosts);

        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));

        // when
        Page<TipPostEntity> result = tipPostRepository.findAllByIsDeletedFalseOrderByCreatedAtDesc(pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(4); // 삭제된 1건 제외
        assertThat(result.getContent().stream().allMatch(post -> !post.getIsDeleted())).isTrue();

        List<TipPostEntity> content = result.getContent();
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
        List<TipPostEntity> tipPosts = IntStream.range(0, 5)
                .mapToObj(i -> createTipPostEntityBuilder()
                        .group(i % 2 == 0 ? testPlantGroup : testOtherGroup)
                        .authMember(testSiteMember)
                        .createMember(testSiteMember)
                        .build()
                ).collect(Collectors.toList());
        tipPosts.get(0).updateIsDeleted(true);
        tipPostRepository.saveAll(tipPosts);

        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));

        // when
        Page<TipPostEntity> result = tipPostRepository.findByGroupAndIsDeletedFalseOrderByCreatedAtDesc(testPlantGroup, pageable);

        // then
        // i = 0, 2, 4 → testPlantGroup로 생성됨 (0번은 삭제됨)
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent().stream().allMatch(post -> post.getGroup().equals(testPlantGroup) && !post.getIsDeleted())).isTrue();

        List<TipPostEntity> content = result.getContent();
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
        List<TipPostEntity> tipPosts = IntStream.range(0, 5)
                .mapToObj(i -> createTipPostEntityBuilder()
                        .group(testPlantGroup)
                        .authMember(i % 2 == 0 ? testSiteMember : testSiteMember2)
                        .createMember(i % 2 == 0 ? testSiteMember : testSiteMember2)
                        .build()
                ).collect(Collectors.toList());
        tipPosts.get(0).updateIsDeleted(true);
        tipPostRepository.saveAll(tipPosts);

        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));

        // when
        Page<TipPostEntity> result = tipPostRepository.findByAuthMemberAndIsDeletedFalseOrderByCreatedAtDesc(testSiteMember,pageable);

        // then
        // i = 0, 2, 4 → testSiteMember로 생성됨 (0번은 삭제됨)
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent().stream().allMatch(post -> post.getAuthMember().equals(testSiteMember) && !post.getIsDeleted())).isTrue();

        List<TipPostEntity> content = result.getContent();
        for (int i = 0; i < content.size() - 1; i++) {
            assertThat(content.get(i).getCreatedAt())
                    .isAfterOrEqualTo(content.get(i + 1).getCreatedAt());
        }
    }


    @Test
    @DisplayName("createdAt으로 회원 찾기")
    void findByCreatedAtTest() {
        // when
        TipPostEntity tipPostEntity = tipPostRepository.save(
                createTipPostEntityBuilder()
                        .group(testPlantGroup)
                        .authMember(testSiteMember)
                        .createMember(testSiteMember)
                        .build()
        );

        // then
        assertThat(tipPostRepository.findByCreatedAt(tipPostEntity.getCreatedAt()).getFirst()).isEqualTo(tipPostEntity);
    }

    @Test
    @DisplayName("updatedAt으로 회원 찾기")
    void findByUpdatedAtTest() {
        // when
        TipPostEntity tipPostEntity = tipPostRepository.save(
                createTipPostEntityBuilder()
                        .group(testPlantGroup)
                        .authMember(testSiteMember)
                        .createMember(testSiteMember)
                        .build()
        );

        // then
        assertThat(tipPostRepository.findByUpdatedAt(tipPostEntity.getUpdatedAt()).getFirst()).isEqualTo(tipPostEntity);
    }

    @Test
    @DisplayName("ULID로 팁 게시글 삭제")
    void deleteByUlidTest() {
        // given
        TipPostEntity tipPostEntity = tipPostRepository.save(
                createTipPostEntityBuilder()
                        .group(testPlantGroup)
                        .authMember(testSiteMember)
                        .createMember(testSiteMember)
                        .build()
        );
        String ulid = tipPostEntity.getUlid();

        // when
        tipPostRepository.deleteByUlid(ulid);

        // then
        assertThat(tipPostRepository.findByUlid(ulid)).isEmpty();
    }

    @Test
    @DisplayName("ULID로 팁 게시글 확인")
    void existsByUlidTest() {
        // when
        TipPostEntity tipPostEntity = tipPostRepository.save(
                createTipPostEntityBuilder()
                        .group(testPlantGroup)
                        .authMember(testSiteMember)
                        .createMember(testSiteMember)
                        .build()
        );

        // then
        assertThat(tipPostRepository.existsByUlid(tipPostEntity.getUlid())).isEqualTo(true);
    }

}