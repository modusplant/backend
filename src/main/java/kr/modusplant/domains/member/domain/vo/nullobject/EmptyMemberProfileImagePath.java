package kr.modusplant.domains.member.domain.vo.nullobject;

import kr.modusplant.domains.member.domain.vo.MemberProfileImagePath;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmptyMemberProfileImagePath extends MemberProfileImagePath {
    public static EmptyMemberProfileImagePath create() {
        return instance;
    }
    private static final EmptyMemberProfileImagePath instance = new EmptyMemberProfileImagePath();

    @Override
    public String getValue() {
        return null;
    }
}
