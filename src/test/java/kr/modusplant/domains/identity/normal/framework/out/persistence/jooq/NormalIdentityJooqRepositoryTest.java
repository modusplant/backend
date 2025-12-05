package kr.modusplant.domains.identity.normal.framework.out.persistence.jooq;

import kr.modusplant.domains.identity.normal.common.util.domain.vo.EmailTestUtils;
import kr.modusplant.domains.identity.normal.common.util.domain.vo.MemberIdTestUtils;
import kr.modusplant.domains.identity.normal.common.util.domain.vo.NicknameTestUtils;
import kr.modusplant.domains.identity.normal.common.util.domain.vo.PasswordTestUtils;
import kr.modusplant.jooq.tables.SiteMemberAuth;
import kr.modusplant.shared.enums.AuthProvider;
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
import static org.mockito.BDDMockito.given;

public class NormalIdentityJooqRepositoryTest implements
        MemberIdTestUtils, EmailTestUtils, PasswordTestUtils, NicknameTestUtils {

    private final SiteMemberAuth memberAuth = SiteMemberAuth.SITE_MEMBER_AUTH;
    PasswordEncoder encoder = Mockito.mock(PasswordEncoder.class);

    private NormalIdentityJooqRepository createRepository(MockDataProvider provider) {
        MockConnection connection = new MockConnection(provider);
        DSLContext dsl = DSL.using(connection, SQLDialect.POSTGRES);
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

            if (bindings[0].equals(testPassword.getPassword())
                    && bindings[1].equals(testMemberId.getValue())
                    && bindings[2].equals(AuthProvider.BASIC.name())) {
                return new MockResult[] {
                        new MockResult(1, null)
                };
            }
            return new MockResult[] { new MockResult(0, null) };
        };
        NormalIdentityJooqRepository repository = createRepository(provider);

        given(encoder.encode(testPassword.getPassword())).willReturn(testPassword.getPassword());

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

            if (bindings[0].equals(testMemberId.getValue()) && bindings[1].equals(AuthProvider.BASIC.name())) {
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
    void testExistsByMemberId_givenValidMemberId_willReturnBoolean() {
        // given
        MockDataProvider provider = ctx -> {
            Object[] bindings = ctx.bindings();

            DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
            Result<Record1<Integer>> result = dsl.newResult(DSL.inline(1));
            result.add(dsl.newRecord(DSL.inline(1)).values(1));

            if (bindings[0].equals(testMemberId.getValue())) {
                return new MockResult[] {
                        new MockResult(0, result)
                };
            }
            return new MockResult[] { new MockResult(0, null) };
        };

        NormalIdentityJooqRepository repository = createRepository(provider);

        // when
        boolean result = repository.existsByMemberId(testMemberId);

        // then
        assertThat(result).isEqualTo(true);
    }

    @Test
    @DisplayName("사용자의 이메일과 인증 제공자로 사용자의 존재 유무 확인하기")
    void testExistsByEmail_givenValidEmail_willReturnBoolean() {
        // given
        MockDataProvider provider = ctx -> {
            Object[] bindings = ctx.bindings();

            DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
            Result<Record1<Integer>> result = dsl.newResult(DSL.inline(1));
            result.add(dsl.newRecord(DSL.inline(1)).values(1));

            if (bindings[0].equals(testEmail.getEmail())) {
                return new MockResult[] {
                        new MockResult(0, result)
                };
            }
            return new MockResult[] { new MockResult(0, null) };
        };

        NormalIdentityJooqRepository repository = createRepository(provider);

        // when
        boolean result = repository.existsByEmail(testEmail);

        // then
        assertThat(result).isEqualTo(true);
    }

    @Test
    @DisplayName("사용자의 닉네임으로 사용자의 존재 유무 확인하기")
    void testExistsByNickname_givenValidNickname_willReturnBoolean() {
        // given
        MockDataProvider provider = ctx -> {
            Object[] bindings = ctx.bindings();

            DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
            Result<Record1<Integer>> result = dsl.newResult(DSL.inline(1));
            result.add(dsl.newRecord(DSL.inline(1)).values(1));

            if (bindings[0].equals(testNickname.getNickname())) {
                return new MockResult[] {
                        new MockResult(0, result)
                };
            }
            return new MockResult[] { new MockResult(0, null) };
        };

        NormalIdentityJooqRepository repository = createRepository(provider);

        // when
        boolean result = repository.existsByNickname(testNickname);

        // then
        assertThat(result).isEqualTo(true);
    }
}
