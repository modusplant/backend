package kr.modusplant.support.util.domain;

import kr.modusplant.global.domain.model.SiteMember;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface SiteMemberTestUtils {
    SiteMember memberBasicAdmin = SiteMember.builder()
            .nickname("관리자")
            .birthDate(LocalDate.of(2000, 1, 1))
            .loggedInAt(LocalDateTime.now())
            .build();

    SiteMember memberBasicUser = SiteMember.builder()
            .nickname("일반 유저")
            .birthDate(LocalDate.of(2000, 1, 1))
            .loggedInAt(LocalDateTime.now().plusDays(1))
            .build();

    SiteMember memberGoogleUser = SiteMember.builder()
            .nickname("구글 유저")
            .birthDate(LocalDate.of(2000, 1, 1))
            .loggedInAt(LocalDateTime.now().plusDays(2))
            .build();

    SiteMember memberKakaoUser = SiteMember.builder()
            .nickname("카카오 유저")
            .birthDate(LocalDate.of(2000, 1, 1))
            .loggedInAt(LocalDateTime.now().plusDays(3))
            .build();
}
