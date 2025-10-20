package kr.modusplant.domains.member.framework.out.jpa.repository;

import kr.modusplant.domains.member.common.util.framework.out.persistence.jpa.entity.MemberEntityTestUtils;
import kr.modusplant.domains.member.domain.aggregate.Member;
import kr.modusplant.domains.member.framework.out.jpa.mapper.MemberJpaMapperImpl;
import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.out.jpa.repository.SiteMemberJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class MemberRepositoryJpaAdapterTest implements MemberEntityTestUtils {
    private final MemberJpaMapperImpl memberJpaMapper = new MemberJpaMapperImpl();
    private final SiteMemberJpaRepository memberJpaRepository = Mockito.mock(SiteMemberJpaRepository.class);
    private final MemberRepositoryJpaAdapter memberRepositoryJpaAdapter = new MemberRepositoryJpaAdapter(memberJpaMapper, memberJpaRepository);

    @Test
    @DisplayName("getByNickname으로 가용한 Member 반환(가용할 때)")
    void testGetByNickname_givenValidMemberNickname_willReturnOptionalAvailableMember() {
        // given
        given(memberJpaRepository.findByNickname(any())).willReturn(Optional.of(createMemberEntityWithUuid()));

        // when & then
        assertThat(memberRepositoryJpaAdapter.getByNickname(testMemberNickname)).isEqualTo(Optional.of(createMember()));
    }

    @Test
    @DisplayName("getByNickname으로 가용한 Member 반환(가용하지 않을 때)")
    void testGetByNickname_givenValidMemberNickname_willReturnOptionalEmptyMember() {
        // given
        given(memberJpaRepository.findByNickname(any())).willReturn(Optional.empty());

        // when & then
        assertThat(memberRepositoryJpaAdapter.getByNickname(testMemberNickname)).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("save(MemberNickname memberNickname)로 Member 반환")
    void testSave_givenValidMemberNickname_willReturn() {
        // given
        SiteMemberEntity memberEntity = createMemberEntityWithUuid();
        given(memberJpaRepository.save(any())).willReturn(memberEntity);

        // when & then
        assertThat(memberRepositoryJpaAdapter.save(testMemberNickname).getMemberNickname()).isEqualTo(testMemberNickname);
    }

    @Test
    @DisplayName("save(MemberId memberId, MemberNickname memberNickname)로 Member 반환")
    void testSave_givenValidMemberIdAndNickname_willReturn() {
        // given
        SiteMemberEntity memberEntity = createMemberEntityWithUuid();
        given(memberJpaRepository.save(any())).willReturn(memberEntity);

        // when & then
        Member member = memberRepositoryJpaAdapter.save(testMemberId, testMemberNickname);
        assertThat(member.getMemberId()).isEqualTo(testMemberId);
        assertThat(member.getMemberNickname()).isEqualTo(testMemberNickname);
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
        given(memberJpaRepository.existsByNickname(testMemberNickname.getValue())).willReturn(true);

        // when & then
        assertThat(memberRepositoryJpaAdapter.isNicknameExist(testMemberNickname)).isEqualTo(true);
    }

    @Test
    @DisplayName("isNicknameExist로 false 반환")
    void testIsNicknameExist_givenNicknameThatIsNotExist_willReturnFalse() {
        // given & when
        given(memberJpaRepository.existsByNickname(testMemberNickname.getValue())).willReturn(false);

        // when & then
        assertThat(memberRepositoryJpaAdapter.isNicknameExist(testMemberNickname)).isEqualTo(false);
    }
}