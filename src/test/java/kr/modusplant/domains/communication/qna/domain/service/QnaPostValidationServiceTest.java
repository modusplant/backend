package kr.modusplant.domains.communication.qna.domain.service;

import com.fasterxml.jackson.databind.JsonNode;
import kr.modusplant.domains.communication.common.error.CategoryNotFoundException;
import kr.modusplant.domains.communication.common.error.PostAccessDeniedException;
import kr.modusplant.domains.communication.common.error.PostNotFoundException;
import kr.modusplant.domains.communication.qna.app.http.request.QnaPostInsertRequest;
import kr.modusplant.domains.communication.qna.common.util.app.http.request.QnaPostRequestTestUtils;
import kr.modusplant.domains.communication.qna.common.util.domain.QnaPostTestUtils;
import kr.modusplant.domains.communication.qna.common.util.entity.QnaCategoryEntityTestUtils;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaCategoryEntity;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaPostEntity;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaCategoryRepository;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaPostRepository;
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
class QnaPostValidationServiceTest implements QnaPostRequestTestUtils, QnaCategoryEntityTestUtils {
    @Mock
    private QnaPostRepository qnaPostRepository;

    @Mock
    private QnaCategoryRepository qnaCategoryRepository;

    @InjectMocks
    private QnaPostValidationService qnaPostValidationService;

    @Test
    @DisplayName("Q&A 게시글 추가/수정 시 QnaPostRequest는 유효한 입력")
    void validateQnaPostInsertRequestTestSuccess() {
        // given & when
        when(qnaCategoryRepository.existsByUuid(requestBasicTypes.categoryUuid())).thenReturn(true);

        // then
        assertDoesNotThrow(() -> qnaPostValidationService.validateQnaPostInsertRequest(requestBasicTypes));
    }

    @Test
    @DisplayName("Q&A 게시글 추가/수정 시 QnaPostRequest의 categoryUuid가 유효하지 않으면 예외 발생")
    void validateQnaPostInsertRequestInvalidCategoryUuidTest() {
        // given & when
        QnaPostInsertRequest qnaPostInsertRequest = new QnaPostInsertRequest(
                UUID.randomUUID(),
                "유용한 Q&A 모음",
                allMediaFiles,
                allMediaFilesOrder
        );

        when(qnaCategoryRepository.existsByUuid(qnaPostInsertRequest.categoryUuid())).thenReturn(false);

        // then
        assertThrows(CategoryNotFoundException.class,
                () -> qnaPostValidationService.validateQnaPostInsertRequest(qnaPostInsertRequest));
    }

    @Test
    @DisplayName("Q&A 게시글 추가/수정 시 QnaPostRequest의 title이 유효하지 않으면 예외 발생")
    void validateQnaPostInsertRequestInvalidTitleTest() {
        // given & when
        QnaPostInsertRequest qnaPostInsertRequest = new QnaPostInsertRequest(
                UUID.randomUUID(),
                "a".repeat(151),
                allMediaFiles,
                allMediaFilesOrder
        );

        when(qnaCategoryRepository.existsByUuid(qnaPostInsertRequest.categoryUuid())).thenReturn(true);

        // then
        assertThrows(IllegalArgumentException.class,
                () -> qnaPostValidationService.validateQnaPostInsertRequest(qnaPostInsertRequest));

    }

    @Test
    @DisplayName("Q&A 게시글 추가/수정 시 QnaPostRequest의 Content와 OrderInfo가 유효하지 않으면 예외 발생")
    void validateQnaPostInsertRequestInvalidContentAndOrderInfoTest() {
        // given & when
        QnaPostInsertRequest qnaPostInsertRequest = new QnaPostInsertRequest(
                UUID.randomUUID(),
                "유용한 Q&A 모음",
                textImageFiles,
                imageTextFilesOrder
        );

        when(qnaCategoryRepository.existsByUuid(qnaPostInsertRequest.categoryUuid())).thenReturn(true);

        // then
        assertThrows(IllegalArgumentException.class,
                () -> qnaPostValidationService.validateQnaPostInsertRequest(qnaPostInsertRequest));
    }

