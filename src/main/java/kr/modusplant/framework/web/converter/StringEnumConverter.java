package kr.modusplant.framework.web.converter;

import org.springframework.core.convert.converter.Converter;

public class StringEnumConverter<T extends Enum<T>> implements Converter<String, T> {

    private final Class<T> enumClass;

    public StringEnumConverter(Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public T convert(String source) {
        try {
            return Enum.valueOf(enumClass, source.toUpperCase());
        } catch (Exception e) {
            throw new RuntimeException(enumClass.getSimpleName() + " is not a valid enum value");
        }
    }
}
