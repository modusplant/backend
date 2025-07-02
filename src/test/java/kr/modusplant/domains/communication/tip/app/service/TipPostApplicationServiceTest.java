package kr.modusplant.domains.communication.tip.app.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import kr.modusplant.domains.common.app.service.MultipartDataProcessor;
import kr.modusplant.domains.common.enums.PostType;
import kr.modusplant.domains.communication.common.error.PostNotFoundException;
import kr.modusplant.domains.communication.tip.app.http.request.TipPostInsertRequest;
import kr.modusplant.domains.communication.tip.app.http.request.TipPostUpdateRequest;
import kr.modusplant.domains.communication.tip.app.http.response.TipPostResponse;
import kr.modusplant.domains.communication.tip.common.util.app.http.request.TipPostRequestTestUtils;
import kr.modusplant.domains.communication.tip.common.util.entity.TipCategoryEntityTestUtils;
import kr.modusplant.domains.communication.tip.common.util.entity.TipPostEntityTestUtils;
import kr.modusplant.domains.communication.tip.domain.service.TipCategoryValidationService;
import kr.modusplant.domains.communication.tip.domain.service.TipPostValidationService;
import kr.modusplant.domains.communication.tip.persistence.entity.TipCategoryEntity;
import kr.modusplant.domains.communication.tip.persistence.entity.TipPostEntity;
import kr.modusplant.domains.communication.tip.persistence.repository.TipCategoryRepository;
import kr.modusplant.domains.communication.tip.persistence.repository.TipPostRepository;
import kr.modusplant.domains.communication.tip.persistence.repository.TipPostViewCountRedisRepository;
import kr.modusplant.domains.communication.tip.persistence.repository.TipPostViewLockRedisRepository;
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
class TipPostApplicationServiceTest implements SiteMemberEntityTestUtils, TipCategoryEntityTestUtils, TipPostEntityTestUtils, TipPostRequestTestUtils {
    @Mock
    private TipPostValidationService tipPostValidationService;
    @Mock
    private TipCategoryValidationService tipCategoryValidationService;
    @Mock
    private SiteMemberValidationService siteMemberValidationService;
    @Mock
    private SiteMemberRepository siteMemberRepository;
    @Mock
    private TipCategoryRepository tipCategoryRepository;
    @Mock
    private TipPostRepository tipPostRepository;
    @Mock
    private MultipartDataProcessor multipartDataProcessor;
    @Mock
    private TipPostViewCountRedisRepository tipPostViewCountRedisRepository;
    @Mock
    private TipPostViewLockRedisRepository tipPostViewLockRedisRepository;
    @InjectMocks
    private TipPostApplicationService tipPostApplicationService;

    private static final UlidIdGenerator generator = new UlidIdGenerator();

    private UUID memberUuid;
    private SiteMemberEntity siteMemberEntity;
    private TipCategoryEntity tipCategoryEntity;
    private TipPostEntity.TipPostEntityBuilder tipPostEntityBuilder;

    @BeforeEach
    void setUp() {
        siteMemberEntity = createMemberBasicUserEntityWithUuid();
        tipCategoryEntity = createTestTipCategoryEntityWithUuid();
        tipPostEntityBuilder = createTipPostEntityBuilder()
                .category(tipCategoryEntity)
                .authMember(siteMemberEntity)
                .createMember(siteMemberEntity);
        memberUuid = siteMemberEntity.getUuid();

        ReflectionTestUtils.setField(tipPostApplicationService, "ttlMinutes", 10L);
    }

    @Test
    @DisplayName("전체 팁 게시글 목록 조회하기")
    void getAllTest() throws IOException {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        TipPostEntity post1 = tipPostEntityBuilder.ulid(generator.generate(null,null,null, EventType.INSERT)).build();
        TipPostEntity post2 = tipPostEntityBuilder.ulid(generator.generate(null,null,null, EventType.INSERT)).build();
        Page<TipPostEntity> page = new PageImpl<>(List.of(post1, post2),pageable,3);

        given(tipPostRepository.findByIsDeletedFalseOrderByCreatedAtDesc(pageable)).willReturn(page);
        given(multipartDataProcessor.convertFileSrcToBinaryData(any(JsonNode.class))).willReturn(mock(ArrayNode.class));

        // when
        Page<TipPostResponse> result = tipPostApplicationService.getAll(pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(2);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getContent()).allMatch(r -> r instanceof TipPostResponse);
        then(tipPostRepository).should().findByIsDeletedFalseOrderByCreatedAtDesc(pageable);
        then(multipartDataProcessor).should(times(2)).convertFileSrcToBinaryData(any(JsonNode.class));
    }

