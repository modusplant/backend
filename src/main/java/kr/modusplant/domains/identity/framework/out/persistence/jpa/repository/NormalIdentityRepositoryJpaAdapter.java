package kr.modusplant.domains.identity.framework.out.persistence.jpa.repository;

import kr.modusplant.domains.identity.usecase.port.repository.NormalIdentityRepository;
import kr.modusplant.domains.identity.domain.vo.SignUpData;
import kr.modusplant.domains.identity.domain.vo.enums.UserRole;
import kr.modusplant.domains.identity.framework.out.persistence.jpa.entity.MemberAuthEntity;
import kr.modusplant.domains.identity.framework.out.persistence.jpa.entity.MemberRoleEntity;
import kr.modusplant.domains.identity.framework.out.persistence.jpa.entity.MemberTermEntity;
import kr.modusplant.domains.identity.framework.out.persistence.jpa.repository.supers.MemberAuthJpaRepository;
import kr.modusplant.domains.identity.framework.out.persistence.jpa.repository.supers.MemberIdentityJpaRepository;
import kr.modusplant.domains.identity.framework.out.persistence.jpa.repository.supers.MemberRoleJpaRepository;
import kr.modusplant.domains.identity.framework.out.persistence.jpa.repository.supers.MemberTermJpaRepository;
import kr.modusplant.legacy.domains.member.enums.AuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class NormalIdentityRepositoryJpaAdapter implements NormalIdentityRepository {

    private final MemberIdentityJpaRepository identityJpaRepository;
    private final MemberAuthJpaRepository authJpaRepository;
    private final MemberRoleJpaRepository roleJpaRepository;
    private final MemberTermJpaRepository termJpaRepository;

    @Override
    public void save(SignUpData signUpData) {
        UUID uuidOfSavedMember = identityJpaRepository.saveIdentity(signUpData).getUuid();
        authJpaRepository.saveAuth(MemberAuthEntity.builder()
                .originalMemberUuid(uuidOfSavedMember)
                .activeMemberUuid(uuidOfSavedMember)
                .email(signUpData.getCredentials().getEmail())
                .password(signUpData.getCredentials().getPassword())
                .provider(AuthProvider.BASIC)
                .build());
        roleJpaRepository.saveRole(MemberRoleEntity.builder()
                .uuid(uuidOfSavedMember)
                .role(UserRole.USER).build());
        termJpaRepository.saveTerm(MemberTermEntity.builder()
                .uuid(uuidOfSavedMember)
                .agreedPrivacyPolicyVersion(signUpData.getAgreedPrivacyPolicyVersion().getVersion())
                .agreedAdInfoReceivingVersion(signUpData.getAgreedAdInfoReceivingVersion().getVersion())
                .agreedTermsOfUseVersion(signUpData.getAgreedTermsOfUseVersion().getVersion())
                .build());
    }

    @Override
    public boolean existsByEmailAndProvider(String email, String provider) {
        return identityJpaRepository.existsByEmailAndProvider(email, provider);
    }
}
