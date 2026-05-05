package kr.modusplant.domains.notification.framework.out.jpa.repository;

import kr.modusplant.domains.notification.domain.vo.RecipientId;
import kr.modusplant.domains.notification.framework.out.jpa.mapper.supers.FcmTokenJpaMapper;
import kr.modusplant.domains.notification.framework.out.jpa.repository.supers.FcmTokenJpaRepository;
import kr.modusplant.domains.notification.usecase.port.repository.FcmTokenRepository;
import kr.modusplant.framework.jpa.entity.FcmTokenEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.framework.jpa.exception.enums.EntityErrorCode;
import kr.modusplant.framework.jpa.repository.SiteMemberJpaRepository;
import kr.modusplant.shared.enums.Platform;
import kr.modusplant.shared.persistence.constant.TableName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@Repository
@RequiredArgsConstructor
public class FcmTokenRepositoryJpaAdapter implements FcmTokenRepository {
    private final FcmTokenJpaRepository fcmTokenJpaRepository;
    private final SiteMemberJpaRepository memberJpaRepository;
    private final FcmTokenJpaMapper fcmTokenJpaMapper;

    @Override
    public void saveOrUpdate(String token, UUID memberUuid, Platform platform) {
        SiteMemberEntity memberEntity = memberJpaRepository.findByUuid(memberUuid)
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

    public List<String> findTokensByRecipientId(RecipientId recipientId) {
        return fcmTokenJpaRepository.findAllByMember(
                memberJpaRepository.findByUuid(recipientId.getValue()).orElseThrow(() -> new NotFoundEntityException(EntityErrorCode.NOT_FOUND_MEMBER, TableName.SITE_MEMBER))
        ).stream().map(FcmTokenEntity::getToken).toList();
    }

    @Override
    @Transactional
    public void deleteByToken(String token) {
        fcmTokenJpaRepository.deleteByToken(token);
    }

}
