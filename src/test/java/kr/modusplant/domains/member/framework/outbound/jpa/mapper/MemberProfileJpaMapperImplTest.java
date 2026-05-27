package kr.modusplant.domains.member.framework.outbound.jpa.mapper;

import kr.modusplant.domains.member.common.util.domain.aggregate.MemberProfileTestUtils;
import kr.modusplant.domains.member.common.util.domain.aggregate.MemberTestUtils;
import kr.modusplant.domains.member.domain.aggregate.MemberProfile;
import kr.modusplant.domains.member.domain.entity.nullobject.EmptyMemberProfileImage;
import kr.modusplant.domains.member.domain.vo.nullobject.EmptyMemberProfileIntroduction;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberProfileEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.common.util.MemberEntityTestUtils;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.common.util.MemberProfileEntityTestUtils;
import kr.modusplant.domains.member.framework.outbound.jpa.mapper.supers.MemberProfileJpaMapper;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.MemberJpaRepository;
import kr.modusplant.shared.framework.aws.service.AmazonS3Service;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Optional;

import static kr.modusplant.domains.member.common.constant.MemberProfileConstant.*;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.shared.kernel.common.util.NicknameTestUtils.testNormalUserNickname;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class MemberProfileJpaMapperImplTest implements
        MemberTestUtils, MemberProfileTestUtils,
        MemberEntityTestUtils, MemberProfileEntityTestUtils {
    private final MemberJpaRepository memberJpaRepository = Mockito.mock(MemberJpaRepository.class);
    private final AmazonS3Service amazonS3Service = Mockito.mock(AmazonS3Service.class);
    private final MemberProfileJpaMapper memberProfileJpaMapper = new MemberProfileJpaMapperImpl(memberJpaRepository, amazonS3Service);

    @Test
    @DisplayName("toMemberProfileEntity로 엔터티 반환")
    void testToMemberProfileEntity_givenValidMemberProfile_willReturnEntity() {
        // given
        MemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        given(memberJpaRepository.findByUuid(any())).willReturn(Optional.of(memberEntity));

        // when
        MemberProfileEntity memberProfileEntity = memberProfileJpaMapper.toMemberProfileEntity(createMemberProfile());

        // then
        assertThat(memberProfileEntity.getMember()).isEqualTo(memberEntity);
        assertThat(memberProfileEntity.getImagePath()).isEqualTo(MEMBER_PROFILE_BASIC_USER_IMAGE_PATH);
        assertThat(memberProfileEntity.getIntroduction()).isEqualTo(MEMBER_PROFILE_BASIC_USER_INTRODUCTION);
    }

    @Test
    @DisplayName("toMemberProfile로 회원 반환")
    void testToMemberProfile_givenValidMemberProfileEntity_willReturnMemberProfile() throws IOException {
        // given & when
        given(amazonS3Service.downloadFile(any())).willReturn(MEMBER_PROFILE_BASIC_USER_IMAGE_BYTES);

        // then
        assertThat(memberProfileJpaMapper.toMemberProfile(createMemberProfileBasicUserEntityBuilder().member(createMemberBasicUserEntityWithUuid()).build())).isEqualTo(createMemberProfile());
    }

    @Test
    @DisplayName("회원 프로필 이미지와 프로필 소개가 비었을 때 toMemberProfile로 회원 반환")
    void testToMemberProfile_givenEmptyProfileImageAndProfileIntroduction_willReturnMemberProfile() throws IOException {
        assertThat(memberProfileJpaMapper.toMemberProfile(
                MemberProfileEntity.builder()
                        .member(createMemberBasicUserEntityWithUuid())
                        .imagePath(null)
                        .introduction(null)
                        .build()))
                .isEqualTo(MemberProfile.create(
                        testMemberId,
                        EmptyMemberProfileImage.create(),
                        EmptyMemberProfileIntroduction.create(),
                        testNormalUserNickname));
    }
}