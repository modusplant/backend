package kr.modusplant.legacy.domains.communication.error;

import kr.modusplant.shared.exception.BusinessException;
import kr.modusplant.shared.exception.enums.ErrorCode;
import lombok.Getter;

@Getter
public class AccessDeniedException extends BusinessException {

    private final String requestedResource;

    public AccessDeniedException(ErrorCode errorCode, String requestedResource) {
        super(errorCode);
        this.requestedResource = requestedResource;
    }
}
