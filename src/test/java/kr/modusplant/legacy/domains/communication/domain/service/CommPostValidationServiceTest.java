package kr.modusplant.legacy.domains.communication.domain.service;

import com.fasterxml.jackson.databind.JsonNode;
import kr.modusplant.framework.out.persistence.entity.SiteMemberEntity;
import kr.modusplant.legacy.domains.common.error.DataPairOrderMismatchException;
import kr.modusplant.legacy.domains.communication.app.http.request.CommPostInsertRequest;
import kr.modusplant.legacy.domains.communication.common.util.app.http.request.CommPostRequestTestUtils;
import kr.modusplant.legacy.domains.communication.common.util.domain.CommPostTestUtils;
import kr.modusplant.legacy.domains.communication.common.util.entity.CommSecondaryCategoryEntityTestUtils;
import kr.modusplant.legacy.domains.communication.error.AccessDeniedException;
import kr.modusplant.framework.out.persistence.entity.CommPostEntity;
import kr.modusplant.framework.out.persistence.entity.CommSecondaryCategoryEntity;
import kr.modusplant.framework.out.persistence.repository.CommPostRepository;
import kr.modusplant.framework.out.persistence.repository.CommSecondaryCategoryRepository;
import kr.modusplant.shared.exception.EntityNotFoundException;
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
class CommPostValidationServiceTest implements CommPostRequestTestUtils, CommSecondaryCategoryEntityTestUtils {
    @Mock
    private CommPostRepository commPostRepository;

    @Mock
    private CommSecondaryCategoryRepository commCategoryRepository;

    @InjectMocks
    private CommPostValidationService commPostValidationService;

    @Test
    @DisplayName("컨텐츠 게시글 추가/수정 시 CommPostRequest는 유효한 입력")
    void validateCommPostInsertRequestTestSuccess() {
        // given & when
        when(commCategoryRepository.existsByUuid(requestBasicTypes.primaryCategoryUuid())).thenReturn(true);

        // then
        assertDoesNotThrow(() -> commPostValidationService.validateCommPostInsertRequest(requestBasicTypes));
    }

    @Test
    @DisplayName("컨텐츠 게시글 추가/수정 시 CommPostRequest의 categoryUuid가 유효하지 않으면 예외 발생")
    void validateCommPostInsertRequestInvalidCategoryUuidTest() {
        // given & when
        CommPostInsertRequest commPostInsertRequest = new CommPostInsertRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "유용한 컨텐츠 모음",
                allMediaFiles,
                allMediaFilesOrder
        );

        when(commCategoryRepository.existsByUuid(commPostInsertRequest.primaryCategoryUuid())).thenReturn(false);

        // then
        assertThrows(EntityNotFoundException.class,
                () -> commPostValidationService.validateCommPostInsertRequest(commPostInsertRequest));
    }

    @Test
    @DisplayName("컨텐츠 게시글 추가/수정 시 CommPostRequest의 Content와 OrderInfo가 유효하지 않으면 예외 발생")
    void validateCommPostInsertRequestInvalidContentAndOrderInfoTest() {
        // given & when
        CommPostInsertRequest commPostInsertRequest = new CommPostInsertRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "유용한 컨텐츠 모음",
                textImageFiles,
                imageTextFilesOrder
        );

        when(commCategoryRepository.existsByUuid(commPostInsertRequest.primaryCategoryUuid())).thenReturn(true);

        // then
        assertThrows(DataPairOrderMismatchException.class,
                () -> commPostValidationService.validateCommPostInsertRequest(commPostInsertRequest));
    }

    @Test
    @DisplayName("게시글이 존재하고 작성자가 본인일 경우 테스트 통과")
    void validateAccessibleCommPostTestSuccess() {
        // given
        UUID memberUuid = UUID.randomUUID();
        String ulid = CommPostTestUtils.generator.generate(null,null,null,EventType.INSERT);
        SiteMemberEntity memberEntity = mock(SiteMemberEntity.class);
        CommPostEntity commPostEntity = CommPostEntity.builder()
                .authMember(memberEntity)
                .secondaryCategory(mock(CommSecondaryCategoryEntity.class)) // 다른 필드도 필요시 같이 mock 처리
                .createMember(memberEntity)
                .likeCount(0)
                .viewCount(0L)
                .title("테스트 제목")
                .content(mock(JsonNode.class))
                .isDeleted(false)
                .build();

        // when
        when(commPostRepository.findByUlidAndIsDeletedFalse(ulid)).thenReturn(Optional.of(commPostEntity));
        when(commPostEntity.getAuthMember().getUuid()).thenReturn(memberUuid);

        assertDoesNotThrow(() -> commPostValidationService.validateAccessibleCommPost(ulid,memberUuid));
    }

    @Test
    @DisplayName("삭제되지 않은 게시글이 존재하지 않을 때 예외 발생")
    void validateAccessibleCommPostNotFoundTest() {
        UUID memberUuid = UUID.randomUUID();
        String ulid = CommPostTestUtils.generator.generate(null, null,null,EventType.INSERT);
        when(commPostRepository.findByUlidAndIsDeletedFalse(ulid)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> commPostValidationService.validateAccessibleCommPost(ulid,memberUuid));
    }

    @Test
    @DisplayName("권한이 없는 사용자가 접근하면 예외 발생")
    void validateAccessibleCommPostTestFail() {
        // given
        UUID memberUuid = UUID.randomUUID();
        String ulid = CommPostTestUtils.generator.generate(null, null,null,EventType.INSERT);
        SiteMemberEntity memberEntity = mock(SiteMemberEntity.class);
        CommPostEntity commPostEntity = CommPostEntity.builder()
                .authMember(memberEntity)
                .secondaryCategory(mock(CommSecondaryCategoryEntity.class)) // 다른 필드도 필요시 같이 mock 처리
                .createMember(memberEntity)
                .likeCount(0)
                .viewCount(0L)
                .title("테스트 제목")
                .content(mock(JsonNode.class))
                .isDeleted(false)
                .build();

        // when
        when(commPostRepository.findByUlidAndIsDeletedFalse(ulid)).thenReturn(Optional.of(commPostEntity));
        when(commPostEntity.getAuthMember().getUuid()).thenReturn(UUID.randomUUID());

        assertThrows(AccessDeniedException.class,
                () -> commPostValidationService.validateAccessibleCommPost(ulid,memberUuid));
    }

    @Test
    @DisplayName("ULID 존재할 경우 통과")
    void validateNotFoundUlidExists() {
        String ulid = CommPostTestUtils.generator.generate(null, null, null, EventType.INSERT);
        when(commPostRepository.existsByUlid(ulid)).thenReturn(true);
        assertDoesNotThrow(() -> commPostValidationService.validateNotFoundUlid(ulid));
    }

    @Test
    @DisplayName("ULID 존재하지 않을 경우 예외 발생")
    void validateNotFoundUlidNotExists() {
        // null ULID
        // given & when
        final String nullUlid = null;

        // then
        assertThrows(EntityNotFoundException.class, () ->
                commPostValidationService.validateNotFoundUlid(nullUlid));

        // Not Found ULID
        // given & when
        String notFoundUlid = CommPostTestUtils.generator.generate(null, null, null, EventType.INSERT);
        when(commPostRepository.existsByUlid(notFoundUlid)).thenReturn(false);

        // then
        assertThrows(EntityNotFoundException.class, () ->
                commPostValidationService.validateNotFoundUlid(notFoundUlid));
    }

}