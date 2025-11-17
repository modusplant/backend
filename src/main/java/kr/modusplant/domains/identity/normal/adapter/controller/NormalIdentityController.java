package kr.modusplant.domains.identity.normal.adapter.controller;

import kr.modusplant.domains.identity.normal.domain.exception.enums.NormalIdentityErrorCode;
import kr.modusplant.domains.identity.normal.domain.vo.Nickname;
import kr.modusplant.domains.identity.normal.domain.exception.DataAlreadyExistsException;
import kr.modusplant.domains.identity.normal.usecase.port.mapper.NormalIdentityMapper;
import kr.modusplant.domains.identity.normal.usecase.port.repository.NormalIdentityRepository;
import kr.modusplant.domains.identity.normal.usecase.request.NormalSignUpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NormalIdentityController {

    private final NormalIdentityMapper mapper;
    private final NormalIdentityRepository repository;

    public void registerNormalMember(NormalSignUpRequest request) {
        if(repository.existsByEmailAndProvider(request.email(), "Basic")) {
            throw new DataAlreadyExistsException(NormalIdentityErrorCode.ALREADY_EXISTS_MEMBER);
        } else if(repository.isNicknameExists(Nickname.create(request.nickname()))) {
            throw new DataAlreadyExistsException(NormalIdentityErrorCode.ALREADY_EXISTS_NICKNAME);
        }  else {
            repository.save(mapper.toSignUpData(request));
        }
    }
}
