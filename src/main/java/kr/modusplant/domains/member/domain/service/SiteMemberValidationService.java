package kr.modusplant.domains.member.domain.service;

import kr.modusplant.domains.member.error.MemberExistsException;
import kr.modusplant.domains.member.error.MemberNotFoundException;
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
            throw MemberExistsException.ofMember();
        }
    }

    public void validateNotFoundUuid(UUID uuid) {
        if (uuid == null || !memberRepository.existsByUuid(uuid)) {
            throw MemberNotFoundException.ofMember();
        }
    }
}
