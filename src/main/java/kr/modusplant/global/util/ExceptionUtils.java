package kr.modusplant.global.util;

import kr.modusplant.global.enums.ExceptionMessage;

import static kr.modusplant.global.enums.ExceptionMessage.FOR_THE_CLASS;

public abstract class ExceptionUtils {
    public static String getFormattedExceptionMessage(String message, String name, Object value, Class<?> clazz) {
        return "%s%s %s%s%s".formatted(message, name, value, FOR_THE_CLASS.getValue(), clazz.getSimpleName());
    }

    public static String getFormattedExceptionMessage(ExceptionMessage message, String name, Object value, Class<?> clazz) {
        return "%s%s %s%s%s".formatted(message.getValue(), name, value, FOR_THE_CLASS.getValue(), clazz.getSimpleName());
    }

    public static String getFormattedExceptionMessage(String message, String name1, Object value1, String name2, Object value2, Class<?> clazz) {
        return "%s%s %s, %s %s%s%s".formatted(message, name1, value1, name2, value2, FOR_THE_CLASS.getValue(), clazz.getSimpleName());
    }

    public static String getFormattedExceptionMessage(ExceptionMessage message, String name1, Object value1, String name2, Object value2, Class<?> clazz) {
        return "%s%s %s, %s %s%s%s".formatted(message.getValue(), name1, value1, name2, value2, FOR_THE_CLASS.getValue(), clazz.getSimpleName());
    }
}