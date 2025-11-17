package kr.modusplant.domains.member.domain.entity.nullobject;

import kr.modusplant.domains.member.common.util.domain.entity.MemberProfileImageTestUtils;
import kr.modusplant.domains.member.common.util.domain.entity.nullobject.MemberEmptyProfileImageTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.member.common.util.domain.vo.nullobject.MemberEmptyProfileImageBytesTestUtils.testMemberEmptyProfileImageBytes;
import static kr.modusplant.domains.member.common.util.domain.vo.nullobject.MemberEmptyProfileImagePathTestUtils.testMemberEmptyProfileImagePath;
import static org.assertj.core.api.Assertions.assertThat;

class MemberEmptyProfileImageTest implements MemberEmptyProfileImageTestUtils, MemberProfileImageTestUtils {
    @Test
    @DisplayName("create로 비어 있는 회원 프로필 이미지 반환")
    void testCreate_givenNothing_willReturnMemberEmptyProfileImage() {
        assertThat(MemberEmptyProfileImage.create()).isEqualTo(MemberEmptyProfileImage.create());
    }

    @Test
    @DisplayName("getMemberProfileImagePath로 비어 있는 회원 프로필 경로 반환")
    void testGetMemberProfileImagePath_givenNothing_willReturnMemberEmptyProfileImagePath() {
        assertThat(testMemberEmptyProfileImage.getMemberProfileImagePath()).isEqualTo(testMemberEmptyProfileImagePath);
    }

    @Test
    @DisplayName("getMemberProfileImageBytes로 비어 있는 회원 프로필 경로 반환")
    void testGetMemberProfileImageBytes_givenNothing_willReturnMemberEmptyProfileImageBytes() {
        assertThat(testMemberEmptyProfileImage.getMemberProfileImageBytes()).isEqualTo(testMemberEmptyProfileImageBytes);
    }
}