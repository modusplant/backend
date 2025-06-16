package kr.modusplant.domains.member.domain.service;

import kr.modusplant.domains.member.enums.AuthProvider;
import kr.modusplant.domains.member.error.SiteMemberAuthExistsException;
import kr.modusplant.domains.member.error.SiteMemberAuthNotFoundException;
import kr.modusplant.domains.member.persistence.repository.SiteMemberAuthRepository;
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
public class SiteMemberAuthValidationService {
    private final SiteMemberRepository memberRepository;
    private final SiteMemberAuthRepository memberAuthRepository;

    public void validateExistedOriginalMemberUuid(UUID uuid) {
        if (uuid == null) {
            return;
        }
        if (memberAuthRepository.existsByOriginalMember(memberRepository.findByUuid(uuid).orElseThrow())) {
            throw new SiteMemberAuthExistsException();
        }
    }

    public void validateExistedEmailAndAuthProvider(String email, AuthProvider authProvider) {
        if (email == null || authProvider == null) {
            return;
        }
        if (memberAuthRepository.findByEmailAndProvider(email, authProvider).isPresent()) {
            throw new SiteMemberAuthExistsException();
        }
    }

    public void validateNotFoundOriginalMemberUuid(UUID uuid) {
        if (uuid == null || !memberAuthRepository.existsByOriginalMember(memberRepository.findByUuid(uuid).orElseThrow())) {
            throw new SiteMemberAuthNotFoundException();
        }
    }

    public void validateNotFoundEmail(String email) {
        if (!memberAuthRepository.existsByEmail(email)) {
            throw new SiteMemberAuthNotFoundException();
        }
    }
}