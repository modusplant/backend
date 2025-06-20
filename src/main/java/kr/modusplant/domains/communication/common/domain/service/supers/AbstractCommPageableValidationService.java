package kr.modusplant.domains.communication.common.domain.service.supers;

import org.springframework.data.domain.Pageable;

public abstract class AbstractCommPageableValidationService {
    public void validateNotUnsorted(Pageable pageable) {
        if (!pageable.getSort().isUnsorted()) {
            throw new IllegalArgumentException("페이지 정렬 방식은 지정되지 않아야 합니다.");
        }
    }
}
