package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.exception.UnsupportedFileException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.EMPTY_REPORT_IMAGE_FILE_NAME;
import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.INVALID_REPORT_IMAGE_FILE_NAME;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ReportImageFileName {
    private final String baseName;
    private final Extension extension;

    public static ReportImageFileName create(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            throw new EmptyValueException(EMPTY_REPORT_IMAGE_FILE_NAME, "fileName");
        }
        String[] split = fileName.split("\\.");
        if (split.length != 2) {
            throw new InvalidValueException(INVALID_REPORT_IMAGE_FILE_NAME, "fileName");
        }
        Extension extension = Extension.EXTENSION_MAP.get(split[1].toUpperCase(Locale.ROOT));
        if (extension == null) {
            throw new UnsupportedFileException();
        }
        return new ReportImageFileName(split[0], extension);
    }

    public String getFileName() {
        return baseName + "." + extension.getValue();
    }

    @Getter
    private enum Extension {
        JPEG("jpeg", true),
        JPG("jpg", true),
        PNG("png", true),
        GIF("gif", true),
        HEIF("heif", true),
        HEIC("heic", true);

        private final String value;
        private final boolean isImage;

        Extension(String value, boolean isImage) {
            this.value = value;
            this.isImage = isImage;
        }

        private static final Map<String, Extension> EXTENSION_MAP =
                Stream.of(values())
                        .collect(
                                Collectors.toUnmodifiableMap(
                                        Enum::name,
                                        Function.identity()
                                ));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ReportImageFileName reportTitle)) return false;

        return new EqualsBuilder().append(getBaseName(), reportTitle.getBaseName()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getBaseName()).toHashCode();
    }
}
