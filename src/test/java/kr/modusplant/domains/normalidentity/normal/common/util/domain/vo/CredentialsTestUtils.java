package kr.modusplant.domains.normalidentity.normal.common.util.domain.vo;

import kr.modusplant.domains.identity.normal.domain.vo.Credentials;

public interface CredentialsTestUtils extends EmailTestUtils, PasswordTestUtils {
    Credentials testCredentials = Credentials.createWithDomain(testEmail, testPassword);
}
