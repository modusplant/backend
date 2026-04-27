package kr.modusplant.domains.notification.usecase.port.repository;

import java.util.UUID;

public interface MemberInfoRepository {
    String getNicknameByUuid(UUID memberUuid);
}
