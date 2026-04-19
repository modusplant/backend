package kr.modusplant.domains.member.domain.entity.nullobject;

import kr.modusplant.domains.member.domain.entity.MemberProfileImage;
import kr.modusplant.domains.member.domain.vo.nullobject.EmptyMemberProfileImageBytes;
import kr.modusplant.domains.member.domain.vo.nullobject.EmptyMemberProfileImagePath;
import lombok.Getter;

@Getter
public class EmptyMemberProfileImage extends MemberProfileImage {

    public EmptyMemberProfileImage() {
        super(EmptyMemberProfileImagePath.create(), EmptyMemberProfileImageBytes.create());
    }

    public static EmptyMemberProfileImage create() {
        return new EmptyMemberProfileImage();
    }
}
