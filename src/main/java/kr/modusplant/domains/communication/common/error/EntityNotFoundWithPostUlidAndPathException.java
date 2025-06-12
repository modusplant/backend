package kr.modusplant.domains.communication.common.error;

import jakarta.persistence.EntityNotFoundException;

import static kr.modusplant.global.enums.ExceptionMessage.NOT_FOUND_ENTITY;
import static kr.modusplant.global.util.ExceptionUtils.getFormattedExceptionMessage;

public class EntityNotFoundWithPostUlidAndPathException extends EntityNotFoundException {
    public EntityNotFoundWithPostUlidAndPathException(String postUlid, String path, Class<?> clazz) {
        super(getFormattedExceptionMessage(
                NOT_FOUND_ENTITY.getValue(), "postUlid", postUlid, "path", path, clazz));
    }
}
