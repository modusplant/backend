package kr.modusplant.domains.identity.account.framework.out.jooq;

import kr.modusplant.domains.identity.account.domain.vo.AccountId;
import kr.modusplant.domains.identity.account.usecase.port.repository.AccountRepository;
import kr.modusplant.domains.identity.account.usecase.response.AccountAuthResponse;
import kr.modusplant.jooq.tables.SiteMember;
import kr.modusplant.jooq.tables.SiteMemberAuth;
import kr.modusplant.shared.enums.AuthProvider;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AccountJooqRepository implements AccountRepository {

    private final DSLContext dsl;
    private final SiteMemberAuth memberAuth = SiteMemberAuth.SITE_MEMBER_AUTH;
    private final SiteMember member = SiteMember.SITE_MEMBER;

    @Override
    public AccountAuthResponse getAuthInfo(AccountId id) {
        return dsl.select(memberAuth.EMAIL, memberAuth.PROVIDER, member.CREATED_AT)
                .from(memberAuth)
                .join(member).on(memberAuth.ACT_MEMB_UUID.eq(member.UUID))
                .where(memberAuth.ACT_MEMB_UUID.eq(id.getValue()))
                .fetchOne(record -> new AccountAuthResponse(
                        record.get(memberAuth.EMAIL),
                        AuthProvider.valueOf(record.get(memberAuth.PROVIDER)),
                        record.get(member.CREATED_AT).toLocalDate()
                ));
    }
}
