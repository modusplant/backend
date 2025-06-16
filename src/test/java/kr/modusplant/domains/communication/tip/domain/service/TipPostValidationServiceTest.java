package kr.modusplant.domains.communication.tip.domain.service;

import com.fasterxml.jackson.databind.JsonNode;
import kr.modusplant.domains.communication.common.error.CategoryNotFoundException;
import kr.modusplant.domains.communication.common.error.PostAccessDeniedException;
import kr.modusplant.domains.communication.common.error.PostNotFoundException;
import kr.modusplant.domains.communication.tip.app.http.request.TipPostInsertRequest;
import kr.modusplant.domains.communication.tip.common.util.app.http.request.TipPostRequestTestUtils;
import kr.modusplant.domains.communication.tip.common.util.domain.TipPostTestUtils;
import kr.modusplant.domains.communication.tip.common.util.entity.TipCategoryEntityTestUtils;
import kr.modusplant.domains.communication.tip.persistence.entity.TipCategoryEntity;
import kr.modusplant.domains.communication.tip.persistence.entity.TipPostEntity;
import kr.modusplant.domains.communication.tip.persistence.repository.TipCategoryRepository;
import kr.modusplant.domains.communication.tip.persistence.repository.TipPostRepository;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import org.hibernate.generator.EventType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TipPostValidationServiceTest implements TipPostRequestTestUtils, TipCategoryEntityTestUtils {
    @Mock
    private TipPostRepository tipPostRepository;

    @Mock
    private TipCategoryRepository tipCategoryRepository;

    @InjectMocks
    private TipPostValidationService tipPostValidationService;

    @Test
    @DisplayName("팁 게시글 추가/수정 시 TipPostRequest는 유효한 입력")
    void validateTipPostInsertRequestTestSuccess() {
        // given & when
        when(tipCategoryRepository.existsByUuid(requestBasicTypes.categoryUuid())).thenReturn(true);

        // then
        assertDoesNotThrow(() -> tipPostValidationService.validateTipPostInsertRequest(requestBasicTypes));
    }

    @Test
    @DisplayName("팁 게시글 추가/수정 시 TipPostRequest의 categoryUuid가 유효하지 않으면 예외 발생")
    void validateTipPostInsertRequestInvalidCategoryUuidTest() {
        // given & when
        TipPostInsertRequest tipPostInsertRequest = new TipPostInsertRequest(
                UUID.randomUUID(),
                "유용한 팁 모음",
                allMediaFiles,
                allMediaFilesOrder
        );

        when(tipCategoryRepository.existsByUuid(tipPostInsertRequest.categoryUuid())).thenReturn(false);

        // then
        assertThrows(CategoryNotFoundException.class,
                () -> tipPostValidationService.validateTipPostInsertRequest(tipPostInsertRequest));
    }

    @Test
    @DisplayName("팁 게시글 추가/수정 시 TipPostRequest의 title이 유효하지 않으면 예외 발생")
    void validateTipPostInsertRequestInvalidTitleTest() {
        // given & when
        TipPostInsertRequest tipPostInsertRequest = new TipPostInsertRequest(
                UUID.randomUUID(),
                "a".repeat(151),
                allMediaFiles,
                allMediaFilesOrder
        );

        when(tipCategoryRepository.existsByUuid(tipPostInsertRequest.categoryUuid())).thenReturn(true);

        // then
        assertThrows(IllegalArgumentException.class,
                () -> tipPostValidationService.validateTipPostInsertRequest(tipPostInsertRequest));

    }

    @Test
    @DisplayName("팁 게시글 추가/수정 시 TipPostRequest의 Content와 OrderInfo가 유효하지 않으면 예외 발생")
    void validateTipPostInsertRequestInvalidContentAndOrderInfoTest() {
        // given & when
        TipPostInsertRequest tipPostInsertRequest = new TipPostInsertRequest(
                UUID.randomUUID(),
                "유용한 팁 모음",
                textImageFiles,
                imageTextFilesOrder
        );

        when(tipCategoryRepository.existsByUuid(tipPostInsertRequest.categoryUuid())).thenReturn(true);

        // then
        assertThrows(IllegalArgumentException.class,
                () -> tipPostValidationService.validateTipPostInsertRequest(tipPostInsertRequest));
    }

    @Test
    @DisplayName("게시글이 존재하고 작성자가 본인일 경우 테스트 통과")
    void validateAccessibleTipPostTestSuccess() {
        // given
        UUID memberUuid = UUID.randomUUID();
        String ulid = TipPostTestUtils.generator.generate(null,null,null,EventType.INSERT);
        SiteMemberEntity memberEntity = mock(SiteMemberEntity.class);
        TipPostEntity tipPostEntity = TipPostEntity.builder()
                .authMember(memberEntity)
                .category(mock(TipCategoryEntity.class)) // 다른 필드도 필요시 같이 mock 처리
                .createMember(memberEntity)
                .likeCount(0)
                .viewCount(0L)
                .title("테스트 제목")
                .content(mock(JsonNode.class))
                .isDeleted(false)
                .build();

        // when
        when(tipPostRepository.findByUlidAndIsDeletedFalse(ulid)).thenReturn(Optional.of(tipPostEntity));
        when(tipPostEntity.getAuthMember().getUuid()).thenReturn(memberUuid);

        assertDoesNotThrow(() -> tipPostValidationService.validateAccessibleTipPost(ulid, memberUuid));
    }

    @Test
    @DisplayName("삭제되지 않은 게시글이 존재하지 않을 때 예외 발생")
    void validateAccessibleTipPostNotFoundTest() {
        UUID memberUuid = UUID.randomUUID();
        String ulid = TipPostTestUtils.generator.generate(null, null,null,EventType.INSERT);
        when(tipPostRepository.findByUlidAndIsDeletedFalse(ulid)).thenReturn(Optional.empty());
        assertThrows(PostNotFoundException.class,
                () -> tipPostValidationService.validateAccessibleTipPost(ulid,memberUuid));
    }

    @Test
    @DisplayName("권한이 없는 사용자가 접근하면 예외 발생")
    void validateAccessibleTipPostTestFail() {
        // given
        UUID memberUuid = UUID.randomUUID();
        String ulid = TipPostTestUtils.generator.generate(null, null,null,EventType.INSERT);
        SiteMemberEntity memberEntity = mock(SiteMemberEntity.class);
        TipPostEntity tipPostEntity = TipPostEntity.builder()
                .authMember(memberEntity)
                .category(mock(TipCategoryEntity.class)) // 다른 필드도 필요시 같이 mock 처리
                .createMember(memberEntity)
                .likeCount(0)
                .viewCount(0L)
                .title("테스트 제목")
                .content(mock(JsonNode.class))
                .isDeleted(false)
                .build();

        // when
        when(tipPostRepository.findByUlidAndIsDeletedFalse(ulid)).thenReturn(Optional.of(tipPostEntity));
        when(tipPostEntity.getAuthMember().getUuid()).thenReturn(UUID.randomUUID());

        assertThrows(PostAccessDeniedException.class,
                () -> tipPostValidationService.validateAccessibleTipPost(ulid,memberUuid));
    }

    @Test
    @DisplayName("ULID 존재할 경우 통과")
    void validateNotFoundUlidExists() {
        String ulid = TipPostTestUtils.generator.generate(null, null, null, EventType.INSERT);
        when(tipPostRepository.existsByUlid(ulid)).thenReturn(true);
        assertDoesNotThrow(() -> tipPostValidationService.validateNotFoundUlid(ulid));
    }

    @Test
    @DisplayName("ULID 존재하지 않을 경우 예외 발생")
    void validateNotFoundUlidNotExists() {
        // null ULID
        // given & when
        final String nullUlid = null;

        // then
        assertThrows(PostNotFoundException.class, () ->
                tipPostValidationService.validateNotFoundUlid(nullUlid));

        // Not Found ULID
        // given & when
        String notFoundUlid = TipPostTestUtils.generator.generate(null, null, null, EventType.INSERT);
        when(tipPostRepository.existsByUlid(notFoundUlid)).thenReturn(false);

        // then
        assertThrows(PostNotFoundException.class, () ->
                tipPostValidationService.validateNotFoundUlid(notFoundUlid));
    }

}