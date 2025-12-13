package kr.modusplant.domains.identity.email.framework.out.persistence.jooq;

import kr.modusplant.domains.identity.email.usecase.port.repository.EmailIdentityRepository;
import kr.modusplant.jooq.tables.SiteMemberAuth;
import kr.modusplant.shared.enums.AuthProvider;
import kr.modusplant.shared.kernel.Email;
import kr.modusplant.shared.kernel.Password;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EmailIdentityJooqRepository implements EmailIdentityRepository {

    private final DSLContext dsl;
    private final SiteMemberAuth memberAuth = SiteMemberAuth.SITE_MEMBER_AUTH;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean existsByEmailAndProvider(Email email) {
        return dsl.selectOne()
                .from(memberAuth)
                .where(memberAuth.EMAIL.eq(email.getValue())).and(memberAuth.PROVIDER.eq(AuthProvider.BASIC.name()))
                .fetch()
                .isNotEmpty();
    }

    @Override
    public int updatePassword(Email email, Password pw) {
        return dsl.update(memberAuth)
                .set(memberAuth.PW, passwordEncoder.encode(pw.getValue()))
                .where(memberAuth.EMAIL.eq(email.getValue()))
                .and(memberAuth.PROVIDER.eq(AuthProvider.BASIC.name()))
                .execute();
    }
}
