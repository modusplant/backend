package kr.modusplant.domains.communication.qna.app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.modusplant.domains.common.domain.service.MediaContentService;
import kr.modusplant.domains.communication.qna.common.util.entity.QnaCategoryEntityTestUtils;
import kr.modusplant.domains.communication.qna.common.util.entity.QnaPostEntityTestUtils;
import kr.modusplant.domains.communication.qna.common.util.app.http.request.QnaPostRequestTestUtils;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaCategoryEntity;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaCategoryRepository;
import kr.modusplant.domains.communication.tip.persistence.entity.TipCategoryEntity;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.domains.communication.qna.app.http.request.QnaPostInsertRequest;
import kr.modusplant.domains.communication.qna.app.http.request.QnaPostUpdateRequest;
import kr.modusplant.domains.communication.qna.app.http.response.QnaPostResponse;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaPostEntity;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaPostRepository;
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
class QnaPostApplicationServiceTest implements SiteMemberEntityTestUtils, QnaCategoryEntityTestUtils, QnaPostRequestTestUtils, QnaPostEntityTestUtils {
    @Autowired
    private QnaPostApplicationService qnaPostApplicationService;

    @Autowired
    private SiteMemberRepository siteMemberRepository;

    @Autowired
    private QnaCategoryRepository qnaCategoryRepository;

    @Autowired
    private QnaPostRepository qnaPostRepository;

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

