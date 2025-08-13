package kr.modusplant.legacy.domains.communication.domain.service;

import kr.modusplant.global.vo.EntityName;
import kr.modusplant.infrastructure.exception.EntityExistsException;
import kr.modusplant.infrastructure.exception.EntityNotFoundException;
import kr.modusplant.infrastructure.exception.enums.ErrorCode;
import kr.modusplant.legacy.domains.common.context.DomainsServiceOnlyContext;
import kr.modusplant.legacy.domains.communication.common.util.app.http.response.CommCategoryResponseTestUtils;
import kr.modusplant.legacy.domains.communication.common.util.entity.CommSecondaryCategoryEntityTestUtils;
import kr.modusplant.legacy.domains.communication.persistence.repository.CommSecondaryCategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@DomainsServiceOnlyContext
class CommCategoryValidationServiceTest implements CommCategoryResponseTestUtils, CommSecondaryCategoryEntityTestUtils {

    private final CommCategoryValidationService commCategoryValidationService;
    private final CommSecondaryCategoryRepository commCategoryRepository;

    @Autowired
    CommCategoryValidationServiceTest(CommCategoryValidationService commCategoryValidationService, CommSecondaryCategoryRepository commCategoryRepository) {
        this.commCategoryValidationService = commCategoryValidationService;
        this.commCategoryRepository = commCategoryRepository;
    }

    @DisplayName("존재하는 순서 검증")
    @Test
    void validateExistedOrderTest() {
        // given
        Integer order = createTestCommSecondaryCategoryEntity().getOrder();

        // when
        given(commCategoryRepository.existsByOrder(order)).willReturn(true);

        // then
        EntityExistsException existsException = assertThrows(EntityExistsException.class,
                () -> commCategoryValidationService.validateExistedOrder(order));
        assertThat(existsException.getMessage()).isEqualTo(new EntityExistsException(ErrorCode.CATEGORY_EXISTS, EntityName.CATEGORY).getMessage());
    }

    @DisplayName("존재하는 항목 검증")
    @Test
    void validateExistedCommCategoryNameTest() {
        // given
        Integer order = createTestCommSecondaryCategoryEntity().getOrder();
        String category = createTestCommSecondaryCategoryEntity().getCategory();

        // when
        given(commCategoryRepository.existsByOrder(order)).willReturn(false);
        given(commCategoryRepository.existsByCategory(category)).willReturn(true);

        // then
        EntityExistsException existsException = assertThrows(EntityExistsException.class,
                () -> commCategoryValidationService.validateExistedCategory(category));
        assertThat(existsException.getMessage()).isEqualTo(new EntityExistsException(ErrorCode.CATEGORY_EXISTS, EntityName.CATEGORY).getMessage());
    }

    @DisplayName("존재하지 않는 순서 검증")
    @Test
    void validateNotFoundUuidTest() {
        // given
        UUID uuid = createTestCommSecondaryCategoryEntity().getUuid();

        // when
        given(commCategoryRepository.existsByUuid(uuid)).willReturn(false);

        // then
        EntityNotFoundException notFoundException = assertThrows(EntityNotFoundException.class,
                () -> commCategoryValidationService.validateNotFoundUuid(uuid));
        assertThat(notFoundException.getMessage())
                .isEqualTo(new EntityNotFoundException(ErrorCode.CATEGORY_NOT_FOUND, EntityName.CATEGORY).getMessage());
    }
}