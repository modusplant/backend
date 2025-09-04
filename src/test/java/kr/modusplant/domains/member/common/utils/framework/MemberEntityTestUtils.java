package kr.modusplant.domains.member.common.utils.framework;

import kr.modusplant.domains.member.framework.out.persistence.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.common.utils.domain.MemberTestUtils;

import java.time.LocalDateTime;

public interface MemberEntityTestUtils extends MemberTestUtils {
    default MemberEntity createMemberEntity() {
        return MemberEntity.builder()
                .nickname(testMemberNickname.getValue())
                .birthDate(testMemberBirthDate.getValue())
                .isActive(testMemberActiveStatus.isActive())
                .isDisabledByLinking(false)
                .isBanned(false)
                .isDeleted(false)
                .loggedInAt(LocalDateTime.now())
                .build();
    }

    default MemberEntity createMemberEntityWithUuid() {
        return MemberEntity.builder()
                .uuid(testMemberId.getValue())
                .nickname(testMemberNickname.getValue())
                .birthDate(testMemberBirthDate.getValue())
                .isActive(testMemberActiveStatus.isActive())
                .isDisabledByLinking(false)
                .isBanned(false)
                .isDeleted(false)
                .loggedInAt(LocalDateTime.now())
                .build();
    }
}
