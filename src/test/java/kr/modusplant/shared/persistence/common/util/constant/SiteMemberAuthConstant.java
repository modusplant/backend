package kr.modusplant.shared.persistence.common.util.constant;

import kr.modusplant.legacy.domains.member.enums.AuthProvider;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SiteMemberAuthConstant {
    public static final UUID MEMBER_AUTH_BASIC_ADMIN_UUID = UUID.fromString("48c75e56-34fb-4fc2-8e45-ee5669f79fdd");
    public static final String MEMBER_AUTH_BASIC_ADMIN_EMAIL = "testAdmin1@gmail.com";
    public static final String MEMBER_AUTH_BASIC_ADMIN_PW = new BCryptPasswordEncoder().encode("testPw12@");
    public static final AuthProvider MEMBER_AUTH_BASIC_ADMIN_PROVIDER = AuthProvider.BASIC;

    public static final UUID MEMBER_AUTH_BASIC_USER_ACTIVE_MEMBER_UUID = UUID.fromString("d6b716f1-60f7-4c79-aeaf-37037101f126");
    public static final UUID MEMBER_AUTH_BASIC_USER_ORIGINAL_MEMBER_UUID = MEMBER_AUTH_BASIC_USER_ACTIVE_MEMBER_UUID;
    public static final String MEMBER_AUTH_BASIC_USER_EMAIL = "TestBasicUser2@naver.com";
    public static final String MEMBER_AUTH_BASIC_USER_PW = new BCryptPasswordEncoder().encode("Test!Pw14@");
    public static final AuthProvider MEMBER_AUTH_BASIC_USER_PROVIDER = AuthProvider.BASIC;
    public static final String MEMBER_AUTH_BASIC_USER_PROVIDER_ID = "";

    public static final UUID MEMBER_AUTH_GOOGLE_USER_ACTIVE_MEMBER_UUID = UUID.fromString("6ba6176c-bbc5-4767-9a25-598631918365");
    public static final UUID MEMBER_AUTH_GOOGLE_USER_ORIGINAL_MEMBER_UUID = MEMBER_AUTH_GOOGLE_USER_ACTIVE_MEMBER_UUID;
    public static final String MEMBER_AUTH_GOOGLE_USER_EMAIL = "Test3gOogleUsser@gmail.com";
    public static final String MEMBER_AUTH_GOOGLE_USER_PW = "";
    public static final AuthProvider MEMBER_AUTH_GOOGLE_USER_PROVIDER = AuthProvider.GOOGLE;
    public static final String MEMBER_AUTH_GOOGLE_USER_PROVIDER_ID = "639796866968871286823";

    public static final UUID MEMBER_AUTH_KAKAO_USER_ACTIVE_MEMBER_UUID = UUID.fromString("4f9e87cd-ca94-4ca0-b32b-8f492ee4b93f");
    public static final UUID MEMBER_AUTH_KAKAO_USER_ORIGINAL_MEMBER_UUID = MEMBER_AUTH_KAKAO_USER_ACTIVE_MEMBER_UUID;
    public static final String MEMBER_AUTH_KAKAO_USER_EMAIL = "test2KaKao4Uzer@kakao.com";
    public static final String MEMBER_AUTH_KAKAO_USER_PW = "";
    public static final AuthProvider MEMBER_AUTH_KAKAO_USER_PROVIDER = AuthProvider.KAKAO;
    public static final String MEMBER_AUTH_KAKAO_USER_PROVIDER_ID = "9348634889";
}