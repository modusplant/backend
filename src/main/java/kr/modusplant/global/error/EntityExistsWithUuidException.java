package kr.modusplant.global.error;

import jakarta.persistence.EntityExistsException;

import java.util.UUID;

import static kr.modusplant.global.enums.ExceptionMessage.EXISTED_ENTITY;
import static kr.modusplant.global.util.ExceptionUtils.getFormattedExceptionMessage;

public class EntityExistsWithUuidException extends EntityExistsException {
    public EntityExistsWithUuidException(UUID uuid, Class<?> clazz) {
        super(getFormattedExceptionMessage(EXISTED_ENTITY.getValue(), "uuid", uuid, clazz));
    }

    public EntityExistsWithUuidException(String name, UUID value, Class<?> clazz) {
        super(getFormattedExceptionMessage(EXISTED_ENTITY.getValue(), name, value, clazz));
    }
}
