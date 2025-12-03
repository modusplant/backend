package kr.modusplant.domains.identity.normal.framework.out.persistence.jooq;

import kr.modusplant.domains.identity.normal.common.util.domain.vo.EmailTestUtils;
import kr.modusplant.domains.identity.normal.common.util.domain.vo.MemberIdTestUtils;
import kr.modusplant.domains.identity.normal.common.util.domain.vo.NicknameTestUtils;
import kr.modusplant.domains.identity.normal.common.util.domain.vo.PasswordTestUtils;
import kr.modusplant.jooq.tables.SiteMemberAuth;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.tools.jdbc.MockConnection;
import org.jooq.tools.jdbc.MockDataProvider;
import org.jooq.tools.jdbc.MockResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class NormalIdentityJooqRepositoryTest implements
        MemberIdTestUtils, EmailTestUtils, PasswordTestUtils, NicknameTestUtils {

    private final SiteMemberAuth memberAuth = SiteMemberAuth.SITE_MEMBER_AUTH;

    private NormalIdentityJooqRepository createRepository(MockDataProvider provider) {
        MockConnection connection = new MockConnection(provider);
        DSLContext dsl = DSL.using(connection, SQLDialect.POSTGRES);
        PasswordEncoder encoder = Mockito.mock(PasswordEncoder.class);

        return new NormalIdentityJooqRepository(dsl, encoder);
    }

    @Test
    @DisplayName("사용자의 식별자와 이메일로 사용자의 이메일 갱신")
    void testUpdateEmail_givenValidMemberIdAndEmail_willUpdateEmail() {
        // given
        MockDataProvider provider = ctx -> {
            Object[] bindings = ctx.bindings();

            if (bindings[0].equals(testEmail.getEmail()) && bindings[1].equals(testMemberId.getValue())) {
                return new MockResult[] {
                        new MockResult(1, null)
                };
            }
            return new MockResult[] { new MockResult(0, null) };
        };
        NormalIdentityJooqRepository repository = createRepository(provider);

        // when
        int result = repository.updateEmail(testMemberId, testEmail);

        // then
        assertThat(result).isEqualTo(1);
    }

    @Test
    @DisplayName("사용자의 식별자와 비밀번호로 사용자의 비밀번호 갱신")
    void testUpdatePassword_givenValidMemberIdAndPassword_willUpdatePassword() {
        // given
        MockDataProvider provider = ctx -> {
            Object[] bindings = ctx.bindings();

            if (bindings[0].equals(testEmail.getEmail()) && bindings[1].equals(testPassword.getPassword())) {
                return new MockResult[] {
                        new MockResult(1, null)
                };
            }
            return new MockResult[] { new MockResult(0, null) };
        };

        NormalIdentityJooqRepository repository = createRepository(provider);

        // when
        int result = repository.updatePassword(testMemberId, testPassword);

        // then
        assertThat(result).isEqualTo(1);
    }

    @Test
    @DisplayName("사용자의 식별자로 비밀번호 가져오기")
    void testGetMemberPassword_givenValidMemberId_willGetPassword() {
        // given
        MockDataProvider provider = ctx -> {
            Object[] bindings = ctx.bindings();

            DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
            Result<Record1<String>> result = dsl.newResult(memberAuth.PW);
            result.add(
                    dsl.newRecord(memberAuth.PW).value1(testPassword.getPassword())
            );

            if (bindings[0].equals(testMemberId.getValue())) {
                return new MockResult[] {
                        new MockResult(0, result)
                };
            }
            return new MockResult[] { new MockResult(0, null) };
        };

        NormalIdentityJooqRepository repository = createRepository(provider);

        // when
        String result = repository.getMemberPassword(testMemberId);

        // then
        assertThat(result).isEqualTo(testPassword.getPassword());
    }

    @Test
    @DisplayName("사용자의 식별자로 사용자의 존재 유무 확인하기")
    void testExistsByMemberId_givenValidMemberId_willCheckMemberExistence() {
//        // given
//        MockDataProvider provider = ctx -> {
//            Object[] bindings = ctx.bindings();
//
//            DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
//            Result<Record1<String>> result = dsl.newResult(memberAuth.PW);
//            result.add(
//                    dsl.newRecord(memberAuth.PW).value1(testPassword.getPassword())
//            );
//
//            if (bindings[0].equals(testMemberId.getValue())) {
//                return new MockResult[] {
//                        new MockResult(0, result)
//                };
//            }
//            return new MockResult[] { new MockResult(0, null) };
//        };
//
//        NormalIdentityJooqRepository repository = createRepository(provider);
//
//        // when
//        String result = repository.getMemberPassword(testMemberId);
//
//        // then
//        assertThat(result).isEqualTo(testPassword.getPassword());
    }
}
