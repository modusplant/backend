package kr.modusplant.domains.member.domain.service;

import kr.modusplant.domains.member.error.SiteMemberExistsException;
import kr.modusplant.domains.member.error.SiteMemberNotFoundException;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Primary
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SiteMemberValidationService {
    private final SiteMemberRepository memberRepository;

    public void validateExistedUuid(UUID uuid) {
        if (uuid == null) {
            return;
        }
        if (memberRepository.existsByUuid(uuid)) {
            throw new SiteMemberExistsException();
        }
    }

    public void validateNotFoundUuid(UUID uuid) {
        if (uuid == null || !memberRepository.existsByUuid(uuid)) {
            throw new SiteMemberNotFoundException();
        }
    }
}
