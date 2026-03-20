package kr.modusplant.domains.member.domain.entity.nullobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.member.common.util.domain.entity.nullobject.EmptyMemberProfileImageTestUtils.TEST_EMPTY_MEMBER_PROFILE_IMAGE;
import static kr.modusplant.domains.member.common.util.domain.vo.nullobject.EmptyMemberProfileImageBytesTestUtils.testEmptyMemberProfileImageBytes;
import static kr.modusplant.domains.member.common.util.domain.vo.nullobject.EmptyMemberProfileImagePathTestUtils.testEmptyMemberProfileImagePath;
import static org.assertj.core.api.Assertions.assertThat;

class EmptyMemberProfileImageTest {
    @Test
    @DisplayName("create로 비어 있는 회원 프로필 이미지 반환")
    void testCreate_givenNothing_willReturnEmptyMemberProfileImage() {
        assertThat(EmptyMemberProfileImage.create()).isEqualTo(EmptyMemberProfileImage.create());
    }

    @Test
    @DisplayName("getMemberProfileImagePath로 비어 있는 회원 프로필 경로 반환")
    void testGetMemberProfileImagePath_givenNothing_willReturnEmptyMemberProfileImagePath() {
        assertThat(TEST_EMPTY_MEMBER_PROFILE_IMAGE.getMemberProfileImagePath()).isEqualTo(testEmptyMemberProfileImagePath);
    }

    @Test
    @DisplayName("getMemberProfileImageBytes로 비어 있는 회원 프로필 경로 반환")
    void testGetMemberProfileImageBytes_givenNothing_willReturnEmptyMemberProfileImageBytes() {
        assertThat(TEST_EMPTY_MEMBER_PROFILE_IMAGE.getMemberProfileImageBytes()).isEqualTo(testEmptyMemberProfileImageBytes);
    }
}