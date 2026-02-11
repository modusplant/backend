package kr.modusplant.infrastructure.security;

import jakarta.transaction.Transactional;
import kr.modusplant.framework.jpa.entity.SiteMemberAuthEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberRoleEntity;
import kr.modusplant.framework.jpa.repository.SiteMemberAuthJpaRepository;
import kr.modusplant.framework.jpa.repository.SiteMemberJpaRepository;
import kr.modusplant.framework.jpa.repository.SiteMemberRoleJpaRepository;
import kr.modusplant.infrastructure.security.enums.SecurityErrorCode;
import kr.modusplant.infrastructure.security.exception.AccountStateException;
import kr.modusplant.infrastructure.security.models.DefaultUserDetails;
import kr.modusplant.shared.enums.AuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DefaultUserDetailsService implements UserDetailsService {

    private final SiteMemberJpaRepository memberRepository;
    private final SiteMemberAuthJpaRepository memberAuthRepository;
    private final SiteMemberRoleJpaRepository memberRoleRepository;

    @Override
    public DefaultUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        SiteMemberAuthEntity auth = memberAuthRepository
                .findByEmailAndProvider(email, AuthProvider.BASIC).orElseThrow(
                        () -> new AccountStateException(SecurityErrorCode.MEMBER_STATE_NOT_FOUND));
        SiteMemberEntity member = memberRepository
                .findByUuid(auth.getOriginalMember().getUuid()).orElseThrow(
                        () -> new AccountStateException(SecurityErrorCode.MEMBER_AUTH_STATE_NOT_FOUND));
        SiteMemberRoleEntity role = memberRoleRepository.findByMember(auth.getOriginalMember()).orElseThrow(
                () -> new AccountStateException(SecurityErrorCode.MEMBER_ROLE_STATE_NOT_FOUND));

        return DefaultUserDetails.builder()
                .email(auth.getEmail())
                .password(auth.getPw())
                .activeUuid(auth.getOriginalMember().getUuid())
                .nickname(member.getNickname())
                .provider(auth.getProvider())
                .isActive(member.getIsActive())
                .isBanned(member.getIsBanned())
                .isDeleted(member.getIsDeleted())
                .authorities(List.of(new SimpleGrantedAuthority(role.getRole().getValue())))
                .build();
    }
}