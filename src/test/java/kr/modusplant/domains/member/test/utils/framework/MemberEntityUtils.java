package kr.modusplant.domains.member.test.utils.framework;

import kr.modusplant.domains.member.framework.outbound.persistence.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.test.utils.domain.MemberUtils;

import java.time.LocalDateTime;

public interface MemberEntityUtils extends MemberUtils {
    default MemberEntity createMemberEntity() {
        return MemberEntity.builder()
                .uuid(testMemberId.getValue())
                .nickname(testNickname.getValue())
                .birthDate(testBirthDate.getValue())
                .isActive(testMemberActiveStatus.isActive())
                .isDisabledByLinking(false)
                .isBanned(false)
                .isDeleted(false)
                .loggedInAt(LocalDateTime.now())
                .build();
    }
}
