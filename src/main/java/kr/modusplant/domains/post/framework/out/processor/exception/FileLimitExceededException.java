package kr.modusplant.domains.post.framework.out.processor.exception;

import kr.modusplant.domains.post.domain.exception.enums.PostErrorCode;
import kr.modusplant.shared.exception.BusinessException;

public class FileLimitExceededException extends BusinessException {
    public FileLimitExceededException() {
        super(PostErrorCode.FILE_LIMIT_EXCEEDED);
    }
}
