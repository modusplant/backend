package kr.modusplant.domains.communication.qna.app.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import kr.modusplant.domains.common.app.service.MultipartDataProcessor;
import kr.modusplant.domains.common.enums.PostType;
import kr.modusplant.domains.communication.common.error.CommunicationNotFoundException;
import kr.modusplant.domains.communication.qna.app.http.request.QnaPostInsertRequest;
import kr.modusplant.domains.communication.qna.app.http.request.QnaPostUpdateRequest;
import kr.modusplant.domains.communication.qna.app.http.response.QnaPostResponse;
import kr.modusplant.domains.communication.qna.common.util.app.http.request.QnaPostRequestTestUtils;
import kr.modusplant.domains.communication.qna.common.util.entity.QnaCategoryEntityTestUtils;
import kr.modusplant.domains.communication.qna.common.util.entity.QnaPostEntityTestUtils;
import kr.modusplant.domains.communication.qna.domain.service.QnaCategoryValidationService;
import kr.modusplant.domains.communication.qna.domain.service.QnaPostValidationService;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaCategoryEntity;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaPostEntity;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaCategoryRepository;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaPostRepository;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaPostViewCountRedisRepository;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaPostViewLockRedisRepository;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.domains.member.domain.service.SiteMemberValidationService;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.global.persistence.generator.UlidIdGenerator;
import org.hibernate.generator.EventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class QnaPostApplicationServiceTest implements SiteMemberEntityTestUtils, QnaCategoryEntityTestUtils, QnaPostEntityTestUtils, QnaPostRequestTestUtils {
    @Mock
    private QnaPostValidationService qnaPostValidationService;
    @Mock
    private QnaCategoryValidationService qnaCategoryValidationService;
    @Mock
    private SiteMemberValidationService siteMemberValidationService;
    @Mock
    private SiteMemberRepository siteMemberRepository;
    @Mock
    private QnaCategoryRepository qnaCategoryRepository;
    @Mock
    private QnaPostRepository qnaPostRepository;
    @Mock
    private MultipartDataProcessor multipartDataProcessor;
    @Mock
    private QnaPostViewCountRedisRepository qnaPostViewCountRedisRepository;
    @Mock
    private QnaPostViewLockRedisRepository qnaPostViewLockRedisRepository;
    @InjectMocks
    private QnaPostApplicationService qnaPostApplicationService;

    private static final UlidIdGenerator generator = new UlidIdGenerator();

    private UUID memberUuid;
    private SiteMemberEntity siteMemberEntity;
    private QnaCategoryEntity qnaCategoryEntity;
    private QnaPostEntity.QnaPostEntityBuilder qnaPostEntityBuilder;

    @BeforeEach
    void setUp() {
        siteMemberEntity = createMemberBasicUserEntityWithUuid();
        qnaCategoryEntity = createTestQnaCategoryEntityWithUuid();
        qnaPostEntityBuilder = createQnaPostEntityBuilder()
                .category(qnaCategoryEntity)
                .authMember(siteMemberEntity)
                .createMember(siteMemberEntity);
        memberUuid = siteMemberEntity.getUuid();

        ReflectionTestUtils.setField(qnaPostApplicationService, "ttlMinutes", 10L);
    }

    @Test
    @DisplayName("전체 Q&A 게시글 목록 조회하기")
    void getAllTest() throws IOException {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        QnaPostEntity post1 = qnaPostEntityBuilder.ulid(generator.generate(null,null,null, EventType.INSERT)).build();
        QnaPostEntity post2 = qnaPostEntityBuilder.ulid(generator.generate(null,null,null, EventType.INSERT)).build();
        Page<QnaPostEntity> page = new PageImpl<>(List.of(post1, post2),pageable,3);

        given(qnaPostRepository.findByIsDeletedFalseOrderByCreatedAtDesc(pageable)).willReturn(page);
        given(multipartDataProcessor.convertFileSrcToBinaryData(any(JsonNode.class))).willReturn(mock(ArrayNode.class));

        // when
        Page<QnaPostResponse> result = qnaPostApplicationService.getAll(pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(2);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getContent()).allMatch(r -> r instanceof QnaPostResponse);
        then(qnaPostRepository).should().findByIsDeletedFalseOrderByCreatedAtDesc(pageable);
        then(multipartDataProcessor).should(times(2)).convertFileSrcToBinaryData(any(JsonNode.class));
    }

    @Test
    @DisplayName("사이트 회원별 Q&A 게시글 목록 조회하기")
    void getByMemberUuidTest() throws IOException {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        QnaPostEntity post1 = qnaPostEntityBuilder.ulid(generator.generate(null,null,null, EventType.INSERT)).build();
        QnaPostEntity post2 = qnaPostEntityBuilder.ulid(generator.generate(null,null,null, EventType.INSERT)).build();
        Page<QnaPostEntity> page = new PageImpl<>(List.of(post1, post2),pageable,3);

        given(siteMemberRepository.findByUuid(memberUuid)).willReturn(Optional.of(siteMemberEntity));
        given(qnaPostRepository.findByAuthMemberAndIsDeletedFalseOrderByCreatedAtDesc(siteMemberEntity, pageable)).willReturn(page);
        given(multipartDataProcessor.convertFileSrcToBinaryData(any(JsonNode.class))).willReturn(mock(ArrayNode.class));

        // when
        Page<QnaPostResponse> result = qnaPostApplicationService.getByMemberUuid(memberUuid, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(2);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getContent()).allMatch(r -> r instanceof QnaPostResponse);
        then(siteMemberRepository).should().findByUuid(memberUuid);
        then(qnaPostRepository).should().findByAuthMemberAndIsDeletedFalseOrderByCreatedAtDesc(siteMemberEntity, pageable);
        then(multipartDataProcessor).should(times(2)).convertFileSrcToBinaryData(any(JsonNode.class));
    }

    @Test
    @DisplayName("항목별 Q&A 게시글 목록 조회하기")
    void getByCategoryUuidTest() throws IOException {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        QnaPostEntity post1 = qnaPostEntityBuilder.ulid(generator.generate(null,null,null, EventType.INSERT)).build();
        QnaPostEntity post2 = qnaPostEntityBuilder.ulid(generator.generate(null,null,null, EventType.INSERT)).build();
        Page<QnaPostEntity> page = new PageImpl<>(List.of(post1, post2),pageable,3);

        given(qnaCategoryRepository.findByUuid(qnaCategoryEntity.getUuid())).willReturn(Optional.of(qnaCategoryEntity));
        given(qnaPostRepository.findByCategoryAndIsDeletedFalseOrderByCreatedAtDesc(qnaCategoryEntity, pageable)).willReturn(page);
        given(multipartDataProcessor.convertFileSrcToBinaryData(any(JsonNode.class))).willReturn(mock(ArrayNode.class));

        // when
        Page<QnaPostResponse> result = qnaPostApplicationService.getByCategoryUuid(qnaCategoryEntity.getUuid(), pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getContent()).allMatch(r -> r instanceof QnaPostResponse);
        then(qnaCategoryRepository).should().findByUuid(qnaCategoryEntity.getUuid());
        then(qnaPostRepository).should().findByCategoryAndIsDeletedFalseOrderByCreatedAtDesc(qnaCategoryEntity, pageable);
        then(multipartDataProcessor).should(times(2)).convertFileSrcToBinaryData(any(JsonNode.class));
    }

    @Test
    @DisplayName("제목+본문 검색어로 게시글 목록 조회하기")
    void searchByKeywordTest() throws IOException {
        // given
        String keyword = "test";
        Pageable pageable = PageRequest.of(0, 2);
        QnaPostEntity post1 = qnaPostEntityBuilder.ulid(generator.generate(null,null,null, EventType.INSERT)).build();
        QnaPostEntity post2 = qnaPostEntityBuilder.ulid(generator.generate(null,null,null, EventType.INSERT)).build();
        Page<QnaPostEntity> page = new PageImpl<>(List.of(post1, post2),pageable,2);

        given(qnaPostRepository.searchByTitleOrContent(keyword,pageable)).willReturn(page);
        given(multipartDataProcessor.convertFileSrcToBinaryData(any(JsonNode.class))).willReturn(mock(ArrayNode.class));

        // when
        Page<QnaPostResponse> result = qnaPostApplicationService.searchByKeyword(keyword, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getContent()).allMatch(r -> r instanceof QnaPostResponse);
        then(qnaPostRepository).should().searchByTitleOrContent(keyword,pageable);
        then(multipartDataProcessor).should(times(2)).convertFileSrcToBinaryData(any(JsonNode.class));
    }

    @Test
    @DisplayName("특정 Q&A 게시글 조회하기")
    void getByUlidTest() throws IOException {
        // given
        QnaPostEntity post = qnaPostEntityBuilder.ulid(generator.generate(null,null,null, EventType.INSERT)).build();

        given(qnaPostRepository.findByUlid(post.getUlid())).willReturn(Optional.of(post));
        given(multipartDataProcessor.convertFileSrcToBinaryData(any(JsonNode.class))).willReturn(mock(ArrayNode.class));
        given(qnaPostViewCountRedisRepository.read(anyString())).willReturn(56L);

        // when
        Optional<QnaPostResponse> result = qnaPostApplicationService.getByUlid(post.getUlid());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getClass()).isEqualTo(QnaPostResponse.class);
        then(qnaPostRepository).should().findByUlid(post.getUlid());
        then(multipartDataProcessor).should(times(1)).convertFileSrcToBinaryData(any(JsonNode.class));
    }

    @Test
    @DisplayName("특정 Q&A 게시글 추가하기")
    void insertTest() throws IOException {
        // given
        QnaPostInsertRequest insertRequest = requestAllTypes;
        willDoNothing().given(qnaPostValidationService).validateQnaPostInsertRequest(insertRequest);
        willDoNothing().given(qnaCategoryValidationService).validateNotFoundUuid(insertRequest.categoryUuid());
        willDoNothing().given(siteMemberValidationService).validateNotFoundUuid(memberUuid);
        given(siteMemberRepository.findByUuid(memberUuid)).willReturn(Optional.of(siteMemberEntity));
        given(qnaCategoryRepository.findByUuid(insertRequest.categoryUuid())).willReturn(Optional.of(qnaCategoryEntity));
        given(multipartDataProcessor.saveFilesAndGenerateContentJson(PostType.QNA_POST, insertRequest.content())).willReturn(mock(JsonNode.class));

        // when
        qnaPostApplicationService.insert(insertRequest,memberUuid);

        // then
        then(qnaPostValidationService).should().validateQnaPostInsertRequest(insertRequest);
        then(qnaCategoryValidationService).should().validateNotFoundUuid(insertRequest.categoryUuid());
        then(siteMemberValidationService).should().validateNotFoundUuid(memberUuid);
        then(siteMemberRepository).should().findByUuid(memberUuid);
        then(qnaCategoryRepository).should().findByUuid(insertRequest.categoryUuid());
        then(multipartDataProcessor).should().saveFilesAndGenerateContentJson(PostType.QNA_POST, insertRequest.content());
        then(qnaPostRepository).should().save(any(QnaPostEntity.class));
    }

    @Test
    @DisplayName("특정 Q&A 게시글 수정하기")
    void updateTest() throws IOException {
        // given
        QnaPostUpdateRequest updateRequest = new QnaPostUpdateRequest(
                generator.generate(null,null,null, EventType.INSERT),
                requestAllTypes.categoryUuid(),
                requestAllTypes.title(),
                requestAllTypes.content(),
                requestAllTypes.orderInfo());
        QnaPostEntity post = qnaPostEntityBuilder.ulid(updateRequest.ulid()).build();

        willDoNothing().given(qnaPostValidationService).validateQnaPostUpdateRequest(updateRequest);
        willDoNothing().given(qnaPostValidationService).validateAccessibleQnaPost(updateRequest.ulid(),memberUuid);
        willDoNothing().given(qnaCategoryValidationService).validateNotFoundUuid(updateRequest.categoryUuid());
        given(qnaPostRepository.findByUlid(updateRequest.ulid())).willReturn(Optional.of(post));
        willDoNothing().given(multipartDataProcessor).deleteFiles(any(JsonNode.class));
        given(qnaCategoryRepository.findByUuid(updateRequest.categoryUuid())).willReturn(Optional.of(qnaCategoryEntity));
        given(multipartDataProcessor.saveFilesAndGenerateContentJson(PostType.QNA_POST, updateRequest.content())).willReturn(mock(JsonNode.class));

        // when
        qnaPostApplicationService.update(updateRequest,memberUuid);

        // then
        then(qnaPostValidationService).should().validateQnaPostUpdateRequest(updateRequest);
        then(qnaPostValidationService).should().validateAccessibleQnaPost(updateRequest.ulid(),memberUuid);
        then(qnaCategoryValidationService).should().validateNotFoundUuid(updateRequest.categoryUuid());
        then(qnaPostRepository).should().findByUlid(updateRequest.ulid());
        then(multipartDataProcessor).should().deleteFiles(any(JsonNode.class));
        then(qnaCategoryRepository).should().findByUuid(updateRequest.categoryUuid());
        then(multipartDataProcessor).should().saveFilesAndGenerateContentJson(PostType.QNA_POST, updateRequest.content());
        then(qnaPostRepository).should().save(any(QnaPostEntity.class));
    }

    @Test
    @DisplayName("특정 Q&A 게시글 삭제하기")
    void removeByUlidTest() throws IOException {
        // given
        QnaPostEntity post = qnaPostEntityBuilder.ulid(generator.generate(null,null,null, EventType.INSERT)).build();

        willDoNothing().given(qnaPostValidationService).validateAccessibleQnaPost(post.getUlid(),memberUuid);
        given(qnaPostRepository.findByUlid(post.getUlid())).willReturn(Optional.of(post));
        willDoNothing().given(multipartDataProcessor).deleteFiles(any(JsonNode.class));

        // when
        qnaPostApplicationService.removeByUlid(post.getUlid(),memberUuid);

        // then
        then(qnaPostValidationService).should().validateAccessibleQnaPost(post.getUlid(),memberUuid);
        then(qnaPostRepository).should().findByUlid(post.getUlid());
        then(multipartDataProcessor).should().deleteFiles(any(JsonNode.class));
        then(qnaPostRepository).should().save(any(QnaPostEntity.class));
    }

    @Test
    @DisplayName("Redis에 조회수값이 있으면 조회")
    void readViewCountShouldReturnRedisValueWhenRedisHasValueTest() {
        // given
        String ulid = generator.generate(null,null,null, EventType.INSERT);
        given(qnaPostViewCountRedisRepository.read(ulid)).willReturn(100L);

        // when
        Long result = qnaPostApplicationService.readViewCount(ulid);

        // then
        assertThat(result).isEqualTo(100L);
        then(qnaPostRepository).should(never()).findByUlid(any());
    }

    @Test
    @DisplayName("Redis에 조회수가 없고 DB에 있으면 DB에서 값을 조회")
    void readViewCountShouldReturnDbValueWhenRedisIsEmptyAndDbHasValueTest() {
        // given
        String ulid = generator.generate(null,null,null, EventType.INSERT);
        given(qnaPostViewCountRedisRepository.read(ulid)).willReturn(null);
        QnaPostEntity qnaPostEntity = createQnaPostEntityBuilder()
                .category(createTestQnaCategoryEntity())
                .authMember(createMemberBasicAdminEntityWithUuid())
                .createMember(createMemberBasicAdminEntityWithUuid())
                .viewCount(55L)
                .build();
        given(qnaPostRepository.findByUlid(ulid)).willReturn(Optional.of(qnaPostEntity));

        // when
        Long result = qnaPostApplicationService.readViewCount(ulid);

        // then
        assertThat(result).isEqualTo(55L);
        then(qnaPostViewCountRedisRepository).should().write(ulid,55L);
    }

    @Test
    @DisplayName("Redis와 DB에 모두 조회수값이 없으면 예외 발생")
    void readViewCountShouldThrowExceptionWhenRedisIsEmptyAndDbEmptyTest() {
        // given
        String ulid = generator.generate(null,null,null, EventType.INSERT);
        given(qnaPostViewCountRedisRepository.read(ulid)).willReturn(null);
        given(qnaPostRepository.findByUlid(ulid)).willReturn(Optional.empty());

        // when & then
        assertThrows(CommunicationNotFoundException.class,
                () -> qnaPostApplicationService.readViewCount(ulid));
    }

    @Test
    @DisplayName("조회수 락이 걸려있을 때 기존 조회수 가져오기")
    void increaseViewCountWhenLockExistsTest() {
        // given
        String ulid = generator.generate(null,null,null, EventType.INSERT);
        when(qnaPostViewLockRedisRepository.lock(ulid,memberUuid,10)).thenReturn(false);
        when(qnaPostViewCountRedisRepository.read(ulid)).thenReturn(10L);

        // when
        Long result = qnaPostApplicationService.increaseViewCount(ulid,memberUuid);

        // then
        verify(qnaPostViewLockRedisRepository, times(1)).lock(ulid,memberUuid,10);
        verify(qnaPostViewCountRedisRepository, times(1)).read(ulid);
        verify(qnaPostViewCountRedisRepository,never()).increase(anyString());
        assertThat(result).isEqualTo(10L);
    }

    @Test
    @DisplayName("조회수 락이 걸려있지 않을 때 조회수 증가")
    void increaseViewCountWhenLockNotExistTest() {
        // given
        String ulid = generator.generate(null,null,null, EventType.INSERT);
        when(qnaPostViewLockRedisRepository.lock(ulid,memberUuid,10)).thenReturn(true);
        when(qnaPostViewCountRedisRepository.increase(ulid)).thenReturn(11L);

        // when
        Long result = qnaPostApplicationService.increaseViewCount(ulid,memberUuid);

        // then
        verify(qnaPostViewLockRedisRepository,times(1)).lock(ulid,memberUuid,10L);
        verify(qnaPostViewCountRedisRepository,times(1)).increase(ulid);
        verify(qnaPostViewCountRedisRepository,never()).read(ulid);
        assertThat(result).isEqualTo(11L);
    }


}
