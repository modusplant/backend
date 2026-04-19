package kr.modusplant.domains.account.normal.framework.out.persistence.jpa.repository;

import kr.modusplant.domains.account.normal.domain.vo.SignUpData;
import kr.modusplant.domains.account.normal.framework.out.persistence.jpa.mapper.NormalIdentityAuthJpaMapper;
import kr.modusplant.domains.account.normal.framework.out.persistence.jpa.mapper.NormalIdentityJpaMapper;
import kr.modusplant.domains.account.normal.framework.out.persistence.jpa.mapper.NormalIdentityProfileJpaMapper;
import kr.modusplant.domains.account.normal.framework.out.persistence.jpa.mapper.NormalIdentityTermJpaMapper;
import kr.modusplant.domains.account.normal.usecase.port.repository.NormalIdentityCreateRepository;
import kr.modusplant.domains.account.normal.usecase.port.repository.NormalIdentityUpdateRepository;
import kr.modusplant.domains.account.shared.kernel.AccountId;
import kr.modusplant.framework.jpa.entity.SiteMemberAuthEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.framework.jpa.exception.enums.EntityErrorCode;
import kr.modusplant.framework.jpa.repository.SiteMemberAuthJpaRepository;
import kr.modusplant.framework.jpa.repository.SiteMemberJpaRepository;
import kr.modusplant.framework.jpa.repository.SiteMemberProfileJpaRepository;
import kr.modusplant.framework.jpa.repository.SiteMemberTermJpaRepository;
import kr.modusplant.shared.enums.AuthProvider;
import kr.modusplant.shared.kernel.Email;
import kr.modusplant.shared.kernel.Password;
import kr.modusplant.shared.persistence.constant.TableName;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

@Repository
public class NormalIdentityRepositoryJpaAdapter implements NormalIdentityCreateRepository, NormalIdentityUpdateRepository {

    private final SiteMemberJpaRepository memberJpaRepository;
    private final SiteMemberAuthJpaRepository authJpaRepository;
    private final SiteMemberTermJpaRepository termJpaRepository;
    private final SiteMemberProfileJpaRepository profileJpaRepository;

    private final NormalIdentityJpaMapper identityMapper;
    private final NormalIdentityAuthJpaMapper authMapper;
    private final NormalIdentityTermJpaMapper termMapper;
    private final NormalIdentityProfileJpaMapper profileMapper;

    private final PasswordEncoder passwordEncoder;

    public NormalIdentityRepositoryJpaAdapter(SiteMemberJpaRepository memberJpaRepository, SiteMemberAuthJpaRepository authJpaRepository,
                                              SiteMemberTermJpaRepository termJpaRepository, SiteMemberProfileJpaRepository profileJpaRepository,
                                              NormalIdentityJpaMapper identityMapper, NormalIdentityAuthJpaMapper authMapper,
                                              NormalIdentityTermJpaMapper termMapper, NormalIdentityProfileJpaMapper profileMapper,
                                              @Qualifier("bcryptPasswordEncoder") PasswordEncoder passwordEncoder) {
        this.memberJpaRepository = memberJpaRepository;
        this.authJpaRepository = authJpaRepository;
        this.termJpaRepository = termJpaRepository;
        this.profileJpaRepository = profileJpaRepository;
        this.identityMapper = identityMapper;
        this.authMapper = authMapper;
        this.termMapper = termMapper;
        this.profileMapper = profileMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void save(SignUpData signUpData) {
        SiteMemberEntity savedMember = memberJpaRepository.save(identityMapper.toSiteMemberEntity(signUpData.getNickname()));
        authJpaRepository.save(authMapper.toSiteMemberAuthEntity(savedMember, signUpData));
        termJpaRepository.save(termMapper.toSiteMemberTermEntity(savedMember, signUpData));
        profileJpaRepository.save(profileMapper.toSiteMemberProfileEntity(savedMember));
    }

    @Override
    public void updateEmail(AccountId accountId, Email email) {
        SiteMemberEntity savedMember = memberJpaRepository.findByUuid(accountId.getValue())
                .orElseThrow(() -> new NotFoundEntityException(EntityErrorCode.NOT_FOUND_MEMBER,TableName.SITE_MEMBER));

        SiteMemberAuthEntity savedAuth = authJpaRepository.findByMember(savedMember)
                .orElseThrow(() -> new NotFoundEntityException(EntityErrorCode.NOT_FOUND_MEMBER_AUTH,TableName.SITE_MEMBER_AUTH));

        savedAuth.updateEmail(email.getValue());
        authJpaRepository.save(savedAuth);
    }

    @Override
    public void updatePassword(AccountId accountId, Password pw) {
        SiteMemberEntity savedMember = memberJpaRepository.findByUuid(accountId.getValue())
                .orElseThrow(() -> new NotFoundEntityException(EntityErrorCode.NOT_FOUND_MEMBER,TableName.SITE_MEMBER));

        SiteMemberAuthEntity savedAuth = authJpaRepository.findByMember(savedMember)
                .orElseThrow(() -> new NotFoundEntityException(EntityErrorCode.NOT_FOUND_MEMBER_AUTH,TableName.SITE_MEMBER_AUTH));

        savedAuth.updatePassword(passwordEncoder.encode(pw.getValue()));
        authJpaRepository.save(savedAuth);
    }

    @Override
    public void updateToGoogleAccount(Email email, Password pw) {
        SiteMemberAuthEntity savedAuth = authJpaRepository.findByEmail(email.getValue())
                .orElseThrow(() -> new NotFoundEntityException(EntityErrorCode.NOT_FOUND_MEMBER_AUTH,TableName.SITE_MEMBER_AUTH));

        savedAuth.updatePassword(passwordEncoder.encode(pw.getValue()));
        savedAuth.updateAuthProvider(AuthProvider.BASIC_GOOGLE);
        authJpaRepository.save(savedAuth);
    }

    @Override
    public void updateToKakaoAccount(Email email, Password pw) {
        SiteMemberAuthEntity savedAuth = authJpaRepository.findByEmail(email.getValue())
                .orElseThrow(() -> new NotFoundEntityException(EntityErrorCode.NOT_FOUND_MEMBER_AUTH,TableName.SITE_MEMBER_AUTH));

        savedAuth.updatePassword(passwordEncoder.encode(pw.getValue()));
        savedAuth.updateAuthProvider(AuthProvider.BASIC_KAKAO);
        authJpaRepository.save(savedAuth);
    }
}
