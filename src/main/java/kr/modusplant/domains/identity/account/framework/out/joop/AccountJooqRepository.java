package kr.modusplant.domains.identity.account.framework.out.joop;

import kr.modusplant.domains.identity.account.domain.vo.MemberId;
import kr.modusplant.domains.identity.account.usecase.port.repository.AccountRepository;
import kr.modusplant.domains.identity.account.usecase.response.AccountAuthResponse;
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

    @Override
    public AccountAuthResponse getAuthInfo(MemberId id) {
        return dsl.select(memberAuth.EMAIL, memberAuth.PROVIDER)
                .from(memberAuth)
                .where(memberAuth.ACT_MEMB_UUID.eq(id.getValue()))
                .fetchOne(record -> new AccountAuthResponse(
                        record.get(memberAuth.EMAIL), AuthProvider.valueOf(record.get(memberAuth.PROVIDER))
                ));
    }
}
