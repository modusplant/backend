package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.shared.enums.ImageExtension;
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
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.EMPTY_REPORT_IMAGE_FILE_NAME;
import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.INVALID_REPORT_IMAGE_FILE_NAME;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ProposalOrBugReportImageFileName {
    private final BaseName baseName;

    @Getter
    private final ImageExtension imageExtension;

    public static ProposalOrBugReportImageFileName create(String fileNameWithExtension) {
        if (StringUtils.isBlank(fileNameWithExtension)) {
            throw new EmptyValueException(EMPTY_REPORT_IMAGE_FILE_NAME, "fileNameWithExtension");
        }
        String[] split = fileNameWithExtension.split("\\.");
        if (split.length != 2) {
            throw new InvalidValueException(INVALID_REPORT_IMAGE_FILE_NAME, "fileNameWithExtension");
        }
        if (!BaseName.POSSIBLE_BASE_NAMES.contains(split[0])) {
            throw new InvalidValueException(INVALID_REPORT_IMAGE_FILE_NAME, "fileNameWithExtension");
        }
        if (!ImageExtension.POSSIBLE_IMAGE_EXTENSIONS.contains(split[1])) {
            throw new UnsupportedFileException();
        }
        return new ProposalOrBugReportImageFileName(
                BaseName.BASE_NAME_MAP.get(split[0].toUpperCase(Locale.ROOT)),
                ImageExtension.EXTENSION_MAP.get(split[1].toUpperCase(Locale.ROOT)));
    }

    @Getter
    private enum BaseName {
        IMAGE_0("image_0"),
        IMAGE_1("image_1"),
        IMAGE_2("image_2");

        private final String value;

        BaseName(String value) {
            this.value = value;
        }

        private static final Set<String> POSSIBLE_BASE_NAMES =
                Stream.of(values()).map(BaseName::getValue).collect(Collectors.toUnmodifiableSet());

        private static final Map<String, BaseName> BASE_NAME_MAP =
                Stream.of(values())
                        .collect(
                                Collectors.toUnmodifiableMap(
                                        Enum::name,
                                        Function.identity()
                                ));
    }

    public String getBaseName() {
        return baseName.getValue();
    }

    public String getFileName() {
        return baseName + "." + imageExtension.getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ProposalOrBugReportImageFileName reportTitle)) return false;

        return new EqualsBuilder().append(getBaseName(), reportTitle.getBaseName()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getBaseName()).toHashCode();
    }
}
