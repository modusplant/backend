package kr.modusplant.domains.member.framework.out.jpa.repository;

import kr.modusplant.domains.member.common.util.domain.aggregate.MemberProfileTestUtils;
import kr.modusplant.domains.member.domain.aggregate.MemberProfile;
import kr.modusplant.domains.member.framework.out.jpa.mapper.MemberProfileJpaMapperImpl;
import kr.modusplant.framework.out.aws.service.S3FileService;
import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.out.jpa.entity.SiteMemberProfileEntity;
import kr.modusplant.framework.out.jpa.entity.common.util.SiteMemberEntityTestUtils;
import kr.modusplant.framework.out.jpa.entity.common.util.SiteMemberProfileEntityTestUtils;
import kr.modusplant.framework.out.jpa.repository.SiteMemberJpaRepository;
import kr.modusplant.framework.out.jpa.repository.SiteMemberProfileJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Optional;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberProfileConstant.MEMBER_PROFILE_BASIC_USER_IMAGE_BYTES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class MemberProfileRepositoryJpaAdapterTest implements MemberProfileTestUtils, SiteMemberProfileEntityTestUtils, SiteMemberEntityTestUtils {
    private final S3FileService s3FileService = Mockito.mock(S3FileService.class);
    private final SiteMemberJpaRepository memberJpaRepository = Mockito.mock(SiteMemberJpaRepository.class);
    private final SiteMemberProfileJpaRepository memberProfileJpaRepository = Mockito.mock(SiteMemberProfileJpaRepository.class);
    private final MemberProfileJpaMapperImpl memberProfileJpaMapper = new MemberProfileJpaMapperImpl(memberJpaRepository, s3FileService);
    private final MemberProfileRepositoryJpaAdapter memberProfileRepositoryJpaAdapter = new MemberProfileRepositoryJpaAdapter(s3FileService, memberProfileJpaMapper, memberJpaRepository, memberProfileJpaRepository);

    @Test
    @DisplayName("getById로 가용한 MemberProfile 반환(가용할 때)")
    void testGetById_givenValidMemberId_willReturnOptionalAvailableMemberProfile() throws IOException {
        // given & when
        given(memberProfileJpaRepository.findByUuid(any())).willReturn(Optional.of(createMemberProfileBasicUserEntityBuilder().member(createMemberBasicUserEntityWithUuid()).build()));
        given(s3FileService.downloadFile(any())).willReturn(MEMBER_PROFILE_BASIC_USER_IMAGE_BYTES);

        // then
        assertThat(memberProfileRepositoryJpaAdapter.getById(testMemberId)).isEqualTo(Optional.of(createMemberProfile()));
    }

    @Test
    @DisplayName("getByNickname으로 가용한 MemberProfile 반환(가용하지 않을 때)")
    void testGetById_givenValidMemberId_willReturnOptionalEmptyMemberProfile() throws IOException {
        // given & when
        given(memberProfileJpaRepository.findByUuid(any())).willReturn(Optional.empty());

        // then
        assertThat(memberProfileRepositoryJpaAdapter.getById(testMemberId)).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("save로 MemberProfile 반환")
    void testSave_givenValidMemberProfileNickname_willReturn() throws IOException {
        // given
        SiteMemberEntity memberBasicUserEntity = createMemberBasicUserEntityWithUuid();
        SiteMemberProfileEntity memberProfileEntity = createMemberProfileBasicUserEntityBuilder().member(memberBasicUserEntity).build();
        given(memberJpaRepository.findByUuid(any())).willReturn(Optional.of(memberBasicUserEntity));
        given(memberProfileJpaRepository.save(memberProfileEntity)).willReturn(memberProfileEntity);
        given(s3FileService.downloadFile(any())).willReturn(MEMBER_PROFILE_BASIC_USER_IMAGE_BYTES);

        // when
        MemberProfile memberProfile = createMemberProfile();

        // then
        assertThat(memberProfileRepositoryJpaAdapter.save(memberProfile)).isEqualTo(memberProfile);
    }

    @Test
    @DisplayName("isIdExist로 true 반환")
    void testIsIdExist_givenIdThatExists_willReturnTrue() {
        // given & when
        given(memberProfileJpaRepository.existsByUuid(testMemberId.getValue())).willReturn(true);

        // when & then
        assertThat(memberProfileRepositoryJpaAdapter.isIdExist(testMemberId)).isEqualTo(true);
    }

    @Test
    @DisplayName("isIdExist로 false 반환")
    void testIsIdExist_givenIdThatIsNotExist_willReturnFalse() {
        // given & when
        given(memberProfileJpaRepository.existsByUuid(testMemberId.getValue())).willReturn(false);

        // when & then
        assertThat(memberProfileRepositoryJpaAdapter.isIdExist(testMemberId)).isEqualTo(false);
    }
}