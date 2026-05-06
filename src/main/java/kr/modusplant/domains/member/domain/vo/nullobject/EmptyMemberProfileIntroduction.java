package kr.modusplant.domains.member.domain.vo.nullobject;

import kr.modusplant.domains.member.domain.vo.MemberProfileIntroduction;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmptyMemberProfileIntroduction extends MemberProfileIntroduction {
    public static EmptyMemberProfileIntroduction create() {
        return instance;
    }
    private static final EmptyMemberProfileIntroduction instance = new EmptyMemberProfileIntroduction();

    @Override
    public String getValue() {
        return null;
    }
}
