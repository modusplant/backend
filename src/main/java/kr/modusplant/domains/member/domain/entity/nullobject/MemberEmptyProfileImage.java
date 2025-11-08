package kr.modusplant.domains.member.domain.entity.nullobject;

import kr.modusplant.domains.member.domain.entity.MemberProfileImage;
import kr.modusplant.domains.member.domain.vo.MemberProfileImageBytes;
import kr.modusplant.domains.member.domain.vo.MemberProfileImagePath;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberEmptyProfileImage extends MemberProfileImage {

    public static MemberEmptyProfileImage create() {
        return new MemberEmptyProfileImage();
    }

    @Override
    public MemberProfileImagePath getMemberProfileImagePath() {
        return null;
    }

    @Override
    public MemberProfileImageBytes getMemberProfileImageBytes() {
        return null;
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
