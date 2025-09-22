package kr.modusplant.domains.identity.adapter.controller;

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
        repository.save(mapper.toSignUpData(request));
    }
}
