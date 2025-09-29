package kr.modusplant.framework.out.jpa.entity.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SiteMemberEntityConstant {
    public static final UUID MEMBER_BASIC_ADMIN_UUID = UUID.fromString("48c75e56-34fb-4fc2-8e45-ee5669f79fdd");
    public static final String MEMBER_BASIC_ADMIN_NICKNAME = "관리자";
    public static final LocalDate MEMBER_BASIC_ADMIN_BIRTH_DATE = LocalDate.of(2001, 1, 1);
    public static final Boolean MEMBER_BASIC_ADMIN_IS_ACTIVE = true;
    public static final LocalDateTime MEMBER_BASIC_ADMIN_LOGGED_IN_AT = LocalDateTime.now().minusDays(1);

    public static final UUID MEMBER_BASIC_USER_UUID = UUID.fromString("d6b716f1-60f7-4c79-aeaf-37037101f126");
    public static final String MEMBER_BASIC_USER_NICKNAME = "일반유저";
    public static final LocalDate MEMBER_BASIC_USER_BIRTH_DATE = LocalDate.of(2002, 2, 2);
    public static final Boolean MEMBER_BASIC_USER_IS_ACTIVE = true;
    public static final LocalDateTime MEMBER_BASIC_USER_LOGGED_IN_AT = LocalDateTime.now().minusDays(2);

    public static final UUID MEMBER_GOOGLE_USER_UUID = UUID.fromString("6ba6176c-bbc5-4767-9a25-598631918365");
    public static final String MEMBER_GOOGLE_USER_NICKNAME = "구글유저";
    public static final LocalDate MEMBER_GOOGLE_USER_BIRTH_DATE = LocalDate.of(2003, 3, 3);
    public static final Boolean MEMBER_GOOGLE_USER_IS_ACTIVE = true;
    public static final LocalDateTime MEMBER_GOOGLE_USER_LOGGED_IN_AT = LocalDateTime.now().minusDays(3);

    public static final UUID MEMBER_KAKAO_USER_UUID = UUID.fromString("4f9e87cd-ca94-4ca0-b32b-8f492ee4b93f");
    public static final String MEMBER_KAKAO_USER_NICKNAME = "카카오유저";
    public static final LocalDate MEMBER_KAKAO_USER_BIRTH_DATE = LocalDate.of(2004, 4, 4);
    public static final Boolean MEMBER_KAKAO_USER_IS_ACTIVE = true;
    public static final LocalDateTime MEMBER_KAKAO_USER_LOGGED_IN_AT = LocalDateTime.now().minusDays(4);
}
