package kr.modusplant.infrastructure.security;

import jakarta.transaction.Transactional;
import kr.modusplant.infrastructure.security.models.DefaultUserDetails;
import kr.modusplant.legacy.domains.member.domain.model.SiteMember;
import kr.modusplant.legacy.domains.member.domain.model.SiteMemberAuth;
import kr.modusplant.legacy.domains.member.domain.model.SiteMemberRole;
import kr.modusplant.legacy.domains.member.domain.service.SiteMemberAuthValidationService;
import kr.modusplant.legacy.domains.member.domain.service.SiteMemberRoleValidationService;
import kr.modusplant.legacy.domains.member.domain.service.SiteMemberValidationService;
import kr.modusplant.legacy.domains.member.enums.AuthProvider;
import kr.modusplant.legacy.domains.member.mapper.SiteMemberAuthDomainInfraMapper;
import kr.modusplant.legacy.domains.member.mapper.SiteMemberDomainInfraMapper;
import kr.modusplant.legacy.domains.member.mapper.SiteMemberRoleDomainInfraMapper;
import kr.modusplant.legacy.domains.member.persistence.repository.SiteMemberAuthRepository;
import kr.modusplant.legacy.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.legacy.domains.member.persistence.repository.SiteMemberRoleRepository;
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

    private final SiteMemberValidationService memberValidationService;
    private final SiteMemberAuthValidationService memberAuthValidationService;
    private final SiteMemberRoleValidationService memberRoleValidationService;

    private final SiteMemberDomainInfraMapper memberDomainInfraMapper;
    private final SiteMemberAuthDomainInfraMapper memberAuthDomainInfraMapper;
    private final SiteMemberRoleDomainInfraMapper memberRoleDomainInfraMapper;

    private final SiteMemberRepository memberRepository;
    private final SiteMemberAuthRepository memberAuthRepository;
    private final SiteMemberRoleRepository memberRoleRepository;

    @Override
    public DefaultUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        memberAuthValidationService.validateNotFoundEmailAndAuthProvider(email, AuthProvider.BASIC);
        SiteMemberAuth memberAuth = memberAuthDomainInfraMapper.toSiteMemberAuth(
                memberAuthRepository.findByEmailAndProvider(email, AuthProvider.BASIC).orElseThrow());

        memberValidationService.validateNotFoundUuid(memberAuth.getActiveMemberUuid());
        SiteMember member = memberDomainInfraMapper.toSiteMember(
                memberRepository.findByUuid(memberAuth.getActiveMemberUuid()).orElseThrow());

        memberRoleValidationService.validateNotFoundUuid(memberAuth.getActiveMemberUuid());
        SiteMemberRole memberRole = memberRoleDomainInfraMapper.toSiteMemberRole(
                memberRoleRepository.findByUuid(memberAuth.getActiveMemberUuid()).orElseThrow());

        return DefaultUserDetails.builder()
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