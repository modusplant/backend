package kr.modusplant.global.error;

import jakarta.persistence.EntityNotFoundException;

import java.util.UUID;

import static kr.modusplant.global.enums.ExceptionMessage.NOT_FOUND_ENTITY;
import static kr.modusplant.global.util.ExceptionUtils.getFormattedExceptionMessage;

public class EntityNotFoundWithUlidException extends EntityNotFoundException {
    public EntityNotFoundWithUlidException(String ulid, Class<?> clazz) {
        super(getFormattedExceptionMessage(NOT_FOUND_ENTITY, "ulid", ulid, clazz));
    }

    public EntityNotFoundWithUlidException(String name, UUID value, Class<?> clazz) {
        super(getFormattedExceptionMessage(NOT_FOUND_ENTITY, name, value, clazz));
    }
}
