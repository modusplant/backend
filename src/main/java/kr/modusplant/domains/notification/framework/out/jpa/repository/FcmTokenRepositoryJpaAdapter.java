package kr.modusplant.domains.notification.framework.out.jpa.repository;

import kr.modusplant.domains.account.shared.kernel.AccountId;
import kr.modusplant.domains.notification.framework.out.jpa.mapper.supers.FcmTokenJpaMapper;
import kr.modusplant.domains.notification.framework.out.jpa.repository.supers.FcmTokenJpaRepository;
import kr.modusplant.domains.notification.usecase.port.repository.FcmTokenRepository;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.framework.jpa.exception.enums.EntityErrorCode;
import kr.modusplant.framework.jpa.repository.SiteMemberJpaRepository;
import kr.modusplant.shared.enums.Platform;
import kr.modusplant.shared.persistence.constant.TableName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class FcmTokenRepositoryJpaAdapter implements FcmTokenRepository {
    private final FcmTokenJpaRepository fcmTokenJpaRepository;
    private final SiteMemberJpaRepository memberJpaRepository;
    private final FcmTokenJpaMapper fcmTokenJpaMapper;

    @Override
    public void saveOrUpdate(String token, AccountId accountId, Platform platform) {
        SiteMemberEntity memberEntity = memberJpaRepository.findByUuid(accountId.getValue())
                .orElseThrow(() -> new NotFoundEntityException(EntityErrorCode.NOT_FOUND_MEMBER, TableName.SITE_MEMBER));
        fcmTokenJpaRepository.findByToken(token)
                        .ifPresentOrElse(
                                entity -> {
                                    entity.updateMember(memberEntity);
                                    entity.updatePlatform(platform);
                                },
                                () -> fcmTokenJpaRepository.save(fcmTokenJpaMapper.toFcmTokenEntity(token,memberEntity,platform))
                        );
    }

}
