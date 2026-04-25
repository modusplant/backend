package kr.modusplant.domains.account.normal.adapter.controller;

import jakarta.transaction.Transactional;
import kr.modusplant.domains.account.normal.domain.exception.DataAlreadyExistsException;
import kr.modusplant.domains.account.normal.domain.exception.enums.NormalIdentityErrorCode;
import kr.modusplant.domains.account.normal.usecase.port.mapper.NormalIdentityMapper;
import kr.modusplant.domains.account.normal.usecase.port.repository.NormalIdentityCreateRepository;
import kr.modusplant.domains.account.normal.usecase.port.repository.NormalIdentityReadRepository;
import kr.modusplant.domains.account.normal.usecase.port.repository.NormalIdentityUpdateRepository;
import kr.modusplant.domains.account.normal.usecase.request.EmailModificationRequest;
import kr.modusplant.domains.account.normal.usecase.request.NormalSignUpRequest;
import kr.modusplant.domains.account.normal.usecase.request.PasswordModificationRequest;
import kr.modusplant.domains.account.shared.kernel.AccountId;
import kr.modusplant.framework.jpa.exception.ExistsEntityException;
import kr.modusplant.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.framework.jpa.exception.enums.EntityErrorCode;
import kr.modusplant.shared.enums.AuthProvider;
import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.kernel.Email;
import kr.modusplant.shared.kernel.Nickname;
import kr.modusplant.shared.kernel.Password;
import kr.modusplant.shared.kernel.enums.KernelErrorCode;
import kr.modusplant.shared.persistence.constant.TableName;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class NormalIdentityController {

    private final NormalIdentityMapper mapper;
    private final NormalIdentityCreateRepository createRepository;
    private final NormalIdentityUpdateRepository updateRepository;
    private final NormalIdentityReadRepository readRepository;

    private final PasswordEncoder encoder;

    public NormalIdentityController(NormalIdentityMapper mapper,
                                    NormalIdentityCreateRepository createRepository,
                                    NormalIdentityUpdateRepository updateRepository,
                                    NormalIdentityReadRepository readRepository,
                                    @Qualifier("bcryptPasswordEncoder") PasswordEncoder encoder) {
        this.mapper = mapper;
        this.createRepository = createRepository;
        this.updateRepository = updateRepository;
        this.readRepository = readRepository;
        this.encoder = encoder;
    }

    @Transactional
    public void registerNormalMember(NormalSignUpRequest request) {
        Email requestEmail = Email.create(request.email());

        if(readRepository.existsByEmail(requestEmail)) {
            AuthProvider providerOfExistingMember = readRepository.getAuthProvider(requestEmail);

            if (providerOfExistingMember == AuthProvider.BASIC || providerOfExistingMember == AuthProvider.BASIC_GOOGLE || providerOfExistingMember == AuthProvider.BASIC_KAKAO) {
                throw new ExistsEntityException(NormalIdentityErrorCode.EXISTS_ACCOUNT, TableName.SITE_MEMBER);
            } else if (providerOfExistingMember == AuthProvider.GOOGLE) {
                updateRepository.updateToGoogleAccount(requestEmail, Password.create(request.password()));
            } else if (providerOfExistingMember == AuthProvider.KAKAO) {
                updateRepository.updateToKakaoAccount(requestEmail, Password.create(request.password()));
            }
        } else {
            if(readRepository.existsByNickname(Nickname.create(request.nickname()))) {
                throw new DataAlreadyExistsException(KernelErrorCode.EXISTS_NICKNAME);
            }
            createRepository.save(mapper.toSignUpData(request));
        }
    }

    @Transactional
    public void modifyEmail(UUID memberUuid, EmailModificationRequest request) {
        if(!readRepository.existsByEmail(Email.create(request.currentEmail()))) {
            throw new NotFoundEntityException(EntityErrorCode.NOT_FOUND_MEMBER, TableName.SITE_MEMBER_AUTH);
        } else {
            updateRepository.updateEmail(AccountId.create(memberUuid), Email.create(request.newEmail()));
        }
    }

    @Transactional
    public void modifyPassword(UUID memberUuid, PasswordModificationRequest request) {
        if(!readRepository.existsByMemberId(AccountId.create(memberUuid))) {
            throw new NotFoundEntityException(EntityErrorCode.NOT_FOUND_MEMBER, TableName.SITE_MEMBER_AUTH);
        } else if(!isPasswordsMatch(AccountId.create(memberUuid), Password.create(request.currentPw()))) {
            throw new InvalidValueException(KernelErrorCode.INVALID_PASSWORD_FORMAT, request.currentPw());
        } else {
            updateRepository.updatePassword(AccountId.create(memberUuid), Password.create(request.newPw()));
        }
    }

    private boolean isPasswordsMatch(AccountId memberUuid, Password currentPassword) {
        Password storedPw = Password.create(readRepository.getMemberPassword(memberUuid));

        return encoder.matches(currentPassword.getValue(), storedPw.getValue());
    }
}
