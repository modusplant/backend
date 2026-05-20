package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.domains.member.domain.vo.nullobject.EmptyMemberWithdrawOpinion;
import kr.modusplant.shared.exception.InvalidValueException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.MEMBER_WITHDRAW_OPINION_OVER_LENGTH;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberWithdrawOpinion {
    private final String value;

    public static MemberWithdrawOpinion create(String value) {
        if (StringUtils.isBlank(value)) {
            return EmptyMemberWithdrawOpinion.create();
        } else if (value.length() > 600) {
            throw new InvalidValueException(MEMBER_WITHDRAW_OPINION_OVER_LENGTH, "memberWithdrawalOpinion");
        }
        return new MemberWithdrawOpinion(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof MemberWithdrawOpinion memberProfileIntroduction)) return false;

        return new EqualsBuilder().append(getValue(), memberProfileIntroduction.getValue()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getValue()).toHashCode();
    }
}
