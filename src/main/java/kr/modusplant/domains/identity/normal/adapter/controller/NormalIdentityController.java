package kr.modusplant.domains.identity.normal.adapter.controller;

import kr.modusplant.domains.identity.normal.domain.exception.DataAlreadyExistsException;
import kr.modusplant.domains.identity.normal.domain.exception.enums.NormalIdentityErrorCode;
import kr.modusplant.domains.identity.normal.domain.vo.MemberId;
import kr.modusplant.domains.identity.normal.domain.vo.Nickname;
import kr.modusplant.domains.identity.normal.domain.vo.Password;
import kr.modusplant.domains.identity.normal.usecase.port.mapper.NormalIdentityMapper;
import kr.modusplant.domains.identity.normal.usecase.port.repository.NormalIdentityCreateRepository;
import kr.modusplant.domains.identity.normal.usecase.port.repository.NormalIdentityReadRepository;
import kr.modusplant.domains.identity.normal.usecase.port.repository.NormalIdentityUpdateRepository;
import kr.modusplant.domains.identity.normal.usecase.request.EmailModificationRequest;
import kr.modusplant.domains.identity.normal.usecase.request.NormalSignUpRequest;
import kr.modusplant.domains.identity.normal.usecase.request.PasswordModificationRequest;
import kr.modusplant.shared.exception.EntityNotFoundException;
import kr.modusplant.shared.exception.InvalidDataException;
import kr.modusplant.shared.exception.enums.ErrorCode;
import kr.modusplant.shared.kernel.Email;
import kr.modusplant.shared.persistence.constant.TableName;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class NormalIdentityController {

    private final NormalIdentityMapper mapper;
    private final NormalIdentityCreateRepository createRepository;
    private final NormalIdentityUpdateRepository updateRepository;
    private final NormalIdentityReadRepository readRepository;

    private final PasswordEncoder encoder;

    public void registerNormalMember(NormalSignUpRequest request) {
        if(readRepository.existsByEmail(Email.create(request.email()))) {
            throw new DataAlreadyExistsException(NormalIdentityErrorCode.ALREADY_EXISTS_MEMBER);
        } else if(readRepository.existsByNickname(Nickname.create(request.nickname()))) {
            throw new DataAlreadyExistsException(NormalIdentityErrorCode.ALREADY_EXISTS_NICKNAME);
        }  else {
            createRepository.save(mapper.toSignUpData(request));
        }
    }

    public void modifyEmail(UUID memberActiveUuid, EmailModificationRequest request) {
        if(!readRepository.existsByEmail(Email.create(request.currentEmail()))) {
            throw new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND, TableName.SITE_MEMBER_AUTH);
        } else {
            updateRepository.updateEmail(MemberId.create(memberActiveUuid), Email.create(request.newEmail()));
        }
    }

    public void modifyPassword(UUID memberActiveUuid, PasswordModificationRequest request) {
        if(!readRepository.existsByMemberId(MemberId.create(memberActiveUuid))) {
            throw new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND, TableName.SITE_MEMBER_AUTH);
        } else if(!isPasswordsMatch(MemberId.create(memberActiveUuid), Password.create(request.currentPw()))) {
            throw new InvalidDataException(ErrorCode.INVALID_PASSWORD, request.currentPw());
        } else {
            updateRepository.updatePassword(MemberId.create(memberActiveUuid), Password.create(request.newPw()));
        }
    }

    private boolean isPasswordsMatch(MemberId memberActiveUuid, Password currentPassword) {
        Password storedPw = Password.create(readRepository.getMemberPassword(memberActiveUuid));

        return encoder.matches(currentPassword.getPassword(), storedPw.getPassword());
    }
}
