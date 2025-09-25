package kr.modusplant.domains.identity.adapter.controller;

import kr.modusplant.domains.identity.domain.exception.enums.IdentityErrorCode;
import kr.modusplant.domains.identity.exception.DataAlreadyExistsException;
import kr.modusplant.domains.identity.usecase.port.mapper.NormalIdentityMapper;
import kr.modusplant.domains.identity.usecase.port.repository.NormalIdentityRepository;
import kr.modusplant.domains.identity.usecase.request.NormalSignUpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NormalIdentityController {

    private final NormalIdentityMapper mapper;
    private final NormalIdentityRepository repository;

    public void registerNormalMember(NormalSignUpRequest request) {
        if(repository.existsByEmailAndProvider(request.email(), "Basic")) {
            throw new DataAlreadyExistsException(IdentityErrorCode.MEMBER_ALREADY_EXISTS);
        } else {
            repository.save(mapper.toSignUpData(request));
        }
    }
}
