package kr.modusplant.domains.member.common.util.framework.out.persistence.jpa.entity;

import kr.modusplant.domains.member.common.util.domain.aggregate.MemberTestUtils;
import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;

import java.time.LocalDateTime;

public interface MemberEntityTestUtils extends MemberTestUtils {
    default SiteMemberEntity createMemberEntity() {
        return SiteMemberEntity.builder()
                .nickname(testMemberNickname.getValue())
                .birthDate(testMemberBirthDate.getValue())
                .isActive(testMemberActiveStatus.isActive())
                .isDisabledByLinking(false)
                .isBanned(false)
                .isDeleted(false)
                .loggedInAt(LocalDateTime.now())
                .build();
    }

    default SiteMemberEntity createMemberEntityWithUuid() {
        return SiteMemberEntity.builder()
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
