package kr.modusplant.domains.tip.domain.service;

import com.fasterxml.jackson.databind.JsonNode;
import kr.modusplant.domains.group.persistence.entity.PlantGroupEntity;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.tip.app.http.request.TipPostRequest;
import kr.modusplant.domains.tip.common.util.http.request.TipPostRequestTestUtils;
import kr.modusplant.domains.tip.error.PostAccessDeniedException;
import kr.modusplant.domains.tip.persistence.entity.TipPostEntity;
import kr.modusplant.domains.tip.persistence.repository.TipPostRepository;
import kr.modusplant.global.error.EntityNotFoundWithUlidException;
import kr.modusplant.global.error.InvalidInputException;
import org.hibernate.generator.EventType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static kr.modusplant.domains.tip.common.util.domain.TipPostTestUtils.generator;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TipPostValidationServiceTest implements TipPostRequestTestUtils {
    @Mock
    private TipPostRepository tipPostRepository;
    @InjectMocks
    private TipPostValidationService tipPostValidationService;

    @Test
    @DisplayName("팁 게시글 추가 시 TipPostRequest는 유효한 입력")
    void validateTipPostInsertRequestTestSuccess() {
        assertDoesNotThrow(() -> tipPostValidationService.validateTipPostInsertRequest(insertRequestBasicTypes));
    }

    @Test
    @DisplayName("팁 게시글 추가 시 TipPostRequest의 groupOrder가 유효하지 않으면 예외 발생")
    void validateTipPostInsertRequestInvalidGroupOrderTest() {
        TipPostRequest tipPostRequest = new TipPostRequest(
                -1,
                "유용한 팁 모음",
                allMediaFiles,
                allMediaFilesOrder
        );

        assertThrows(InvalidInputException.class,
                () -> tipPostValidationService.validateTipPostInsertRequest(updateRequestWithTitleAndContentAndOrderInfo));
        assertThrows(InvalidInputException.class,
                () -> tipPostValidationService.validateTipPostInsertRequest(tipPostRequest));
    }

    @Test
    @DisplayName("팁 게시글 추가 시 TipPostRequest의 title이 유효하지 않으면 예외 발생")
    void validateTipPostInsertRequestInvalidTitleTest() {
        TipPostRequest tipPostRequest = new TipPostRequest(
                2,
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                allMediaFiles,
                allMediaFilesOrder
        );

        assertThrows(InvalidInputException.class,
                () -> tipPostValidationService.validateTipPostInsertRequest(updateRequestWithGroupOrderAndContentAndOrderInfo));
        assertThrows(InvalidInputException.class,
                () -> tipPostValidationService.validateTipPostInsertRequest(tipPostRequest));

    }

    @Test
    @DisplayName("팁 게시글 추가 시 TipPostRequest의 Content와 OrderInfo가 유효하지 않으면 예외 발생")
    void validateTipPostInsertRequestInvalidContentAndOrderInfoTest() {
        TipPostRequest tipPostRequest = new TipPostRequest(
                1,
                "유용한 팁 모음",
                textImageFiles,
                imageTextFilesOrder
        );

        assertThrows(InvalidInputException.class,
                () -> tipPostValidationService.validateTipPostInsertRequest(updateRequestWithGroupOrderAndTitle));
        assertThrows(InvalidInputException.class,
                () -> tipPostValidationService.validateTipPostInsertRequest(tipPostRequest));
    }

    @Test
    @DisplayName("팁 게시글 수정 시 TipPostRequest는 유효한 입력")
    void validateTipPostUpdateRequestTestSuccess() {
        assertDoesNotThrow(() -> tipPostValidationService.validateTipPostUpdateRequest(updateRequestAllFields));
        assertDoesNotThrow(() -> tipPostValidationService.validateTipPostUpdateRequest(updateRequestWithGroupOrder));
        assertDoesNotThrow(() -> tipPostValidationService.validateTipPostUpdateRequest(updateRequestWithTitle));
        assertDoesNotThrow(() -> tipPostValidationService.validateTipPostUpdateRequest(updateRequestWithContentAndOrderInfo));
        assertDoesNotThrow(() -> tipPostValidationService.validateTipPostUpdateRequest(updateRequestWithGroupOrderAndTitle));
        assertDoesNotThrow(() -> tipPostValidationService.validateTipPostUpdateRequest(updateRequestWithGroupOrderAndContentAndOrderInfo));
        assertDoesNotThrow(() -> tipPostValidationService.validateTipPostUpdateRequest(updateRequestWithTitleAndContentAndOrderInfo));
    }

    @Test
    @DisplayName("팁 게시글 수정 시 TipPostRequest의 Content와 OrderInfo 중 1개만 들어오면 예외 발생")
    void validateTipPostUpdateRequestMissingBetweenContentAndOrderInfoTest() {
        TipPostRequest tipPostRequest = new TipPostRequest(
                1,
                "유용한 팁 모음",
                allMediaFiles,
                null
        );

        assertThrows(InvalidInputException.class,
                () -> tipPostValidationService.validateTipPostUpdateRequest(tipPostRequest));
    }

    @Test
    @DisplayName("게시글이 존재하고 작성자가 본인일 경우 테스트 통과")
    void validateAccessibleTipPostTestSuccess() {
        // given
        UUID memberUuid = UUID.randomUUID();
        String ulid = generator.generate(null,null,null,EventType.INSERT);
        SiteMemberEntity memberEntity = mock(SiteMemberEntity.class);
        TipPostEntity tipPostEntity = TipPostEntity.builder()
                .authMember(memberEntity)
                .group(mock(PlantGroupEntity.class)) // 다른 필드도 필요시 같이 mock 처리
                .createMember(memberEntity)
                .likeCount(0)
                .viewCount(0L)
                .title("테스트 제목")
                .content(mock(JsonNode.class))
                .isDeleted(false)
                .build();

        // when
        when(tipPostRepository.findByUlid(ulid)).thenReturn(Optional.of(tipPostEntity));
        when(tipPostEntity.getAuthMember().getUuid()).thenReturn(memberUuid);

        assertDoesNotThrow(() -> tipPostValidationService.validateAccessibleTipPost(ulid,memberUuid));
    }

    @Test
    @DisplayName("게시글이 존재하지 않을 때 예외 발생")
    void validateAccessibleTipPostNotFoundTest() {
        UUID memberUuid = UUID.randomUUID();
        String ulid = generator.generate(null, null,null,EventType.INSERT);
        when(tipPostRepository.findByUlid(ulid)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundWithUlidException.class,
                () -> tipPostValidationService.validateAccessibleTipPost(ulid,memberUuid));
    }

    @Test
    @DisplayName("삭제되지 않은 게시글이 존재하지 않을 때 예외 발생")
    void validateNotFoundUndeletedTipPostTest() {
        UUID memberUuid = UUID.randomUUID();
        String ulid = generator.generate(null, null,null,EventType.INSERT);
        SiteMemberEntity memberEntity = mock(SiteMemberEntity.class);
        TipPostEntity tipPostEntity = TipPostEntity.builder()
                .authMember(memberEntity)
                .group(mock(PlantGroupEntity.class)) // 다른 필드도 필요시 같이 mock 처리
                .createMember(memberEntity)
                .likeCount(0)
                .viewCount(0L)
                .title("테스트 제목")
                .content(mock(JsonNode.class))
                .isDeleted(true)
                .build();
        when(tipPostRepository.findByUlid(ulid)).thenReturn(Optional.of(tipPostEntity));
        assertThrows(EntityNotFoundWithUlidException.class,
                () -> tipPostValidationService.validateAccessibleTipPost(ulid,memberUuid));
    }

    @Test
    @DisplayName("권한이 없는 사용자가 접근하면 예외 발생")
    void validateAccessibleTipPostTestFail() {
        // given
        UUID memberUuid = UUID.randomUUID();
        String ulid = generator.generate(null, null,null,EventType.INSERT);
        SiteMemberEntity memberEntity = mock(SiteMemberEntity.class);
        TipPostEntity tipPostEntity = TipPostEntity.builder()
                .authMember(memberEntity)
                .group(mock(PlantGroupEntity.class)) // 다른 필드도 필요시 같이 mock 처리
                .createMember(memberEntity)
                .likeCount(0)
                .viewCount(0L)
                .title("테스트 제목")
                .content(mock(JsonNode.class))
                .isDeleted(false)
                .build();

        // when
        when(tipPostRepository.findByUlid(ulid)).thenReturn(Optional.of(tipPostEntity));
        when(tipPostEntity.getAuthMember().getUuid()).thenReturn(UUID.randomUUID());

        assertThrows(PostAccessDeniedException.class,
                () -> tipPostValidationService.validateAccessibleTipPost(ulid,memberUuid));
    }

    @Test
    @DisplayName("ULID 존재할 경우 통과")
    void validateNotFoundUlidExists() {
        String ulid = generator.generate(null, null,null,EventType.INSERT);
        when(tipPostRepository.existsByUlid(ulid)).thenReturn(true);
        assertDoesNotThrow(() -> tipPostValidationService.validateNotFoundUlid(ulid));
    }

    @Test
    @DisplayName("ULID 존재하지 않을 경우 예외 발생")
    void validateNotFoundUlidNotExists() {
        String ulid = generator.generate(null, null,null,EventType.INSERT);
        when(tipPostRepository.existsByUlid(ulid)).thenReturn(false);
        assertThrows(EntityNotFoundWithUlidException.class, () -> {
            tipPostValidationService.validateNotFoundUlid(ulid);
        });
    }

}