package kr.modusplant.domains.identity.normal.usecase.port.repository;

import kr.modusplant.domains.identity.normal.domain.vo.Email;
import kr.modusplant.domains.identity.normal.domain.vo.MemberId;
import kr.modusplant.domains.identity.normal.domain.vo.Password;

public interface NormalIdentityUpdateRepository {

    int updateEmail(MemberId memberId, Email email);

    int updatePassword(MemberId memberId, Password pw);

}
