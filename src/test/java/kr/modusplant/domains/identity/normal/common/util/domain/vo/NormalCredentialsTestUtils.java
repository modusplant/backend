package kr.modusplant.domains.identity.normal.common.util.domain.vo;

import kr.modusplant.domains.identity.normal.domain.vo.NormalCredentials;
import kr.modusplant.shared.kernel.common.util.PasswordTestUtils;

import static kr.modusplant.shared.kernel.common.util.EmailTestUtils.testNormalUserEmail;

public interface NormalCredentialsTestUtils extends PasswordTestUtils {
    NormalCredentials testNormalCredentials = NormalCredentials.createWithDomain(testNormalUserEmail, testNormalUserPassword);
}
