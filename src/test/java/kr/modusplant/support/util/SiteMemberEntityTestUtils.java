package kr.modusplant.support.util;

import kr.modusplant.global.persistence.entity.SiteMemberEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface SiteMemberEntityTestUtils {
    default SiteMemberEntity createMemberBasicAdminEntity() {
        return SiteMemberEntity.builder()
                .nickname("관리자")
                .birthDate(LocalDate.of(2000, 1, 1))
                .loggedInAt(LocalDateTime.now())
                .build();
    }

    default SiteMemberEntity createMemberBasicUserEntity() {
        return SiteMemberEntity.builder()
                .nickname("일반 유저")
                .birthDate(LocalDate.of(2000, 1, 1))
                .loggedInAt(LocalDateTime.now().plusDays(1))
                .build();
    }

    default SiteMemberEntity createMemberGoogleUserEntity() {
        return SiteMemberEntity.builder()
                .nickname("구글 유저")
                .birthDate(LocalDate.of(2000, 1, 1))
                .loggedInAt(LocalDateTime.now().plusDays(2))
                .build();
    }

    default SiteMemberEntity createMemberKakaoUserEntity() {
        return SiteMemberEntity.builder()
                .nickname("카카오 유저")
                .birthDate(LocalDate.of(2000, 1, 1))
                .loggedInAt(LocalDateTime.now().plusDays(3))
                .build();
    }
}
