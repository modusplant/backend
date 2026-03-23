package kr.modusplant.domains.account.normal.framework.out.persistence.jpa.repository;

import kr.modusplant.domains.account.normal.domain.vo.SignUpData;
import kr.modusplant.domains.account.normal.framework.out.persistence.jpa.mapper.NormalIdentityAuthJpaMapper;
import kr.modusplant.domains.account.normal.framework.out.persistence.jpa.mapper.NormalIdentityJpaMapper;
import kr.modusplant.domains.account.normal.framework.out.persistence.jpa.mapper.NormalIdentityProfileJpaMapper;
import kr.modusplant.domains.account.normal.framework.out.persistence.jpa.mapper.NormalIdentityTermJpaMapper;
import kr.modusplant.domains.account.normal.framework.out.persistence.jpa.repository.supers.NormalIdentityAuthJpaRepository;
import kr.modusplant.domains.account.normal.framework.out.persistence.jpa.repository.supers.NormalIdentityJpaRepository;
import kr.modusplant.domains.account.normal.framework.out.persistence.jpa.repository.supers.NormalIdentityProfileJpaRepository;
import kr.modusplant.domains.account.normal.framework.out.persistence.jpa.repository.supers.NormalIdentityTermJpaRepository;
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
    private final NormalIdentityTermJpaRepository termRepository;
    private final NormalIdentityProfileJpaRepository profileRepository;

    private final NormalIdentityJpaMapper identityMapper;
    private final NormalIdentityAuthJpaMapper authMapper;
    private final NormalIdentityTermJpaMapper termMapper;
    private final NormalIdentityProfileJpaMapper profileMapper;

    @Override
    @Transactional
    public void save(SignUpData signUpData) {
        SiteMemberEntity savedMember = identityRepository.save(identityMapper.toSiteMemberEntity(signUpData.getNickname()));
        authRepository.save(authMapper.toSiteMemberAuthEntity(savedMember, signUpData));
        termRepository.save(termMapper.toSiteMemberTermEntity(savedMember, signUpData));
        profileRepository.save(profileMapper.toSiteMemberProfileEntity(savedMember));
    }

}
