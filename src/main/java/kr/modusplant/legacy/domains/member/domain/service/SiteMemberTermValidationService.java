package kr.modusplant.legacy.domains.member.domain.service;

import kr.modusplant.framework.out.persistence.constant.EntityName;
import kr.modusplant.legacy.domains.member.persistence.repository.SiteMemberTermRepository;
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
public class SiteMemberTermValidationService {
    private final SiteMemberTermRepository memberTermRepository;

    public void validateExistedUuid(UUID uuid) {
        if (memberTermRepository.existsByUuid(uuid)) {
            throw new EntityExistsException(ErrorCode.MEMBER_TERM_EXISTS, EntityName.SITE_MEMBER_TERM);
        }
    }

    public void validateNotFoundUuid(UUID uuid) {
        if (uuid == null || !memberTermRepository.existsByUuid(uuid)) {
            throw new EntityNotFoundException(ErrorCode.MEMBER_TERM_NOT_FOUND, EntityName.SITE_MEMBER_TERM);
        }
    }
}
