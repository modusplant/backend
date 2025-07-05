package kr.modusplant.domains.communication.conversation.app.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import kr.modusplant.domains.common.app.service.MultipartDataProcessor;
import kr.modusplant.domains.common.enums.PostType;
import kr.modusplant.domains.communication.common.error.CommunicationNotFoundException;
import kr.modusplant.domains.communication.conversation.app.http.request.ConvPostInsertRequest;
import kr.modusplant.domains.communication.conversation.app.http.request.ConvPostUpdateRequest;
import kr.modusplant.domains.communication.conversation.app.http.response.ConvPostResponse;
import kr.modusplant.domains.communication.conversation.common.util.app.http.request.ConvPostRequestTestUtils;
import kr.modusplant.domains.communication.conversation.common.util.entity.ConvCategoryEntityTestUtils;
import kr.modusplant.domains.communication.conversation.common.util.entity.ConvPostEntityTestUtils;
import kr.modusplant.domains.communication.conversation.domain.service.ConvCategoryValidationService;
import kr.modusplant.domains.communication.conversation.domain.service.ConvPostValidationService;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvCategoryEntity;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvPostEntity;
import kr.modusplant.domains.communication.conversation.persistence.repository.ConvCategoryRepository;
import kr.modusplant.domains.communication.conversation.persistence.repository.ConvPostRepository;
import kr.modusplant.domains.communication.conversation.persistence.repository.ConvPostViewCountRedisRepository;
import kr.modusplant.domains.communication.conversation.persistence.repository.ConvPostViewLockRedisRepository;
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
class ConvPostApplicationServiceTest implements SiteMemberEntityTestUtils, ConvCategoryEntityTestUtils, ConvPostEntityTestUtils, ConvPostRequestTestUtils {
    @Mock
    private ConvPostValidationService convPostValidationService;
    @Mock
    private ConvCategoryValidationService convCategoryValidationService;
    @Mock
    private SiteMemberValidationService siteMemberValidationService;
    @Mock
    private SiteMemberRepository siteMemberRepository;
    @Mock
    private ConvCategoryRepository convCategoryRepository;
    @Mock
    private ConvPostRepository convPostRepository;
    @Mock
    private MultipartDataProcessor multipartDataProcessor;
    @Mock
    private ConvPostViewCountRedisRepository convPostViewCountRedisRepository;
    @Mock
    private ConvPostViewLockRedisRepository convPostViewLockRedisRepository;
    @InjectMocks
    private ConvPostApplicationService convPostApplicationService;

    private static final UlidIdGenerator generator = new UlidIdGenerator();

    private UUID memberUuid;
    private SiteMemberEntity siteMemberEntity;
    private ConvCategoryEntity convCategoryEntity;
    private ConvPostEntity.ConvPostEntityBuilder convPostEntityBuilder;

    @BeforeEach
    void setUp() {
        siteMemberEntity = createMemberBasicUserEntityWithUuid();
        convCategoryEntity = createTestConvCategoryEntityWithUuid();
        convPostEntityBuilder = createConvPostEntityBuilder()
                .category(convCategoryEntity)
                .authMember(siteMemberEntity)
                .createMember(siteMemberEntity);
        memberUuid = siteMemberEntity.getUuid();

        ReflectionTestUtils.setField(convPostApplicationService, "ttlMinutes", 10L);
    }


    @Test
    @DisplayName("전체 대화 게시글 목록 조회하기")
    void getAllTest() throws IOException {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        ConvPostEntity post1 = convPostEntityBuilder.ulid(generator.generate(null,null,null, EventType.INSERT)).build();
        ConvPostEntity post2 = convPostEntityBuilder.ulid(generator.generate(null,null,null, EventType.INSERT)).build();
        Page<ConvPostEntity> page = new PageImpl<>(List.of(post1, post2),pageable,3);

        given(convPostRepository.findByIsDeletedFalseOrderByCreatedAtDesc(pageable)).willReturn(page);
        given(multipartDataProcessor.convertFileSrcToBinaryData(any(JsonNode.class))).willReturn(mock(ArrayNode.class));

        // when
        Page<ConvPostResponse> result = convPostApplicationService.getAll(pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(2);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getContent()).allMatch(r -> r instanceof ConvPostResponse);
        then(convPostRepository).should().findByIsDeletedFalseOrderByCreatedAtDesc(pageable);
        then(multipartDataProcessor).should(times(2)).convertFileSrcToBinaryData(any(JsonNode.class));
    }

