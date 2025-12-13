package kr.modusplant.domains.identity.normal.framework.out.persistence.jooq;

import kr.modusplant.domains.identity.normal.domain.vo.NormalMemberId;
import kr.modusplant.domains.identity.normal.domain.vo.NormalNickname;
import kr.modusplant.domains.identity.normal.domain.vo.NormalPassword;
import kr.modusplant.domains.identity.normal.usecase.port.repository.NormalIdentityReadRepository;
import kr.modusplant.domains.identity.normal.usecase.port.repository.NormalIdentityUpdateRepository;
import kr.modusplant.jooq.tables.SiteMember;
import kr.modusplant.jooq.tables.SiteMemberAuth;
import kr.modusplant.shared.enums.AuthProvider;
import kr.modusplant.shared.kernel.Email;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NormalIdentityJooqRepository implements
        NormalIdentityUpdateRepository, NormalIdentityReadRepository {

    private final DSLContext dsl;
    private final SiteMemberAuth memberAuth = SiteMemberAuth.SITE_MEMBER_AUTH;
    private final SiteMember member = SiteMember.SITE_MEMBER;
    private final PasswordEncoder passwordEncoder;

    @Override
    public int updateEmail(NormalMemberId normalMemberId, Email newEmail) {
        return dsl.update(memberAuth)
                .set(memberAuth.EMAIL, newEmail.getEmail())
                .where(memberAuth.ACT_MEMB_UUID.eq(normalMemberId.getValue()))
                .and(memberAuth.PROVIDER.eq(AuthProvider.BASIC.name()))
                .execute();
    }

    @Override
    public int updatePassword(NormalMemberId normalMemberId, NormalPassword pw) {
        return dsl.update(memberAuth)
                .set(memberAuth.PW, passwordEncoder.encode(pw.getValue()))
                .where(memberAuth.ACT_MEMB_UUID.eq(normalMemberId.getValue()))
                .and(memberAuth.PROVIDER.eq(AuthProvider.BASIC.name()))
                .execute();
    }

    @Override
    public String getMemberPassword(NormalMemberId normalMemberId) {
        return dsl.select(memberAuth.PW)
                .from(memberAuth)
                .where(memberAuth.ACT_MEMB_UUID.eq(normalMemberId.getValue())).and(memberAuth.PROVIDER.eq(AuthProvider.BASIC.name()))
                .fetchOne(memberAuth.PW);
    }

    @Override
    public boolean existsByMemberId(NormalMemberId normalMemberId) {
        return dsl.fetchExists(
                dsl.selectOne()
                        .from(memberAuth)
                        .where(memberAuth.ACT_MEMB_UUID.eq(normalMemberId.getValue())).and(memberAuth.PROVIDER.eq(AuthProvider.BASIC.name()))
        );
    }

    @Override
    public boolean existsByEmail(Email email) {
        return dsl.fetchExists(
                dsl.selectOne()
                        .from(memberAuth)
                        .where(memberAuth.EMAIL.eq(email.getEmail())).and(memberAuth.PROVIDER.eq(AuthProvider.BASIC.name()))
        );
    }

    @Override
    public boolean existsByNickname(NormalNickname normalNickname) {
        return dsl.fetchExists(
                dsl.selectOne()
                        .from(member)
                        .where(member.NICKNAME.eq(normalNickname.getValue()))
        );
    }
}
