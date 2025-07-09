package kr.modusplant.domains.communication.domain.service;

import kr.modusplant.domains.common.context.DomainsServiceOnlyContext;
import kr.modusplant.domains.common.error.InvalidPaginationRangeException;
import kr.modusplant.domains.communication.persistence.repository.CommPostRepository;
import kr.modusplant.global.enums.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import static kr.modusplant.domains.communication.vo.CommPageableValue.PAGE_SIZE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@DomainsServiceOnlyContext
class CommPageableValidationServiceTest {

    private final CommPageableValidationService pageableValidationService;
    private final CommPostRepository postRepository;

    @Autowired
    CommPageableValidationServiceTest(CommPageableValidationService pageableValidationService, CommPostRepository postRepository) {
        this.pageableValidationService = pageableValidationService;
        this.postRepository = postRepository;
    }

    @DisplayName("totalElement가 0일 때 존재하지 않는 페이지 검증")
    @Test
    void validatePageExistenceZeroTotalElementTest() {
        // given
        given(postRepository.count()).willReturn(0L);

        // when
        InvalidPaginationRangeException exception = assertThrows(InvalidPaginationRangeException.class, () ->
                pageableValidationService.validatePageExistence(PageRequest.of(2, PAGE_SIZE)));

        // then
        assertThat(exception.getMessage()).isEqualTo(ErrorCode.INVALID_PAGE_RANGE.getMessage());
    }

    @DisplayName("totalElement가 0을 초과할 때 존재하지 않는 페이지 검증")
    @Test
    void validatePageExistenceMoreThanZeroTotalElementTest() {
        // given
        given(postRepository.count()).willReturn(1L);

        // when
        InvalidPaginationRangeException exception = assertThrows(InvalidPaginationRangeException.class, () ->
                pageableValidationService.validatePageExistence(PageRequest.of(2, PAGE_SIZE)));

        // then
        assertThat(exception.getMessage()).isEqualTo(ErrorCode.INVALID_PAGE_RANGE.getMessage());
    }
}