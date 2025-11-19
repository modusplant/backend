package kr.modusplant.domains.identity.normal.usecase.port.repository;

import kr.modusplant.domains.identity.normal.domain.vo.MemberId;

public interface NormalIdentityReadRepository {

    boolean existsByMemberId(MemberId memberId);
}
