package kr.modusplant.domains.account.normal.framework.outbound.persistence.jpa.repository;

import kr.modusplant.domains.account.identity.framework.outbound.jpa.entity.MemberAuthEntity;
import kr.modusplant.domains.account.identity.framework.outbound.jpa.repository.MemberAuthJpaRepository;
import kr.modusplant.domains.account.normal.domain.vo.SignUpData;
import kr.modusplant.domains.account.normal.framework.outbound.persistence.jpa.mapper.NormalIdentityAuthJpaMapper;
import kr.modusplant.domains.account.normal.framework.outbound.persistence.jpa.mapper.NormalIdentityJpaMapper;
import kr.modusplant.domains.account.normal.framework.outbound.persistence.jpa.mapper.NormalIdentityProfileJpaMapper;
import kr.modusplant.domains.account.normal.framework.outbound.persistence.jpa.mapper.NormalIdentityTermJpaMapper;
import kr.modusplant.domains.account.normal.usecase.port.repository.NormalIdentityCreateRepository;
import kr.modusplant.domains.account.normal.usecase.port.repository.NormalIdentityUpdateRepository;
import kr.modusplant.domains.account.shared.kernel.AccountId;
import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.MemberJpaRepository;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.MemberProfileJpaRepository;
import kr.modusplant.domains.term.framework.outbound.jpa.repository.MemberTermJpaRepository;
import kr.modusplant.shared.enums.AuthProvider;
import kr.modusplant.shared.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.shared.framework.jpa.exception.enums.EntityErrorCode;
import kr.modusplant.shared.kernel.Email;
import kr.modusplant.shared.kernel.Password;
import kr.modusplant.shared.persistence.constant.TableName;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

@Repository
public class NormalIdentityRepositoryJpaAdapter implements NormalIdentityCreateRepository, NormalIdentityUpdateRepository {

    private final MemberJpaRepository memberJpaRepository;
    private final MemberAuthJpaRepository authJpaRepository;
    private final MemberTermJpaRepository termJpaRepository;
    private final MemberProfileJpaRepository profileJpaRepository;

    private final NormalIdentityJpaMapper identityMapper;
    private final NormalIdentityAuthJpaMapper authMapper;
    private final NormalIdentityTermJpaMapper termMapper;
    private final NormalIdentityProfileJpaMapper profileMapper;

    private final PasswordEncoder passwordEncoder;

    public NormalIdentityRepositoryJpaAdapter(MemberJpaRepository memberJpaRepository, MemberAuthJpaRepository authJpaRepository,
                                              MemberTermJpaRepository termJpaRepository, MemberProfileJpaRepository profileJpaRepository,
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
        MemberEntity savedMember = memberJpaRepository.save(identityMapper.toMemberEntity(signUpData.getNickname()));
        authJpaRepository.save(authMapper.toSiteMemberAuthEntity(savedMember, signUpData));
        termJpaRepository.save(termMapper.toSiteMemberTermEntity(savedMember, signUpData));
        profileJpaRepository.save(profileMapper.toSiteMemberProfileEntity(savedMember));
    }

    @Override
    public void updateEmail(AccountId accountId, Email email) {
        MemberEntity savedMember = memberJpaRepository.findByUuid(accountId.getValue())
                .orElseThrow(() -> new NotFoundEntityException(MemberErrorCode.NOT_FOUND_MEMBER,TableName.SITE_MEMBER));

        MemberAuthEntity savedAuth = authJpaRepository.findByMember(savedMember)
                .orElseThrow(() -> new NotFoundEntityException(EntityErrorCode.NOT_FOUND_MEMBER_AUTH,TableName.SITE_MEMBER_AUTH));

        savedAuth.updateEmail(email.getValue());
        authJpaRepository.save(savedAuth);
    }

    @Override
    public void updatePassword(AccountId accountId, Password pw) {
        MemberEntity savedMember = memberJpaRepository.findByUuid(accountId.getValue())
                .orElseThrow(() -> new NotFoundEntityException(MemberErrorCode.NOT_FOUND_MEMBER,TableName.SITE_MEMBER));

        MemberAuthEntity savedAuth = authJpaRepository.findByMember(savedMember)
                .orElseThrow(() -> new NotFoundEntityException(EntityErrorCode.NOT_FOUND_MEMBER_AUTH,TableName.SITE_MEMBER_AUTH));

        savedAuth.updatePassword(passwordEncoder.encode(pw.getValue()));
        authJpaRepository.save(savedAuth);
    }

    @Override
    public void updateToGoogleAccount(Email email, Password pw) {
        MemberAuthEntity savedAuth = authJpaRepository.findByEmail(email.getValue())
                .orElseThrow(() -> new NotFoundEntityException(EntityErrorCode.NOT_FOUND_MEMBER_AUTH,TableName.SITE_MEMBER_AUTH));

        savedAuth.updatePassword(passwordEncoder.encode(pw.getValue()));
        savedAuth.updateAuthProvider(AuthProvider.BASIC_GOOGLE);
        authJpaRepository.save(savedAuth);
    }

    @Override
    public void updateToKakaoAccount(Email email, Password pw) {
        MemberAuthEntity savedAuth = authJpaRepository.findByEmail(email.getValue())
                .orElseThrow(() -> new NotFoundEntityException(EntityErrorCode.NOT_FOUND_MEMBER_AUTH,TableName.SITE_MEMBER_AUTH));

        savedAuth.updatePassword(passwordEncoder.encode(pw.getValue()));
        savedAuth.updateAuthProvider(AuthProvider.BASIC_KAKAO);
        authJpaRepository.save(savedAuth);
    }
}