    @Test
    @DisplayName("사이트 회원별 팁 게시글 목록 조회하기")
    void getByMemberUuidTest() throws IOException {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        TipPostEntity post1 = tipPostEntityBuilder.ulid(generator.generate(null,null,null, EventType.INSERT)).build();
        TipPostEntity post2 = tipPostEntityBuilder.ulid(generator.generate(null,null,null, EventType.INSERT)).build();
        Page<TipPostEntity> page = new PageImpl<>(List.of(post1, post2),pageable,3);

        given(siteMemberRepository.findByUuid(memberUuid)).willReturn(Optional.of(siteMemberEntity));
        given(tipPostRepository.findByAuthMemberAndIsDeletedFalseOrderByCreatedAtDesc(siteMemberEntity, pageable)).willReturn(page);
        given(multipartDataProcessor.convertFileSrcToBinaryData(any(JsonNode.class))).willReturn(mock(ArrayNode.class));

        // when
        Page<TipPostResponse> result = tipPostApplicationService.getByMemberUuid(memberUuid, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(2);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getContent()).allMatch(r -> r instanceof TipPostResponse);
        then(siteMemberRepository).should().findByUuid(memberUuid);
        then(tipPostRepository).should().findByAuthMemberAndIsDeletedFalseOrderByCreatedAtDesc(siteMemberEntity, pageable);
        then(multipartDataProcessor).should(times(2)).convertFileSrcToBinaryData(any(JsonNode.class));
    }

    @Test
    @DisplayName("항목별 팁 게시글 목록 조회하기")
    void getByCategoryUuidTest() throws IOException {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        TipPostEntity post1 = tipPostEntityBuilder.ulid(generator.generate(null,null,null, EventType.INSERT)).build();
        TipPostEntity post2 = tipPostEntityBuilder.ulid(generator.generate(null,null,null, EventType.INSERT)).build();
        Page<TipPostEntity> page = new PageImpl<>(List.of(post1, post2),pageable,3);

        given(tipCategoryRepository.findByUuid(tipCategoryEntity.getUuid())).willReturn(Optional.of(tipCategoryEntity));
        given(tipPostRepository.findByCategoryAndIsDeletedFalseOrderByCreatedAtDesc(tipCategoryEntity, pageable)).willReturn(page);
        given(multipartDataProcessor.convertFileSrcToBinaryData(any(JsonNode.class))).willReturn(mock(ArrayNode.class));

        // when
        Page<TipPostResponse> result = tipPostApplicationService.getByCategoryUuid(tipCategoryEntity.getUuid(), pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(2);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getContent()).allMatch(r -> r instanceof TipPostResponse);
        then(tipCategoryRepository).should().findByUuid(tipCategoryEntity.getUuid());
        then(tipPostRepository).should().findByCategoryAndIsDeletedFalseOrderByCreatedAtDesc(tipCategoryEntity, pageable);
        then(multipartDataProcessor).should(times(2)).convertFileSrcToBinaryData(any(JsonNode.class));
    }

    @Test
    @DisplayName("제목+본문 검색어로 게시글 목록 조회하기")
    void searchByKeywordTest() throws IOException {
        // given
        String keyword = "test";
        Pageable pageable = PageRequest.of(0, 2);
        TipPostEntity post1 = tipPostEntityBuilder.ulid(generator.generate(null,null,null, EventType.INSERT)).build();
        TipPostEntity post2 = tipPostEntityBuilder.ulid(generator.generate(null,null,null, EventType.INSERT)).build();
        Page<TipPostEntity> page = new PageImpl<>(List.of(post1, post2),pageable,2);

        given(tipPostRepository.searchByTitleOrContent(keyword,pageable)).willReturn(page);
        given(multipartDataProcessor.convertFileSrcToBinaryData(any(JsonNode.class))).willReturn(mock(ArrayNode.class));

        // when
        Page<TipPostResponse> result = tipPostApplicationService.searchByKeyword(keyword, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getContent()).allMatch(r -> r instanceof TipPostResponse);
        then(tipPostRepository).should().searchByTitleOrContent(keyword,pageable);
        then(multipartDataProcessor).should(times(2)).convertFileSrcToBinaryData(any(JsonNode.class));
    }