    @Test
    @DisplayName("게시글이 존재하고 작성자가 본인일 경우 테스트 통과")
    void validateAccessibleQnaPostTestSuccess() {
        // given
        UUID memberUuid = UUID.randomUUID();
        String ulid = QnaPostTestUtils.generator.generate(null,null,null,EventType.INSERT);
        SiteMemberEntity memberEntity = mock(SiteMemberEntity.class);
        QnaPostEntity qnaPostEntity = QnaPostEntity.builder()
                .authMember(memberEntity)
                .category(mock(QnaCategoryEntity.class)) // 다른 필드도 필요시 같이 mock 처리
                .createMember(memberEntity)
                .likeCount(0)
                .viewCount(0L)
                .title("테스트 제목")
                .content(mock(JsonNode.class))
                .isDeleted(false)
                .build();

        // when
        when(qnaPostRepository.findByUlidAndIsDeletedFalse(ulid)).thenReturn(Optional.of(qnaPostEntity));
        when(qnaPostEntity.getAuthMember().getUuid()).thenReturn(memberUuid);

        assertDoesNotThrow(() -> qnaPostValidationService.validateAccessibleQnaPost(ulid,memberUuid));
    }

    @Test
    @DisplayName("삭제되지 않은 게시글이 존재하지 않을 때 예외 발생")
    void validateAccessibleQnaPostNotFoundTest() {
        UUID memberUuid = UUID.randomUUID();
        String ulid = QnaPostTestUtils.generator.generate(null, null,null,EventType.INSERT);
        when(qnaPostRepository.findByUlidAndIsDeletedFalse(ulid)).thenReturn(Optional.empty());
        assertThrows(PostNotFoundException.class,
                () -> qnaPostValidationService.validateAccessibleQnaPost(ulid,memberUuid));
    }

    @Test
    @DisplayName("권한이 없는 사용자가 접근하면 예외 발생")
    void validateAccessibleQnaPostTestFail() {
        // given
        UUID memberUuid = UUID.randomUUID();
        String ulid = QnaPostTestUtils.generator.generate(null, null,null,EventType.INSERT);
        SiteMemberEntity memberEntity = mock(SiteMemberEntity.class);
        QnaPostEntity qnaPostEntity = QnaPostEntity.builder()
                .authMember(memberEntity)
                .category(mock(QnaCategoryEntity.class)) // 다른 필드도 필요시 같이 mock 처리
                .createMember(memberEntity)
                .likeCount(0)
                .viewCount(0L)
                .title("테스트 제목")
                .content(mock(JsonNode.class))
                .isDeleted(false)
                .build();

        // when
        when(qnaPostRepository.findByUlidAndIsDeletedFalse(ulid)).thenReturn(Optional.of(qnaPostEntity));
        when(qnaPostEntity.getAuthMember().getUuid()).thenReturn(UUID.randomUUID());

        assertThrows(PostAccessDeniedException.class,
                () -> qnaPostValidationService.validateAccessibleQnaPost(ulid,memberUuid));
    }

    @Test
    @DisplayName("ULID 존재할 경우 통과")
    void validateNotFoundUlidExists() {
        String ulid = QnaPostTestUtils.generator.generate(null, null, null, EventType.INSERT);
        when(qnaPostRepository.existsByUlid(ulid)).thenReturn(true);
        assertDoesNotThrow(() -> qnaPostValidationService.validateNotFoundUlid(ulid));
    }

    @Test
    @DisplayName("ULID 존재하지 않을 경우 예외 발생")
    void validateNotFoundUlidNotExists() {
        // null ULID
        // given & when
        final String nullUlid = null;

        // then
        assertThrows(PostNotFoundException.class, () ->
                qnaPostValidationService.validateNotFoundUlid(nullUlid));

        // Not Found ULID
        // given & when
        String notFoundUlid = QnaPostTestUtils.generator.generate(null, null, null, EventType.INSERT);
        when(qnaPostRepository.existsByUlid(notFoundUlid)).thenReturn(false);

        // then
        assertThrows(PostNotFoundException.class, () ->
                qnaPostValidationService.validateNotFoundUlid(notFoundUlid));
    }

}