package kr.modusplant.domains.identity.normal.usecase.port.repository;

import kr.modusplant.domains.identity.normal.domain.vo.MemberId;
import kr.modusplant.domains.identity.normal.domain.vo.Nickname;
import kr.modusplant.shared.enums.AuthProvider;
import kr.modusplant.shared.kernel.Email;

import java.util.UUID;

public interface NormalIdentityReadRepository {

    String getMemberPassword(MemberId memberId);

    UUID getMemberId(Email email, AuthProvider provider);

    boolean existsByMemberId(MemberId memberId);

    boolean existsByEmailAndProvider(Email email);

    boolean existsByNickname(Nickname nickname);
}
