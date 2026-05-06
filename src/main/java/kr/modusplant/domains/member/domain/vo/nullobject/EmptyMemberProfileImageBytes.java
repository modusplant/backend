package kr.modusplant.domains.member.domain.vo.nullobject;

import kr.modusplant.domains.member.domain.vo.MemberProfileImageBytes;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmptyMemberProfileImageBytes extends MemberProfileImageBytes {
    public static EmptyMemberProfileImageBytes create() {
        return instance;
    }
    private static final EmptyMemberProfileImageBytes instance = new EmptyMemberProfileImageBytes();

    @Override
    public byte[] getValue() {
        return null;
    }
}
