package kr.modusplant.domains.identity.normal.usecase.port.repository;

import kr.modusplant.domains.identity.normal.domain.vo.MemberId;
import kr.modusplant.domains.identity.normal.domain.vo.Nickname;
import kr.modusplant.shared.kernel.Email;

public interface NormalIdentityReadRepository {

    String getMemberPassword(MemberId memberId);

    boolean existsByMemberId(MemberId memberId);

    boolean existsByEmail(Email email);

    boolean existsByNickname(Nickname nickname);
}
