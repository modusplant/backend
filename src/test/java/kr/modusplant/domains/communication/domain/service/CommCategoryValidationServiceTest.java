package kr.modusplant.domains.communication.domain.service;

import kr.modusplant.domains.common.context.DomainsServiceOnlyContext;
import kr.modusplant.domains.communication.common.util.app.http.response.CommCategoryResponseTestUtils;
import kr.modusplant.domains.communication.common.util.entity.CommSecondaryCategoryEntityTestUtils;
import kr.modusplant.domains.communication.error.CommunicationExistsException;
import kr.modusplant.domains.communication.error.CommunicationNotFoundException;
import kr.modusplant.domains.communication.persistence.repository.CommSecondaryCategoryRepository;
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
        CommunicationExistsException existsException = assertThrows(CommunicationExistsException.class,
                () -> commCategoryValidationService.validateExistedOrder(order));
        assertThat(existsException.getMessage()).isEqualTo(CommunicationExistsException.ofCategory().getMessage());
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
        CommunicationExistsException existsException = assertThrows(CommunicationExistsException.class,
                () -> commCategoryValidationService.validateExistedCategory(category));
        assertThat(existsException.getMessage()).isEqualTo(CommunicationExistsException.ofCategory().getMessage());
    }

    @DisplayName("존재하지 않는 순서 검증")
    @Test
    void validateNotFoundUuidTest() {
        // given
        UUID uuid = createTestCommSecondaryCategoryEntity().getUuid();

        // when
        given(commCategoryRepository.existsByUuid(uuid)).willReturn(false);

        // then
        CommunicationNotFoundException notFoundException = assertThrows(CommunicationNotFoundException.class,
                () -> commCategoryValidationService.validateNotFoundUuid(uuid));
        assertThat(notFoundException.getMessage()).isEqualTo(CommunicationNotFoundException.ofCategory().getMessage());
    }
}