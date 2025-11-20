package kr.modusplant.domains.identity.normal.usecase.port.repository;

import kr.modusplant.domains.identity.normal.domain.vo.Email;
import kr.modusplant.domains.identity.normal.domain.vo.MemberId;
import kr.modusplant.domains.identity.normal.domain.vo.Nickname;
import kr.modusplant.shared.enums.AuthProvider;

public interface NormalIdentityReadRepository {

    boolean existsByMemberId(MemberId memberId);

    boolean existsByEmailAndProvider(Email email, AuthProvider provider);

    boolean existsByNickname(Nickname nickname);
}
