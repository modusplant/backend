package kr.modusplant.domains.member.common.utils.framework.out.persistence.jpa.entity;

import kr.modusplant.domains.member.common.utils.domain.aggregate.MemberTestUtils;
import kr.modusplant.domains.member.framework.out.jpa.entity.MemberEntity;

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
