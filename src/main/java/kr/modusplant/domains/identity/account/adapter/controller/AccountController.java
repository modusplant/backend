package kr.modusplant.domains.identity.account.adapter.controller;

import kr.modusplant.domains.identity.account.domain.vo.AccountId;
import kr.modusplant.domains.identity.account.usecase.port.repository.AccountRepository;
import kr.modusplant.domains.identity.account.usecase.response.AccountAuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountController {

    private final AccountRepository repository;

    public AccountAuthResponse getAuthInfo(UUID memberActiveUuid) {
        AccountId accountId = AccountId.create(memberActiveUuid);
        return repository.getAuthInfo(accountId);
    }
}
