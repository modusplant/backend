package kr.modusplant.domains.conversation.app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.modusplant.domains.common.domain.service.MediaContentService;
import kr.modusplant.domains.conversation.common.util.entity.ConvPostEntityTestUtils;
import kr.modusplant.domains.conversation.common.util.http.request.ConvPostRequestTestUtils;
import kr.modusplant.domains.group.common.util.entity.PlantGroupEntityTestUtils;
import kr.modusplant.domains.group.persistence.entity.PlantGroupEntity;
import kr.modusplant.domains.group.persistence.repository.PlantGroupRepository;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.domains.conversation.app.http.request.ConvPostRequest;
import kr.modusplant.domains.conversation.app.http.response.ConvPostResponse;
import kr.modusplant.domains.conversation.app.service.ConvPostApplicationService;
import kr.modusplant.domains.conversation.persistence.entity.ConvPostEntity;
import kr.modusplant.domains.conversation.persistence.repository.ConvPostRepository;
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
class ConvPostApplicationServiceTest implements SiteMemberEntityTestUtils, PlantGroupEntityTestUtils, ConvPostRequestTestUtils, ConvPostEntityTestUtils {
    @Autowired
    private ConvPostApplicationService convPostApplicationService;

    @Autowired
    private SiteMemberRepository siteMemberRepository;

    @Autowired
    private PlantGroupRepository plantGroupRepository;

    @Autowired
    private ConvPostRepository convPostRepository;

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
        ConvPostRequest convPostRequest1 = requestAllTypes;
        ConvPostRequest convPostRequest2 = requestAllTypes;
        ConvPostRequest convPostRequest3 = requestAllTypes;
        convPostApplicationService.insert(convPostRequest1,memberUuid);
        convPostApplicationService.insert(convPostRequest2,memberUuid);
        convPostApplicationService.insert(convPostRequest3,memberUuid);

