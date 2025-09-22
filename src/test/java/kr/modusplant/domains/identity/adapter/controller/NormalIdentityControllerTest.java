package kr.modusplant.domains.identity.adapter.controller;

import kr.modusplant.domains.identity.adapter.mapper.NormalIdentityMapperImpl;
import kr.modusplant.domains.identity.usecase.port.repository.NormalIdentityRepository;
import kr.modusplant.domains.identity.common.utils.domain.vo.SignUpDataTestUtils;
import kr.modusplant.domains.identity.common.utils.usecase.request.NormalSignUpRequestTestUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class NormalIdentityControllerTest implements
        NormalSignUpRequestTestUtils, SignUpDataTestUtils {
    private final NormalIdentityMapperImpl mapper = Mockito.mock(NormalIdentityMapperImpl.class);
    private final NormalIdentityRepository repository = Mockito.mock(NormalIdentityRepository.class);
    private final NormalIdentityController controller = new NormalIdentityController(mapper, repository);

    @Test
    public void testRegisterNormalMember_givenValidRequest_willProcessRequest() {
        // given & when
        given(mapper.toSignUpData(testNormalSignUpRequest)).willReturn(testSignUpData);
        controller.registerNormalMember(testNormalSignUpRequest);

        // then
        verify(mapper, times(1)).toSignUpData(testNormalSignUpRequest);
        verify(repository, times(1)).save(testSignUpData);
    }
}
