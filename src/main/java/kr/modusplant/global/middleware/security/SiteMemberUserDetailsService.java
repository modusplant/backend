package kr.modusplant.global.middleware.security;

import jakarta.transaction.Transactional;
import kr.modusplant.domains.member.domain.model.SiteMember;
import kr.modusplant.domains.member.domain.model.SiteMemberAuth;
import kr.modusplant.domains.member.domain.model.SiteMemberRole;
import kr.modusplant.domains.member.domain.service.SiteMemberAuthValidationService;
import kr.modusplant.domains.member.domain.service.SiteMemberRoleValidationService;
import kr.modusplant.domains.member.domain.service.SiteMemberValidationService;
import kr.modusplant.domains.member.enums.AuthProvider;
import kr.modusplant.domains.member.mapper.SiteMemberAuthDomainInfraMapper;
import kr.modusplant.domains.member.mapper.SiteMemberDomainInfraMapper;
import kr.modusplant.domains.member.mapper.SiteMemberRoleDomainInfraMapper;
import kr.modusplant.domains.member.persistence.repository.SiteMemberAuthRepository;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRoleRepository;
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
    public SiteMemberUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        memberAuthValidationService.validateNotFoundEmailAndAuthProvider(email, AuthProvider.BASIC);
        SiteMemberAuth memberAuth = memberAuthDomainInfraMapper.toSiteMemberAuth(
                memberAuthRepository.findByEmailAndProvider(email, AuthProvider.BASIC).orElseThrow());

        memberValidationService.validateNotFoundUuid(memberAuth.getActiveMemberUuid());
        SiteMember member = memberDomainInfraMapper.toSiteMember(
                memberRepository.findByUuid(memberAuth.getActiveMemberUuid()).orElseThrow());

        memberRoleValidationService.validateNotFoundUuid(memberAuth.getActiveMemberUuid());
        SiteMemberRole memberRole = memberRoleDomainInfraMapper.toSiteMemberRole(
                memberRoleRepository.findByUuid(memberAuth.getActiveMemberUuid()).orElseThrow());

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