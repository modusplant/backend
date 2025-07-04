package kr.modusplant.domains.communication.tip.domain.service;

import kr.modusplant.domains.common.context.DomainsServiceOnlyContext;
import kr.modusplant.domains.communication.common.error.CommunicationExistsException;
import kr.modusplant.domains.communication.common.error.CommunicationNotFoundException;
import kr.modusplant.domains.communication.tip.common.util.app.http.response.TipCategoryResponseTestUtils;
import kr.modusplant.domains.communication.tip.common.util.entity.TipCategoryEntityTestUtils;
import kr.modusplant.domains.communication.tip.persistence.repository.TipCategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

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
        Integer order = createTestTipCategoryEntity().getOrder();

        // when
        given(tipCategoryRepository.existsByOrder(order)).willReturn(true);

        // then
        CommunicationExistsException existsException = assertThrows(CommunicationExistsException.class,
                () -> tipCategoryValidationService.validateExistedOrder(order));
        assertThat(existsException.getMessage()).isEqualTo(CommunicationExistsException.ofCategory().getMessage());
    }

    @DisplayName("존재하는 항목 검증")
    @Test
    void validateExistedTipCategoryNameTest() {
        // given
        Integer order = createTestTipCategoryEntity().getOrder();
        String category = createTestTipCategoryEntity().getCategory();

        // when
        given(tipCategoryRepository.existsByOrder(order)).willReturn(false);
        given(tipCategoryRepository.existsByCategory(category)).willReturn(true);

        // then
        CommunicationExistsException existsException = assertThrows(CommunicationExistsException.class,
                () -> tipCategoryValidationService.validateExistedCategory(category));
        assertThat(existsException.getMessage()).isEqualTo(CommunicationExistsException.ofCategory().getMessage());
    }

    @DisplayName("존재하지 않는 순서 검증")
    @Test
    void validateNotFoundUuidTest() {
        // given
        UUID uuid = createTestTipCategoryEntity().getUuid();

        // when
        given(tipCategoryRepository.existsByUuid(uuid)).willReturn(false);

        // then
        CommunicationNotFoundException notFoundException = assertThrows(CommunicationNotFoundException.class,
                () -> tipCategoryValidationService.validateNotFoundUuid(uuid));
        assertThat(notFoundException.getMessage()).isEqualTo(CommunicationNotFoundException.ofCategory().getMessage());
    }
}