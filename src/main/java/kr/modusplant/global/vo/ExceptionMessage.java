package kr.modusplant.global.vo;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExceptionMessage {
    // EntityExistsException
    public static final String EXISTED_ENTITY = "Existed entity with the name - value: ";

    // EntityNotFoundException
    public static final String NOT_FOUND_ENTITY = "Not found entity with the name - value: ";

    // Others
    public static final String FOR_THE_CLASS = " for the class ";
}