        // when
        Pageable pageable = PageRequest.of(0, 2);
        Page<ConvPostResponse> result = convPostApplicationService.getAll(pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(2);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(2);
        List<ConvPostResponse> posts = result.getContent();
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
        ConvPostRequest convPostRequest1 = requestAllTypes;
        ConvPostRequest convPostRequest2 = requestAllTypes;
        ConvPostRequest convPostRequest3 = requestBasicTypes;
        convPostApplicationService.insert(convPostRequest1,memberUuid);
        convPostApplicationService.insert(convPostRequest2,memberUuid2);
        convPostApplicationService.insert(convPostRequest3,memberUuid);

        // when
        Pageable pageable = PageRequest.of(0, 2);
        Page<ConvPostResponse> result = convPostApplicationService.getByMemberUuid(memberUuid,pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(1);
        List<ConvPostResponse> posts = result.getContent();
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
        ConvPostRequest convPostRequest1 = requestAllTypes;
        ConvPostRequest convPostRequest2 = requestAllTypes;
        ConvPostRequest convPostRequest3 = requestBasicTypes;
        convPostApplicationService.insert(convPostRequest1,memberUuid);
        convPostApplicationService.insert(convPostRequest2,memberUuid2);
        convPostApplicationService.insert(convPostRequest3,memberUuid);

        // when
        Pageable pageable = PageRequest.of(0, 2);
        Page<ConvPostResponse> result = convPostApplicationService.getByGroupOrder(groupOrder,pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(1);
        List<ConvPostResponse> posts = result.getContent();
        assertThat(posts.get(0).getCreatedAt()).isAfterOrEqualTo(posts.get(1).getCreatedAt());
    }

    @Test
    @DisplayName("제목+본문 검색어로 게시글 목록 조회하기")
    void searchByKeywordTest() throws IOException {
        // given
        PlantGroupEntity group = createOtherGroupEntity();
        plantGroupRepository.save(group);
        group.getOrder();
        ConvPostRequest convPostRequest1 = requestAllTypes;
        ConvPostRequest convPostRequest2 = requestBasicTypes;
        convPostApplicationService.insert(convPostRequest1,memberUuid);
        convPostApplicationService.insert(convPostRequest2,memberUuid);
        Pageable pageable = PageRequest.of(0, 10);

        // when
        String keyword1 = "기르기";
        String keyword2 = "test";
        Page<ConvPostResponse> result1 = convPostApplicationService.searchByKeyword(keyword1,pageable);
        Page<ConvPostResponse> result2 = convPostApplicationService.searchByKeyword(keyword2,pageable);

        assertThat(result1.getTotalElements()).isEqualTo(1);
        assertThat(result2.getTotalElements()).isEqualTo(2);
        ConvPostResponse post = result1.getContent().get(0);
        assertThat(post.getTitle()).isEqualTo(convPostRequest2.title());
        assertThat(post.getContent().get(1).has("data")).isEqualTo(true);
    }


    @Test
    @DisplayName("특정 팁 게시글 조회하기")
    void getByUlidTest() throws IOException {
        // given
        SiteMemberEntity siteMember = siteMemberRepository.findByUuid(memberUuid).orElseThrow();
        ConvPostRequest convPostRequest = requestAllTypes;
        PlantGroupEntity plantGroupEntity = plantGroupRepository.findByOrder(convPostRequest.groupOrder()).orElseThrow();
        ConvPostEntity convPostEntity = ConvPostEntity.builder()
                .group(plantGroupEntity)
                .authMember(siteMember)
                .createMember(siteMember)
                .title(convPostRequest.title())
                .content(mediaContentService.saveFilesAndGenerateContentJson(convPostRequest.content()))
                .build();
        convPostRepository.save(convPostEntity);

        // when
        Optional<ConvPostResponse> result = convPostApplicationService.getByUlid(convPostEntity.getUlid());

        assertThat(result).isPresent();
        ConvPostResponse response = result.get();
        assertThat(response.getNickname()).isEqualTo(siteMember.getNickname());
        assertThat(response.getCategory()).isEqualTo(plantGroupEntity.getCategory());
        assertThat(response.getTitle()).isEqualTo(convPostRequest.title());
    }

    @Test
    @DisplayName("특정 팁 게시글 수정하기")
    void updateTest() throws IOException {
        // given
        PlantGroupEntity group = createOtherGroupEntity();
        plantGroupRepository.save(group);
        SiteMemberEntity siteMember = siteMemberRepository.findByUuid(memberUuid).orElseThrow();
        ConvPostRequest convPostRequest = requestAllTypes;
        PlantGroupEntity plantGroupEntity = plantGroupRepository.findByOrder(convPostRequest.groupOrder()).orElseThrow();
        ConvPostEntity convPostEntity = ConvPostEntity.builder()
                .group(plantGroupEntity)
                .authMember(siteMember)
                .createMember(siteMember)
                .title(convPostRequest.title())
                .content(mediaContentService.saveFilesAndGenerateContentJson(convPostRequest.content()))
                .build();
        convPostRepository.save(convPostEntity);

        // when
        ConvPostRequest convPostUpdateRequest = requestBasicTypes;
        convPostApplicationService.update(convPostUpdateRequest,convPostEntity.getUlid(),memberUuid);

        // then
        ConvPostEntity result = convPostRepository.findByUlid(convPostEntity.getUlid()).orElseThrow();
        assertThat(result.getContent().get(2).get("filename").asText()).isEqualTo(convPostUpdateRequest.content().get(2).getOriginalFilename());
    }

    @Test
    @DisplayName("특정 팁 게시글 삭제하기")
    void removeByUlidTest() throws IOException {
        // given
        SiteMemberEntity siteMember = siteMemberRepository.findByUuid(memberUuid).orElseThrow();
        ConvPostRequest convPostRequest = requestAllTypes;
        PlantGroupEntity plantGroupEntity = plantGroupRepository.findByOrder(convPostRequest.groupOrder()).orElseThrow();
        ConvPostEntity convPostEntity = ConvPostEntity.builder()
                .group(plantGroupEntity)
                .authMember(siteMember)
                .createMember(siteMember)
                .title(convPostRequest.title())
                .content(mediaContentService.saveFilesAndGenerateContentJson(convPostRequest.content()))
                .build();
        convPostRepository.save(convPostEntity);

        // when
        convPostApplicationService.removeByUlid(convPostEntity.getUlid(),memberUuid);

        // then
        ConvPostEntity result = convPostRepository.findByUlid(convPostEntity.getUlid()).orElseThrow();
        assertThat(result.getIsDeleted()).isTrue();
    }
}