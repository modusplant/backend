package kr.modusplant.domains.communication.common.domain.service.supers;

import kr.modusplant.domains.common.error.SpecifiedSortingMethodException;
import org.springframework.data.domain.Pageable;

public abstract class AbstractCommPageableValidationService {
    public void validateNotUnsorted(Pageable pageable) {
        if (!pageable.getSort().isUnsorted()) {
            throw new SpecifiedSortingMethodException();
        }
    }
}
