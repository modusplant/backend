package kr.modusplant.domains.communication.tip.domain.service;

import kr.modusplant.domains.common.context.DomainsServiceOnlyContext;
import kr.modusplant.domains.communication.tip.persistence.repository.TipPostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import static kr.modusplant.domains.communication.common.vo.CommPageableValue.PAGE_SIZE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@DomainsServiceOnlyContext
class TipPageableValidationServiceTest {

    private final TipPageableValidationService pageableValidationService;
    private final TipPostRepository postRepository;

    @Autowired
    TipPageableValidationServiceTest(TipPageableValidationService pageableValidationService, TipPostRepository postRepository) {
        this.pageableValidationService = pageableValidationService;
        this.postRepository = postRepository;
    }

    @DisplayName("totalElement가 0일 때 존재하지 않는 페이지 검증")
    @Test
    void validatePageExistenceZeroTotalElementTest() {
        // given
        given(postRepository.count()).willReturn(0L);

        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pageableValidationService.validatePageExistence(PageRequest.of(2, PAGE_SIZE)));

        // then
        assertThat(exception.getMessage()).isEqualTo("현재 이용할 수 있는 페이지 범위(1 ~ 1)를 벗어났습니다.");
    }

    @DisplayName("totalElement가 0을 초과할 때 존재하지 않는 페이지 검증")
    @Test
    void validatePageExistenceMoreThanZeroTotalElementTest() {
        // given
        given(postRepository.count()).willReturn(1L);

        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                pageableValidationService.validatePageExistence(PageRequest.of(2, PAGE_SIZE)));

        // then
        assertThat(exception.getMessage()).isEqualTo("현재 이용할 수 있는 페이지 범위(1 ~ 1)를 벗어났습니다.");
    }
}