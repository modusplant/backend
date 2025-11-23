package kr.modusplant.infrastructure.security;

import jakarta.transaction.Transactional;
import kr.modusplant.framework.jpa.entity.SiteMemberAuthEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberRoleEntity;
import kr.modusplant.framework.jpa.repository.SiteMemberAuthJpaRepository;
import kr.modusplant.framework.jpa.repository.SiteMemberJpaRepository;
import kr.modusplant.framework.jpa.repository.SiteMemberRoleJpaRepository;
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
                .findByEmailAndProvider(email, AuthProvider.BASIC).orElseThrow();
        SiteMemberEntity member = memberRepository
                .findByUuid(auth.getActiveMember().getUuid()).orElseThrow();
        SiteMemberRoleEntity role = memberRoleRepository.findByMember(auth.getActiveMember())
                .orElseThrow();

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