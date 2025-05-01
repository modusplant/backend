package kr.modusplant.modules.jwt.domain.service.supers;

import kr.modusplant.domains.commons.app.service.supers.UuidCrudApplicationService;
import kr.modusplant.modules.jwt.domain.model.RefreshToken;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenApplicationService extends UuidCrudApplicationService<RefreshToken, RefreshToken, RefreshToken> {
    Optional<RefreshToken> getByMemberUuidAndDeviceId(UUID memberUuid, UUID deviceId);

    Optional<RefreshToken> getByRefreshToken(String refreshToken);

    Optional<RefreshToken> getByDeviceId(UUID deviceId);
}
