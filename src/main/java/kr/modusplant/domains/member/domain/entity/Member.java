package kr.modusplant.domains.member.domain.entity;

import kr.modusplant.domains.member.domain.exception.EmptyNicknameException;
import kr.modusplant.domains.member.domain.vo.BirthDate;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.domain.vo.MemberStatus;
import kr.modusplant.domains.member.domain.vo.Nickname;
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
    private Nickname nickname;
    private BirthDate birthDate;

    public static Member create(MemberId memberId, MemberStatus memberStatus, Nickname nickname, BirthDate birthDate) {
        return new Member(memberId, memberStatus, nickname, birthDate);
    }

    public static Member create(Nickname nickname) {
        if (nickname.isEmpty()) {
            throw new EmptyNicknameException();
        }
        return new Member(MemberId.generate(), MemberStatus.active(), nickname, null);
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
