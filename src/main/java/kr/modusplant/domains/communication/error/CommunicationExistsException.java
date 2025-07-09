package kr.modusplant.domains.communication.error;

import kr.modusplant.global.enums.ErrorCode;
import kr.modusplant.global.error.EntityExistsException;
import kr.modusplant.global.vo.EntityName;

public class CommunicationExistsException extends EntityExistsException {

    public CommunicationExistsException(ErrorCode errorCode, String entityName) {
        super(errorCode, entityName);
    }

    public static CommunicationExistsException ofCategory() {
        return new CommunicationExistsException(ErrorCode.CATEGORY_EXISTS, EntityName.CATEGORY);
    }

    public static CommunicationExistsException ofComment() {
        return new CommunicationExistsException(ErrorCode.COMMENT_EXISTS, EntityName.COMMENT);
    }

    public static CommunicationExistsException ofLike() {
        return new CommunicationExistsException(ErrorCode.LIKE_EXISTS, EntityName.LIKE);
    }

    public static CommunicationExistsException ofPost() {
        return new CommunicationExistsException(ErrorCode.POST_EXISTS, EntityName.POST);
    }
}
