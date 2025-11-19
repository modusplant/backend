package kr.modusplant.domains.identity.normal.framework.out.persistence.jooq;

import kr.modusplant.domains.identity.normal.domain.vo.Email;
import kr.modusplant.domains.identity.normal.domain.vo.MemberId;
import kr.modusplant.domains.identity.normal.usecase.port.repository.NormalIdentityUpdateRepository;
import kr.modusplant.jooq.tables.SiteMemberAuth;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NormalIdentityJooqRepository implements NormalIdentityUpdateRepository {

    private final DSLContext dsl;
    private final SiteMemberAuth memberAuth = SiteMemberAuth.SITE_MEMBER_AUTH;

    @Override
    public int updateEmail(MemberId memberId, Email newEmail) {
        return dsl.update(memberAuth)
                .set(memberAuth.EMAIL, newEmail.getEmail())
                .where(memberAuth.ACT_MEMB_UUID.eq(memberId.getValue()))
                .execute();
    }
}
