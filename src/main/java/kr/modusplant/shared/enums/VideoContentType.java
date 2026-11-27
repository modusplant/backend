package kr.modusplant.shared.enums;

import kr.modusplant.shared.exception.InvalidValueException;
import lombok.Getter;

import java.util.stream.Stream;

import static kr.modusplant.shared.exception.enums.GeneralErrorCode.UNSUPPORTED_FILE;

@Getter
public enum VideoContentType {
    MP4("video/mp4", VideoExtension.MP4),
    QUICKTIME("video/quicktime", VideoExtension.MOV);

    private final String value;
    private final VideoExtension extension;

    VideoContentType(String value, VideoExtension extension) {
        this.value = value;
        this.extension = extension;
    }

    public static VideoContentType fromMimeType(String mimeType) {
        return Stream.of(values())
                .filter(t -> t.value.equals(mimeType))
                .findFirst()
                .orElseThrow(() -> new InvalidValueException(UNSUPPORTED_FILE, java.util.List.of("contentType")));
    }
}
