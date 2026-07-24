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

import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.EMPTY_MEMBER_PROFILE_IMAGE_FILE_NAME;
import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.INVALID_MEMBER_PROFILE_IMAGE_FILE_NAME;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberProfileImageFileName {
    private final String baseName;
    private final ImageExtension imageExtension;

    public static MemberProfileImageFileName create(String fileNameWithExtension) {
        if (StringUtils.isBlank(fileNameWithExtension)) {
            throw new EmptyValueException(EMPTY_MEMBER_PROFILE_IMAGE_FILE_NAME, "fileNameWithExtension");
        }
        String[] split = fileNameWithExtension.split("\\.");
        if (split.length != 2 || StringUtils.isBlank(split[0])) {
            throw new InvalidValueException(INVALID_MEMBER_PROFILE_IMAGE_FILE_NAME, "fileNameWithExtension");
        }
        if (!ImageExtension.POSSIBLE_IMAGE_EXTENSIONS.contains(split[1])) {
            throw new UnsupportedFileException();
        }
        return new MemberProfileImageFileName(
                fileNameWithExtension,
                ImageExtension.EXTENSION_MAP.get(split[1].toUpperCase(Locale.ROOT)));
    }

    public String getFileName() {
        return baseName + "." + imageExtension.getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof MemberProfileImageFileName fileName)) return false;

        return new EqualsBuilder().append(getFileName(), fileName.getFileName()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getFileName()).toHashCode();
    }
}
