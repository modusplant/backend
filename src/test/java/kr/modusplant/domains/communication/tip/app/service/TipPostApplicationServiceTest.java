package kr.modusplant.domains.communication.tip.app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.modusplant.domains.common.domain.service.MediaContentService;
import kr.modusplant.domains.communication.tip.common.util.entity.TipPostEntityTestUtils;
import kr.modusplant.domains.communication.tip.common.util.http.request.TipPostRequestTestUtils;
import kr.modusplant.domains.group.common.util.entity.PlantGroupEntityTestUtils;
import kr.modusplant.domains.group.persistence.entity.PlantGroupEntity;
import kr.modusplant.domains.group.persistence.repository.PlantGroupRepository;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.domains.communication.tip.app.http.request.TipPostInsertRequest;
import kr.modusplant.domains.communication.tip.app.http.request.TipPostUpdateRequest;
import kr.modusplant.domains.communication.tip.app.http.response.TipPostResponse;
import kr.modusplant.domains.communication.tip.persistence.entity.TipPostEntity;
import kr.modusplant.domains.communication.tip.persistence.repository.TipPostRepository;
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
class TipPostApplicationServiceTest implements SiteMemberEntityTestUtils, PlantGroupEntityTestUtils, TipPostRequestTestUtils, TipPostEntityTestUtils {
    @Autowired
    private TipPostApplicationService tipPostApplicationService;

    @Autowired
    private SiteMemberRepository siteMemberRepository;

    @Autowired
    private PlantGroupRepository plantGroupRepository;

    @Autowired
    private TipPostRepository tipPostRepository;

    @Autowired
    private MediaContentService mediaContentService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID memberUuid;
    private Integer groupOrder;

    @BeforeEach
    void setUp() {
        SiteMemberEntity member = createMemberBasicUserEntity();
        siteMemberRepository.save(member);
        memberUuid = member.getUuid();

        PlantGroupEntity group = createPlantGroupEntity();
        plantGroupRepository.save(group);
        groupOrder = group.getOrder();
    }

    @Test
    @DisplayName("전체 팁 게시글 목록 조회하기")
    void getAllTest() throws IOException {
        // given
        TipPostInsertRequest tipPostInsertRequest1 = requestAllTypes;
        TipPostInsertRequest tipPostInsertRequest2 = requestAllTypes;
        TipPostInsertRequest tipPostInsertRequest3 = requestAllTypes;
        tipPostApplicationService.insert(tipPostInsertRequest1,memberUuid);
        tipPostApplicationService.insert(tipPostInsertRequest2,memberUuid);
        tipPostApplicationService.insert(tipPostInsertRequest3,memberUuid);

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
        assertThat(posts.get(0).getGroupOrder()).isEqualTo(groupOrder);
    }


