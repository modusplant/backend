package kr.modusplant.domains.member.domain.service;

import kr.modusplant.domains.member.error.MemberExistsException;
import kr.modusplant.domains.member.error.MemberNotFoundException;
import kr.modusplant.domains.member.persistence.repository.SiteMemberTermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Primary
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SiteMemberTermValidationService {
    private final SiteMemberTermRepository memberTermRepository;

    public void validateExistedUuid(UUID uuid) {
        if (memberTermRepository.existsByUuid(uuid)) {
            throw MemberExistsException.ofMemberTerm();
        }
    }

    public void validateNotFoundUuid(UUID uuid) {
        if (uuid == null || !memberTermRepository.existsByUuid(uuid)) {
            throw MemberNotFoundException.ofMemberTerm();
        }
    }
}
