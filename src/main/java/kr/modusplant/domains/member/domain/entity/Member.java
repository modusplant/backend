package kr.modusplant.domains.member.domain.entity;

import kr.modusplant.domains.member.domain.exception.EmptyMemberNicknameException;
import kr.modusplant.domains.member.domain.vo.MemberBirthDate;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.domain.vo.MemberStatus;
import kr.modusplant.domains.member.domain.vo.MemberNickname;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Member {
    private MemberId memberId;
    private MemberStatus memberStatus;
    private MemberNickname memberNickname;
    private MemberBirthDate memberBirthDate;

    public static Member create(MemberId memberId, MemberStatus memberStatus, MemberNickname memberNickname, MemberBirthDate memberBirthDate) {
        return new Member(memberId, memberStatus, memberNickname, memberBirthDate);
    }

    public static Member create(MemberId memberId, MemberStatus status, MemberNickname memberNickname) {
        return new Member(memberId, status, memberNickname, null);
    }

    public static Member create(MemberNickname memberNickname) {
        if (memberNickname.isEmpty()) {
            throw new EmptyMemberNicknameException();
        }
        return new Member(MemberId.generate(), MemberStatus.active(), memberNickname, null);
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
