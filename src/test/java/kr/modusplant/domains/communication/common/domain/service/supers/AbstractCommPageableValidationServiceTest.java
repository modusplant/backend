package kr.modusplant.domains.communication.common.domain.service.supers;

import kr.modusplant.domains.common.context.DomainsServiceOnlyContext;
import kr.modusplant.domains.common.error.SpecifiedSortingMethodException;
import kr.modusplant.domains.communication.conversation.domain.service.ConvPageableValidationService;
import kr.modusplant.global.enums.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import static kr.modusplant.global.vo.DatabaseFieldName.CREATED_AT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DomainsServiceOnlyContext
class AbstractCommPageableValidationServiceTest {

    private final ConvPageableValidationService pageableValidationService;

    @Autowired
    AbstractCommPageableValidationServiceTest(ConvPageableValidationService pageableValidationService) {
        this.pageableValidationService = pageableValidationService;
    }

    @DisplayName("정렬 방식이 지정된 페이지 검증")
    @Test
    void validateNotUnsortedTest() {
        // given & when
        SpecifiedSortingMethodException exception = assertThrows(SpecifiedSortingMethodException.class, () ->
                pageableValidationService.validateNotUnsorted(
                        PageRequest.of(1, 20, Sort.by(Sort.Direction.ASC, CREATED_AT))));

        // then
        assertThat(exception.getMessage()).isEqualTo(ErrorCode.SPECIFIED_SORTING_METHOD.getMessage());
    }
}