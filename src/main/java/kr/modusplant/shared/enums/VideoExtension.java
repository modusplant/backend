package kr.modusplant.shared.enums;

import lombok.Getter;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum VideoExtension {
    MP4("mp4"),
    MOV("mov");

    private final String value;

    VideoExtension(String value) {
        this.value = value;
    }

    public static final Set<String> POSSIBLE_VIDEO_EXTENSIONS =
            Stream.of(values()).map(VideoExtension::getValue).collect(Collectors.toUnmodifiableSet());

    public static final Map<String, VideoExtension> EXTENSION_MAP =
            Stream.of(values())
                    .collect(
                            Collectors.toUnmodifiableMap(
                                    Enum::name,
                                    Function.identity()
                            ));
}
