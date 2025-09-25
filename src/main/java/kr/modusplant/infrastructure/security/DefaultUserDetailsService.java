package kr.modusplant.infrastructure.security;

import jakarta.transaction.Transactional;
import kr.modusplant.framework.out.jpa.entity.SiteMemberAuthEntity;
import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.out.jpa.entity.SiteMemberRoleEntity;
import kr.modusplant.framework.out.jpa.repository.SiteMemberAuthRepository;
import kr.modusplant.framework.out.jpa.repository.SiteMemberRepository;
import kr.modusplant.framework.out.jpa.repository.SiteMemberRoleRepository;
import kr.modusplant.infrastructure.security.models.DefaultUserDetails;
import kr.modusplant.legacy.domains.member.enums.AuthProvider;
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

    private final SiteMemberRepository memberRepository;
    private final SiteMemberAuthRepository memberAuthRepository;
    private final SiteMemberRoleRepository memberRoleRepository;

    @Override
    public DefaultUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        SiteMemberAuthEntity auth = memberAuthRepository
                .findByEmailAndProvider(email, AuthProvider.BASIC).orElseThrow();
        SiteMemberEntity member = memberRepository
                .findByUuid(auth.getActiveMember().getUuid()).orElseThrow();
        SiteMemberRoleEntity role = memberRoleRepository
                .findByUuid(auth.getActiveMember().getUuid()).orElseThrow();

        return DefaultUserDetails.builder()
                .email(auth.getEmail())
                .password(auth.getPw())
                .activeUuid(auth.getActiveMember().getUuid())
                .nickname(member.getNickname())
                .provider(auth.getProvider())
                .isActive(member.getIsActive())
                .isDisabledByLinking(member.getIsDisabledByLinking())
                .isBanned(member.getIsBanned())
                .isDeleted(member.getIsDeleted())
                .authorities(List.of(new SimpleGrantedAuthority(role.getRole().getValue())))
                .build();
    }
}