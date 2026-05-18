package kr.modusplant.domains.notification.framework.out.jpa.repository;

import kr.modusplant.domains.member.framework.out.jpa.repository.MemberJpaRepository;
import kr.modusplant.domains.notification.usecase.port.repository.MemberInfoRepository;
import kr.modusplant.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.framework.jpa.exception.enums.EntityErrorCode;
import kr.modusplant.shared.persistence.constant.TableName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class MemberInfoRepositoryJpaAdapter implements MemberInfoRepository {
    private final MemberJpaRepository memberJpaRepository;

    public String getNicknameByUuid(UUID memberUuid) {
        return memberJpaRepository.findByUuid(memberUuid)
                .orElseThrow(() -> new NotFoundEntityException(EntityErrorCode.NOT_FOUND_MEMBER, TableName.SITE_MEMBER))
                .getNickname();
    }
}