    @Test
    @DisplayName("사이트 회원별 대화 게시글 목록 조회하기")
    void getByMemberUuidTest() throws IOException {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        ConvPostEntity post1 = convPostEntityBuilder.ulid(generator.generate(null,null,null, EventType.INSERT)).build();
        ConvPostEntity post2 = convPostEntityBuilder.ulid(generator.generate(null,null,null, EventType.INSERT)).build();
        Page<ConvPostEntity> page = new PageImpl<>(List.of(post1, post2),pageable,3);

        given(siteMemberRepository.findByUuid(memberUuid)).willReturn(Optional.of(siteMemberEntity));
        given(convPostRepository.findByAuthMemberAndIsDeletedFalseOrderByCreatedAtDesc(siteMemberEntity,pageable)).willReturn(page);
        given(multipartDataProcessor.convertFileSrcToBinaryData(any(JsonNode.class))).willReturn(mock(ArrayNode.class));

        // when
        Page<ConvPostResponse> result = convPostApplicationService.getByMemberUuid(memberUuid, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(2);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getContent()).allMatch(r -> r instanceof ConvPostResponse);
        then(siteMemberRepository).should().findByUuid(memberUuid);
        then(convPostRepository).should().findByAuthMemberAndIsDeletedFalseOrderByCreatedAtDesc(siteMemberEntity,pageable);
        then(multipartDataProcessor).should(times(2)).convertFileSrcToBinaryData(any(JsonNode.class));
    }

    @Test
    @DisplayName("항목별 대화 게시글 목록 조회하기")
    void getByCategoryUuidTest() throws IOException {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        ConvPostEntity post1 = convPostEntityBuilder.ulid(generator.generate(null,null,null, EventType.INSERT)).build();
        ConvPostEntity post2 = convPostEntityBuilder.ulid(generator.generate(null,null,null, EventType.INSERT)).build();
        Page<ConvPostEntity> page = new PageImpl<>(List.of(post1, post2),pageable,3);

        given(convCategoryRepository.findByUuid(convCategoryEntity.getUuid())).willReturn(Optional.of(convCategoryEntity));
        given(convPostRepository.findByCategoryAndIsDeletedFalseOrderByCreatedAtDesc(convCategoryEntity,pageable)).willReturn(page);
        given(multipartDataProcessor.convertFileSrcToBinaryData(any(JsonNode.class))).willReturn(mock(ArrayNode.class));

        // when
        Page<ConvPostResponse> result = convPostApplicationService.getByCategoryUuid(convCategoryEntity.getUuid(), pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(2);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getContent()).allMatch(r -> r instanceof ConvPostResponse);
        then(convCategoryRepository).should().findByUuid(convCategoryEntity.getUuid());
        then(convPostRepository).should().findByCategoryAndIsDeletedFalseOrderByCreatedAtDesc(convCategoryEntity,pageable);
        then(multipartDataProcessor).should(times(2)).convertFileSrcToBinaryData(any(JsonNode.class));
    }

    @Test
    @DisplayName("제목+본문 검색어로 게시글 목록 조회하기")
    void searchByKeywordTest() throws IOException {
        // given
        String keyword = "test";
        Pageable pageable = PageRequest.of(0, 2);
        ConvPostEntity post1 = convPostEntityBuilder.ulid(generator.generate(null,null,null, EventType.INSERT)).build();
        ConvPostEntity post2 = convPostEntityBuilder.ulid(generator.generate(null,null,null, EventType.INSERT)).build();
        Page<ConvPostEntity> page = new PageImpl<>(List.of(post1, post2),pageable,2);

        given(convPostRepository.searchByTitleOrContent(keyword,pageable)).willReturn(page);
        given(multipartDataProcessor.convertFileSrcToBinaryData(any(JsonNode.class))).willReturn(mock(ArrayNode.class));

        // when
        Page<ConvPostResponse> result = convPostApplicationService.searchByKeyword(keyword, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getContent()).allMatch(r -> r instanceof ConvPostResponse);
        then(convPostRepository).should().searchByTitleOrContent(keyword,pageable);
        then(multipartDataProcessor).should(times(2)).convertFileSrcToBinaryData(any(JsonNode.class));
    }

