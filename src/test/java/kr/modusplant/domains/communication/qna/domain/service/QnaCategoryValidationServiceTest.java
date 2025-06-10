package kr.modusplant.domains.communication.qna.domain.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import kr.modusplant.domains.common.context.DomainsServiceOnlyContext;
import kr.modusplant.domains.communication.qna.common.util.app.http.response.QnaCategoryResponseTestUtils;
import kr.modusplant.domains.communication.qna.common.util.entity.QnaCategoryEntityTestUtils;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaCategoryEntity;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaCategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static kr.modusplant.global.enums.ExceptionMessage.EXISTED_ENTITY;
import static kr.modusplant.global.enums.ExceptionMessage.NOT_FOUND_ENTITY;
import static kr.modusplant.global.util.ExceptionUtils.getFormattedExceptionMessage;
import static kr.modusplant.global.vo.CamelCaseWord.CATEGORY;
import static kr.modusplant.global.vo.CamelCaseWord.ORDER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@DomainsServiceOnlyContext
class QnaCategoryValidationServiceTest implements QnaCategoryResponseTestUtils, QnaCategoryEntityTestUtils {

    private final QnaCategoryValidationService qnaCategoryValidationService;
    private final QnaCategoryRepository qnaCategoryRepository;

    @Autowired
    QnaCategoryValidationServiceTest(QnaCategoryValidationService qnaCategoryValidationService, QnaCategoryRepository qnaCategoryRepository) {
        this.qnaCategoryValidationService = qnaCategoryValidationService;
        this.qnaCategoryRepository = qnaCategoryRepository;
    }

    @DisplayName("존재하는 순서 검증")
    @Test
    void validateExistedOrderTest() {
        // given
        Integer order = createTestQnaCategoryEntity().getOrder();

        // when
        given(qnaCategoryRepository.existsByOrder(order)).willReturn(true);

        // then
        EntityExistsException existsException = assertThrows(EntityExistsException.class,
                () -> qnaCategoryValidationService.validateExistedOrder(order));
        assertThat(existsException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                EXISTED_ENTITY.getValue(), ORDER, order, QnaCategoryEntity.class));
    }

    @DisplayName("존재하는 항목 검증")
    @Test
    void validateExistedQnaCategoryNameTest() {
        // given
        Integer order = createTestQnaCategoryEntity().getOrder();
        String category = createTestQnaCategoryEntity().getCategory();

        // when
        given(qnaCategoryRepository.existsByOrder(order)).willReturn(false);
        given(qnaCategoryRepository.existsByCategory(category)).willReturn(true);

        // then
        EntityExistsException existsException = assertThrows(EntityExistsException.class,
                () -> qnaCategoryValidationService.validateExistedCategory(category));
        assertThat(existsException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                EXISTED_ENTITY.getValue(), CATEGORY, category, QnaCategoryEntity.class));
    }

    @DisplayName("존재하지 않는 순서 검증")
    @Test
    void validateNotFoundUuidTest() {
        // given
        UUID uuid = createTestQnaCategoryEntity().getUuid();

        // when
        given(qnaCategoryRepository.existsByUuid(uuid)).willReturn(false);

        // then
        EntityNotFoundException existsException = assertThrows(EntityNotFoundException.class,
                () -> qnaCategoryValidationService.validateNotFoundUuid(uuid));
        assertThat(existsException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                NOT_FOUND_ENTITY.getValue(), "uuid", uuid, QnaCategoryEntity.class));
    }
}