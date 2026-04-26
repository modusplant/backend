package kr.modusplant.framework.web.converter;

import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.exception.enums.GeneralErrorCode;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;

import java.util.Locale;

public class StringEnumConverter<T extends Enum<T>> implements Converter<String, T> {

    private final Class<T> enumClass;

    public StringEnumConverter(Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    @NotNull
    @Override
    public T convert(String source) {
        try {
            return Enum.valueOf(enumClass, source.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new InvalidValueException(GeneralErrorCode.UNEXPECTED_INPUT, source, e);
        }
    }
}
