package kr.modusplant.domains.member.domain.aggregate;

import kr.modusplant.domains.member.domain.entity.MemberProfileImage;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.domain.vo.MemberProfileIntroduction;
import kr.modusplant.shared.exception.EmptyNicknameException;
import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.kernel.Nickname;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberProfile {
    private final MemberId memberId;
    private MemberProfileImage memberProfileImage;
    private MemberProfileIntroduction memberProfileIntroduction;
    private Nickname nickname;

    public static MemberProfile create(MemberId id, MemberProfileImage profileImage, MemberProfileIntroduction profileIntroduction, Nickname nickname) {
        if (id == null) {
            throw new EmptyValueException(EMPTY_MEMBER_ID, "memberId");
        } else if (profileImage == null) {
            throw new EmptyValueException(EMPTY_MEMBER_PROFILE_IMAGE, "memberProfileImage");
        } else if (profileIntroduction == null) {
            throw new EmptyValueException(EMPTY_MEMBER_PROFILE_INTRODUCTION, "memberProfileIntroduction");
        } else if (nickname == null) {
            throw new EmptyNicknameException();
        }
        return new MemberProfile(id, profileImage, profileIntroduction, nickname);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof MemberProfile member)) return false;

        return new EqualsBuilder().append(getMemberId(), member.getMemberId()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getMemberId()).toHashCode();
    }
}
