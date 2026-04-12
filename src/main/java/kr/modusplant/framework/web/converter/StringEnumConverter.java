package kr.modusplant.framework.web.converter;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;

public class StringEnumConverter<T extends Enum<T>> implements Converter<String, T> {

    private final Class<T> enumClass;

    public StringEnumConverter(Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    @NotNull
    @Override
    public T convert(String source) {
        return Enum.valueOf(enumClass, source.toUpperCase());
    }
}
