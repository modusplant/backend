package kr.modusplant.domains.account.normal.framework.out.persistence.jpa.repository;

import kr.modusplant.domains.account.normal.domain.vo.SignUpData;
import kr.modusplant.domains.account.normal.framework.out.persistence.jpa.mapper.*;
import kr.modusplant.domains.account.normal.framework.out.persistence.jpa.repository.supers.*;
import kr.modusplant.domains.account.normal.usecase.port.repository.NormalIdentityCreateRepository;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class NormalIdentityRepositoryJpaAdapter implements NormalIdentityCreateRepository{
    private final NormalIdentityJpaRepository identityRepository;
    private final NormalIdentityAuthJpaRepository authRepository;
    private final NormalIdentityRoleJpaRepository roleRepository;
    private final NormalIdentityTermJpaRepository termRepository;
    private final NormalIdentityProfileJpaRepository profileRepository;

    private final NormalIdentityJpaMapper identityMapper;
    private final NormalIdentityAuthJpaMapper authMapper;
    private final NormalIdentityRoleJpaMapper roleMapper;
    private final NormalIdentityTermJpaMapper termMapper;
    private final NormalIdentityProfileJpaMapper profileMapper;

    @Override
    @Transactional
    public void save(SignUpData signUpData) {
        SiteMemberEntity savedMember = identityRepository.save(identityMapper.toSiteMemberEntity(signUpData.getNickname()));
        authRepository.save(authMapper.toSiteMemberAuthEntity(savedMember, signUpData));
        roleRepository.save(roleMapper.toSiteMemberRoleEntity(savedMember));
        termRepository.save(termMapper.toSiteMemberTermEntity(savedMember, signUpData));
        profileRepository.save(profileMapper.toSiteMemberProfileEntity(savedMember));
    }

}
