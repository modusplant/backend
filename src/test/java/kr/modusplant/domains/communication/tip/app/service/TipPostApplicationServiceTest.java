package kr.modusplant.domains.communication.tip.app.service;

import kr.modusplant.domains.common.domain.service.MediaContentService;
import kr.modusplant.domains.communication.tip.app.http.request.TipPostInsertRequest;
import kr.modusplant.domains.communication.tip.app.http.request.TipPostUpdateRequest;
import kr.modusplant.domains.communication.tip.app.http.response.TipPostResponse;
import kr.modusplant.domains.communication.tip.common.util.app.http.request.TipPostRequestTestUtils;
import kr.modusplant.domains.communication.tip.common.util.entity.TipCategoryEntityTestUtils;
import kr.modusplant.domains.communication.tip.common.util.entity.TipPostEntityTestUtils;
import kr.modusplant.domains.communication.tip.persistence.entity.TipCategoryEntity;
import kr.modusplant.domains.communication.tip.persistence.entity.TipPostEntity;
import kr.modusplant.domains.communication.tip.persistence.repository.TipCategoryRepository;
import kr.modusplant.domains.communication.tip.persistence.repository.TipPostRepository;
import kr.modusplant.domains.communication.tip.persistence.repository.TipPostViewCountRedisRepository;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class TipPostApplicationServiceTest implements SiteMemberEntityTestUtils, TipCategoryEntityTestUtils, TipPostRequestTestUtils, TipPostEntityTestUtils {
    @Autowired
    private TipPostApplicationService tipPostApplicationService;

    @Autowired
    private SiteMemberRepository siteMemberRepository;

    @Autowired
    private TipCategoryRepository tipCategoryRepository;

    @Autowired
    private TipPostRepository tipPostRepository;

    @Autowired
    private MediaContentService mediaContentService;

    @Autowired
    private TipPostViewCountRedisRepository tipPostViewCountRedisRepository;

    private UUID memberUuid;
    private UUID categoryUuid;
    private TipPostInsertRequest testRequestAllTypes;
    private TipPostInsertRequest testRequestBasicTypes;

    @BeforeEach
    void setUp() {
        SiteMemberEntity member = createMemberBasicUserEntity();
        siteMemberRepository.save(member);
        memberUuid = member.getUuid();

        TipCategoryEntity tipCategory = createTestTipCategoryEntity();
        tipCategoryRepository.save(tipCategory);
        categoryUuid = tipCategory.getUuid();

        testRequestAllTypes = new TipPostInsertRequest(categoryUuid, requestAllTypes.title(), requestAllTypes.content(), requestAllTypes.orderInfo());
        testRequestBasicTypes = new TipPostInsertRequest(categoryUuid, requestBasicTypes.title(), requestBasicTypes.content(), requestBasicTypes.orderInfo());
    }

    @Test
    @DisplayName("전체 팁 게시글 목록 조회하기")
    void getAllTest() throws IOException {
        // given
        tipPostApplicationService.insert(testRequestAllTypes, memberUuid);
        tipPostApplicationService.insert(testRequestAllTypes, memberUuid);
        tipPostApplicationService.insert(testRequestAllTypes, memberUuid);

        // when
        Pageable pageable = PageRequest.of(0, 2);
        Page<TipPostResponse> result = tipPostApplicationService.getAll(pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(2);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(2);
        List<TipPostResponse> posts = result.getContent();
        assertThat(posts.get(0).getCreatedAt()).isAfterOrEqualTo(posts.get(1).getCreatedAt());
        assertThat(posts.get(0).getCategoryUuid()).isEqualTo(categoryUuid);
    }


    @Test
    @DisplayName("사이트 회원별 팁 게시글 목록 조회하기")
    void getByMemberUuidTest() throws IOException {
        // given
        SiteMemberEntity member2 = createMemberKakaoUserEntity();
        siteMemberRepository.save(member2);
        UUID memberUuid2 = member2.getUuid();
        tipPostApplicationService.insert(testRequestAllTypes, memberUuid);
        tipPostApplicationService.insert(testRequestAllTypes, memberUuid2);
        tipPostApplicationService.insert(testRequestAllTypes, memberUuid);

        // when
        Pageable pageable = PageRequest.of(0, 2);
        Page<TipPostResponse> result = tipPostApplicationService.getByMemberUuid(memberUuid, pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(1);
        List<TipPostResponse> posts = result.getContent();
        assertThat(posts.get(0).getCreatedAt()).isAfterOrEqualTo(posts.get(1).getCreatedAt());
    }

    @Test
    @DisplayName("항목별 팁 게시글 목록 조회하기")
    void getByCategoryUuidTest() throws IOException {
        // given
        tipPostApplicationService.insert(testRequestAllTypes, memberUuid);
        tipPostApplicationService.insert(testRequestAllTypes, memberUuid);
        tipPostApplicationService.insert(testRequestBasicTypes, memberUuid);

        // when
        Pageable pageable = PageRequest.of(0, 2);
        Page<TipPostResponse> result = tipPostApplicationService.getByCategoryUuid(categoryUuid, pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(2);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(2);
        List<TipPostResponse> posts = result.getContent();
        assertThat(posts.get(0).getCreatedAt()).isAfterOrEqualTo(posts.get(1).getCreatedAt());
    }

    @Test
    @DisplayName("제목+본문 검색어로 게시글 목록 조회하기")
    void searchByKeywordTest() throws IOException {
        // given
        tipCategoryRepository.save(TipCategoryEntity.builder()
                .order(2)
                .category("기타")
                .build());
        TipPostInsertRequest tipPostInsertRequest2 = testRequestBasicTypes;
        tipPostApplicationService.insert(testRequestAllTypes, memberUuid);
        tipPostApplicationService.insert(tipPostInsertRequest2, memberUuid);
        Pageable pageable = PageRequest.of(0, 10);

        // when
        String keyword1 = "기르기";
        String keyword2 = "test";
        Page<TipPostResponse> result1 = tipPostApplicationService.searchByKeyword(keyword1, pageable);
        Page<TipPostResponse> result2 = tipPostApplicationService.searchByKeyword(keyword2, pageable);

        assertThat(result1.getTotalElements()).isEqualTo(1);
        assertThat(result2.getTotalElements()).isEqualTo(2);
        TipPostResponse post = result1.getContent().getFirst();
        assertThat(post.getTitle()).isEqualTo(tipPostInsertRequest2.title());
        assertThat(post.getContent().get(1).has("data")).isEqualTo(true);
    }


    @Test
    @DisplayName("특정 팁 게시글 조회하기")
    void getByUlidTest() throws IOException {
        // given
        SiteMemberEntity siteMember = siteMemberRepository.findByUuid(memberUuid).orElseThrow();
        TipPostInsertRequest tipPostInsertRequest = testRequestAllTypes;
        TipCategoryEntity tipCategoryEntity = tipCategoryRepository.findByUuid(tipPostInsertRequest.categoryUuid()).orElseThrow();
        TipPostEntity tipPostEntity = TipPostEntity.builder()
                .category(tipCategoryEntity)
                .authMember(siteMember)
                .createMember(siteMember)
                .title(tipPostInsertRequest.title())
                .content(mediaContentService.saveFilesAndGenerateContentJson(tipPostInsertRequest.content()))
                .build();
        tipPostRepository.save(tipPostEntity);
        tipPostViewCountRedisRepository.write(tipPostEntity.getUlid(),5L);

        // when
        Optional<TipPostResponse> result = tipPostApplicationService.getByUlid(tipPostEntity.getUlid());

        assertThat(result).isPresent();
        TipPostResponse response = result.get();
        assertThat(response.getNickname()).isEqualTo(siteMember.getNickname());
        assertThat(response.getCategory()).isEqualTo(tipCategoryEntity.getCategory());
        assertThat(response.getTitle()).isEqualTo(tipPostInsertRequest.title());
        assertThat(response.getViewCount()).isEqualTo(5L);
    }

    @Test
    @DisplayName("특정 팁 게시글 수정하기")
    void updateTest() throws IOException {
        // given
        SiteMemberEntity siteMember = siteMemberRepository.findByUuid(memberUuid).orElseThrow();
        TipPostInsertRequest tipPostInsertRequest = testRequestAllTypes;
        TipCategoryEntity tipCategoryEntity = tipCategoryRepository.findByUuid(tipPostInsertRequest.categoryUuid()).orElseThrow();
        TipPostEntity tipPostEntity = TipPostEntity.builder()
                .category(tipCategoryEntity)
                .authMember(siteMember)
                .createMember(siteMember)
                .title(tipPostInsertRequest.title())
                .content(mediaContentService.saveFilesAndGenerateContentJson(tipPostInsertRequest.content()))
                .build();
        tipPostRepository.save(tipPostEntity);

        // when
        TipPostUpdateRequest tipPostUpdateRequest = new TipPostUpdateRequest(
                tipPostEntity.getUlid(),
                tipPostEntity.getCategory().getUuid(),
                "식물 기르기 팁",
                basicMediaFiles,
                basicMediaFilesOrder
        );
        tipPostApplicationService.update(tipPostUpdateRequest, memberUuid);

        // then
        TipPostEntity result = tipPostRepository.findByUlid(tipPostEntity.getUlid()).orElseThrow();
        assertThat(result.getContent().get(2).get("filename").asText()).isEqualTo(tipPostUpdateRequest.content().get(2).getOriginalFilename());
    }

    @Test
    @DisplayName("특정 팁 게시글 삭제하기")
    void removeByUlidTest() throws IOException {
        // given
        SiteMemberEntity siteMember = siteMemberRepository.findByUuid(memberUuid).orElseThrow();
        TipPostInsertRequest tipPostInsertRequest = testRequestAllTypes;
        TipCategoryEntity tipCategoryEntity = tipCategoryRepository.findByUuid(tipPostInsertRequest.categoryUuid()).orElseThrow();
        TipPostEntity tipPostEntity = TipPostEntity.builder()
                .category(tipCategoryEntity)
                .authMember(siteMember)
                .createMember(siteMember)
                .title(tipPostInsertRequest.title())
                .content(mediaContentService.saveFilesAndGenerateContentJson(tipPostInsertRequest.content()))
                .build();
        tipPostRepository.save(tipPostEntity);

        // when
        tipPostApplicationService.removeByUlid(tipPostEntity.getUlid(),memberUuid);

        // then
        TipPostEntity result = tipPostRepository.findByUlid(tipPostEntity.getUlid()).orElseThrow();
        assertThat(result.getIsDeleted()).isTrue();
    }
}