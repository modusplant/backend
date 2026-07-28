package kr.modusplant.shared.enums;

import lombok.Getter;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Getter
public enum ImageContentType {
    JPEG("image/jpeg"),
    PNG("image/png"),
    GIF("image/gif"),
    HEIF("image/heif"),
    HEIC("image/heic");

    private final String value;

    ImageContentType(String value) {
        this.value = value;
    }

    private static final Map<ImageContentType, List<ImageExtension>> relationMap =
            new EnumMap<>(ImageContentType.class);

    static {
        relationMap.put(JPEG, List.of(ImageExtension.JPEG, ImageExtension.JPG));
        relationMap.put(PNG, List.of(ImageExtension.PNG));
        relationMap.put(GIF, List.of(ImageExtension.GIF));
        relationMap.put(HEIF, List.of(ImageExtension.HEIF));
        relationMap.put(HEIC, List.of(ImageExtension.HEIC));
    }

    public static List<String> getImageExtensionValues(String value) {
        return Stream.of(values())
                .filter(contentType -> contentType.value.equals(value))
                .findFirst()
                .map(relationMap::get)
                .map(extensions -> extensions.stream()
                        .map(ImageExtension::getValue)
                        .toList())
                .orElseGet(List::of);
    }
}
