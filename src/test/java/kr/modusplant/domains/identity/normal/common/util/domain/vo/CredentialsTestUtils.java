package kr.modusplant.domains.identity.normal.common.util.domain.vo;

import kr.modusplant.domains.identity.normal.domain.vo.Credentials;

import static kr.modusplant.shared.kernel.common.util.EmailTestUtils.testNormalUserEmail;

public interface CredentialsTestUtils extends PasswordTestUtils {
    Credentials testCredentials = Credentials.createWithDomain(testNormalUserEmail, TEST_NORMAL_PASSWORD);
}
