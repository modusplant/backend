package kr.modusplant.domains.account.identity.adapter.controller;

import kr.modusplant.domains.account.identity.common.util.usecase.response.IdentityAuthResponseTestUtils;
import kr.modusplant.domains.account.identity.usecase.port.repository.IdentityRepository;
import kr.modusplant.domains.account.identity.usecase.response.IdentityAuthResponse;
import kr.modusplant.domains.account.shared.exception.enums.AccountErrorCode;
import kr.modusplant.shared.exception.NotFoundValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static kr.modusplant.domains.account.shared.kernel.common.util.AccountIdTestUtils.testNormalMemberId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

public class IdentityControllerTest implements IdentityAuthResponseTestUtils {
    private final IdentityRepository repository = Mockito.mock(IdentityRepository.class);
    private final IdentityController controller = new IdentityController(repository);

    @Test
    @DisplayName("존재하는 회원의 식별자로 IdentityAuthResponse 반환")
    public void testGetAuthInfo_givenExistingMemberId_willReturnIdentityAuthResponse() {
        // given
        given(repository.getAuthInfo(testNormalMemberId)).willReturn(testIdentityAuthResponse);

        // when
        IdentityAuthResponse response = controller.getAuthInfo(testNormalMemberId.getValue());

        // then
        assertThat(response).isEqualTo(testIdentityAuthResponse);
        verify(repository).getAuthInfo(testNormalMemberId);
    }

    @Test
    @DisplayName("존재하지 않는 회원의 식별자로 예외 반환")
    public void testGetAuthInfo_givenNotFoundMemberId_willThrowException() {
        // given
        given(repository.getAuthInfo(testNormalMemberId)).willReturn(null);

        // when & then
        NotFoundValueException exception = assertThrows(NotFoundValueException.class,
                () -> controller.getAuthInfo(testNormalMemberId.getValue()));

        assertThat(exception.getErrorCode()).isEqualTo(AccountErrorCode.NOT_FOUND_ACCOUNT);
    }
}
