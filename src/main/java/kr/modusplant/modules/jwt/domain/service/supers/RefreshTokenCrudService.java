package kr.modusplant.modules.jwt.domain.service.supers;

import kr.modusplant.domains.commons.domain.supers.UuidCrudService;
import kr.modusplant.modules.jwt.domain.model.RefreshToken;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenCrudService extends UuidCrudService<RefreshToken> {
    Optional<RefreshToken> getByMemberUuidAndDeviceId(UUID memberUuid, UUID deviceId);

    Optional<RefreshToken> getByRefreshToken(String refreshToken);

    Optional<RefreshToken> getByDeviceId(UUID deviceId);
}
