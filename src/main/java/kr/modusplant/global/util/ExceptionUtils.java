package kr.modusplant.global.util;

import static kr.modusplant.global.enums.ExceptionMessage.FOR_THE_CLASS;

public abstract class ExceptionUtils {
    public static String getFormattedExceptionMessage(String message, String name, Object value, Class<?> clazz) {
        return "%s%s %s%s%s".formatted(message, name, value, FOR_THE_CLASS.getValue(), clazz.getSimpleName());
    }
}
