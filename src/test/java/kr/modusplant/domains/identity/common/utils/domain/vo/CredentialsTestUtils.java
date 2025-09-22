package kr.modusplant.domains.identity.common.utils.domain.vo;

import kr.modusplant.domains.identity.domain.vo.Credentials;

public interface CredentialsTestUtils {
    Credentials testCredentials = Credentials.create("test123@example.com", "userPw2!");
}
