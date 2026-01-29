//package kr.modusplant.infrastructure.aop.concurrency;
//
//import kr.modusplant.shared.concurrency.utils.VirtualThreadUtils;
//import kr.modusplant.shared.kernel.common.util.NicknameTestUtils;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.MockedStatic;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.concurrent.Callable;
//
//import static kr.modusplant.shared.concurrency.utils.VirtualThreadUtils.submitAndGet;
//import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_NICKNAME;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.times;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//class VirtualThreadAspectTest implements NicknameTestUtils {
//    private final MockMvc mockMvc;
//
//    @Autowired
//    VirtualThreadAspectTest(MockMvc mockMvc) {
//        this.mockMvc = mockMvc;
//    }
//
//    @Test
//    @DisplayName("MemberController 메소드 호출 시 runOnVirtualThread 실행")
//    @SuppressWarnings("unchecked")
//    void testRunOnVirtualThread_givenCallToController_willTriggerAOP() throws Exception {
//        try (MockedStatic<VirtualThreadUtils> virtualThreadUtils = Mockito.mockStatic(VirtualThreadUtils.class)) {
//            // given
//            virtualThreadUtils.when(() -> submitAndGet(any(Callable.class))).thenReturn(true);
//
//            // when
//            mockMvc.perform(get("/api/v1/members/check/nickname/{nickname}",
//                            MEMBER_BASIC_USER_NICKNAME))
//                    .andExpect(status().isOk());
//
//            // then
//            virtualThreadUtils.verify(
//                    () -> VirtualThreadUtils.submitAndGet(any(Callable.class)), times(1));
//        }
//    }
//}