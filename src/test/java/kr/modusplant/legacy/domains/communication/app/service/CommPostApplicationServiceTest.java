package kr.modusplant.legacy.domains.communication.app.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import kr.modusplant.framework.out.jpa.entity.CommPostEntity;
import kr.modusplant.framework.out.jpa.entity.CommPrimaryCategoryEntity;
import kr.modusplant.framework.out.jpa.entity.CommSecondaryCategoryEntity;
import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.out.jpa.entity.util.SiteMemberEntityTestUtils;
import kr.modusplant.framework.out.jpa.repository.CommPostRepository;
import kr.modusplant.framework.out.jpa.repository.CommPrimaryCategoryRepository;
import kr.modusplant.framework.out.jpa.repository.CommSecondaryCategoryRepository;
import kr.modusplant.framework.out.jpa.repository.SiteMemberRepository;
import kr.modusplant.infrastructure.persistence.generator.UlidIdGenerator;
import kr.modusplant.legacy.domains.common.app.service.MultipartDataProcessor;
import kr.modusplant.legacy.domains.communication.app.http.request.CommPostInsertRequest;
import kr.modusplant.legacy.domains.communication.app.http.request.CommPostUpdateRequest;
import kr.modusplant.legacy.domains.communication.app.http.response.CommPostResponse;
import kr.modusplant.legacy.domains.communication.common.util.app.http.request.CommPostRequestTestUtils;
import kr.modusplant.legacy.domains.communication.common.util.entity.CommPostEntityTestUtils;
import kr.modusplant.legacy.domains.communication.common.util.entity.CommPrimaryCategoryEntityTestUtils;
import kr.modusplant.legacy.domains.communication.common.util.entity.CommSecondaryCategoryEntityTestUtils;
import kr.modusplant.legacy.domains.communication.domain.service.CommCategoryValidationService;
import kr.modusplant.legacy.domains.communication.domain.service.CommPostValidationService;
import kr.modusplant.legacy.domains.communication.mapper.CommPostAppInfraMapper;
import kr.modusplant.legacy.domains.communication.persistence.repository.CommPostViewCountRedisRepository;
import kr.modusplant.legacy.domains.communication.persistence.repository.CommPostViewLockRedisRepository;
import kr.modusplant.legacy.domains.member.domain.service.SiteMemberValidationService;
import kr.modusplant.shared.exception.EntityNotFoundException;
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
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static kr.modusplant.framework.out.jpa.entity.CommPostEntity.CommPostEntityBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CommPostApplicationServiceTest implements SiteMemberEntityTestUtils, CommPrimaryCategoryEntityTestUtils, CommSecondaryCategoryEntityTestUtils, CommPostEntityTestUtils, CommPostRequestTestUtils {
    @Mock
    private CommPostValidationService commPostValidationService;
    @Mock
    private CommCategoryValidationService commCategoryValidationService;
    @Mock
    private SiteMemberValidationService siteMemberValidationService;
    @Mock
    private SiteMemberRepository siteMemberRepository;
    @Mock
    private CommPrimaryCategoryRepository commPrimaryCategoryRepository;
    @Mock
    private CommSecondaryCategoryRepository commSecondaryCategoryRepository;
    @Mock
    private CommPostRepository commPostRepository;
    @Mock
    private MultipartDataProcessor multipartDataProcessor;
    @Mock
    private CommPostViewCountRedisRepository commPostViewCountRedisRepository;
    @Mock
    private CommPostViewLockRedisRepository commPostViewLockRedisRepository;
    @Mock
    private CommPostAppInfraMapper commPostAppInfraMapper;
    @InjectMocks
    private CommPostApplicationService commPostApplicationService;

    private static final UlidIdGenerator generator = new UlidIdGenerator();

    private UUID memberUuid;
    private SiteMemberEntity siteMemberEntity;
    private CommPrimaryCategoryEntity commPrimaryCategoryEntity;
    private CommSecondaryCategoryEntity commSecondaryCategoryEntity;
    private CommPostEntityBuilder commPostEntityBuilder;

    @BeforeEach
    void setUp() {
        siteMemberEntity = createMemberBasicUserEntityWithUuid();
        commPrimaryCategoryEntity = createTestCommPrimaryCategoryEntityWithUuid();
        commSecondaryCategoryEntity = createTestCommSecondaryCategoryEntityWithUuid();
        commPostEntityBuilder = createCommPostEntityBuilder()
                .primaryCategory(commPrimaryCategoryEntity)
                .secondaryCategory(commSecondaryCategoryEntity)
                .authMember(siteMemberEntity)
                .createMember(siteMemberEntity);
        memberUuid = siteMemberEntity.getUuid();

        ReflectionTestUtils.setField(commPostApplicationService, "ttlMinutes", 10L);
    }

    @Test
    @DisplayName("전체 컨텐츠 게시글 목록 조회하기")
    void getAllTest() throws IOException {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        CommPostEntity post1 = commPostEntityBuilder.ulid(generator.generate(null,null,null, EventType.INSERT)).build();
        CommPostEntity post2 = commPostEntityBuilder.ulid(generator.generate(null,null,null, EventType.INSERT)).build();
        Page<CommPostEntity> page = new PageImpl<>(List.of(post1, post2),pageable,3);

        given(commPostRepository.findByIsDeletedFalseOrderByCreatedAtDesc(pageable)).willReturn(page);
        given(multipartDataProcessor.convertFileSrcToBinaryData(any(JsonNode.class))).willReturn(mock(ArrayNode.class));
        given(commPostAppInfraMapper.toCommPostResponse(any())).willReturn(mock(CommPostResponse.class));

        // when
        Page<CommPostResponse> result = commPostApplicationService.getAll(pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(2);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getContent()).allMatch(Objects::nonNull);
        then(commPostRepository).should().findByIsDeletedFalseOrderByCreatedAtDesc(pageable);
        then(multipartDataProcessor).should(times(2)).convertFileSrcToBinaryData(any(JsonNode.class));
    }

    @Test
    @DisplayName("사이트 회원별 컨텐츠 게시글 목록 조회하기")
    void getByMemberUuidTest() throws IOException {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        CommPostEntity post1 = commPostEntityBuilder.ulid(generator.generate(null,null,null, EventType.INSERT)).build();
        CommPostEntity post2 = commPostEntityBuilder.ulid(generator.generate(null,null,null, EventType.INSERT)).build();
        Page<CommPostEntity> page = new PageImpl<>(List.of(post1, post2),pageable,3);

        given(siteMemberRepository.findByUuid(memberUuid)).willReturn(Optional.of(siteMemberEntity));
        given(commPostRepository.findByAuthMemberAndIsDeletedFalseOrderByCreatedAtDesc(siteMemberEntity, pageable)).willReturn(page);
        given(multipartDataProcessor.convertFileSrcToBinaryData(any(JsonNode.class))).willReturn(mock(ArrayNode.class));
        given(commPostAppInfraMapper.toCommPostResponse(any())).willReturn(mock(CommPostResponse.class));

        // when
        Page<CommPostResponse> result = commPostApplicationService.getByMemberUuid(memberUuid, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(2);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getContent()).allMatch(Objects::nonNull);
        then(siteMemberRepository).should().findByUuid(memberUuid);
        then(commPostRepository).should().findByAuthMemberAndIsDeletedFalseOrderByCreatedAtDesc(siteMemberEntity, pageable);
        then(multipartDataProcessor).should(times(2)).convertFileSrcToBinaryData(any(JsonNode.class));
    }

    @Test
    @DisplayName("1차 항목별 컨텐츠 게시글 목록 조회하기")
    void getByPrimaryCategoryUuidTest() throws IOException {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        CommPostEntity post1 = commPostEntityBuilder.ulid(generator.generate(null, null, null, EventType.INSERT)).build();
        CommPostEntity post2 = commPostEntityBuilder.ulid(generator.generate(null, null, null, EventType.INSERT)).build();
        Page<CommPostEntity> page = new PageImpl<>(List.of(post1, post2),pageable,3);

        given(commPrimaryCategoryRepository.findByUuid(commPrimaryCategoryEntity.getUuid())).willReturn(Optional.of(commPrimaryCategoryEntity));
        given(commPostRepository.findByPrimaryCategoryAndIsDeletedFalseOrderByCreatedAtDesc(commPrimaryCategoryEntity, pageable)).willReturn(page);
        given(multipartDataProcessor.convertFileSrcToBinaryData(any(JsonNode.class))).willReturn(mock(ArrayNode.class));
        given(commPostAppInfraMapper.toCommPostResponse(any())).willReturn(mock(CommPostResponse.class));

        // when
        Page<CommPostResponse> result = commPostApplicationService.getByPrimaryCategoryUuid(commPrimaryCategoryEntity.getUuid(), pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getContent()).allMatch(Objects::nonNull);
        then(commPrimaryCategoryRepository).should().findByUuid(commPrimaryCategoryEntity.getUuid());
        then(commPostRepository).should().findByPrimaryCategoryAndIsDeletedFalseOrderByCreatedAtDesc(commPrimaryCategoryEntity, pageable);
        then(multipartDataProcessor).should(times(2)).convertFileSrcToBinaryData(any(JsonNode.class));
    }

    @Test
    @DisplayName("2차 항목별 컨텐츠 게시글 목록 조회하기")
    void getBySecondaryCategoryUuidTest() throws IOException {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        CommPostEntity post1 = commPostEntityBuilder.ulid(generator.generate(null,null,null, EventType.INSERT)).build();
        CommPostEntity post2 = commPostEntityBuilder.ulid(generator.generate(null,null,null, EventType.INSERT)).build();
        Page<CommPostEntity> page = new PageImpl<>(List.of(post1, post2),pageable,3);

        given(commSecondaryCategoryRepository.findByUuid(commSecondaryCategoryEntity.getUuid())).willReturn(Optional.of(commSecondaryCategoryEntity));
        given(commPostRepository.findBySecondaryCategoryAndIsDeletedFalseOrderByCreatedAtDesc(commSecondaryCategoryEntity, pageable)).willReturn(page);
        given(multipartDataProcessor.convertFileSrcToBinaryData(any(JsonNode.class))).willReturn(mock(ArrayNode.class));
        given(commPostAppInfraMapper.toCommPostResponse(any())).willReturn(mock(CommPostResponse.class));

        // when
        Page<CommPostResponse> result = commPostApplicationService.getBySecondaryCategoryUuid(commSecondaryCategoryEntity.getUuid(), pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getContent()).allMatch(Objects::nonNull);
        then(commSecondaryCategoryRepository).should().findByUuid(commSecondaryCategoryEntity.getUuid());
        then(commPostRepository).should().findBySecondaryCategoryAndIsDeletedFalseOrderByCreatedAtDesc(commSecondaryCategoryEntity, pageable);
        then(multipartDataProcessor).should(times(2)).convertFileSrcToBinaryData(any(JsonNode.class));
    }

    @Test
    @DisplayName("제목+본문 검색어로 게시글 목록 조회하기")
    void searchByKeywordTest() throws IOException {
        // given
        String keyword = "test";
        Pageable pageable = PageRequest.of(0, 2);
        CommPostEntity post1 = commPostEntityBuilder.ulid(generator.generate(null,null,null, EventType.INSERT)).build();
        CommPostEntity post2 = commPostEntityBuilder.ulid(generator.generate(null,null,null, EventType.INSERT)).build();
        Page<CommPostEntity> page = new PageImpl<>(List.of(post1, post2),pageable,2);

        given(commPostRepository.searchByTitleOrContent(keyword,pageable)).willReturn(page);
        given(multipartDataProcessor.convertFileSrcToBinaryData(any(JsonNode.class))).willReturn(mock(ArrayNode.class));
        given(commPostAppInfraMapper.toCommPostResponse(any())).willReturn(mock(CommPostResponse.class));

        // when
        Page<CommPostResponse> result = commPostApplicationService.searchByKeyword(keyword, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getContent()).allMatch(Objects::nonNull);
        then(commPostRepository).should().searchByTitleOrContent(keyword,pageable);
        then(multipartDataProcessor).should(times(2)).convertFileSrcToBinaryData(any(JsonNode.class));
    }

    @Test
    @DisplayName("특정 컨텐츠 게시글 조회하기")
    void getByUlidTest() throws IOException {
        // given
        CommPostEntity post = commPostEntityBuilder.ulid(generator.generate(null,null,null, EventType.INSERT)).build();

        given(commPostRepository.findByUlid(post.getUlid())).willReturn(Optional.of(post));
        given(multipartDataProcessor.convertFileSrcToBinaryData(any(JsonNode.class))).willReturn(mock(ArrayNode.class));
        given(commPostViewCountRedisRepository.read(anyString())).willReturn(56L);
        given(commPostAppInfraMapper.toCommPostResponse(any())).willReturn(mock(CommPostResponse.class));

        // when
        Optional<CommPostResponse> result = commPostApplicationService.getByUlid(post.getUlid());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getClass()).isEqualTo(CommPostResponse.class);
        then(commPostRepository).should().findByUlid(post.getUlid());
        then(multipartDataProcessor).should(times(1)).convertFileSrcToBinaryData(any(JsonNode.class));
    }

    @Test
    @DisplayName("특정 컨텐츠 게시글 추가하기")
    void insertTest() throws IOException {
        // given
        CommPostInsertRequest insertRequest = requestAllTypes;
        willDoNothing().given(commPostValidationService).validateCommPostInsertRequest(insertRequest);
        willDoNothing().given(commCategoryValidationService).validateNotFoundUuid(insertRequest.primaryCategoryUuid());
        willDoNothing().given(commCategoryValidationService).validateNotFoundUuid(insertRequest.secondaryCategoryUuid());
        willDoNothing().given(siteMemberValidationService).validateNotFoundUuid(memberUuid);
        given(siteMemberRepository.findByUuid(memberUuid)).willReturn(Optional.of(siteMemberEntity));
        given(commPrimaryCategoryRepository.findByUuid(insertRequest.primaryCategoryUuid())).willReturn(Optional.of(commPrimaryCategoryEntity));
        given(commSecondaryCategoryRepository.findByUuid(insertRequest.secondaryCategoryUuid())).willReturn(Optional.of(commSecondaryCategoryEntity));
        given(multipartDataProcessor.saveFilesAndGenerateContentJson(insertRequest.content())).willReturn(mock(JsonNode.class));

        // when
        commPostApplicationService.insert(insertRequest,memberUuid);

        // then
        then(commPostValidationService).should().validateCommPostInsertRequest(insertRequest);
        then(commCategoryValidationService).should().validateNotFoundUuid(insertRequest.primaryCategoryUuid());
        then(commCategoryValidationService).should().validateNotFoundUuid(insertRequest.secondaryCategoryUuid());
        then(siteMemberValidationService).should().validateNotFoundUuid(memberUuid);
        then(siteMemberRepository).should().findByUuid(memberUuid);
        then(commPrimaryCategoryRepository).should().findByUuid(insertRequest.primaryCategoryUuid());
        then(commSecondaryCategoryRepository).should().findByUuid(insertRequest.secondaryCategoryUuid());
        then(multipartDataProcessor).should().saveFilesAndGenerateContentJson(insertRequest.content());
        then(commPostRepository).should().save(any(CommPostEntity.class));
    }

    @Test
    @DisplayName("특정 컨텐츠 게시글 수정하기")
    void updateTest() throws IOException {
        // given
        CommPostUpdateRequest updateRequest = new CommPostUpdateRequest(
                generator.generate(null,null,null, EventType.INSERT),
                requestAllTypes.primaryCategoryUuid(),
                requestAllTypes.secondaryCategoryUuid(),
                requestAllTypes.title(),
                requestAllTypes.content(),
                requestAllTypes.orderInfo());
        CommPostEntity post = commPostEntityBuilder.ulid(updateRequest.ulid()).build();

        willDoNothing().given(commPostValidationService).validateCommPostUpdateRequest(updateRequest);
        willDoNothing().given(commPostValidationService).validateAccessibleCommPost(updateRequest.ulid(),memberUuid);
        willDoNothing().given(commCategoryValidationService).validateNotFoundUuid(updateRequest.primaryCategoryUuid());
        willDoNothing().given(commCategoryValidationService).validateNotFoundUuid(updateRequest.secondaryCategoryUuid());
        given(commPostRepository.findByUlid(updateRequest.ulid())).willReturn(Optional.of(post));
        willDoNothing().given(multipartDataProcessor).deleteFiles(any(JsonNode.class));
        given(commPrimaryCategoryRepository.findByUuid(updateRequest.primaryCategoryUuid())).willReturn(Optional.of(commPrimaryCategoryEntity));
        given(commSecondaryCategoryRepository.findByUuid(updateRequest.secondaryCategoryUuid())).willReturn(Optional.of(commSecondaryCategoryEntity));
        given(multipartDataProcessor.saveFilesAndGenerateContentJson(updateRequest.content())).willReturn(mock(JsonNode.class));

        // when
        commPostApplicationService.update(updateRequest, memberUuid);

        // then
        then(commPostValidationService).should().validateCommPostUpdateRequest(updateRequest);
        then(commPostValidationService).should().validateAccessibleCommPost(updateRequest.ulid(), memberUuid);
        then(commCategoryValidationService).should().validateNotFoundUuid(updateRequest.primaryCategoryUuid());
        then(commCategoryValidationService).should().validateNotFoundUuid(updateRequest.secondaryCategoryUuid());
        then(commPostRepository).should().findByUlid(updateRequest.ulid());
        then(multipartDataProcessor).should().deleteFiles(any(JsonNode.class));
        then(commPrimaryCategoryRepository).should().findByUuid(updateRequest.primaryCategoryUuid());
        then(commSecondaryCategoryRepository).should().findByUuid(updateRequest.secondaryCategoryUuid());
        then(multipartDataProcessor).should().saveFilesAndGenerateContentJson(updateRequest.content());
        then(commPostRepository).should().save(any(CommPostEntity.class));
    }

    @Test
    @DisplayName("특정 컨텐츠 게시글 삭제하기")
    void removeByUlidTest() {
        // given
        CommPostEntity post = commPostEntityBuilder.ulid(generator.generate(null,null,null, EventType.INSERT)).build();

        willDoNothing().given(commPostValidationService).validateAccessibleCommPost(post.getUlid(),memberUuid);
        given(commPostRepository.findByUlid(post.getUlid())).willReturn(Optional.of(post));
        willDoNothing().given(multipartDataProcessor).deleteFiles(any(JsonNode.class));

        // when
        commPostApplicationService.removeByUlid(post.getUlid(),memberUuid);

        // then
        then(commPostValidationService).should().validateAccessibleCommPost(post.getUlid(),memberUuid);
        then(commPostRepository).should().findByUlid(post.getUlid());
        then(multipartDataProcessor).should().deleteFiles(any(JsonNode.class));
        then(commPostRepository).should().save(any(CommPostEntity.class));
    }

    @Test
    @DisplayName("Redis에 조회수값이 있으면 조회")
    void readViewCountShouldReturnRedisValueWhenRedisHasValueTest() {
        // given
        String ulid = generator.generate(null,null,null, EventType.INSERT);
        given(commPostViewCountRedisRepository.read(ulid)).willReturn(100L);

        // when
        Long result = commPostApplicationService.readViewCount(ulid);

        // then
        assertThat(result).isEqualTo(100L);
        then(commPostRepository).should(never()).findByUlid(any());
    }

    @Test
    @DisplayName("Redis에 조회수가 없고 DB에 있으면 DB에서 값을 조회")
    void readViewCountShouldReturnDbValueWhenRedisIsEmptyAndDbHasValueTest() {
        // given
        String ulid = generator.generate(null,null,null, EventType.INSERT);
        given(commPostViewCountRedisRepository.read(ulid)).willReturn(null);
        CommPostEntity commPostEntity = createCommPostEntityBuilder()
                .secondaryCategory(createTestCommSecondaryCategoryEntity())
                .authMember(createMemberBasicAdminEntityWithUuid())
                .createMember(createMemberBasicAdminEntityWithUuid())
                .viewCount(55L)
                .build();
        given(commPostRepository.findByUlid(ulid)).willReturn(Optional.of(commPostEntity));

        // when
        Long result = commPostApplicationService.readViewCount(ulid);

        // then
        assertThat(result).isEqualTo(55L);
        then(commPostViewCountRedisRepository).should().write(ulid,55L);
    }

    @Test
    @DisplayName("Redis와 DB에 모두 조회수값이 없으면 예외 발생")
    void readViewCountShouldThrowExceptionWhenRedisIsEmptyAndDbEmptyTest() {
        // given
        String ulid = generator.generate(null,null,null, EventType.INSERT);
        given(commPostViewCountRedisRepository.read(ulid)).willReturn(null);
        given(commPostRepository.findByUlid(ulid)).willReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class,
                () -> commPostApplicationService.readViewCount(ulid));
    }

    @Test
    @DisplayName("조회수 락이 걸려있을 때 기존 조회수 가져오기")
    void increaseViewCountWhenLockExistsTest() {
        // given
        String ulid = generator.generate(null,null,null, EventType.INSERT);
        when(commPostViewLockRedisRepository.lock(ulid,memberUuid,10)).thenReturn(false);
        when(commPostViewCountRedisRepository.read(ulid)).thenReturn(10L);

        // when
        Long result = commPostApplicationService.increaseViewCount(ulid,memberUuid);

        // then
        verify(commPostViewLockRedisRepository, times(1)).lock(ulid,memberUuid,10);
        verify(commPostViewCountRedisRepository, times(1)).read(ulid);
        verify(commPostViewCountRedisRepository,never()).increase(anyString());
        assertThat(result).isEqualTo(10L);
    }

    @Test
    @DisplayName("조회수 락이 걸려있지 않을 때 조회수 증가")
    void increaseViewCountWhenLockNotExistTest() {
        // given
        String ulid = generator.generate(null,null,null, EventType.INSERT);
        when(commPostViewLockRedisRepository.lock(ulid,memberUuid,10)).thenReturn(true);
        when(commPostViewCountRedisRepository.increase(ulid)).thenReturn(11L);

        // when
        Long result = commPostApplicationService.increaseViewCount(ulid,memberUuid);

        // then
        verify(commPostViewLockRedisRepository,times(1)).lock(ulid,memberUuid,10L);
        verify(commPostViewCountRedisRepository,times(1)).increase(ulid);
        verify(commPostViewCountRedisRepository,never()).read(ulid);
        assertThat(result).isEqualTo(11L);
    }


}
