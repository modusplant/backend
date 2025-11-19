package kr.modusplant.domains.identity.normal.adapter.controller;

import kr.modusplant.domains.identity.normal.domain.exception.DataAlreadyExistsException;
import kr.modusplant.domains.identity.normal.domain.exception.enums.NormalIdentityErrorCode;
import kr.modusplant.domains.identity.normal.domain.vo.Email;
import kr.modusplant.domains.identity.normal.domain.vo.MemberId;
import kr.modusplant.domains.identity.normal.domain.vo.Nickname;
import kr.modusplant.domains.identity.normal.usecase.port.mapper.NormalIdentityMapper;
import kr.modusplant.domains.identity.normal.usecase.port.repository.NormalIdentityRepository;
import kr.modusplant.domains.identity.normal.usecase.port.repository.NormalIdentityUpdateRepository;
import kr.modusplant.domains.identity.normal.usecase.request.EmailModificationRequest;
import kr.modusplant.domains.identity.normal.usecase.request.NormalSignUpRequest;
import kr.modusplant.infrastructure.persistence.constant.EntityName;
import kr.modusplant.shared.enums.AuthProvider;
import kr.modusplant.shared.exception.EntityNotFoundException;
import kr.modusplant.shared.exception.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class NormalIdentityController {

    private final NormalIdentityMapper mapper;
    private final NormalIdentityRepository repository;
    private final NormalIdentityUpdateRepository updateRepository;

    public void registerNormalMember(NormalSignUpRequest request) {
        if(repository.existsByEmailAndProvider(request.email(), "Basic")) {
            throw new DataAlreadyExistsException(NormalIdentityErrorCode.ALREADY_EXISTS_MEMBER);
        } else if(repository.isNicknameExists(Nickname.create(request.nickname()))) {
            throw new DataAlreadyExistsException(NormalIdentityErrorCode.ALREADY_EXISTS_NICKNAME);
        }  else {
            repository.save(mapper.toSignUpData(request));
        }
    }

    public void modifyEmail(UUID memberActiveUuid, EmailModificationRequest request) {
        if(!repository.existsByEmailAndProvider(request.currentEmail(), AuthProvider.BASIC.getValue())) {
            throw new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND, EntityName.SITE_MEMBER);
        } else {
            updateRepository.updateEmail(MemberId.create(memberActiveUuid), Email.create(request.newEmail()));
        }
    }
}