    @Test
    @DisplayName("특정 대화 게시글 조회하기")
    void getByUlidTest() throws IOException {
        // given
        ConvPostEntity post = convPostEntityBuilder.ulid(generator.generate(null,null,null, EventType.INSERT)).build();

        given(convPostRepository.findByUlid(post.getUlid())).willReturn(Optional.of(post));
        given(multipartDataProcessor.convertFileSrcToBinaryData(any(JsonNode.class))).willReturn(mock(ArrayNode.class));
        given(convPostViewCountRedisRepository.read(anyString())).willReturn(56L);

        // when
        Optional<ConvPostResponse> result = convPostApplicationService.getByUlid(post.getUlid());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getClass()).isEqualTo(ConvPostResponse.class);
        then(convPostRepository).should().findByUlid(post.getUlid());
        then(multipartDataProcessor).should(times(1)).convertFileSrcToBinaryData(any(JsonNode.class));
    }

    @Test
    @DisplayName("특정 대화 게시글 추가하기")
    void insertTest() throws IOException {
        // given
        ConvPostInsertRequest insertRequest = requestAllTypes;
        willDoNothing().given(convPostValidationService).validateConvPostInsertRequest(insertRequest);
        willDoNothing().given(convCategoryValidationService).validateNotFoundUuid(insertRequest.categoryUuid());
        willDoNothing().given(siteMemberValidationService).validateNotFoundUuid(memberUuid);
        given(siteMemberRepository.findByUuid(memberUuid)).willReturn(Optional.of(siteMemberEntity));
        given(convCategoryRepository.findByUuid(insertRequest.categoryUuid())).willReturn(Optional.of(convCategoryEntity));
        given(multipartDataProcessor.saveFilesAndGenerateContentJson(PostType.CONV_POST, insertRequest.content())).willReturn(mock(JsonNode.class));

        // when
        convPostApplicationService.insert(insertRequest,memberUuid);

        // then
        then(convPostValidationService).should().validateConvPostInsertRequest(insertRequest);
        then(convCategoryValidationService).should().validateNotFoundUuid(insertRequest.categoryUuid());
        then(siteMemberValidationService).should().validateNotFoundUuid(memberUuid);
        then(siteMemberRepository).should().findByUuid(memberUuid);
        then(convCategoryRepository).should().findByUuid(insertRequest.categoryUuid());
        then(multipartDataProcessor).should().saveFilesAndGenerateContentJson(PostType.CONV_POST, insertRequest.content());
        then(convPostRepository).should().save(any(ConvPostEntity.class));
    }


    @Test
    @DisplayName("특정 대화 게시글 수정하기")
    void updateTest() throws IOException {
        // given
        ConvPostUpdateRequest updateRequest = new ConvPostUpdateRequest(
                generator.generate(null,null,null, EventType.INSERT),
                        requestAllTypes.categoryUuid(),
                        requestAllTypes.title(),
                        requestAllTypes.content(),
                        requestAllTypes.orderInfo());
        ConvPostEntity post = convPostEntityBuilder.ulid(updateRequest.ulid()).build();

        willDoNothing().given(convPostValidationService).validateConvPostUpdateRequest(updateRequest);
        willDoNothing().given(convPostValidationService).validateAccessibleConvPost(updateRequest.ulid(),memberUuid);
        willDoNothing().given(convCategoryValidationService).validateNotFoundUuid(updateRequest.categoryUuid());
        given(convPostRepository.findByUlid(updateRequest.ulid())).willReturn(Optional.of(post));
        willDoNothing().given(multipartDataProcessor).deleteFiles(any(JsonNode.class));
        given(convCategoryRepository.findByUuid(updateRequest.categoryUuid())).willReturn(Optional.of(convCategoryEntity));
        given(multipartDataProcessor.saveFilesAndGenerateContentJson(PostType.CONV_POST, updateRequest.content())).willReturn(mock(JsonNode.class));

        // when
        convPostApplicationService.update(updateRequest,memberUuid);

        // then
        then(convPostValidationService).should().validateConvPostUpdateRequest(updateRequest);
        then(convPostValidationService).should().validateAccessibleConvPost(updateRequest.ulid(),memberUuid);
        then(convCategoryValidationService).should().validateNotFoundUuid(updateRequest.categoryUuid());
        then(convPostRepository).should().findByUlid(updateRequest.ulid());
        then(multipartDataProcessor).should().deleteFiles(any(JsonNode.class));
        then(convCategoryRepository).should().findByUuid(updateRequest.categoryUuid());
        then(multipartDataProcessor).should().saveFilesAndGenerateContentJson(PostType.CONV_POST, updateRequest.content());
        then(convPostRepository).should().save(any(ConvPostEntity.class));
    }

    @Test
    @DisplayName("특정 대화 게시글 삭제하기")
    void removeByUlidTest() throws IOException {
        // given
        ConvPostEntity post = convPostEntityBuilder.ulid(generator.generate(null,null,null, EventType.INSERT)).build();

        willDoNothing().given(convPostValidationService).validateAccessibleConvPost(post.getUlid(),memberUuid);
        given(convPostRepository.findByUlid(post.getUlid())).willReturn(Optional.of(post));
        willDoNothing().given(multipartDataProcessor).deleteFiles(any(JsonNode.class));

        // when
        convPostApplicationService.removeByUlid(post.getUlid(),memberUuid);

        // then
        then(convPostValidationService).should().validateAccessibleConvPost(post.getUlid(),memberUuid);
        then(convPostRepository).should().findByUlid(post.getUlid());
        then(multipartDataProcessor).should().deleteFiles(any(JsonNode.class));
        then(convPostRepository).should().save(any(ConvPostEntity.class));
    }

    @Test
    @DisplayName("Redis에 조회수값이 있으면 조회")
    void readViewCountShouldReturnRedisValueWhenRedisHasValueTest() {
        // given
        String ulid = generator.generate(null,null,null, EventType.INSERT);
        given(convPostViewCountRedisRepository.read(ulid)).willReturn(100L);

        // when
        Long result = convPostApplicationService.readViewCount(ulid);

        // then
        assertThat(result).isEqualTo(100L);
        then(convPostRepository).should(never()).findByUlid(any());
    }

    @Test
    @DisplayName("Redis에 조회수가 없고 DB에 있으면 DB에서 값을 조회")
    void readViewCountShouldReturnDbValueWhenRedisIsEmptyAndDbHasValueTest() {
        // given
        String ulid = generator.generate(null,null,null, EventType.INSERT);
        given(convPostViewCountRedisRepository.read(ulid)).willReturn(null);
        ConvPostEntity convPostEntity = createConvPostEntityBuilder()
                .category(createTestConvCategoryEntity())
                .authMember(createMemberBasicAdminEntityWithUuid())
                .createMember(createMemberBasicAdminEntityWithUuid())
                .viewCount(55L)
                .build();
        given(convPostRepository.findByUlid(ulid)).willReturn(Optional.of(convPostEntity));

        // when
        Long result = convPostApplicationService.readViewCount(ulid);

        // then
        assertThat(result).isEqualTo(55L);
        then(convPostViewCountRedisRepository).should().write(ulid,55L);
    }

    @Test
    @DisplayName("Redis와 DB에 모두 조회수값이 없으면 예외 발생")
    void readViewCountShouldThrowExceptionWhenRedisIsEmptyAndDbEmptyTest() {
        // given
        String ulid = generator.generate(null,null,null, EventType.INSERT);
        given(convPostViewCountRedisRepository.read(ulid)).willReturn(null);
        given(convPostRepository.findByUlid(ulid)).willReturn(Optional.empty());

        // when & then
        assertThrows(CommunicationNotFoundException.class,
                () -> convPostApplicationService.readViewCount(ulid));
    }

    @Test
    @DisplayName("조회수 락이 걸려있을 때 기존 조회수 가져오기")
    void increaseViewCountWhenLockExistsTest() {
        // given
        String ulid = generator.generate(null,null,null, EventType.INSERT);
        when(convPostViewLockRedisRepository.lock(ulid,memberUuid,10)).thenReturn(false);
        when(convPostViewCountRedisRepository.read(ulid)).thenReturn(10L);

        // when
        Long result = convPostApplicationService.increaseViewCount(ulid,memberUuid);

        // then
        verify(convPostViewLockRedisRepository, times(1)).lock(ulid,memberUuid,10);
        verify(convPostViewCountRedisRepository, times(1)).read(ulid);
        verify(convPostViewCountRedisRepository,never()).increase(anyString());
        assertThat(result).isEqualTo(10L);
    }

    @Test
    @DisplayName("조회수 락이 걸려있지 않을 때 조회수 증가")
    void increaseViewCountWhenLockNotExistTest() {
        // given
        String ulid = generator.generate(null,null,null, EventType.INSERT);
        when(convPostViewLockRedisRepository.lock(ulid,memberUuid,10)).thenReturn(true);
        when(convPostViewCountRedisRepository.increase(ulid)).thenReturn(11L);

        // when
        Long result = convPostApplicationService.increaseViewCount(ulid,memberUuid);

        // then
        verify(convPostViewLockRedisRepository,times(1)).lock(ulid,memberUuid,10L);
        verify(convPostViewCountRedisRepository,times(1)).increase(ulid);
        verify(convPostViewCountRedisRepository,never()).read(ulid);
        assertThat(result).isEqualTo(11L);
    }
}
