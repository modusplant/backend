package kr.modusplant.domains.member.domain.aggregate;

import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.domain.vo.MemberStatus;
import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.kernel.Nickname;
import kr.modusplant.shared.kernel.enums.KernelErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.EMPTY_MEMBER_ID;
import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.EMPTY_MEMBER_STATUS;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Member {
    private final MemberId memberId;
    private MemberStatus memberStatus;
    private Nickname nickname;

    public static Member create(MemberId id, MemberStatus status, Nickname nickname) {
        if (id == null) {
            throw new EmptyValueException(EMPTY_MEMBER_ID, "memberId");
        } else if (status == null) {
            throw new EmptyValueException(EMPTY_MEMBER_STATUS, "memberStatus");
        } else if (nickname == null) {
            throw new EmptyValueException(KernelErrorCode.EMPTY_NICKNAME, "nickname");
        }
        return new Member(id, status, nickname);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Member member)) return false;

        return new EqualsBuilder().append(getMemberId(), member.getMemberId()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getMemberId()).toHashCode();
    }
}
