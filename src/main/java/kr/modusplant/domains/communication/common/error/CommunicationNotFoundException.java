package kr.modusplant.domains.communication.common.error;

import kr.modusplant.global.enums.ErrorCode;
import kr.modusplant.global.error.EntityNotFoundException;
import kr.modusplant.global.vo.EntityName;

public class CommunicationNotFoundException extends EntityNotFoundException {

    public CommunicationNotFoundException(ErrorCode errorCode, String entityName) {
        super(errorCode, entityName);
    }

    public static CommunicationNotFoundException ofCategory() {
        return new CommunicationNotFoundException(ErrorCode.CATEGORY_NOT_FOUND, EntityName.CATEGORY);
    }

    public static CommunicationNotFoundException ofComment() {
        return new CommunicationNotFoundException(ErrorCode.COMMENT_NOT_FOUND, EntityName.COMMENT);
    }

    public static CommunicationNotFoundException ofLike() {
        return new CommunicationNotFoundException(ErrorCode.LIKE_NOT_FOUND, EntityName.LIKE);
    }

    public static CommunicationNotFoundException ofPost() {
        return new CommunicationNotFoundException(ErrorCode.POST_NOT_FOUND, EntityName.POST);
    }
}
