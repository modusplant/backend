package kr.modusplant.domains.member.domain.entity.nullobject;

import kr.modusplant.domains.member.domain.entity.MemberProfileImage;
import kr.modusplant.domains.member.domain.vo.nullobject.EmptyMemberProfileImageBytes;
import kr.modusplant.domains.member.domain.vo.nullobject.EmptyMemberProfileImagePath;

public class EmptyMemberProfileImage extends MemberProfileImage {
    private EmptyMemberProfileImage() {
        super(EmptyMemberProfileImagePath.create(), EmptyMemberProfileImageBytes.create());
    }

    public static EmptyMemberProfileImage create() {
        return instance;
    }
    private static final EmptyMemberProfileImage instance = new EmptyMemberProfileImage();
}
