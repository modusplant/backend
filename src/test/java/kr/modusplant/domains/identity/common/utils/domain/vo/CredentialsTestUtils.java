package kr.modusplant.domains.identity.common.utils.domain.vo;

import kr.modusplant.domains.identity.domain.vo.Credentials;

public interface CredentialsTestUtils extends EmailTestUtils, PasswordTestUtils {
    Credentials testCredentials = Credentials.createWithDomain(testEmail, testPassword);
}
