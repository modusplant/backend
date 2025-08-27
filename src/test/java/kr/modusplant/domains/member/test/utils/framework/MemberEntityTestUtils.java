package kr.modusplant.domains.member.test.utils.framework;

import kr.modusplant.domains.member.framework.out.persistence.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.test.utils.domain.MemberTestUtils;

import java.time.LocalDateTime;

public interface MemberEntityTestUtils extends MemberTestUtils {
    default MemberEntity createMemberEntity() {
        return MemberEntity.builder()
                .uuid(testMemberId.getValue())
                .nickname(TEST_MEMBER_NICKNAME.getValue())
                .birthDate(TEST_MEMBER_BIRTH_DATE.getValue())
                .isActive(testMemberActiveStatus.isActive())
                .isDisabledByLinking(false)
                .isBanned(false)
                .isDeleted(false)
                .loggedInAt(LocalDateTime.now())
                .build();
    }
}
