package kr.modusplant.legacy.domains.communication.error;

import kr.modusplant.domain.exception.enums.ErrorCode;
import kr.modusplant.global.error.BusinessException;
import lombok.Getter;

@Getter
public class AccessDeniedException extends BusinessException {

    private final String requestedResource;

    public AccessDeniedException(ErrorCode errorCode, String requestedResource) {
        super(errorCode);
        this.requestedResource = requestedResource;
    }
}