    @Test
    @DisplayName("사이트 회원별 팁 게시글 목록 조회하기")
    void getByMemberUuidTest() throws IOException {
        // given
        SiteMemberEntity member = createMemberKakaoUserEntity();
        siteMemberRepository.save(member);
        UUID memberUuid2 = member.getUuid();
        PlantGroupEntity group = createOtherGroupEntity();
        plantGroupRepository.save(group);
        Integer groupOrder2 = group.getOrder();
        TipPostInsertRequest tipPostInsertRequest1 = requestAllTypes;
        TipPostInsertRequest tipPostInsertRequest2 = requestAllTypes;
        TipPostInsertRequest tipPostInsertRequest3 = requestBasicTypes;
        tipPostApplicationService.insert(tipPostInsertRequest1,memberUuid);
        tipPostApplicationService.insert(tipPostInsertRequest2,memberUuid2);
        tipPostApplicationService.insert(tipPostInsertRequest3,memberUuid);

        // when
        Pageable pageable = PageRequest.of(0, 2);
        Page<TipPostResponse> result = tipPostApplicationService.getByMemberUuid(memberUuid,pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(1);
        List<TipPostResponse> posts = result.getContent();
        assertThat(posts.get(0).getCreatedAt()).isAfterOrEqualTo(posts.get(1).getCreatedAt());
        assertThat(posts.get(0).getGroupOrder()).isEqualTo(groupOrder2);
        assertThat(posts.get(1).getGroupOrder()).isEqualTo(groupOrder);
    }

    @Test
    @DisplayName("식물 그룹별 팁 게시글 목록 조회하기")
    void getByGroupOrderTest() throws IOException {
        // given
        SiteMemberEntity member = createMemberKakaoUserEntity();
        siteMemberRepository.save(member);
        UUID memberUuid2 = member.getUuid();
        PlantGroupEntity group = createOtherGroupEntity();
        plantGroupRepository.save(group);
        group.getOrder();
        TipPostInsertRequest tipPostInsertRequest1 = requestAllTypes;
        TipPostInsertRequest tipPostInsertRequest2 = requestAllTypes;
        TipPostInsertRequest tipPostInsertRequest3 = requestBasicTypes;
        tipPostApplicationService.insert(tipPostInsertRequest1,memberUuid);
        tipPostApplicationService.insert(tipPostInsertRequest2,memberUuid2);
        tipPostApplicationService.insert(tipPostInsertRequest3,memberUuid);

        // when
        Pageable pageable = PageRequest.of(0, 2);
        Page<TipPostResponse> result = tipPostApplicationService.getByGroupOrder(groupOrder,pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(1);
        List<TipPostResponse> posts = result.getContent();
        assertThat(posts.get(0).getCreatedAt()).isAfterOrEqualTo(posts.get(1).getCreatedAt());
    }

    @Test
    @DisplayName("제목+본문 검색어로 게시글 목록 조회하기")
    void searchByKeywordTest() throws IOException {
        // given
        PlantGroupEntity group = createOtherGroupEntity();
        plantGroupRepository.save(group);
        group.getOrder();
        TipPostInsertRequest tipPostInsertRequest1 = requestAllTypes;
        TipPostInsertRequest tipPostInsertRequest2 = requestBasicTypes;
        tipPostApplicationService.insert(tipPostInsertRequest1,memberUuid);
        tipPostApplicationService.insert(tipPostInsertRequest2,memberUuid);
        Pageable pageable = PageRequest.of(0, 10);

        // when
        String keyword1 = "기르기";
        String keyword2 = "test";
        Page<TipPostResponse> result1 = tipPostApplicationService.searchByKeyword(keyword1,pageable);
        Page<TipPostResponse> result2 = tipPostApplicationService.searchByKeyword(keyword2,pageable);

        assertThat(result1.getTotalElements()).isEqualTo(1);
        assertThat(result2.getTotalElements()).isEqualTo(2);
        TipPostResponse post = result1.getContent().get(0);
        assertThat(post.getTitle()).isEqualTo(tipPostInsertRequest2.title());
        assertThat(post.getContent().get(1).has("data")).isEqualTo(true);
    }


    @Test
    @DisplayName("특정 팁 게시글 조회하기")
    void getByUlidTest() throws IOException {
        // given
        SiteMemberEntity siteMember = siteMemberRepository.findByUuid(memberUuid).orElseThrow();
        TipPostInsertRequest tipPostInsertRequest = requestAllTypes;
        PlantGroupEntity plantGroupEntity = plantGroupRepository.findByOrder(tipPostInsertRequest.groupOrder()).orElseThrow();
        TipPostEntity tipPostEntity = TipPostEntity.builder()
                .group(plantGroupEntity)
                .authMember(siteMember)
                .createMember(siteMember)
                .title(tipPostInsertRequest.title())
                .content(mediaContentService.saveFilesAndGenerateContentJson(tipPostInsertRequest.content()))
                .build();
        tipPostRepository.save(tipPostEntity);

        // when
        Optional<TipPostResponse> result = tipPostApplicationService.getByUlid(tipPostEntity.getUlid());

        assertThat(result).isPresent();
        TipPostResponse response = result.get();
        assertThat(response.getNickname()).isEqualTo(siteMember.getNickname());
        assertThat(response.getCategory()).isEqualTo(plantGroupEntity.getCategory());
        assertThat(response.getTitle()).isEqualTo(tipPostInsertRequest.title());
    }

    @Test
    @DisplayName("특정 팁 게시글 수정하기")
    void updateTest() throws IOException {
        // given
        PlantGroupEntity group = createOtherGroupEntity();
        plantGroupRepository.save(group);
        SiteMemberEntity siteMember = siteMemberRepository.findByUuid(memberUuid).orElseThrow();
        TipPostInsertRequest tipPostInsertRequest = requestAllTypes;
        PlantGroupEntity plantGroupEntity = plantGroupRepository.findByOrder(tipPostInsertRequest.groupOrder()).orElseThrow();
        TipPostEntity tipPostEntity = TipPostEntity.builder()
                .group(plantGroupEntity)
                .authMember(siteMember)
                .createMember(siteMember)
                .title(tipPostInsertRequest.title())
                .content(mediaContentService.saveFilesAndGenerateContentJson(tipPostInsertRequest.content()))
                .build();
        tipPostRepository.save(tipPostEntity);

        // when
        TipPostUpdateRequest tipPostUpdateRequest = new TipPostUpdateRequest(
                tipPostEntity.getUlid(),
                2,
                "유용한 식물 기르기 팁",
                basicMediaFiles,
                basicMediaFilesOrder
        );
        tipPostApplicationService.update(tipPostUpdateRequest,memberUuid);

        // then
        TipPostEntity result = tipPostRepository.findByUlid(tipPostEntity.getUlid()).orElseThrow();
        assertThat(result.getContent().get(2).get("filename").asText()).isEqualTo(tipPostUpdateRequest.content().get(2).getOriginalFilename());
    }

    @Test
    @DisplayName("특정 팁 게시글 삭제하기")
    void removeByUlidTest() throws IOException {
        // given
        SiteMemberEntity siteMember = siteMemberRepository.findByUuid(memberUuid).orElseThrow();
        TipPostInsertRequest tipPostInsertRequest = requestAllTypes;
        PlantGroupEntity plantGroupEntity = plantGroupRepository.findByOrder(tipPostInsertRequest.groupOrder()).orElseThrow();
        TipPostEntity tipPostEntity = TipPostEntity.builder()
                .group(plantGroupEntity)
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