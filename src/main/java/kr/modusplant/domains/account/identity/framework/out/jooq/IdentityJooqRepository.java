package kr.modusplant.domains.account.identity.framework.out.jooq;

import kr.modusplant.domains.account.identity.usecase.port.repository.IdentityRepository;
import kr.modusplant.domains.account.identity.usecase.response.IdentityAuthResponse;
import kr.modusplant.domains.account.shared.kernel.AccountId;
import kr.modusplant.jooq.tables.SiteMember;
import kr.modusplant.jooq.tables.SiteMemberAuth;
import kr.modusplant.shared.enums.AuthProvider;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class IdentityJooqRepository implements IdentityRepository {

    private final DSLContext dsl;
    private final SiteMemberAuth memberAuth = SiteMemberAuth.SITE_MEMBER_AUTH;
    private final SiteMember member = SiteMember.SITE_MEMBER;

    @Override
    public IdentityAuthResponse getAuthInfo(AccountId id) {
        return dsl.select(memberAuth.EMAIL, memberAuth.PROVIDER, member.CREATED_AT)
                .from(memberAuth)
                .join(member).on(memberAuth.UUID.eq(member.UUID))
                .where(memberAuth.UUID.eq(id.getValue()))
                .fetchOne(record -> new IdentityAuthResponse(
                        record.get(memberAuth.EMAIL),
                        AuthProvider.valueOf(record.get(memberAuth.PROVIDER)),
                        record.get(member.CREATED_AT).toLocalDate()
                ));
    }
}
