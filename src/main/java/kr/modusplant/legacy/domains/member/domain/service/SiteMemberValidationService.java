package kr.modusplant.legacy.domains.member.domain.service;

import kr.modusplant.framework.outbound.persistence.vo.EntityName;
import kr.modusplant.legacy.domains.member.persistence.repository.SiteMemberRepository;
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
public class SiteMemberValidationService {
    private final SiteMemberRepository memberRepository;

    public void validateExistedUuid(UUID uuid) {
        if (uuid == null) {
            return;
        }
        if (memberRepository.existsByUuid(uuid)) {
            throw new EntityExistsException(ErrorCode.MEMBER_EXISTS, EntityName.SITE_MEMBER);
        }
    }

    public void validateNotFoundUuid(UUID uuid) {
        if (uuid == null || !memberRepository.existsByUuid(uuid)) {
            throw new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND, EntityName.SITE_MEMBER);
        }
    }
}
