package kr.modusplant.shared.persistence.common.util.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_ADMIN_UUID;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SiteMemberProfileConstant {
    public static final UUID MEMBER_PROFILE_BASIC_ADMIN_UUID = MEMBER_BASIC_ADMIN_UUID;
    public static final MultipartFile MEMBER_PROFILE_BASIC_ADMIN_IMAGE = new MockMultipartFile("image", "image.png", "image/png", "Image for basic admin".getBytes());
    public static final String MEMBER_PROFILE_BASIC_ADMIN_IMAGE_URL = "/images/197d2005-1d60-4707-ab4e-35f09fef9d44";
    public static final String MEMBER_PROFILE_BASIC_ADMIN_INTRODUCTION = "기본 관리자 프로필 소개";

    public static final UUID MEMBER_PROFILE_BASIC_USER_UUID = MEMBER_BASIC_USER_UUID;
    public static final MultipartFile MEMBER_PROFILE_BASIC_USER_IMAGE = new MockMultipartFile("image", "image.png", "image/png", "Image for basic user".getBytes());
    public static final String MEMBER_PROFILE_BASIC_USER_IMAGE_URL = "/images/08b705b6-f237-46c5-8ec8-12f828673afc";
    public static final String MEMBER_PROFILE_BASIC_USER_INTRODUCTION = "기본 유저 프로필 소개";
}