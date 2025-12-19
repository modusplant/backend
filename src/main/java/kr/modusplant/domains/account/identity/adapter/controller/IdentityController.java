package kr.modusplant.domains.account.identity.adapter.controller;

import kr.modusplant.domains.account.identity.usecase.port.repository.IdentityRepository;
import kr.modusplant.domains.account.identity.usecase.response.IdentityAuthResponse;
import kr.modusplant.domains.account.shared.kernel.AccountId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IdentityController {

    private final IdentityRepository repository;

    public IdentityAuthResponse getAuthInfo(UUID memberActiveUuid) {
        AccountId accountId = AccountId.create(memberActiveUuid);
        return repository.getAuthInfo(accountId);
    }
}