        QnaCategoryEntity group = testQnaCategoryEntity;
        qnaCategoryRepository.save(group);
        groupOrder = group.getOrder();
    }

    @Test
    @DisplayName("전체 팁 게시글 목록 조회하기")
    void getAllTest() throws IOException {
        // given
        QnaPostInsertRequest qnaPostInsertRequest1 = requestAllTypes;
        QnaPostInsertRequest qnaPostInsertRequest2 = requestAllTypes;
        QnaPostInsertRequest qnaPostInsertRequest3 = requestAllTypes;
        qnaPostApplicationService.insert(qnaPostInsertRequest1,memberUuid);
        qnaPostApplicationService.insert(qnaPostInsertRequest2,memberUuid);
        qnaPostApplicationService.insert(qnaPostInsertRequest3,memberUuid);

        // when
        Pageable pageable = PageRequest.of(0, 2);
        Page<QnaPostResponse> result = qnaPostApplicationService.getAll(pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(2);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(2);
        List<QnaPostResponse> posts = result.getContent();
        assertThat(posts.get(0).getCreatedAt()).isAfterOrEqualTo(posts.get(1).getCreatedAt());
        assertThat(posts.get(0).getGroupOrder()).isEqualTo(groupOrder);
    }


    @Test
    @DisplayName("사이트 회원별 팁 게시글 목록 조회하기")
    void getByMemberUuidTest() throws IOException {
        // given
        SiteMemberEntity member2 = createMemberKakaoUserEntity();
        siteMemberRepository.save(member2);
        UUID memberUuid2 = member2.getUuid();
        QnaPostInsertRequest qnaPostInsertRequest1 = requestAllTypes;
        QnaPostInsertRequest qnaPostInsertRequest2 = requestAllTypes;
        QnaPostInsertRequest qnaPostInsertRequest3 = requestAllTypes;
        qnaPostApplicationService.insert(qnaPostInsertRequest1,memberUuid);
        qnaPostApplicationService.insert(qnaPostInsertRequest2,memberUuid2);
        qnaPostApplicationService.insert(qnaPostInsertRequest3,memberUuid);

        // when
        Pageable pageable = PageRequest.of(0, 2);
        Page<QnaPostResponse> result = qnaPostApplicationService.getByMemberUuid(memberUuid,pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(1);
        List<QnaPostResponse> posts = result.getContent();
        assertThat(posts.get(0).getCreatedAt()).isAfterOrEqualTo(posts.get(1).getCreatedAt());
    }

    @Test
    @DisplayName("식물 그룹별 팁 게시글 목록 조회하기")
    void getByGroupOrderTest() throws IOException {
        // given
        qnaCategoryRepository.save(QnaCategoryEntity.builder()
                        .order(2).category("기타").build());
        QnaPostInsertRequest qnaPostInsertRequest1 = requestAllTypes;
        QnaPostInsertRequest qnaPostInsertRequest2 = requestAllTypes;
        QnaPostInsertRequest qnaPostInsertRequest3 = requestBasicTypes;
        qnaPostApplicationService.insert(qnaPostInsertRequest1,memberUuid);
        qnaPostApplicationService.insert(qnaPostInsertRequest2,memberUuid);
        qnaPostApplicationService.insert(qnaPostInsertRequest3,memberUuid);

        // when
        Pageable pageable = PageRequest.of(0, 2);
        Page<QnaPostResponse> result = qnaPostApplicationService.getByGroupOrder(groupOrder,pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(1);
        List<QnaPostResponse> posts = result.getContent();
        assertThat(posts.get(0).getCreatedAt()).isAfterOrEqualTo(posts.get(1).getCreatedAt());
    }

    @Test
    @DisplayName("제목+본문 검색어로 게시글 목록 조회하기")
    void searchByKeywordTest() throws IOException {
        // given
        qnaCategoryRepository.save(QnaCategoryEntity.builder()
                .order(2).category("기타").build());
        QnaPostInsertRequest qnaPostInsertRequest1 = requestAllTypes;
        QnaPostInsertRequest qnaPostInsertRequest2 = requestBasicTypes;
        qnaPostApplicationService.insert(qnaPostInsertRequest1,memberUuid);
        qnaPostApplicationService.insert(qnaPostInsertRequest2,memberUuid);
        Pageable pageable = PageRequest.of(0, 10);

        // when
        String keyword1 = "기르기";
        String keyword2 = "test";
        Page<QnaPostResponse> result1 = qnaPostApplicationService.searchByKeyword(keyword1,pageable);
        Page<QnaPostResponse> result2 = qnaPostApplicationService.searchByKeyword(keyword2,pageable);

        assertThat(result1.getTotalElements()).isEqualTo(1);
        assertThat(result2.getTotalElements()).isEqualTo(2);
        QnaPostResponse post = result1.getContent().get(0);
        assertThat(post.getTitle()).isEqualTo(qnaPostInsertRequest2.title());
        assertThat(post.getContent().get(1).has("data")).isEqualTo(true);
    }


    @Test
    @DisplayName("특정 팁 게시글 조회하기")
    void getByUlidTest() throws IOException {
        // given
        SiteMemberEntity siteMember = siteMemberRepository.findByUuid(memberUuid).orElseThrow();
        QnaPostInsertRequest qnaPostInsertRequest = requestAllTypes;
        QnaCategoryEntity qnaCategoryEntity = qnaCategoryRepository.findByOrder(qnaPostInsertRequest.groupOrder()).orElseThrow();
        QnaPostEntity qnaPostEntity = QnaPostEntity.builder()
                .group(qnaCategoryEntity)
                .authMember(siteMember)
                .createMember(siteMember)
                .title(qnaPostInsertRequest.title())
                .content(mediaContentService.saveFilesAndGenerateContentJson(qnaPostInsertRequest.content()))
                .build();
        qnaPostRepository.save(qnaPostEntity);

        // when
        Optional<QnaPostResponse> result = qnaPostApplicationService.getByUlid(qnaPostEntity.getUlid());

        assertThat(result).isPresent();
        QnaPostResponse response = result.get();
        assertThat(response.getNickname()).isEqualTo(siteMember.getNickname());
        assertThat(response.getCategory()).isEqualTo(qnaCategoryEntity.getCategory());
        assertThat(response.getTitle()).isEqualTo(qnaPostInsertRequest.title());
    }

    @Test
    @DisplayName("특정 팁 게시글 수정하기")
    void updateTest() throws IOException {
        // given
        SiteMemberEntity siteMember = siteMemberRepository.findByUuid(memberUuid).orElseThrow();
        QnaPostInsertRequest qnaPostInsertRequest = requestAllTypes;
        QnaCategoryEntity qnaCategoryEntity = qnaCategoryRepository.findByOrder(qnaPostInsertRequest.groupOrder()).orElseThrow();
        QnaPostEntity qnaPostEntity = QnaPostEntity.builder()
                .group(qnaCategoryEntity)
                .authMember(siteMember)
                .createMember(siteMember)
                .title(qnaPostInsertRequest.title())
                .content(mediaContentService.saveFilesAndGenerateContentJson(qnaPostInsertRequest.content()))
                .build();
        qnaPostRepository.save(qnaPostEntity);

        // when
        QnaPostUpdateRequest qnaPostUpdateRequest = new QnaPostUpdateRequest(
                qnaPostEntity.getUlid(),
                1,
                "식물 기르기 팁",
                basicMediaFiles,
                basicMediaFilesOrder
        );
        qnaPostApplicationService.update(qnaPostUpdateRequest,memberUuid);

        // then
        QnaPostEntity result = qnaPostRepository.findByUlid(qnaPostEntity.getUlid()).orElseThrow();
        assertThat(result.getContent().get(2).get("filename").asText()).isEqualTo(qnaPostUpdateRequest.content().get(2).getOriginalFilename());
    }

    @Test
    @DisplayName("특정 팁 게시글 삭제하기")
    void removeByUlidTest() throws IOException {
        // given
        SiteMemberEntity siteMember = siteMemberRepository.findByUuid(memberUuid).orElseThrow();
        QnaPostInsertRequest qnaPostInsertRequest = requestAllTypes;
        QnaCategoryEntity qnaCategoryEntity = qnaCategoryRepository.findByOrder(qnaPostInsertRequest.groupOrder()).orElseThrow();
        QnaPostEntity qnaPostEntity = QnaPostEntity.builder()
                .group(qnaCategoryEntity)
                .authMember(siteMember)
                .createMember(siteMember)
                .title(qnaPostInsertRequest.title())
                .content(mediaContentService.saveFilesAndGenerateContentJson(qnaPostInsertRequest.content()))
                .build();
        qnaPostRepository.save(qnaPostEntity);

        // when
        qnaPostApplicationService.removeByUlid(qnaPostEntity.getUlid(),memberUuid);

        // then
        QnaPostEntity result = qnaPostRepository.findByUlid(qnaPostEntity.getUlid()).orElseThrow();
        assertThat(result.getIsDeleted()).isTrue();
    }
}