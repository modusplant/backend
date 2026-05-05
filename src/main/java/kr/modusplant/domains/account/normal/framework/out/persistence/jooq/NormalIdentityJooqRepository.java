package kr.modusplant.domains.account.normal.framework.out.persistence.jooq;

import kr.modusplant.domains.account.normal.usecase.port.repository.NormalIdentityReadRepository;
import kr.modusplant.domains.account.shared.kernel.AccountId;
import kr.modusplant.jooq.tables.SiteMember;
import kr.modusplant.jooq.tables.SiteMemberAuth;
import kr.modusplant.shared.enums.AuthProvider;
import kr.modusplant.shared.kernel.Email;
import kr.modusplant.shared.kernel.Nickname;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
public class NormalIdentityJooqRepository implements
        NormalIdentityReadRepository {

    private final SiteMemberAuth memberAuth = SiteMemberAuth.SITE_MEMBER_AUTH;
    private final SiteMember member = SiteMember.SITE_MEMBER;
    private final DSLContext dsl;

    public NormalIdentityJooqRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public String getMemberPassword(AccountId accountId) {
        return dsl.select(memberAuth.PW)
                .from(memberAuth)
                .where(memberAuth.UUID.eq(accountId.getValue())).and(memberAuth.PROVIDER.eq(AuthProvider.BASIC.name()))
                .fetchOne(memberAuth.PW);
    }

    @Override
    public AuthProvider getAuthProvider(Email email) {
        String fetchedAuthProvider = dsl.select(memberAuth.PROVIDER)
                .from(memberAuth)
                .where(memberAuth.EMAIL.eq(email.getValue()))
                .fetchOne(0, String.class);
        return AuthProvider.valueOf(fetchedAuthProvider);
    }


    @Override
    public boolean existsByMemberId(AccountId accountId) {
        return dsl.fetchExists(
                dsl.selectOne()
                        .from(memberAuth)
                        .where(memberAuth.UUID.eq(accountId.getValue()))
        );
    }

    @Override
    public boolean existsByEmail(Email email) {
        return dsl.fetchExists(
                dsl.selectOne()
                        .from(memberAuth)
                        .where(memberAuth.EMAIL.eq(email.getValue()))
        );
    }

    @Override
    public boolean existsByNickname(Nickname nickname) {
        return dsl.fetchExists(
                dsl.selectOne()
                        .from(member)
                        .where(member.NICKNAME.eq(nickname.getValue()))
        );
    }
}
