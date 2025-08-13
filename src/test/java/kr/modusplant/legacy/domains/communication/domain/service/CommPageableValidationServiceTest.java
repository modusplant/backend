package kr.modusplant.legacy.domains.communication.domain.service;

import kr.modusplant.infrastructure.exception.InvalidDataException;
import kr.modusplant.infrastructure.exception.enums.ErrorCode;
import kr.modusplant.legacy.domains.common.context.DomainsServiceOnlyContext;
import kr.modusplant.legacy.domains.communication.persistence.repository.CommPostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import static kr.modusplant.legacy.domains.communication.vo.CommPageableValue.PAGE_SIZE;
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
        InvalidDataException exception = assertThrows(InvalidDataException.class, () ->
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
        InvalidDataException exception = assertThrows(InvalidDataException.class, () ->
                pageableValidationService.validatePageExistence(PageRequest.of(2, PAGE_SIZE)));

        // then
        assertThat(exception.getMessage()).isEqualTo(ErrorCode.INVALID_PAGE_RANGE.getMessage());
    }
}