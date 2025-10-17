package kr.modusplant.domains.member.domain.aggregate;

import kr.modusplant.domains.member.domain.exception.EmptyMemberBirthDateException;
import kr.modusplant.domains.member.domain.exception.EmptyMemberIdException;
import kr.modusplant.domains.member.domain.exception.EmptyMemberNicknameException;
import kr.modusplant.domains.member.domain.exception.EmptyMemberStatusException;
import kr.modusplant.domains.member.domain.vo.MemberBirthDate;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.domain.vo.MemberNickname;
import kr.modusplant.domains.member.domain.vo.MemberStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Member {
    private final MemberId memberId;
    private MemberStatus memberStatus;
    private MemberNickname memberNickname;
    private MemberBirthDate memberBirthDate;

    public static Member create(MemberId id, MemberStatus status, MemberNickname nickname, MemberBirthDate birthDate) {
        if (id == null) {
            throw new EmptyMemberIdException();
        } else if (status == null) {
            throw new EmptyMemberStatusException();
        } else if (nickname == null) {
            throw new EmptyMemberNicknameException();
        } else if (birthDate == null) {
            throw new EmptyMemberBirthDateException();
        }
        return new Member(id, status, nickname, birthDate);
    }

    public static Member createToRegister(MemberNickname nickname) {
        if (nickname == null) {
            throw new EmptyMemberNicknameException();
        }
        return new Member(null, null, nickname, null);
    }

    public static Member createToUpdateNickname(MemberId id, MemberNickname nickname) {
        if (id == null) {
            throw new EmptyMemberIdException();
        } else if (nickname == null) {
            throw new EmptyMemberNicknameException();
        }
        return new Member(id, null, nickname, null);
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
