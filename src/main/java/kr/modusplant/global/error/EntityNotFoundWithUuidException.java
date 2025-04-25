package kr.modusplant.global.error;

import jakarta.persistence.EntityNotFoundException;

import java.util.UUID;

import static kr.modusplant.global.util.ExceptionUtils.getFormattedExceptionMessage;
import static kr.modusplant.global.enums.ExceptionMessage.NOT_FOUND_ENTITY;

public class EntityNotFoundWithUuidException extends EntityNotFoundException {
    public EntityNotFoundWithUuidException(UUID uuid, Class<?> clazz) {
        super(getFormattedExceptionMessage(NOT_FOUND_ENTITY.getValue(), "uuid", uuid, clazz));
    }

    public EntityNotFoundWithUuidException(String name, UUID value, Class<?> clazz) {
        super(getFormattedExceptionMessage(NOT_FOUND_ENTITY.getValue(), name, value, clazz));
    }
}
