package kr.modusplant.domains.member.domain.entity;

import kr.modusplant.domains.member.domain.exception.EmptyMemberProfileImageBytesException;
import kr.modusplant.domains.member.domain.exception.EmptyMemberProfileImagePathException;
import kr.modusplant.domains.member.domain.vo.MemberProfileImageBytes;
import kr.modusplant.domains.member.domain.vo.MemberProfileImagePath;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberProfileImage {
    private final MemberProfileImagePath memberProfileImagePath;
    private MemberProfileImageBytes memberProfileImageBytes;

    public static MemberProfileImage create(MemberProfileImagePath profileImagePath, MemberProfileImageBytes profileImageBytes) {
        if (profileImagePath == null) {
            throw new EmptyMemberProfileImagePathException();
        } else if (profileImageBytes.isEmpty()) {
            throw new EmptyMemberProfileImageBytesException();
        }
        return new MemberProfileImage(profileImagePath, profileImageBytes);
    }

    public boolean isEmpty() {
        return memberProfileImagePath.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof MemberProfileImage memberProfileImage)) return false;

        return new EqualsBuilder().append(getMemberProfileImagePath(), memberProfileImage.getMemberProfileImagePath()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getMemberProfileImagePath()).toHashCode();
    }
}
