package kr.modusplant.domains.normalidentity.normal.adapter.controller;

import kr.modusplant.domains.identity.normal.adapter.controller.NormalIdentityController;
import kr.modusplant.domains.identity.normal.adapter.mapper.NormalIdentityMapperImpl;
import kr.modusplant.domains.identity.normal.usecase.port.mapper.NormalIdentityMapper;
import kr.modusplant.domains.identity.normal.usecase.port.repository.NormalIdentityRepository;
import kr.modusplant.domains.identity.normal.usecase.port.repository.NormalIdentityUpdateRepository;
import kr.modusplant.domains.normalidentity.normal.common.util.domain.vo.SignUpDataTestUtils;
import kr.modusplant.domains.normalidentity.normal.common.util.usecase.request.NormalSignUpRequestTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class NormalIdentityControllerTest implements
        NormalSignUpRequestTestUtils, SignUpDataTestUtils {
    private final NormalIdentityMapper mapper = Mockito.mock(NormalIdentityMapperImpl.class);
    private final NormalIdentityRepository repository = Mockito.mock(NormalIdentityRepository.class);
    private final NormalIdentityUpdateRepository updateRepository = Mockito.mock(NormalIdentityUpdateRepository.class);
    private final NormalIdentityController controller = new NormalIdentityController(mapper, repository, updateRepository);

    @Test
    @DisplayName("유효한 요청 데이터를 받았을 시 일반 회원가입 진행")
    public void testRegisterNormalMember_givenValidRequest_willProcessRequest() {
        // given & when
        given(mapper.toSignUpData(testNormalSignUpRequest)).willReturn(testSignUpData);
        controller.registerNormalMember(testNormalSignUpRequest);

        // then
        verify(mapper, times(1)).toSignUpData(testNormalSignUpRequest);
        verify(repository, times(1)).save(testSignUpData);
    }
}
