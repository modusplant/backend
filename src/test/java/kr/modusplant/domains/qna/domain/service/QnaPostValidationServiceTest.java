package kr.modusplant.domains.qna.domain.service;

import com.fasterxml.jackson.databind.JsonNode;
import kr.modusplant.domains.group.persistence.entity.PlantGroupEntity;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.qna.app.http.request.QnaPostRequest;
import kr.modusplant.domains.qna.common.util.http.request.QnaPostRequestTestUtils;
import kr.modusplant.domains.qna.domain.service.QnaPostValidationService;
import kr.modusplant.domains.qna.error.PostAccessDeniedException;
import kr.modusplant.domains.qna.persistence.entity.QnaPostEntity;
import kr.modusplant.domains.qna.persistence.repository.QnaPostRepository;
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

import static kr.modusplant.domains.qna.common.util.domain.QnaPostTestUtils.generator;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QnaPostValidationServiceTest implements QnaPostRequestTestUtils {
    @Mock
    private QnaPostRepository qnaPostRepository;
    @InjectMocks
    private QnaPostValidationService qnaPostValidationService;

    @Test
    @DisplayName("팁 게시글 추가/수정 시 QnaPostRequest는 유효한 입력")
    void validateQnaPostInsertRequestTestSuccess() {
        assertDoesNotThrow(() -> qnaPostValidationService.validateQnaPostRequest(requestBasicTypes));
    }

    @Test
    @DisplayName("팁 게시글 추가/수정 시 QnaPostRequest의 groupOrder가 유효하지 않으면 예외 발생")
    void validateQnaPostInsertRequestInvalidGroupOrderTest() {
        QnaPostRequest qnaPostRequest = new QnaPostRequest(
                -1,
                "유용한 팁 모음",
                allMediaFiles,
                allMediaFilesOrder
        );

        assertThrows(IllegalArgumentException.class,
                () -> qnaPostValidationService.validateQnaPostRequest(qnaPostRequest));
    }

    @Test
    @DisplayName("팁 게시글 추가/수정 시 QnaPostRequest의 title이 유효하지 않으면 예외 발생")
    void validateQnaPostInsertRequestInvalidTitleTest() {
        QnaPostRequest qnaPostRequest = new QnaPostRequest(
                2,
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                allMediaFiles,
                allMediaFilesOrder
        );

        assertThrows(IllegalArgumentException.class,
                () -> qnaPostValidationService.validateQnaPostRequest(qnaPostRequest));

    }

    @Test
    @DisplayName("팁 게시글 추가/수정 시 QnaPostRequest의 Content와 OrderInfo가 유효하지 않으면 예외 발생")
    void validateQnaPostInsertRequestInvalidContentAndOrderInfoTest() {
        QnaPostRequest qnaPostRequest = new QnaPostRequest(
                1,
                "유용한 팁 모음",
                textImageFiles,
                imageTextFilesOrder
        );

        assertThrows(IllegalArgumentException.class,
                () -> qnaPostValidationService.validateQnaPostRequest(qnaPostRequest));
    }

    @Test
    @DisplayName("게시글이 존재하고 작성자가 본인일 경우 테스트 통과")
    void validateAccessibleQnaPostTestSuccess() {
        // given
        UUID memberUuid = UUID.randomUUID();
        String ulid = generator.generate(null,null,null,EventType.INSERT);
        SiteMemberEntity memberEntity = mock(SiteMemberEntity.class);
        QnaPostEntity qnaPostEntity = QnaPostEntity.builder()
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
        when(qnaPostRepository.findByUlid(ulid)).thenReturn(Optional.of(qnaPostEntity));
        when(qnaPostEntity.getAuthMember().getUuid()).thenReturn(memberUuid);

        assertDoesNotThrow(() -> qnaPostValidationService.validateAccessibleQnaPost(ulid,memberUuid));
    }

    @Test
    @DisplayName("게시글이 존재하지 않을 때 예외 발생")
    void validateAccessibleQnaPostNotFoundTest() {
        UUID memberUuid = UUID.randomUUID();
        String ulid = generator.generate(null, null,null,EventType.INSERT);
        when(qnaPostRepository.findByUlid(ulid)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundWithUlidException.class,
                () -> qnaPostValidationService.validateAccessibleQnaPost(ulid,memberUuid));
    }

    @Test
    @DisplayName("삭제되지 않은 게시글이 존재하지 않을 때 예외 발생")
    void validateNotFoundUndeletedQnaPostTest() {
        UUID memberUuid = UUID.randomUUID();
        String ulid = generator.generate(null, null,null,EventType.INSERT);
        SiteMemberEntity memberEntity = mock(SiteMemberEntity.class);
        QnaPostEntity qnaPostEntity = QnaPostEntity.builder()
                .authMember(memberEntity)
                .group(mock(PlantGroupEntity.class)) // 다른 필드도 필요시 같이 mock 처리
                .createMember(memberEntity)
                .likeCount(0)
                .viewCount(0L)
                .title("테스트 제목")
                .content(mock(JsonNode.class))
                .isDeleted(true)
                .build();
        when(qnaPostRepository.findByUlid(ulid)).thenReturn(Optional.of(qnaPostEntity));
        assertThrows(EntityNotFoundWithUlidException.class,
                () -> qnaPostValidationService.validateAccessibleQnaPost(ulid,memberUuid));
    }

    @Test
    @DisplayName("권한이 없는 사용자가 접근하면 예외 발생")
    void validateAccessibleQnaPostTestFail() {
        // given
        UUID memberUuid = UUID.randomUUID();
        String ulid = generator.generate(null, null,null,EventType.INSERT);
        SiteMemberEntity memberEntity = mock(SiteMemberEntity.class);
        QnaPostEntity qnaPostEntity = QnaPostEntity.builder()
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
        when(qnaPostRepository.findByUlid(ulid)).thenReturn(Optional.of(qnaPostEntity));
        when(qnaPostEntity.getAuthMember().getUuid()).thenReturn(UUID.randomUUID());

        assertThrows(PostAccessDeniedException.class,
                () -> qnaPostValidationService.validateAccessibleQnaPost(ulid,memberUuid));
    }

    @Test
    @DisplayName("ULID 존재할 경우 통과")
    void validateNotFoundUlidExists() {
        String ulid = generator.generate(null, null,null,EventType.INSERT);
        when(qnaPostRepository.existsByUlid(ulid)).thenReturn(true);
        assertDoesNotThrow(() -> qnaPostValidationService.validateNotFoundUlid(ulid));
    }

    @Test
    @DisplayName("ULID 존재하지 않을 경우 예외 발생")
    void validateNotFoundUlidNotExists() {
        String ulid = generator.generate(null, null,null,EventType.INSERT);
        when(qnaPostRepository.existsByUlid(ulid)).thenReturn(false);
        assertThrows(EntityNotFoundWithUlidException.class, () -> {
            qnaPostValidationService.validateNotFoundUlid(ulid);
        });
    }

}