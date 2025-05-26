package kr.modusplant.global.middleware.security;

import jakarta.transaction.Transactional;
import kr.modusplant.domains.member.domain.model.SiteMember;
import kr.modusplant.domains.member.domain.model.SiteMemberAuth;
import kr.modusplant.domains.member.domain.model.SiteMemberRole;
import kr.modusplant.domains.member.enums.AuthProvider;
import kr.modusplant.domains.member.persistence.repository.SiteMemberAuthRepository;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRoleRepository;
import kr.modusplant.global.middleware.security.mapper.SiteMemberAuthEntityToDomainMapper;
import kr.modusplant.global.middleware.security.mapper.SiteMemberEntityToDomainMapper;
import kr.modusplant.global.middleware.security.mapper.SiteMemberRoleEntityToDomainMapper;
import kr.modusplant.global.middleware.security.models.SiteMemberUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SiteMemberUserDetailsService implements UserDetailsService {

    private final SiteMemberRepository memberRepository;
    private final SiteMemberAuthRepository memberAuthRepository;
    private final SiteMemberRoleRepository memberRoleRepository;
    private final SiteMemberEntityToDomainMapper memberEntityToDomainMapper;
    private final SiteMemberAuthEntityToDomainMapper memberAuthEntityToDomainMapper;
    private final SiteMemberRoleEntityToDomainMapper memberRoleEntityToDomainMapper;

    @Override
    public SiteMemberUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        SiteMemberAuth memberAuth = memberAuthEntityToDomainMapper.toSiteMemberAuth(
                memberAuthRepository.findByEmailAndProvider(email, AuthProvider.BASIC)
                        .orElseThrow(() -> new IllegalStateException("no user auth")));

        SiteMember member = memberEntityToDomainMapper.toSiteMember(
                memberRepository.findByUuid(memberAuth.getActiveMemberUuid())
                        .orElseThrow(() -> new IllegalStateException("no user")));

        SiteMemberRole memberRole = memberRoleEntityToDomainMapper.toSiteMemberRole(
                memberRoleRepository.findByUuid(memberAuth.getActiveMemberUuid())
                        .orElseThrow(() -> new IllegalStateException("no user role")));

        return SiteMemberUserDetails.builder()
                .email(memberAuth.getEmail())
                .password(memberAuth.getPw())
                .activeUuid(memberAuth.getActiveMemberUuid())
                .nickname(member.getNickname())
                .provider(memberAuth.getProvider())
                .isActive(member.getIsActive())
                .isDisabledByLinking(member.getIsDisabledByLinking())
                .isBanned(member.getIsBanned())
                .isDeleted(member.getIsDeleted())
                .authorities(List.of(new SimpleGrantedAuthority(memberRole.getRole().getValue())))
                .build();
    }
}