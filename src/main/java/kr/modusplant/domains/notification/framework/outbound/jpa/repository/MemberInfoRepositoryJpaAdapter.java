package kr.modusplant.domains.notification.framework.outbound.jpa.repository;

import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.MemberJpaRepository;
import kr.modusplant.domains.notification.usecase.port.repository.MemberInfoRepository;
import kr.modusplant.shared.framework.jpa.exception.NotFoundEntityException;
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
                .orElseThrow(() -> new NotFoundEntityException(MemberErrorCode.NOT_FOUND_MEMBER, TableName.SITE_MEMBER))
                .getNickname();
    }
}
