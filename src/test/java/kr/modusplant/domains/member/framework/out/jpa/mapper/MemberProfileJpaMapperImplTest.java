package kr.modusplant.domains.member.framework.out.jpa.mapper;

import kr.modusplant.domains.member.common.util.domain.aggregate.MemberProfileTestUtils;
import kr.modusplant.domains.member.common.util.domain.aggregate.MemberTestUtils;
import kr.modusplant.domains.member.domain.aggregate.MemberProfile;
import kr.modusplant.domains.member.domain.entity.nullobject.MemberEmptyProfileImage;
import kr.modusplant.domains.member.domain.vo.nullobject.MemberEmptyProfileIntroduction;
import kr.modusplant.domains.member.framework.out.jpa.mapper.supers.MemberProfileJpaMapper;
import kr.modusplant.framework.aws.service.S3FileService;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberProfileEntity;
import kr.modusplant.framework.jpa.entity.common.util.SiteMemberEntityTestUtils;
import kr.modusplant.framework.jpa.entity.common.util.SiteMemberProfileEntityTestUtils;
import kr.modusplant.framework.jpa.repository.SiteMemberJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Optional;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.shared.kernel.common.util.NicknameTestUtils.testNormalUserNickname;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberProfileConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class MemberProfileJpaMapperImplTest implements MemberTestUtils, MemberProfileTestUtils, SiteMemberEntityTestUtils, SiteMemberProfileEntityTestUtils {
    private final SiteMemberJpaRepository memberJpaRepository = Mockito.mock(SiteMemberJpaRepository.class);
    private final S3FileService s3FileService = Mockito.mock(S3FileService.class);
    private final MemberProfileJpaMapper memberProfileJpaMapper = new MemberProfileJpaMapperImpl(memberJpaRepository, s3FileService);

    @Test
    @DisplayName("toMemberProfileEntity로 엔터티 반환")
    void testToMemberProfileEntity_givenValidMemberProfile_willReturnEntity() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        given(memberJpaRepository.findByUuid(any())).willReturn(Optional.of(memberEntity));

        // when
        SiteMemberProfileEntity memberProfileEntity = memberProfileJpaMapper.toMemberProfileEntity(createMemberProfile());

        // then
        assertThat(memberProfileEntity.getMember()).isEqualTo(memberEntity);
        assertThat(memberProfileEntity.getImagePath()).isEqualTo(MEMBER_PROFILE_BASIC_USER_IMAGE_PATH);
        assertThat(memberProfileEntity.getIntroduction()).isEqualTo(MEMBER_PROFILE_BASIC_USER_INTRODUCTION);
    }

    @Test
    @DisplayName("toMemberProfile로 회원 반환")
    void testToMemberProfile_givenValidMemberProfileEntity_willReturnMemberProfile() throws IOException {
        // given & when
        given(s3FileService.downloadFile(any())).willReturn(MEMBER_PROFILE_BASIC_USER_IMAGE_BYTES);

        // then
        assertThat(memberProfileJpaMapper.toMemberProfile(createMemberProfileBasicUserEntityBuilder().member(createMemberBasicUserEntityWithUuid()).build())).isEqualTo(createMemberProfile());
    }

    @Test
    @DisplayName("회원 프로필 이미지와 프로필 소개가 비었을 때 toMemberProfile로 회원 반환")
    void testToMemberProfile_givenEmptyProfileImageAndProfileIntroduction_willReturnMemberProfile() throws IOException {
        assertThat(memberProfileJpaMapper.toMemberProfile(
                SiteMemberProfileEntity.builder()
                        .member(createMemberBasicUserEntityWithUuid())
                        .imagePath(null)
                        .introduction(null)
                        .build()))
                .isEqualTo(MemberProfile.create(
                        testMemberId,
                        MemberEmptyProfileImage.create(),
                        MemberEmptyProfileIntroduction.create(),
                        testNormalUserNickname));
    }
}