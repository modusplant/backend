package kr.modusplant.domains.account.normal.framework.out.persistence.jooq;

import kr.modusplant.domains.account.normal.usecase.port.repository.NormalIdentityReadRepository;
import kr.modusplant.domains.account.normal.usecase.port.repository.NormalIdentityUpdateRepository;
import kr.modusplant.domains.account.shared.kernel.AccountId;
import kr.modusplant.jooq.tables.SiteMember;
import kr.modusplant.jooq.tables.SiteMemberAuth;
import kr.modusplant.shared.enums.AuthProvider;
import kr.modusplant.shared.kernel.Email;
import kr.modusplant.shared.kernel.Nickname;
import kr.modusplant.shared.kernel.Password;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

@Repository
public class NormalIdentityJooqRepository implements
        NormalIdentityUpdateRepository, NormalIdentityReadRepository {

    private final SiteMemberAuth memberAuth = SiteMemberAuth.SITE_MEMBER_AUTH;
    private final SiteMember member = SiteMember.SITE_MEMBER;
    private final DSLContext dsl;
    private final PasswordEncoder passwordEncoder;

    public NormalIdentityJooqRepository(DSLContext dsl,
                                        @Qualifier("bcryptPasswordEncoder") PasswordEncoder passwordEncoder) {
        this.dsl = dsl;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public int updateEmail(AccountId accountId, Email newEmail) {
        return dsl.update(memberAuth)
                .set(memberAuth.EMAIL, newEmail.getValue())
                .where(memberAuth.UUID.eq(accountId.getValue()))
                .and(memberAuth.PROVIDER.eq(AuthProvider.BASIC.name()))
                .execute();
    }

    @Override
    public int updatePassword(AccountId accountId, Password pw) {
        return dsl.update(memberAuth)
                .set(memberAuth.PW, passwordEncoder.encode(pw.getValue()))
                .where(memberAuth.UUID.eq(accountId.getValue()))
                .and(memberAuth.PROVIDER.eq(AuthProvider.BASIC.name()))
                .execute();
    }

    @Override
    public String getMemberPassword(AccountId accountId) {
        return dsl.select(memberAuth.PW)
                .from(memberAuth)
                .where(memberAuth.UUID.eq(accountId.getValue())).and(memberAuth.PROVIDER.eq(AuthProvider.BASIC.name()))
                .fetchOne(memberAuth.PW);
    }

    @Override
    public boolean existsByMemberId(AccountId accountId) {
        return dsl.fetchExists(
                dsl.selectOne()
                        .from(memberAuth)
                        .where(memberAuth.UUID.eq(accountId.getValue())).and(memberAuth.PROVIDER.eq(AuthProvider.BASIC.name()))
        );
    }

    @Override
    public boolean existsByEmail(Email email) {
        return dsl.fetchExists(
                dsl.selectOne()
                        .from(memberAuth)
                        .where(memberAuth.EMAIL.eq(email.getValue())).and(memberAuth.PROVIDER.eq(AuthProvider.BASIC.name()))
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
