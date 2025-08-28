package kr.modusplant.legacy.domains.member.domain.service;

import kr.modusplant.framework.out.persistence.constant.EntityName;
import kr.modusplant.framework.out.persistence.repository.SiteMemberAuthRepository;
import kr.modusplant.framework.out.persistence.repository.SiteMemberRepository;
import kr.modusplant.legacy.domains.member.enums.AuthProvider;
import kr.modusplant.shared.exception.EntityExistsException;
import kr.modusplant.shared.exception.EntityNotFoundException;
import kr.modusplant.shared.exception.enums.ErrorCode;
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
            throw new EntityExistsException(ErrorCode.MEMBER_AUTH_EXISTS, EntityName.SITE_MEMBER_AUTH);
        }
    }

    public void validateExistedEmailAndAuthProvider(String email, AuthProvider authProvider) {
        if (email == null || authProvider == null) {
            return;
        }
        if (memberAuthRepository.findByEmailAndProvider(email, authProvider).isPresent()) {
            throw new EntityExistsException(ErrorCode.MEMBER_AUTH_EXISTS, EntityName.SITE_MEMBER_AUTH);
        }
    }

    public void validateNotFoundEmailAndAuthProvider(String email, AuthProvider authProvider) {
        if (email == null || authProvider == null) {
            throw new EntityNotFoundException(ErrorCode.MEMBER_AUTH_NOT_FOUND, EntityName.SITE_MEMBER_AUTH);
        }
        if (memberAuthRepository.findByEmailAndProvider(email, authProvider).isEmpty()) {
            throw new EntityNotFoundException(ErrorCode.MEMBER_AUTH_NOT_FOUND, EntityName.SITE_MEMBER_AUTH);
        }
    }

    public void validateNotFoundOriginalMemberUuid(UUID uuid) {
        if (uuid == null || !memberAuthRepository.existsByOriginalMember(memberRepository.findByUuid(uuid).orElseThrow())) {
            throw new EntityNotFoundException(ErrorCode.MEMBER_AUTH_NOT_FOUND, EntityName.SITE_MEMBER_AUTH);
        }
    }

    public void validateNotFoundEmail(String email) {
        if (!memberAuthRepository.existsByEmail(email)) {
            throw new EntityNotFoundException(ErrorCode.MEMBER_AUTH_NOT_FOUND, EntityName.SITE_MEMBER_AUTH);
        }
    }
}