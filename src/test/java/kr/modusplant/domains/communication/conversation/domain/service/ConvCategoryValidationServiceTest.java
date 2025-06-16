package kr.modusplant.domains.communication.conversation.domain.service;

import kr.modusplant.domains.common.context.DomainsServiceOnlyContext;
import kr.modusplant.domains.communication.common.error.CategoryExistsException;
import kr.modusplant.domains.communication.common.error.CategoryNotFoundException;
import kr.modusplant.domains.communication.conversation.common.util.app.http.response.ConvCategoryResponseTestUtils;
import kr.modusplant.domains.communication.conversation.common.util.entity.ConvCategoryEntityTestUtils;
import kr.modusplant.domains.communication.conversation.persistence.repository.ConvCategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

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
        Integer order = createTestConvCategoryEntity().getOrder();

        // when
        given(convCategoryRepository.existsByOrder(order)).willReturn(true);

        // then
        CategoryExistsException existsException = assertThrows(CategoryExistsException.class,
                () -> convCategoryValidationService.validateExistedOrder(order));
        assertThat(existsException.getMessage()).isEqualTo(new CategoryExistsException().getMessage());
    }

    @DisplayName("존재하는 항목 검증")
    @Test
    void validateExistedConvCategoryNameTest() {
        // given
        Integer order = createTestConvCategoryEntity().getOrder();
        String category = createTestConvCategoryEntity().getCategory();

        // when
        given(convCategoryRepository.existsByOrder(order)).willReturn(false);
        given(convCategoryRepository.existsByCategory(category)).willReturn(true);

        // then
        CategoryExistsException existsException = assertThrows(CategoryExistsException.class,
                () -> convCategoryValidationService.validateExistedCategory(category));
        assertThat(existsException.getMessage()).isEqualTo(new CategoryExistsException().getMessage());
    }

    @DisplayName("존재하지 않는 순서 검증")
    @Test
    void validateNotFoundUuidTest() {
        // given
        UUID uuid = createTestConvCategoryEntity().getUuid();

        // when
        given(convCategoryRepository.existsByUuid(uuid)).willReturn(false);

        // then
        CategoryNotFoundException notFoundException = assertThrows(CategoryNotFoundException.class,
                () -> convCategoryValidationService.validateNotFoundUuid(uuid));
        assertThat(notFoundException.getMessage()).isEqualTo(new CategoryNotFoundException().getMessage());
    }
}