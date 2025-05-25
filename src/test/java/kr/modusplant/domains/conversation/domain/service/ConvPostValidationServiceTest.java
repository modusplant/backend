package kr.modusplant.domains.conversation.domain.service;

import com.fasterxml.jackson.databind.JsonNode;
import kr.modusplant.domains.conversation.common.util.http.request.ConvPostRequestTestUtils;
import kr.modusplant.domains.group.persistence.entity.PlantGroupEntity;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.conversation.app.http.request.ConvPostRequest;
import kr.modusplant.domains.conversation.error.PostAccessDeniedException;
import kr.modusplant.domains.conversation.persistence.entity.ConvPostEntity;
import kr.modusplant.domains.conversation.persistence.repository.ConvPostRepository;
import kr.modusplant.global.error.EntityNotFoundWithUlidException;
import org.hibernate.generator.EventType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static kr.modusplant.domains.conversation.common.util.domain.ConvPostTestUtils.generator;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConvPostValidationServiceTest implements ConvPostRequestTestUtils {
    @Mock
    private ConvPostRepository convPostRepository;
    @InjectMocks
    private ConvPostValidationService convPostValidationService;

    @Test
    @DisplayName("팁 게시글 추가/수정 시 ConvPostRequest는 유효한 입력")
    void validateConvPostInsertRequestTestSuccess() {
        assertDoesNotThrow(() -> convPostValidationService.validateConvPostRequest(requestBasicTypes));
    }

    @Test
    @DisplayName("팁 게시글 추가/수정 시 ConvPostRequest의 groupOrder가 유효하지 않으면 예외 발생")
    void validateConvPostInsertRequestInvalidGroupOrderTest() {
        ConvPostRequest convPostRequest = new ConvPostRequest(
                -1,
                "유용한 팁 모음",
                allMediaFiles,
                allMediaFilesOrder
        );

        assertThrows(IllegalArgumentException.class,
                () -> convPostValidationService.validateConvPostRequest(convPostRequest));
    }

    @Test
    @DisplayName("팁 게시글 추가/수정 시 ConvPostRequest의 title이 유효하지 않으면 예외 발생")
    void validateConvPostInsertRequestInvalidTitleTest() {
        ConvPostRequest convPostRequest = new ConvPostRequest(
                2,
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                allMediaFiles,
                allMediaFilesOrder
        );

        assertThrows(IllegalArgumentException.class,
                () -> convPostValidationService.validateConvPostRequest(convPostRequest));

    }

    @Test
    @DisplayName("팁 게시글 추가/수정 시 ConvPostRequest의 Content와 OrderInfo가 유효하지 않으면 예외 발생")
    void validateConvPostInsertRequestInvalidContentAndOrderInfoTest() {
        ConvPostRequest convPostRequest = new ConvPostRequest(
                1,
                "유용한 팁 모음",
                textImageFiles,
                imageTextFilesOrder
        );

        assertThrows(IllegalArgumentException.class,
                () -> convPostValidationService.validateConvPostRequest(convPostRequest));
    }

    @Test
    @DisplayName("게시글이 존재하고 작성자가 본인일 경우 테스트 통과")
    void validateAccessibleConvPostTestSuccess() {
        // given
        UUID memberUuid = UUID.randomUUID();
        String ulid = generator.generate(null,null,null,EventType.INSERT);
        SiteMemberEntity memberEntity = mock(SiteMemberEntity.class);
        ConvPostEntity convPostEntity = ConvPostEntity.builder()
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
        when(convPostRepository.findByUlid(ulid)).thenReturn(Optional.of(convPostEntity));
        when(convPostEntity.getAuthMember().getUuid()).thenReturn(memberUuid);

        assertDoesNotThrow(() -> convPostValidationService.validateAccessibleConvPost(ulid,memberUuid));
    }

    @Test
    @DisplayName("게시글이 존재하지 않을 때 예외 발생")
    void validateAccessibleConvPostNotFoundTest() {
        UUID memberUuid = UUID.randomUUID();
        String ulid = generator.generate(null, null,null,EventType.INSERT);
        when(convPostRepository.findByUlid(ulid)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundWithUlidException.class,
                () -> convPostValidationService.validateAccessibleConvPost(ulid,memberUuid));
    }

    @Test
    @DisplayName("삭제되지 않은 게시글이 존재하지 않을 때 예외 발생")
    void validateNotFoundUndeletedConvPostTest() {
        UUID memberUuid = UUID.randomUUID();
        String ulid = generator.generate(null, null,null,EventType.INSERT);
        SiteMemberEntity memberEntity = mock(SiteMemberEntity.class);
        ConvPostEntity convPostEntity = ConvPostEntity.builder()
                .authMember(memberEntity)
                .group(mock(PlantGroupEntity.class)) // 다른 필드도 필요시 같이 mock 처리
                .createMember(memberEntity)
                .likeCount(0)
                .viewCount(0L)
                .title("테스트 제목")
                .content(mock(JsonNode.class))
                .isDeleted(true)
                .build();
        when(convPostRepository.findByUlid(ulid)).thenReturn(Optional.of(convPostEntity));
        assertThrows(EntityNotFoundWithUlidException.class,
                () -> convPostValidationService.validateAccessibleConvPost(ulid,memberUuid));
    }

    @Test
    @DisplayName("권한이 없는 사용자가 접근하면 예외 발생")
    void validateAccessibleConvPostTestFail() {
        // given
        UUID memberUuid = UUID.randomUUID();
        String ulid = generator.generate(null, null,null,EventType.INSERT);
        SiteMemberEntity memberEntity = mock(SiteMemberEntity.class);
        ConvPostEntity convPostEntity = ConvPostEntity.builder()
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
        when(convPostRepository.findByUlid(ulid)).thenReturn(Optional.of(convPostEntity));
        when(convPostEntity.getAuthMember().getUuid()).thenReturn(UUID.randomUUID());

        assertThrows(PostAccessDeniedException.class,
                () -> convPostValidationService.validateAccessibleConvPost(ulid,memberUuid));
    }

    @Test
    @DisplayName("ULID 존재할 경우 통과")
    void validateNotFoundUlidExists() {
        String ulid = generator.generate(null, null,null,EventType.INSERT);
        when(convPostRepository.existsByUlid(ulid)).thenReturn(true);
        assertDoesNotThrow(() -> convPostValidationService.validateNotFoundUlid(ulid));
    }

    @Test
    @DisplayName("ULID 존재하지 않을 경우 예외 발생")
    void validateNotFoundUlidNotExists() {
        String ulid = generator.generate(null, null,null,EventType.INSERT);
        when(convPostRepository.existsByUlid(ulid)).thenReturn(false);
        assertThrows(EntityNotFoundWithUlidException.class, () -> {
            convPostValidationService.validateNotFoundUlid(ulid);
        });
    }

}