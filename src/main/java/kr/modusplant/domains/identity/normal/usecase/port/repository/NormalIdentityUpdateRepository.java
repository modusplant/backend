package kr.modusplant.domains.identity.normal.usecase.port.repository;

import kr.modusplant.domains.identity.normal.domain.vo.NormalMemberId;
import kr.modusplant.domains.identity.normal.domain.vo.NormalPassword;
import kr.modusplant.shared.kernel.Email;

public interface NormalIdentityUpdateRepository {

    int updateEmail(NormalMemberId normalMemberId, Email email);

    int updatePassword(NormalMemberId normalMemberId, NormalPassword pw);

}
