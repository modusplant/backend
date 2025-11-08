package kr.modusplant.domains.member.domain.entity.nullobject;

import kr.modusplant.domains.member.domain.entity.MemberProfileImage;
import kr.modusplant.domains.member.domain.vo.nullobject.MemberEmptyProfileImageBytes;
import kr.modusplant.domains.member.domain.vo.nullobject.MemberEmptyProfileImagePath;
import lombok.Getter;

@Getter
public class MemberEmptyProfileImage extends MemberProfileImage {

    public MemberEmptyProfileImage() {
        super(MemberEmptyProfileImagePath.create(), MemberEmptyProfileImageBytes.create());
    }

    public static MemberEmptyProfileImage create() {
        return new MemberEmptyProfileImage();
    }
}
