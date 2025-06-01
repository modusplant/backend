package kr.modusplant.domains.communication.tip.domain.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import kr.modusplant.domains.common.context.DomainsServiceOnlyContext;
import kr.modusplant.domains.communication.tip.common.util.app.http.response.TipCategoryResponseTestUtils;
import kr.modusplant.domains.communication.tip.common.util.entity.TipCategoryEntityTestUtils;
import kr.modusplant.domains.communication.tip.persistence.entity.TipCategoryEntity;
import kr.modusplant.domains.communication.tip.persistence.repository.TipCategoryRepository;
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
class TipCategoryValidationServiceTest implements TipCategoryResponseTestUtils, TipCategoryEntityTestUtils {

    private final TipCategoryValidationService tipCategoryValidationService;
    private final TipCategoryRepository tipCategoryRepository;

    @Autowired
    TipCategoryValidationServiceTest(TipCategoryValidationService tipCategoryValidationService, TipCategoryRepository tipCategoryRepository) {
        this.tipCategoryValidationService = tipCategoryValidationService;
        this.tipCategoryRepository = tipCategoryRepository;
    }

    @DisplayName("존재하는 순서 검증")
    @Test
    void validateExistedOrderTest() {
        // given
        Integer order = testTipCategoryEntity.getOrder();

        // when
        given(tipCategoryRepository.findByOrder(order)).willReturn(Optional.of(testTipCategoryEntity));

        // then
        EntityExistsException existsException = assertThrows(EntityExistsException.class,
                () -> tipCategoryValidationService.validateExistedOrder(order));
        assertThat(existsException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                EXISTED_ENTITY.getValue(), ORDER, order, TipCategoryEntity.class));
    }

    @DisplayName("존재하는 항목 검증")
    @Test
    void validateExistedTipCategoryNameTest() {
        // given
        Integer order = testTipCategoryEntity.getOrder();
        String category = testTipCategoryEntity.getCategory();

        // when
        given(tipCategoryRepository.findByOrder(order)).willReturn(Optional.empty());
        given(tipCategoryRepository.findByCategory(category)).willReturn(Optional.of(testTipCategoryEntity));

        // then
        EntityExistsException existsException = assertThrows(EntityExistsException.class,
                () -> tipCategoryValidationService.validateExistedCategory(category));
        assertThat(existsException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                EXISTED_ENTITY.getValue(), CATEGORY, category, TipCategoryEntity.class));
    }

    @DisplayName("존재하지 않는 순서 검증")
    @Test
    void validateNotFoundOrderTest() {
        // given
        Integer order = testTipCategoryEntity.getOrder();

        // when
        given(tipCategoryRepository.findByOrder(order)).willReturn(Optional.empty());

        // then
        EntityNotFoundException existsException = assertThrows(EntityNotFoundException.class,
                () -> tipCategoryValidationService.validateNotFoundOrder(order));
        assertThat(existsException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                NOT_FOUND_ENTITY.getValue(), ORDER, order, TipCategoryEntity.class));
    }
}