    @Test
    @DisplayName("특정 팁 게시글 조회하기")
    void getByUlidTest() throws IOException {
        // given
        TipPostEntity post = tipPostEntityBuilder.ulid(generator.generate(null,null,null, EventType.INSERT)).build();

        given(tipPostRepository.findByUlid(post.getUlid())).willReturn(Optional.of(post));
        given(multipartDataProcessor.convertFileSrcToBinaryData(any(JsonNode.class))).willReturn(mock(ArrayNode.class));
        given(tipPostViewCountRedisRepository.read(anyString())).willReturn(56L);

        // when
        Optional<TipPostResponse> result = tipPostApplicationService.getByUlid(post.getUlid());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getClass()).isEqualTo(TipPostResponse.class);
        then(tipPostRepository).should().findByUlid(post.getUlid());
        then(multipartDataProcessor).should(times(1)).convertFileSrcToBinaryData(any(JsonNode.class));
    }

    @Test
    @DisplayName("특정 팁 게시글 추가하기")
    void insertTest() throws IOException {
        // given
        TipPostInsertRequest insertRequest = requestAllTypes;
        willDoNothing().given(tipPostValidationService).validateTipPostInsertRequest(insertRequest);
        willDoNothing().given(tipCategoryValidationService).validateNotFoundUuid(insertRequest.categoryUuid());
        willDoNothing().given(siteMemberValidationService).validateNotFoundUuid(memberUuid);
        given(siteMemberRepository.findByUuid(memberUuid)).willReturn(Optional.of(siteMemberEntity));
        given(tipCategoryRepository.findByUuid(insertRequest.categoryUuid())).willReturn(Optional.of(tipCategoryEntity));
        given(multipartDataProcessor.saveFilesAndGenerateContentJson(PostType.TIP_POST, insertRequest.content())).willReturn(mock(JsonNode.class));

        // when
        tipPostApplicationService.insert(insertRequest,memberUuid);

        // then
        then(tipPostValidationService).should().validateTipPostInsertRequest(insertRequest);
        then(tipCategoryValidationService).should().validateNotFoundUuid(insertRequest.categoryUuid());
        then(siteMemberValidationService).should().validateNotFoundUuid(memberUuid);
        then(siteMemberRepository).should().findByUuid(memberUuid);
        then(tipCategoryRepository).should().findByUuid(insertRequest.categoryUuid());
        then(multipartDataProcessor).should().saveFilesAndGenerateContentJson(PostType.TIP_POST, insertRequest.content());
        then(tipPostRepository).should().save(any(TipPostEntity.class));
    }

    @Test
    @DisplayName("특정 팁 게시글 수정하기")
    void updateTest() throws IOException {
        // given
        TipPostUpdateRequest updateRequest = new TipPostUpdateRequest(
                generator.generate(null,null,null, EventType.INSERT),
                requestAllTypes.categoryUuid(),
                requestAllTypes.title(),
                requestAllTypes.content(),
                requestAllTypes.orderInfo());
        TipPostEntity post = tipPostEntityBuilder.ulid(updateRequest.ulid()).build();

        willDoNothing().given(tipPostValidationService).validateTipPostUpdateRequest(updateRequest);
        willDoNothing().given(tipPostValidationService).validateAccessibleTipPost(updateRequest.ulid(),memberUuid);
        willDoNothing().given(tipCategoryValidationService).validateNotFoundUuid(updateRequest.categoryUuid());
        given(tipPostRepository.findByUlid(updateRequest.ulid())).willReturn(Optional.of(post));
        willDoNothing().given(multipartDataProcessor).deleteFiles(any(JsonNode.class));
        given(tipCategoryRepository.findByUuid(updateRequest.categoryUuid())).willReturn(Optional.of(tipCategoryEntity));
        given(multipartDataProcessor.saveFilesAndGenerateContentJson(PostType.TIP_POST, updateRequest.content())).willReturn(mock(JsonNode.class));

        // when
        tipPostApplicationService.update(updateRequest,memberUuid);

        // then
        then(tipPostValidationService).should().validateTipPostUpdateRequest(updateRequest);
        then(tipPostValidationService).should().validateAccessibleTipPost(updateRequest.ulid(),memberUuid);
        then(tipCategoryValidationService).should().validateNotFoundUuid(updateRequest.categoryUuid());
        then(tipPostRepository).should().findByUlid(updateRequest.ulid());
        then(multipartDataProcessor).should().deleteFiles(any(JsonNode.class));
        then(tipCategoryRepository).should().findByUuid(updateRequest.categoryUuid());
        then(multipartDataProcessor).should().saveFilesAndGenerateContentJson(PostType.TIP_POST, updateRequest.content());
        then(tipPostRepository).should().save(any(TipPostEntity.class));
    }

    @Test
    @DisplayName("특정 팁 게시글 삭제하기")
    void removeByUlidTest() throws IOException {
        // given
        TipPostEntity post = tipPostEntityBuilder.ulid(generator.generate(null,null,null, EventType.INSERT)).build();

        willDoNothing().given(tipPostValidationService).validateAccessibleTipPost(post.getUlid(),memberUuid);
        given(tipPostRepository.findByUlid(post.getUlid())).willReturn(Optional.of(post));
        willDoNothing().given(multipartDataProcessor).deleteFiles(any(JsonNode.class));

        // when
        tipPostApplicationService.removeByUlid(post.getUlid(),memberUuid);

        // then
        then(tipPostValidationService).should().validateAccessibleTipPost(post.getUlid(),memberUuid);
        then(tipPostRepository).should().findByUlid(post.getUlid());
        then(multipartDataProcessor).should().deleteFiles(any(JsonNode.class));
        then(tipPostRepository).should().save(any(TipPostEntity.class));
    }

    @Test
    @DisplayName("Redis에 조회수값이 있으면 조회")
    void readViewCountShouldReturnRedisValueWhenRedisHasValueTest() {
        // given
        String ulid = generator.generate(null,null,null, EventType.INSERT);
        given(tipPostViewCountRedisRepository.read(ulid)).willReturn(100L);

        // when
        Long result = tipPostApplicationService.readViewCount(ulid);

        // then
        assertThat(result).isEqualTo(100L);
        then(tipPostRepository).should(never()).findByUlid(any());
    }

    @Test
    @DisplayName("Redis에 조회수가 없고 DB에 있으면 DB에서 값을 조회")
    void readViewCountShouldReturnDbValueWhenRedisIsEmptyAndDbHasValueTest() {
        // given
        String ulid = generator.generate(null,null,null, EventType.INSERT);
        given(tipPostViewCountRedisRepository.read(ulid)).willReturn(null);
        TipPostEntity tipPostEntity = createTipPostEntityBuilder()
                .category(createTestTipCategoryEntity())
                .authMember(createMemberBasicAdminEntityWithUuid())
                .createMember(createMemberBasicAdminEntityWithUuid())
                .viewCount(55L)
                .build();
        given(tipPostRepository.findByUlid(ulid)).willReturn(Optional.of(tipPostEntity));

        // when
        Long result = tipPostApplicationService.readViewCount(ulid);

        // then
        assertThat(result).isEqualTo(55L);
        then(tipPostViewCountRedisRepository).should().write(ulid,55L);
    }

    @Test
    @DisplayName("Redis와 DB에 모두 조회수값이 없으면 예외 발생")
    void readViewCountShouldThrowExceptionWhenRedisIsEmptyAndDbEmptyTest() {
        // given
        String ulid = generator.generate(null,null,null, EventType.INSERT);
        given(tipPostViewCountRedisRepository.read(ulid)).willReturn(null);
        given(tipPostRepository.findByUlid(ulid)).willReturn(Optional.empty());

        // when & then
        assertThrows(PostNotFoundException.class,
                () -> tipPostApplicationService.readViewCount(ulid));
    }

    @Test
    @DisplayName("조회수 락이 걸려있을 때 기존 조회수 가져오기")
    void increaseViewCountWhenLockExistsTest() {
        // given
        String ulid = generator.generate(null,null,null, EventType.INSERT);
        when(tipPostViewLockRedisRepository.lock(ulid,memberUuid,10)).thenReturn(false);
        when(tipPostViewCountRedisRepository.read(ulid)).thenReturn(10L);

        // when
        Long result = tipPostApplicationService.increaseViewCount(ulid,memberUuid);

        // then
        verify(tipPostViewLockRedisRepository, times(1)).lock(ulid,memberUuid,10);
        verify(tipPostViewCountRedisRepository, times(1)).read(ulid);
        verify(tipPostViewCountRedisRepository,never()).increase(anyString());
        assertThat(result).isEqualTo(10L);
    }

    @Test
    @DisplayName("조회수 락이 걸려있지 않을 때 조회수 증가")
    void increaseViewCountWhenLockNotExistTest() {
        // given
        String ulid = generator.generate(null,null,null, EventType.INSERT);
        when(tipPostViewLockRedisRepository.lock(ulid,memberUuid,10)).thenReturn(true);
        when(tipPostViewCountRedisRepository.increase(ulid)).thenReturn(11L);

        // when
        Long result = tipPostApplicationService.increaseViewCount(ulid,memberUuid);

        // then
        verify(tipPostViewLockRedisRepository,times(1)).lock(ulid,memberUuid,10L);
        verify(tipPostViewCountRedisRepository,times(1)).increase(ulid);
        verify(tipPostViewCountRedisRepository,never()).read(ulid);
        assertThat(result).isEqualTo(11L);
    }


}
