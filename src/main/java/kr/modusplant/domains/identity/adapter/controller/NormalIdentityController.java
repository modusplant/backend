package kr.modusplant.domains.identity.adapter.controller;

import kr.modusplant.domains.identity.adapter.mapper.NormalIdentityMapperImpl;
import kr.modusplant.domains.identity.usecase.port.repository.NormalIdentityRepository;
import kr.modusplant.domains.identity.usecase.request.NormalSignUpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NormalIdentityController {

    private final NormalIdentityMapperImpl mapper;
    private final NormalIdentityRepository repository;

    public void registerNormalMember(NormalSignUpRequest request) {
        repository.save(mapper.toSignUpData(request));
    }
}
