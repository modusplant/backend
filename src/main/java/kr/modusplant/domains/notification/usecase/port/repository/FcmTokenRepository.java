package kr.modusplant.domains.notification.usecase.port.repository;

import kr.modusplant.domains.notification.domain.vo.RecipientId;
import kr.modusplant.shared.enums.Platform;

import java.util.List;
import java.util.UUID;

public interface FcmTokenRepository {
    void saveOrUpdate(String token, UUID memberUuid, Platform platform);

    List<String> findTokensByRecipientId(RecipientId recipientId);

    void deleteByToken(String token);
}
