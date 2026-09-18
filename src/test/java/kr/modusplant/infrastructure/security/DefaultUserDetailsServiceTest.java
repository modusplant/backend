package kr.modusplant.infrastructure.security;

import kr.modusplant.domains.account.identity.framework.outbound.jpa.entity.MemberAuthEntity;
import kr.modusplant.domains.account.identity.framework.outbound.jpa.repository.MemberAuthJpaRepository;
import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.MemberJpaRepository;
import kr.modusplant.infrastructure.security.exception.AccountStateException;
import kr.modusplant.infrastructure.security.models.DefaultUserDetails;
import kr.modusplant.shared.enums.AuthProvider;
import kr.modusplant.shared.enums.Role;
import kr.modusplant.shared.framework.jpa.exception.enums.EntityErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

class DefaultUserDetailsServiceTest {

    private final MemberJpaRepository memberRepository = Mockito.mock(MemberJpaRepository.class);
    private final MemberAuthJpaRepository memberAuthRepository = Mockito.mock(MemberAuthJpaRepository.class);
    private final DefaultUserDetailsService service = new DefaultUserDetailsService(memberRepository, memberAuthRepository);

    @Test
    @DisplayName("존재하는 회원 정보로 DefaultUserDetails 반환")
    void testLoadUserByUsername_givenExistingMember_willReturnDefaultUserDetails() {
        // given
        UUID memberUuid = UUID.randomUUID();
        MemberEntity member = Mockito.mock(MemberEntity.class);
        MemberAuthEntity auth = Mockito.mock(MemberAuthEntity.class);
        given(auth.getMember()).willReturn(member);
        given(auth.getEmail()).willReturn("test123@example.com");
        given(auth.getPw()).willReturn("encodedPw");
        given(auth.getProvider()).willReturn(AuthProvider.BASIC);
        given(member.getUuid()).willReturn(memberUuid);
        given(member.getNickname()).willReturn("nickname");
        given(member.getIsActive()).willReturn(true);
        given(member.getIsBanned()).willReturn(false);
        given(member.getRole()).willReturn(Role.USER);
        given(memberAuthRepository.findByEmail("test123@example.com")).willReturn(Optional.of(auth));
        given(memberRepository.findByUuid(memberUuid)).willReturn(Optional.of(member));

        // when
        DefaultUserDetails result = service.loadUserByUsername("test123@example.com");

        // then
        assertThat(result.getEmail()).isEqualTo("test123@example.com");
        assertThat(result.getUuid()).isEqualTo(memberUuid);
        assertThat(result.isActive()).isTrue();
    }

    @Test
    @DisplayName("인증 정보가 존재하지 않는 회원으로 예외 반환")
    void testLoadUserByUsername_givenNoMemberAuth_willThrowException() {
        // given
        given(memberAuthRepository.findByEmail("missing@example.com")).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> service.loadUserByUsername("missing@example.com"))
                .isInstanceOf(AccountStateException.class)
                .extracting("errorCode").isEqualTo(EntityErrorCode.NOT_FOUND_MEMBER_AUTH);
    }

    @Test
    @DisplayName("회원 정보가 존재하지 않는 인증 정보로 예외 반환")
    void testLoadUserByUsername_givenNoMember_willThrowException() {
        // given
        UUID memberUuid = UUID.randomUUID();
        MemberEntity member = Mockito.mock(MemberEntity.class);
        MemberAuthEntity auth = Mockito.mock(MemberAuthEntity.class);
        given(auth.getMember()).willReturn(member);
        given(member.getUuid()).willReturn(memberUuid);
        given(auth.getEmail()).willReturn("test123@example.com");
        given(memberAuthRepository.findByEmail("test123@example.com")).willReturn(Optional.of(auth));
        given(memberRepository.findByUuid(memberUuid)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> service.loadUserByUsername("test123@example.com"))
                .isInstanceOf(AccountStateException.class)
                .extracting("errorCode").isEqualTo(MemberErrorCode.NOT_FOUND_MEMBER);
    }
}
