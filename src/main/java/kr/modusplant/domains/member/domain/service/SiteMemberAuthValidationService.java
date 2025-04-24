package kr.modusplant.domains.member.domain.service;

import jakarta.persistence.EntityExistsException;
import kr.modusplant.domains.member.persistence.entity.SiteMemberAuthEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberAuthRepository;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.global.error.EntityExistsWithUuidException;
import kr.modusplant.global.error.EntityNotFoundWithUuidException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static kr.modusplant.global.util.ExceptionUtils.getFormattedExceptionMessage;
import static kr.modusplant.global.vo.CamelCaseWord.ORIGINAL_MEMBER_UUID;
import static kr.modusplant.global.vo.ExceptionMessage.EXISTED_ENTITY;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SiteMemberAuthValidationService {
    private final SiteMemberRepository memberRepository;
    private final SiteMemberAuthRepository memberAuthRepository;

    public void validateExistedMemberAuthUuid(UUID uuid) {
        if (uuid == null) {
            return;
        }
        if (memberAuthRepository.findByUuid(uuid).isPresent()) {
            throw new EntityExistsWithUuidException(uuid, SiteMemberAuthEntity.class);
        }
    }

    public void validateExistedMemberAuthOriginalMemberUuid(UUID uuid) {
        if (memberAuthRepository.findByOriginalMember(memberRepository.findByUuid(uuid).orElseThrow()).isPresent()) {
            throw new EntityExistsException(getFormattedExceptionMessage(EXISTED_ENTITY, ORIGINAL_MEMBER_UUID, uuid, SiteMemberAuthEntity.class));
        }
    }

    public void validateNotFoundMemberAuthUuid(UUID uuid) {
        if (uuid == null || memberAuthRepository.findByUuid(uuid).isEmpty()) {
            throw new EntityNotFoundWithUuidException(uuid, SiteMemberAuthEntity.class);
        }
    }
}
