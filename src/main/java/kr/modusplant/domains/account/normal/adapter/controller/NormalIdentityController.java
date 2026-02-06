package kr.modusplant.domains.account.normal.adapter.controller;

import kr.modusplant.domains.account.normal.domain.exception.DataAlreadyExistsException;
import kr.modusplant.domains.account.normal.usecase.port.mapper.NormalIdentityMapper;
import kr.modusplant.domains.account.normal.usecase.port.repository.NormalIdentityCreateRepository;
import kr.modusplant.domains.account.normal.usecase.port.repository.NormalIdentityReadRepository;
import kr.modusplant.domains.account.normal.usecase.port.repository.NormalIdentityUpdateRepository;
import kr.modusplant.domains.account.normal.usecase.request.EmailModificationRequest;
import kr.modusplant.domains.account.normal.usecase.request.NormalSignUpRequest;
import kr.modusplant.domains.account.normal.usecase.request.PasswordModificationRequest;
import kr.modusplant.domains.account.shared.kernel.AccountId;
import kr.modusplant.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.shared.exception.InvalidDataException;
import kr.modusplant.shared.exception.enums.ErrorCode;
import kr.modusplant.shared.kernel.Email;
import kr.modusplant.shared.kernel.Nickname;
import kr.modusplant.shared.kernel.Password;
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

    public void registerNormalMember(NormalSignUpRequest request) {
        if(readRepository.existsByEmail(Email.create(request.email()))) {
            throw new DataAlreadyExistsException(ErrorCode.MEMBER_EXISTS);
        } else if(readRepository.existsByNickname(Nickname.create(request.nickname()))) {
            throw new DataAlreadyExistsException(ErrorCode.NICKNAME_EXISTS);
        }  else {
            createRepository.save(mapper.toSignUpData(request));
        }
    }

    public void modifyEmail(UUID memberActiveUuid, EmailModificationRequest request) {
        if(!readRepository.existsByEmail(Email.create(request.currentEmail()))) {
            throw new NotFoundEntityException(ErrorCode.MEMBER_NOT_FOUND, TableName.SITE_MEMBER_AUTH);
        } else {
            updateRepository.updateEmail(AccountId.create(memberActiveUuid), Email.create(request.newEmail()));
        }
    }

    public void modifyPassword(UUID memberActiveUuid, PasswordModificationRequest request) {
        if(!readRepository.existsByMemberId(AccountId.create(memberActiveUuid))) {
            throw new NotFoundEntityException(ErrorCode.MEMBER_NOT_FOUND, TableName.SITE_MEMBER_AUTH);
        } else if(!isPasswordsMatch(AccountId.create(memberActiveUuid), Password.create(request.currentPw()))) {
            throw new InvalidDataException(ErrorCode.INVALID_PASSWORD, request.currentPw());
        } else {
            updateRepository.updatePassword(AccountId.create(memberActiveUuid), Password.create(request.newPw()));
        }
    }

    private boolean isPasswordsMatch(AccountId memberActiveUuid, Password currentPassword) {
        Password storedPw = Password.create(readRepository.getMemberPassword(memberActiveUuid));

        return encoder.matches(currentPassword.getValue(), storedPw.getValue());
    }
}
