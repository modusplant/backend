package kr.modusplant.global.error;

import jakarta.persistence.EntityExistsException;

import java.util.UUID;

import static kr.modusplant.global.util.ExceptionUtils.getFormattedExceptionMessage;
import static kr.modusplant.global.vo.ExceptionMessage.EXISTED_ENTITY;

public class EntityExistsWithUuidException extends EntityExistsException {
    public EntityExistsWithUuidException(UUID uuid, Class<?> clazz) {
        super(getFormattedExceptionMessage(EXISTED_ENTITY, "uuid", uuid, clazz));
    }

    public EntityExistsWithUuidException(String name, UUID value, Class<?> clazz) {
        super(getFormattedExceptionMessage(EXISTED_ENTITY, name, value, clazz));
    }
}
