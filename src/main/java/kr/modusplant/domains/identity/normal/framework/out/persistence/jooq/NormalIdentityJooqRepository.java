package kr.modusplant.domains.identity.normal.framework.out.persistence.jooq;

import kr.modusplant.domains.identity.normal.domain.vo.Email;
import kr.modusplant.domains.identity.normal.domain.vo.MemberId;
import kr.modusplant.domains.identity.normal.domain.vo.Nickname;
import kr.modusplant.domains.identity.normal.domain.vo.Password;
import kr.modusplant.domains.identity.normal.usecase.port.repository.NormalIdentityReadRepository;
import kr.modusplant.domains.identity.normal.usecase.port.repository.NormalIdentityUpdateRepository;
import kr.modusplant.jooq.tables.SiteMember;
import kr.modusplant.jooq.tables.SiteMemberAuth;
import kr.modusplant.shared.enums.AuthProvider;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NormalIdentityJooqRepository implements
        NormalIdentityUpdateRepository, NormalIdentityReadRepository {

    private final DSLContext dsl;
    private final SiteMemberAuth memberAuth = SiteMemberAuth.SITE_MEMBER_AUTH;
    private final SiteMember member = SiteMember.SITE_MEMBER;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public int updateEmail(MemberId memberId, Email newEmail) {
        return dsl.update(memberAuth)
                .set(memberAuth.EMAIL, newEmail.getEmail())
                .where(memberAuth.ACT_MEMB_UUID.eq(memberId.getValue())).and(memberAuth.PROVIDER.eq(AuthProvider.BASIC.name()))
                .execute();
    }

    @Override
    public int updatePassword(MemberId memberId, Password pw) {
        return dsl.update(memberAuth)
                .set(memberAuth.PW, passwordEncoder.encode(pw.getPassword()))
                .where(memberAuth.ACT_MEMB_UUID.eq(memberId.getValue())).and(memberAuth.PROVIDER.eq(AuthProvider.BASIC.name()))
                .execute();
    }

    @Override
    public String getMemberPassword(MemberId memberId) {
        return dsl.select(memberAuth.PW)
                .from(memberAuth)
                .where(memberAuth.ACT_MEMB_UUID.eq(memberId.getValue())).and(memberAuth.PROVIDER.eq(AuthProvider.BASIC.name()))
                .fetchOne(memberAuth.PW);
    }

    @Override
    public boolean existsByMemberId(MemberId memberId) {
        return dsl.selectOne()
                .from(memberAuth)
                .where(memberAuth.ACT_MEMB_UUID.eq(memberId.getValue())).and(memberAuth.PROVIDER.eq(AuthProvider.BASIC.name()))
                .fetch()
                .isNotEmpty();
    }

    @Override
    public boolean existsByEmailAndProvider(Email email) {
        return dsl.selectOne()
                .from(memberAuth)
                .where(memberAuth.EMAIL.eq(email.getEmail())).and(memberAuth.PROVIDER.eq(AuthProvider.BASIC.name()))
                .fetch()
                .isNotEmpty();
    }

    @Override
    public boolean existsByNickname(Nickname nickname) {
        return dsl.selectOne()
                .from(member)
                .where(member.NICKNAME.eq(nickname.getNickname()))
                .fetch()
                .isNotEmpty();
    }
}
