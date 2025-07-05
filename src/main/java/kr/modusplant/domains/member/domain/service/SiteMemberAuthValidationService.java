package kr.modusplant.domains.member.domain.service;

import kr.modusplant.domains.member.enums.AuthProvider;
import kr.modusplant.domains.member.error.MemberExistsException;
import kr.modusplant.domains.member.error.MemberNotFoundException;
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
            throw MemberExistsException.ofMemberAuth();
        }
    }

    public void validateExistedEmailAndAuthProvider(String email, AuthProvider authProvider) {
        if (email == null || authProvider == null) {
            return;
        }
        if (memberAuthRepository.findByEmailAndProvider(email, authProvider).isPresent()) {
            throw MemberExistsException.ofMemberAuth();
        }
    }

    public void validateNotFoundEmailAndAuthProvider(String email, AuthProvider authProvider) {
        if (email == null || authProvider == null) {
            throw MemberNotFoundException.ofMemberAuth();
        }
        if (memberAuthRepository.findByEmailAndProvider(email, authProvider).isEmpty()) {
            throw MemberNotFoundException.ofMemberAuth();
        }
    }

    public void validateNotFoundOriginalMemberUuid(UUID uuid) {
        if (uuid == null || !memberAuthRepository.existsByOriginalMember(memberRepository.findByUuid(uuid).orElseThrow())) {
            throw MemberNotFoundException.ofMemberAuth();
        }
    }

    public void validateNotFoundEmail(String email) {
        if (!memberAuthRepository.existsByEmail(email)) {
            throw MemberNotFoundException.ofMemberAuth();
        }
    }
}