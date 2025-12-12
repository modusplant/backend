package kr.modusplant.domains.member.framework.out.jpa.repository;

import kr.modusplant.domains.member.common.util.domain.aggregate.MemberTestUtils;
import kr.modusplant.domains.member.domain.aggregate.Member;
import kr.modusplant.domains.member.framework.out.jpa.mapper.MemberJpaMapperImpl;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.jpa.entity.common.util.SiteMemberEntityTestUtils;
import kr.modusplant.framework.jpa.repository.SiteMemberJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberNicknameTestUtils.TEST_NICKNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class MemberRepositoryJpaAdapterTest implements MemberTestUtils, SiteMemberEntityTestUtils {
    private final MemberJpaMapperImpl memberJpaMapper = new MemberJpaMapperImpl();
    private final SiteMemberJpaRepository memberJpaRepository = Mockito.mock(SiteMemberJpaRepository.class);
    private final MemberRepositoryJpaAdapter memberRepositoryJpaAdapter = new MemberRepositoryJpaAdapter(memberJpaMapper, memberJpaRepository);

    @Test
    @DisplayName("getById로 가용한 Member 반환(가용할 때)")
    void testGetById_givenValidMemberId_willReturnOptionalAvailableMember() {
        // given
        given(memberJpaRepository.findByUuid(any())).willReturn(Optional.of(createMemberBasicUserEntityWithUuid()));

        // when & then
        assertThat(memberRepositoryJpaAdapter.getById(testMemberId)).isEqualTo(Optional.of(createMember()));
    }

    @Test
    @DisplayName("getByNickname으로 가용한 Member 반환(가용하지 않을 때)")
    void testGetById_givenValidMemberId_willReturnOptionalEmptyMember() {
        // given
        given(memberJpaRepository.findByUuid(any())).willReturn(Optional.empty());

        // when & then
        assertThat(memberRepositoryJpaAdapter.getById(testMemberId)).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("getByNickname으로 가용한 Member 반환(가용할 때)")
    void testGetByNickname_givenValidMemberNickname_willReturnOptionalAvailableMember() {
        // given
        given(memberJpaRepository.findByNickname(any())).willReturn(Optional.of(createMemberBasicUserEntityWithUuid()));

        // when & then
        assertThat(memberRepositoryJpaAdapter.getByNickname(TEST_NICKNAME)).isEqualTo(Optional.of(createMember()));
    }

    @Test
    @DisplayName("getByNickname으로 가용한 Member 반환(가용하지 않을 때)")
    void testGetByNickname_givenValidMemberNickname_willReturnOptionalEmptyMember() {
        // given
        given(memberJpaRepository.findByNickname(any())).willReturn(Optional.empty());

        // when & then
        assertThat(memberRepositoryJpaAdapter.getByNickname(TEST_NICKNAME)).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("save(MemberNickname nickname)로 Member 반환")
    void testSave_givenValidMemberNickname_willReturnMember() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        given(memberJpaRepository.save(any())).willReturn(memberEntity);

        // when & then
        assertThat(memberRepositoryJpaAdapter.save(TEST_NICKNAME).getNickname()).isEqualTo(TEST_NICKNAME);
    }

    @Test
    @DisplayName("save(MemberId memberId, MemberNickname nickname)로 Member 반환")
    void testSave_givenValidMemberIdAndNickname_willReturnMember() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        given(memberJpaRepository.save(any())).willReturn(memberEntity);

        // when & then
        Member member = memberRepositoryJpaAdapter.save(testMemberId, TEST_NICKNAME);
        assertThat(member.getMemberId()).isEqualTo(testMemberId);
        assertThat(member.getNickname()).isEqualTo(TEST_NICKNAME);
    }

    @Test
    @DisplayName("isIdExist로 true 반환")
    void testIsIdExist_givenIdThatExists_willReturnTrue() {
        // given & when
        given(memberJpaRepository.existsByUuid(testMemberId.getValue())).willReturn(true);

        // when & then
        assertThat(memberRepositoryJpaAdapter.isIdExist(testMemberId)).isEqualTo(true);
    }

    @Test
    @DisplayName("isIdExist로 false 반환")
    void testIsIdExist_givenIdThatIsNotExist_willReturnFalse() {
        // given & when
        given(memberJpaRepository.existsByUuid(testMemberId.getValue())).willReturn(false);

        // when & then
        assertThat(memberRepositoryJpaAdapter.isIdExist(testMemberId)).isEqualTo(false);
    }

    @Test
    @DisplayName("isNicknameExist로 true 반환")
    void testIsNicknameExist_givenNicknameThatExists_willReturnTrue() {
        // given & when
        given(memberJpaRepository.existsByNickname(TEST_NICKNAME.getValue())).willReturn(true);

        // when & then
        assertThat(memberRepositoryJpaAdapter.isNicknameExist(TEST_NICKNAME)).isEqualTo(true);
    }

    @Test
    @DisplayName("isNicknameExist로 false 반환")
    void testIsNicknameExist_givenNicknameThatIsNotExist_willReturnFalse() {
        // given & when
        given(memberJpaRepository.existsByNickname(TEST_NICKNAME.getValue())).willReturn(false);

        // when & then
        assertThat(memberRepositoryJpaAdapter.isNicknameExist(TEST_NICKNAME)).isEqualTo(false);
    }
}