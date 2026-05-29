package kr.modusplant.domains.notification.framework.outbound.jpa.repository;

import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.MemberJpaRepository;
import kr.modusplant.domains.notification.domain.vo.RecipientId;
import kr.modusplant.domains.notification.framework.outbound.jpa.entity.FcmTokenEntity;
import kr.modusplant.domains.notification.framework.outbound.jpa.mapper.supers.FcmTokenJpaMapper;
import kr.modusplant.domains.notification.framework.outbound.jpa.repository.supers.FcmTokenJpaRepository;
import kr.modusplant.domains.notification.usecase.port.repository.FcmTokenRepository;
import kr.modusplant.shared.enums.Platform;
import kr.modusplant.shared.framework.jpa.exception.NotFoundEntityException;
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
    private final MemberJpaRepository memberJpaRepository;
    private final FcmTokenJpaMapper fcmTokenJpaMapper;

    @Override
    public void saveOrUpdate(String token, UUID memberUuid, Platform platform) {
        MemberEntity memberEntity = memberJpaRepository.findByUuid(memberUuid)
                .orElseThrow(() -> new NotFoundEntityException(MemberErrorCode.NOT_FOUND_MEMBER, TableName.SITE_MEMBER));
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
                memberJpaRepository.findByUuid(recipientId.getValue()).orElseThrow(() -> new NotFoundEntityException(MemberErrorCode.NOT_FOUND_MEMBER, TableName.SITE_MEMBER))
        ).stream().map(FcmTokenEntity::getToken).toList();
    }

    @Override
    @Transactional
    public void deleteByToken(String token) {
        fcmTokenJpaRepository.deleteByToken(token);
    }

}
