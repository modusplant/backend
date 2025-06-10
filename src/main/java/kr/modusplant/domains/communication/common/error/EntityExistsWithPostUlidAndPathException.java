package kr.modusplant.domains.communication.common.error;

import jakarta.persistence.EntityExistsException;

import static kr.modusplant.global.enums.ExceptionMessage.EXISTED_ENTITY;
import static kr.modusplant.global.util.ExceptionUtils.getFormattedExceptionMessage;

public class EntityExistsWithPostUlidAndPathException extends EntityExistsException {
    public EntityExistsWithPostUlidAndPathException(String postUlid, String path, Class<?> clazz) {
        super(getFormattedExceptionMessage(
                EXISTED_ENTITY.getValue(), "postUlid", postUlid, "path", path, clazz));
    }
}
