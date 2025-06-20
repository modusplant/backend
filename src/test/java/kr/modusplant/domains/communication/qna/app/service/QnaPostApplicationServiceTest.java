package kr.modusplant.domains.communication.qna.app.service;

import kr.modusplant.domains.common.app.service.MultipartDataProcessor;
import kr.modusplant.domains.common.enums.PostType;
import kr.modusplant.domains.communication.qna.app.http.request.QnaPostInsertRequest;
import kr.modusplant.domains.communication.qna.app.http.request.QnaPostUpdateRequest;
import kr.modusplant.domains.communication.qna.app.http.response.QnaPostResponse;
import kr.modusplant.domains.communication.qna.common.util.app.http.request.QnaPostRequestTestUtils;
import kr.modusplant.domains.communication.qna.common.util.entity.QnaCategoryEntityTestUtils;
import kr.modusplant.domains.communication.qna.common.util.entity.QnaPostEntityTestUtils;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaCategoryEntity;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaPostEntity;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaCategoryRepository;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaPostRepository;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaPostViewCountRedisRepository;
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
    private MultipartDataProcessor multipartDataProcessor;

    @Autowired
    private QnaPostViewCountRedisRepository qnaPostViewCountRedisRepository;

    private UUID memberUuid;
    private UUID categoryUuid;
    private QnaPostInsertRequest testRequestAllTypes;
    private QnaPostInsertRequest testRequestBasicTypes;

    @BeforeEach
    void setUp() {
        SiteMemberEntity member = createMemberBasicUserEntity();
        siteMemberRepository.save(member);
        memberUuid = member.getUuid();

        QnaCategoryEntity qnaCategory = createTestQnaCategoryEntity();
        qnaCategoryRepository.save(qnaCategory);
        categoryUuid = qnaCategory.getUuid();

        testRequestAllTypes = new QnaPostInsertRequest(categoryUuid, requestAllTypes.title(), requestAllTypes.content(), requestAllTypes.orderInfo());
        testRequestBasicTypes = new QnaPostInsertRequest(categoryUuid, requestBasicTypes.title(), requestBasicTypes.content(), requestBasicTypes.orderInfo());
    }

    @Test
    @DisplayName("전체 Q&A 게시글 목록 조회하기")
    void getAllTest() throws IOException {
        // given
        qnaPostApplicationService.insert(testRequestAllTypes, memberUuid);
        qnaPostApplicationService.insert(testRequestAllTypes, memberUuid);
        qnaPostApplicationService.insert(testRequestAllTypes, memberUuid);

        // when
        Pageable pageable = PageRequest.of(0, 2);
        Page<QnaPostResponse> result = qnaPostApplicationService.getAll(pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(2);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(2);
        List<QnaPostResponse> posts = result.getContent();
        assertThat(posts.get(0).createdAt()).isAfterOrEqualTo(posts.get(1).createdAt());
        assertThat(posts.get(0).categoryUuid()).isEqualTo(categoryUuid);
    }


    @Test
    @DisplayName("사이트 회원별 Q&A 게시글 목록 조회하기")
    void getByMemberUuidTest() throws IOException {
        // given
        SiteMemberEntity member2 = createMemberKakaoUserEntity();
        siteMemberRepository.save(member2);
        UUID memberUuid2 = member2.getUuid();
        qnaPostApplicationService.insert(testRequestAllTypes, memberUuid);
        qnaPostApplicationService.insert(testRequestAllTypes, memberUuid2);
        qnaPostApplicationService.insert(testRequestAllTypes, memberUuid);

        // when
        Pageable pageable = PageRequest.of(0, 2);
        Page<QnaPostResponse> result = qnaPostApplicationService.getByMemberUuid(memberUuid, pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(1);
        List<QnaPostResponse> posts = result.getContent();
        assertThat(posts.get(0).createdAt()).isAfterOrEqualTo(posts.get(1).createdAt());
    }

    @Test
    @DisplayName("항목별 Q&A 게시글 목록 조회하기")
    void getByCategoryUuidTest() throws IOException {
        // given
        qnaPostApplicationService.insert(testRequestAllTypes, memberUuid);
        qnaPostApplicationService.insert(testRequestAllTypes, memberUuid);
        qnaPostApplicationService.insert(testRequestBasicTypes, memberUuid);

        // when
        Pageable pageable = PageRequest.of(0, 2);
        Page<QnaPostResponse> result = qnaPostApplicationService.getByCategoryUuid(categoryUuid, pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(2);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(2);
        List<QnaPostResponse> posts = result.getContent();
        assertThat(posts.get(0).createdAt()).isAfterOrEqualTo(posts.get(1).createdAt());
    }

    @Test
    @DisplayName("제목+본문 검색어로 게시글 목록 조회하기")
    void searchByKeywordTest() throws IOException {
        // given
        qnaCategoryRepository.save(QnaCategoryEntity.builder()
                .order(2)
                .category("기타")
                .build());
        QnaPostInsertRequest qnaPostInsertRequest2 = testRequestBasicTypes;
        qnaPostApplicationService.insert(testRequestAllTypes, memberUuid);
        qnaPostApplicationService.insert(qnaPostInsertRequest2, memberUuid);
        Pageable pageable = PageRequest.of(0, 10);

        // when
        String keyword1 = "기르기";
        String keyword2 = "test";
        Page<QnaPostResponse> result1 = qnaPostApplicationService.searchByKeyword(keyword1, pageable);
        Page<QnaPostResponse> result2 = qnaPostApplicationService.searchByKeyword(keyword2, pageable);

        assertThat(result1.getTotalElements()).isEqualTo(1);
        assertThat(result2.getTotalElements()).isEqualTo(2);
        QnaPostResponse post = result1.getContent().getFirst();
        assertThat(post.title()).isEqualTo(qnaPostInsertRequest2.title());
        assertThat(post.content().get(1).has("data")).isEqualTo(true);
    }


    @Test
    @DisplayName("특정 Q&A 게시글 조회하기")
    void getByUlidTest() throws IOException {
        // given
        SiteMemberEntity siteMember = siteMemberRepository.findByUuid(memberUuid).orElseThrow();
        QnaPostInsertRequest qnaPostInsertRequest = testRequestAllTypes;
        QnaCategoryEntity qnaCategoryEntity = qnaCategoryRepository.findByUuid(qnaPostInsertRequest.categoryUuid()).orElseThrow();
        QnaPostEntity qnaPostEntity = QnaPostEntity.builder()
                .category(qnaCategoryEntity)
                .authMember(siteMember)
                .createMember(siteMember)
                .title(qnaPostInsertRequest.title())
                .content(multipartDataProcessor.saveFilesAndGenerateContentJson(PostType.QNA_POST,qnaPostInsertRequest.content()))
                .build();
        qnaPostRepository.save(qnaPostEntity);
        qnaPostViewCountRedisRepository.write(qnaPostEntity.getUlid(),5L);

        // when
        Optional<QnaPostResponse> result = qnaPostApplicationService.getByUlid(qnaPostEntity.getUlid());

        assertThat(result).isPresent();
        QnaPostResponse response = result.get();
        assertThat(response.nickname()).isEqualTo(siteMember.getNickname());
        assertThat(response.category()).isEqualTo(qnaCategoryEntity.getCategory());
        assertThat(response.title()).isEqualTo(qnaPostInsertRequest.title());
        assertThat(response.viewCount()).isEqualTo(5L);
    }

    @Test
    @DisplayName("특정 Q&A 게시글 수정하기")
    void updateTest() throws IOException {
        // given
        SiteMemberEntity siteMember = siteMemberRepository.findByUuid(memberUuid).orElseThrow();
        QnaPostInsertRequest qnaPostInsertRequest = testRequestAllTypes;
        QnaCategoryEntity qnaCategoryEntity = qnaCategoryRepository.findByUuid(qnaPostInsertRequest.categoryUuid()).orElseThrow();
        QnaPostEntity qnaPostEntity = QnaPostEntity.builder()
                .category(qnaCategoryEntity)
                .authMember(siteMember)
                .createMember(siteMember)
                .title(qnaPostInsertRequest.title())
                .content(multipartDataProcessor.saveFilesAndGenerateContentJson(PostType.QNA_POST,qnaPostInsertRequest.content()))
                .build();
        qnaPostRepository.save(qnaPostEntity);

        // when
        QnaPostUpdateRequest qnaPostUpdateRequest = new QnaPostUpdateRequest(
                qnaPostEntity.getUlid(),
                qnaPostEntity.getCategory().getUuid(),
                "식물 기르기 Q&A",
                basicMediaFiles,
                basicMediaFilesOrder
        );
        qnaPostApplicationService.update(qnaPostUpdateRequest, memberUuid);

        // then
        QnaPostEntity result = qnaPostRepository.findByUlid(qnaPostEntity.getUlid()).orElseThrow();
        assertThat(result.getContent().get(2).get("filename").asText()).isEqualTo(qnaPostUpdateRequest.content().get(2).getOriginalFilename());
    }

    @Test
    @DisplayName("특정 Q&A 게시글 삭제하기")
    void removeByUlidTest() throws IOException {
        // given
        SiteMemberEntity siteMember = siteMemberRepository.findByUuid(memberUuid).orElseThrow();
        QnaPostInsertRequest qnaPostInsertRequest = testRequestAllTypes;
        QnaCategoryEntity qnaCategoryEntity = qnaCategoryRepository.findByUuid(qnaPostInsertRequest.categoryUuid()).orElseThrow();
        QnaPostEntity qnaPostEntity = QnaPostEntity.builder()
                .category(qnaCategoryEntity)
                .authMember(siteMember)
                .createMember(siteMember)
                .title(qnaPostInsertRequest.title())
                .content(multipartDataProcessor.saveFilesAndGenerateContentJson(PostType.QNA_POST,qnaPostInsertRequest.content()))
                .build();
        qnaPostRepository.save(qnaPostEntity);

        // when
        qnaPostApplicationService.removeByUlid(qnaPostEntity.getUlid(),memberUuid);

        // then
        QnaPostEntity result = qnaPostRepository.findByUlid(qnaPostEntity.getUlid()).orElseThrow();
        assertThat(result.getIsDeleted()).isTrue();
    }
}