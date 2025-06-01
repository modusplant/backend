package kr.modusplant.domains.communication.conversation.domain.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import kr.modusplant.domains.common.context.DomainsServiceOnlyContext;
import kr.modusplant.domains.communication.conversation.common.util.app.http.response.ConvCategoryResponseTestUtils;
import kr.modusplant.domains.communication.conversation.common.util.entity.ConvCategoryEntityTestUtils;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvCategoryEntity;
import kr.modusplant.domains.communication.conversation.persistence.repository.ConvCategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static kr.modusplant.global.enums.ExceptionMessage.EXISTED_ENTITY;
import static kr.modusplant.global.enums.ExceptionMessage.NOT_FOUND_ENTITY;
import static kr.modusplant.global.util.ExceptionUtils.getFormattedExceptionMessage;
import static kr.modusplant.global.vo.CamelCaseWord.CATEGORY;
import static kr.modusplant.global.vo.CamelCaseWord.ORDER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@DomainsServiceOnlyContext
class ConvCategoryValidationServiceTest implements ConvCategoryResponseTestUtils, ConvCategoryEntityTestUtils {

    private final ConvCategoryValidationService convCategoryValidationService;
    private final ConvCategoryRepository convCategoryRepository;

    @Autowired
    ConvCategoryValidationServiceTest(ConvCategoryValidationService convCategoryValidationService, ConvCategoryRepository convCategoryRepository) {
        this.convCategoryValidationService = convCategoryValidationService;
        this.convCategoryRepository = convCategoryRepository;
    }

    @DisplayName("존재하는 순서 검증")
    @Test
    void validateExistedOrderTest() {
        // given
        Integer order = testConvCategoryEntity.getOrder();

        // when
        given(convCategoryRepository.findByOrder(order)).willReturn(Optional.of(testConvCategoryEntity));

        // then
        EntityExistsException existsException = assertThrows(EntityExistsException.class,
                () -> convCategoryValidationService.validateExistedOrder(order));
        assertThat(existsException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                EXISTED_ENTITY.getValue(), ORDER, order, ConvCategoryEntity.class));
    }

    @DisplayName("존재하는 항목 검증")
    @Test
    void validateExistedConvCategoryNameTest() {
        // given
        Integer order = testConvCategoryEntity.getOrder();
        String category = testConvCategoryEntity.getCategory();

        // when
        given(convCategoryRepository.findByOrder(order)).willReturn(Optional.empty());
        given(convCategoryRepository.findByCategory(category)).willReturn(Optional.of(testConvCategoryEntity));

        // then
        EntityExistsException existsException = assertThrows(EntityExistsException.class,
                () -> convCategoryValidationService.validateExistedCategory(category));
        assertThat(existsException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                EXISTED_ENTITY.getValue(), CATEGORY, category, ConvCategoryEntity.class));
    }

    @DisplayName("존재하지 않는 순서 검증")
    @Test
    void validateNotFoundOrderTest() {
        // given
        Integer order = testConvCategoryEntity.getOrder();

        // when
        given(convCategoryRepository.findByOrder(order)).willReturn(Optional.empty());

        // then
        EntityNotFoundException existsException = assertThrows(EntityNotFoundException.class,
                () -> convCategoryValidationService.validateNotFoundOrder(order));
        assertThat(existsException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                NOT_FOUND_ENTITY.getValue(), ORDER, order, ConvCategoryEntity.class));
    }
}