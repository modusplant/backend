package kr.modusplant.domains.normalidentity.normal.framework.out.persistence.jpa.repository;

import kr.modusplant.domains.normalidentity.normal.domain.vo.Nickname;
import kr.modusplant.domains.normalidentity.normal.domain.vo.SignUpData;
import kr.modusplant.domains.normalidentity.normal.framework.out.persistence.jpa.mapper.NormalIdentityAuthJpaMapper;
import kr.modusplant.domains.normalidentity.normal.framework.out.persistence.jpa.mapper.NormalIdentityJpaMapper;
import kr.modusplant.domains.normalidentity.normal.framework.out.persistence.jpa.mapper.NormalIdentityRoleJpaMapper;
import kr.modusplant.domains.normalidentity.normal.framework.out.persistence.jpa.mapper.NormalIdentityTermJpaMapper;
import kr.modusplant.domains.normalidentity.normal.framework.out.persistence.jpa.repository.supers.NormalIdentityAuthJpaRepository;
import kr.modusplant.domains.normalidentity.normal.framework.out.persistence.jpa.repository.supers.NormalIdentityJpaRepository;
import kr.modusplant.domains.normalidentity.normal.framework.out.persistence.jpa.repository.supers.NormalIdentityRoleJpaRepository;
import kr.modusplant.domains.normalidentity.normal.framework.out.persistence.jpa.repository.supers.NormalIdentityTermJpaRepository;
import kr.modusplant.domains.normalidentity.normal.usecase.port.repository.NormalIdentityRepository;
import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import kr.modusplant.shared.enums.AuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class NormalIdentityRepositoryJpaAdapter implements NormalIdentityRepository {
    private final NormalIdentityJpaRepository identityRepository;
    private final NormalIdentityAuthJpaRepository authRepository;
    private final NormalIdentityRoleJpaRepository roleRepository;
    private final NormalIdentityTermJpaRepository termRepository;

    private final NormalIdentityJpaMapper identityMapper;
    private final NormalIdentityAuthJpaMapper authMapper;
    private final NormalIdentityRoleJpaMapper roleMapper;
    private final NormalIdentityTermJpaMapper termMapper;

    @Override
    @Transactional
    public void save(SignUpData signUpData) {
        SiteMemberEntity savedMember = identityRepository.save(identityMapper.toSiteMemberEntity(signUpData.getNickname()));
        authRepository.save(authMapper.toSiteMemberAuthEntity(savedMember, signUpData));
        roleRepository.save(roleMapper.toSiteMemberRoleEntity(savedMember));
        termRepository.save(termMapper.toSiteMemberTermEntity(savedMember, signUpData));
    }

    @Override
    public boolean existsByEmailAndProvider(String email, String provider) {
        return authRepository.existsByEmailAndProvider(email, AuthProvider.valueOf(provider.toUpperCase()));
    }

    @Override
    public boolean isNicknameExists(Nickname nickname) {
        return identityRepository.existsByNickname(nickname.getNickname());
    }
}
