package kr.modusplant.domains.member.domain.service;

import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.global.enums.ErrorCode;
import kr.modusplant.global.error.EntityExistsException;
import kr.modusplant.global.error.EntityNotFoundException;
import kr.modusplant.global.vo.EntityName;
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
            throw new EntityExistsException(ErrorCode.SITEMEMBER_EXISTS, EntityName.SITE_MEMBER);
        }
    }

    public void validateNotFoundUuid(UUID uuid) {
        if (uuid == null || !memberRepository.existsByUuid(uuid)) {
            throw new EntityNotFoundException(ErrorCode.SITEMEMBER_NOT_FOUND, EntityName.SITE_MEMBER);
        }
    }
}
