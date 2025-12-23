package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.domains.member.domain.exception.EmptyMemberProfileImagePathException;
import kr.modusplant.domains.member.domain.exception.InvalidMemberProfileImagePathException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.regex.Pattern;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberProfileImagePath {
    private final String value;
    private static final Pattern PATTERN_MEMBER_PROFILE_IMAGE_PATH = Pattern.compile(
            "^member/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}/profile/.+\\..+$");

    public static MemberProfileImagePath create(String value) {
        if (StringUtils.isBlank(value)) {
            throw new EmptyMemberProfileImagePathException();
        } else if (!PATTERN_MEMBER_PROFILE_IMAGE_PATH.matcher(value).matches()) {
            throw new InvalidMemberProfileImagePathException();
        }
        return new MemberProfileImagePath(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof MemberProfileImagePath memberProfileImagePath)) return false;

        return new EqualsBuilder().append(getValue(), memberProfileImagePath.getValue()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getValue()).toHashCode();
    }
}
