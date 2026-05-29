package kr.modusplant.domains.member.framework.outbound.jpa.adapter;

import kr.modusplant.domains.member.common.util.domain.aggregate.MemberProfileTestUtils;
import kr.modusplant.domains.member.common.util.framework.outbound.jpa.entity.MemberEntityTestUtils;
import kr.modusplant.domains.member.common.util.framework.outbound.jpa.entity.MemberProfileEntityTestUtils;
import kr.modusplant.domains.member.domain.aggregate.MemberProfile;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberProfileEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.mapper.MemberProfileJpaMapperImpl;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.MemberJpaRepository;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.MemberProfileJpaRepository;
import kr.modusplant.shared.framework.aws.service.AmazonS3Service;
import kr.modusplant.shared.framework.jpa.exception.NotFoundEntityException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Optional;

import static kr.modusplant.domains.member.common.constant.MemberProfileConstant.MEMBER_PROFILE_BASIC_USER_IMAGE_BYTES;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class MemberProfileRepositoryJpaAdapterTest implements
        MemberProfileTestUtils,
        MemberEntityTestUtils, MemberProfileEntityTestUtils {
    private final AmazonS3Service amazonS3Service = Mockito.mock(AmazonS3Service.class);
    private final MemberJpaRepository memberJpaRepository = Mockito.mock(MemberJpaRepository.class);
    private final MemberProfileJpaRepository memberProfileJpaRepository = Mockito.mock(MemberProfileJpaRepository.class);
    private final MemberProfileJpaMapperImpl memberProfileJpaMapper = new MemberProfileJpaMapperImpl(memberJpaRepository, amazonS3Service);
    private final MemberProfileRepositoryJpaAdapter memberProfileRepositoryJpaAdapter = new MemberProfileRepositoryJpaAdapter(amazonS3Service, memberProfileJpaMapper, memberJpaRepository, memberProfileJpaRepository);

    @Test
    @DisplayName("선택적인 데이터가 모두 있을 때 getById로 가용한 MemberProfile 반환(가용할 때)")
    void testGetById_givenValidMemberIdAndNotNullImageAndIntro_willReturnOptionalAvailableMemberProfile() throws IOException {
        // given & when
        given(memberProfileJpaRepository.findByUuid(any())).willReturn(Optional.of(createMemberProfileBasicUserEntityBuilder().member(createMemberBasicUserEntityWithUuid()).build()));
        given(amazonS3Service.downloadFile(any())).willReturn(MEMBER_PROFILE_BASIC_USER_IMAGE_BYTES);

        // then
        assertThat(memberProfileRepositoryJpaAdapter.getById(testMemberId)).isEqualTo(createMemberProfile());
    }

    @Test
    @DisplayName("선택적인 데이터가 모두 없을 때 getById로 가용한 MemberProfile 반환(가용할 때)")
    void testGetById_givenValidMemberIdAndNullImageAndIntro_willReturnOptionalAvailableMemberProfile() throws IOException {
        // given & when
        given(memberProfileJpaRepository.findByUuid(any())).willReturn(Optional.of(MemberProfileEntity.builder().member(createMemberBasicUserEntityWithUuid()).imagePath(null).introduction(null).build()));

        // then
        assertThat(memberProfileRepositoryJpaAdapter.getById(testMemberId)).isEqualTo(createMemberProfile());
    }

    @Test
    @DisplayName("getByNickname으로 예외 반환(가용하지 않을 때)")
    void testGetById_givenValidMemberId_willThrowException() {
        // given & when
        given(memberProfileJpaRepository.findByUuid(any())).willReturn(Optional.empty());

        // then
        assertThrows(NotFoundEntityException.class, () -> memberProfileRepositoryJpaAdapter.getById(testMemberId));
    }

    @Test
    @DisplayName("add로 MemberProfile 반환")
    void testAdd_givenValidMemberProfile_willReturnMemberProfile() throws IOException {
        // given
        MemberEntity memberBasicUserEntity = createMemberBasicUserEntityWithUuid();
        MemberProfileEntity memberProfileEntity = createMemberProfileBasicUserEntityBuilder().member(memberBasicUserEntity).build();
        given(memberJpaRepository.findByUuid(any())).willReturn(Optional.of(memberBasicUserEntity));
        given(memberProfileJpaRepository.save(memberProfileEntity)).willReturn(memberProfileEntity);
        given(amazonS3Service.downloadFile(any())).willReturn(MEMBER_PROFILE_BASIC_USER_IMAGE_BYTES);

        // when
        MemberProfile memberProfile = createMemberProfile();

        // then
        assertThat(memberProfileRepositoryJpaAdapter.add(memberProfile)).isEqualTo(memberProfile);
    }

    @Test
    @DisplayName("update로 MemberProfile 반환")
    void testUpdate_givenValidProfile_willReturnMemberProfile() throws IOException {
        // given
        MemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        MemberProfileEntity memberProfileEntity = createMemberProfileBasicUserEntityBuilder().member(memberEntity).build();
        MemberEntity updatedMemberEntity = MemberEntity.builder().member(memberEntity).nickname("abcNickname").build();
        MemberProfileEntity updatedMemberProfileEntity =
                MemberProfileEntity.builder().member(updatedMemberEntity).introduction("abcIntroduction").build();
        given(memberProfileJpaRepository.findByUuid(any())).willReturn(Optional.of(memberProfileEntity));
        given(memberProfileJpaRepository.save(updatedMemberProfileEntity)).willReturn(updatedMemberProfileEntity);
        given(amazonS3Service.downloadFile(any())).willReturn(MEMBER_PROFILE_BASIC_USER_IMAGE_BYTES);

        // when
        MemberProfile updatedMemberProfile = memberProfileJpaMapper.toMemberProfile(updatedMemberProfileEntity);
        MemberProfile result = memberProfileRepositoryJpaAdapter.update(updatedMemberProfile);

        // then
        assertThat(result.getNickname().getValue()).isEqualTo("abcNickname");
        assertThat(result.getMemberProfileIntroduction().getValue()).isEqualTo("abcIntroduction");
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