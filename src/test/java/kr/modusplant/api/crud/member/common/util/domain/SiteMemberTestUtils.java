package kr.modusplant.api.crud.member.common.util.domain;

import kr.modusplant.api.crud.member.domain.model.SiteMember;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public interface SiteMemberTestUtils {
    SiteMember memberBasicAdmin = SiteMember.builder()
            .nickname("관리자")
            .birthDate(LocalDate.of(2000, 1, 1))
            .loggedInAt(LocalDateTime.now())
            .build();

    SiteMember memberBasicAdminWithUuid = SiteMember.builder()
            .uuid(UUID.fromString("48c75e56-34fb-4fc2-8e45-ee5669f79fdd"))
            .nickname(memberBasicAdmin.getNickname())
            .birthDate(memberBasicAdmin.getBirthDate())
            .loggedInAt(memberBasicAdmin.getLoggedInAt())
            .build();

    SiteMember memberBasicUser = SiteMember.builder()
            .nickname("일반 유저")
            .birthDate(LocalDate.of(2000, 1, 1))
            .loggedInAt(LocalDateTime.now().plusDays(1))
            .build();

    SiteMember memberBasicUserWithUuid = SiteMember.builder()
            .uuid(UUID.fromString("d6b716f1-60f7-4c79-aeaf-37037101f126"))
            .nickname(memberBasicUser.getNickname())
            .birthDate(memberBasicUser.getBirthDate())
            .loggedInAt(memberBasicUser.getLoggedInAt())
            .build();

    SiteMember memberGoogleUser = SiteMember.builder()
            .nickname("구글 유저")
            .birthDate(LocalDate.of(2000, 1, 1))
            .loggedInAt(LocalDateTime.now().plusDays(2))
            .build();

    SiteMember memberGoogleUserWithUuid = SiteMember.builder()
            .uuid(UUID.fromString("6ba6176c-bbc5-4767-9a25-598631918365"))
            .nickname(memberGoogleUser.getNickname())
            .birthDate(memberGoogleUser.getBirthDate())
            .loggedInAt(memberGoogleUser.getLoggedInAt())
            .build();

    SiteMember memberKakaoUser = SiteMember.builder()
            .nickname("카카오 유저")
            .birthDate(LocalDate.of(2000, 1, 1))
            .loggedInAt(LocalDateTime.now().plusDays(3))
            .build();

    SiteMember memberKakaoUserWithUuid = SiteMember.builder()
            .uuid(UUID.fromString("4f9e87cd-ca94-4ca0-b32b-8f492ee4b93f"))
            .nickname(memberKakaoUser.getNickname())
            .birthDate(memberKakaoUser.getBirthDate())
            .loggedInAt(memberKakaoUser.getLoggedInAt())
            .build();
}
