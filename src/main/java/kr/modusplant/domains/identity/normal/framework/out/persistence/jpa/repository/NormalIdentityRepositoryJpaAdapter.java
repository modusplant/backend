package kr.modusplant.domains.identity.normal.framework.out.persistence.jpa.repository;

import kr.modusplant.domains.identity.normal.domain.vo.SignUpData;
import kr.modusplant.domains.identity.normal.framework.out.persistence.jpa.mapper.IdentityAuthJpaMapper;
import kr.modusplant.domains.identity.normal.framework.out.persistence.jpa.mapper.IdentityJpaMapper;
import kr.modusplant.domains.identity.normal.framework.out.persistence.jpa.mapper.IdentityRoleJpaMapper;
import kr.modusplant.domains.identity.normal.framework.out.persistence.jpa.mapper.IdentityTermJpaMapper;
import kr.modusplant.domains.identity.normal.framework.out.persistence.jpa.repository.supers.IdentityAuthJpaRepository;
import kr.modusplant.domains.identity.normal.framework.out.persistence.jpa.repository.supers.IdentityJpaRepository;
import kr.modusplant.domains.identity.normal.framework.out.persistence.jpa.repository.supers.IdentityRoleJpaRepository;
import kr.modusplant.domains.identity.normal.framework.out.persistence.jpa.repository.supers.IdentityTermJpaRepository;
import kr.modusplant.domains.identity.normal.usecase.port.repository.NormalIdentityRepository;
import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import kr.modusplant.legacy.domains.member.enums.AuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class NormalIdentityRepositoryJpaAdapter implements NormalIdentityRepository {
    private final IdentityJpaRepository identityRepository;
    private final IdentityAuthJpaRepository authRepository;
    private final IdentityRoleJpaRepository roleRepository;
    private final IdentityTermJpaRepository termRepository;

    private final IdentityJpaMapper identityMapper;
    private final IdentityAuthJpaMapper authMapper;
    private final IdentityRoleJpaMapper roleMapper;
    private final IdentityTermJpaMapper termMapper;

    @Override
    @Transactional
    public void save(SignUpData signUpData) {
        SiteMemberEntity savedMember = identityRepository.save(identityMapper.toSiteMemberEntity(signUpData));
        authRepository.save(authMapper.toSiteMemberAuthEntity(savedMember, signUpData));
        roleRepository.save(roleMapper.toSiteMemberRoleEntity(savedMember));
        termRepository.save(termMapper.toSiteMemberTermEntity(savedMember, signUpData));

    }

    @Override
    public boolean existsByEmailAndProvider(String email, String provider) {
        return authRepository.existsByEmailAndProvider(email, AuthProvider.valueOf(provider.toUpperCase()));
    }
}
