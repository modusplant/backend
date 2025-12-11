package kr.modusplant.domains.identity.normal.usecase.port.repository;

import kr.modusplant.domains.identity.normal.domain.vo.NormalMemberId;
import kr.modusplant.domains.identity.normal.domain.vo.NormalNickname;
import kr.modusplant.shared.kernel.Email;

public interface NormalIdentityReadRepository {

    String getMemberPassword(NormalMemberId normalMemberId);

    boolean existsByMemberId(NormalMemberId normalMemberId);

    boolean existsByEmail(Email email);

    boolean existsByNickname(NormalNickname normalNickname);
}
