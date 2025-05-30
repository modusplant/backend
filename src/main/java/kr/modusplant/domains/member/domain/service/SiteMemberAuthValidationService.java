package kr.modusplant.domains.member.domain.service;

import jakarta.persistence.EntityExistsException;
import kr.modusplant.domains.member.persistence.entity.SiteMemberAuthEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberAuthRepository;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.global.error.EntityNotFoundWithUuidException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static kr.modusplant.global.enums.ExceptionMessage.EXISTED_ENTITY;
import static kr.modusplant.global.util.ExceptionUtils.getFormattedExceptionMessage;
import static kr.modusplant.global.vo.CamelCaseWord.ORIGINAL_MEMBER_UUID;

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
        if (memberAuthRepository.findByOriginalMember(memberRepository.findByUuid(uuid).orElseThrow()).isPresent()) {
            throw new EntityExistsException(getFormattedExceptionMessage(EXISTED_ENTITY.getValue(), ORIGINAL_MEMBER_UUID, uuid, SiteMemberAuthEntity.class));
        }
    }

    public void validateNotFoundOriginalMemberUuid(UUID uuid) {
        if (uuid == null || memberAuthRepository.findByUuid(uuid).isEmpty()) {
            throw new EntityNotFoundWithUuidException(uuid, SiteMemberAuthEntity.class);
        }
    }
}
