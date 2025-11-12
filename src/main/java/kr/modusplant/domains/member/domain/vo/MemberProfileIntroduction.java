package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.domains.member.domain.exception.EmptyMemberProfileIntroductionException;
import kr.modusplant.shared.exception.DataLengthException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.MEMBER_PROFILE_INTRODUCTION_OVER_LENGTH;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberProfileIntroduction {
    private final String value;

    public static MemberProfileIntroduction create(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new EmptyMemberProfileIntroductionException();
        } else if (value.length() > 60) {
            throw new DataLengthException(MEMBER_PROFILE_INTRODUCTION_OVER_LENGTH);
        }
        return new MemberProfileIntroduction(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof MemberProfileIntroduction memberProfileIntroduction)) return false;

        return new EqualsBuilder().append(getValue(), memberProfileIntroduction.getValue()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getValue()).toHashCode();
    }
}
