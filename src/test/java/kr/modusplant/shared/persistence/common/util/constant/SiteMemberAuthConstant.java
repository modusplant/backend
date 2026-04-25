package kr.modusplant.shared.persistence.common.util.constant;

import kr.modusplant.shared.enums.AuthProvider;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SiteMemberAuthConstant {
    public static final String MEMBER_AUTH_BASIC_ADMIN_EMAIL = "testAdmin1@gmail.com";
    public static final String MEMBER_AUTH_BASIC_ADMIN_PW = new BCryptPasswordEncoder().encode("testPw12@");
    public static final AuthProvider MEMBER_AUTH_BASIC_ADMIN_PROVIDER = AuthProvider.BASIC;
    public static final String MEMBER_AUTH_BASIC_ADMIN_ACCESS_TOKEN = "member_auth_basic_admin.access.token";
    public static final String MEMBER_AUTH_BASIC_ADMIN_AUTHORIZATION = "Bearer " + MEMBER_AUTH_BASIC_ADMIN_ACCESS_TOKEN;

    public static final String MEMBER_AUTH_BASIC_USER_EMAIL = "TestBasicUser2@naver.com";
    public static final String MEMBER_AUTH_BASIC_USER_PW = new BCryptPasswordEncoder().encode("Test!Pw14@");
    public static final AuthProvider MEMBER_AUTH_BASIC_USER_PROVIDER = AuthProvider.BASIC;
    public static final String MEMBER_AUTH_BASIC_USER_ACCESS_TOKEN = "member_auth_basic_user.access.token";
    public static final String MEMBER_AUTH_BASIC_USER_AUTHORIZATION = "Bearer " + MEMBER_AUTH_BASIC_USER_ACCESS_TOKEN;

    public static final String MEMBER_AUTH_GOOGLE_USER_EMAIL = "Test3gOogleUsser@gmail.com";
    public static final AuthProvider MEMBER_AUTH_GOOGLE_USER_PROVIDER = AuthProvider.GOOGLE;
    public static final String MEMBER_AUTH_GOOGLE_USER_PROVIDER_ID = "639796866968871286823";

    public static final String MEMBER_AUTH_KAKAO_USER_EMAIL = "test2KaKao4Uzer@kakao.com";
    public static final AuthProvider MEMBER_AUTH_KAKAO_USER_PROVIDER = AuthProvider.KAKAO;
    public static final String MEMBER_AUTH_KAKAO_USER_PROVIDER_ID = "9348634889";

    public static final AuthProvider MEMBER_AUTH_BASIC_KAKAO_USER_PROVIDER = AuthProvider.BASIC_KAKAO;
    public static final AuthProvider MEMBER_AUTH_BASIC_GOOGLE_USER_PROVIDER = AuthProvider.BASIC_GOOGLE;
}