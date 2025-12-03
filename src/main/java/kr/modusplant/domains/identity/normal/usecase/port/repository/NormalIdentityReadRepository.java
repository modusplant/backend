package kr.modusplant.domains.identity.normal.usecase.port.repository;

import kr.modusplant.domains.identity.normal.domain.vo.Email;
import kr.modusplant.domains.identity.normal.domain.vo.MemberId;
import kr.modusplant.domains.identity.normal.domain.vo.Nickname;

public interface NormalIdentityReadRepository {

    String getMemberPassword(MemberId memberId);

    boolean existsByMemberId(MemberId memberId);

    boolean existsByEmailAndProvider(Email email);

    boolean existsByNickname(Nickname nickname);
}
