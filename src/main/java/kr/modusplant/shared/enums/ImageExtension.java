package kr.modusplant.shared.enums;

import lombok.Getter;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum ImageExtension {
    JPEG("jpeg"),
    JPG("jpg"),
    PNG("png"),
    GIF("gif"),
    HEIF("heif"),
    HEIC("heic");

    private final String value;

    ImageExtension(String value) {
        this.value = value;
    }

    public static final Set<String> POSSIBLE_IMAGE_EXTENSIONS =
            Stream.of(values()).map(ImageExtension::getValue).collect(Collectors.toUnmodifiableSet());

    public static final Map<String, ImageExtension> EXTENSION_MAP =
            Stream.of(values())
                    .collect(
                            Collectors.toUnmodifiableMap(
                                    Enum::name,
                                    Function.identity()
                            ));
}