package kr.modusplant.legacy.domains.member.domain.service;

import kr.modusplant.framework.out.persistence.constant.EntityName;
import kr.modusplant.framework.out.persistence.repository.SiteMemberRoleRepository;
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
public class SiteMemberRoleValidationService {
    private final SiteMemberRoleRepository memberRoleRepository;

    public void validateExistedUuid(UUID uuid) {
        if (memberRoleRepository.existsByUuid(uuid)) {
            throw new EntityExistsException(ErrorCode.MEMBER_ROLE_EXISTS, EntityName.SITE_MEMBER_ROLE);
        }
    }

    public void validateNotFoundUuid(UUID uuid) {
        if (uuid == null || !memberRoleRepository.existsByUuid(uuid)) {
            throw new EntityNotFoundException(ErrorCode.MEMBER_ROLE_NOT_FOUND, EntityName.SITE_MEMBER_ROLE);
        }
    }
}
