package kr.modusplant.domains.member.domain.service;

import kr.modusplant.domains.member.error.SiteMemberRoleExistsException;
import kr.modusplant.domains.member.error.SiteMemberRoleNotFoundException;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRoleRepository;
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
            throw new SiteMemberRoleExistsException();
        }
    }

    public void validateNotFoundUuid(UUID uuid) {
        if (uuid == null || !memberRoleRepository.existsByUuid(uuid)) {
            throw new SiteMemberRoleNotFoundException();
        }
    }
}
