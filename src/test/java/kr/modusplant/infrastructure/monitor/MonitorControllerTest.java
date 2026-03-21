package kr.modusplant.infrastructure.monitor;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

class MonitorControllerTest {
    private final MonitorService monitorService = Mockito.mock(MonitorService.class);
    private final MonitorController monitorController = new MonitorController(monitorService);

    @Test
    void monitorSuccessTest() {
        // given & when
        String returnedValue = "Business logic executed successfully!";
        given(monitorService.performBusinessLogic(true)).willReturn(returnedValue);

        // then
        assertThat(monitorController.monitorSuccess()).isEqualTo(returnedValue);
    }

    @Test
    void monitorErrorTest() {
        // given
        given(monitorService.performBusinessLogic(false)).willThrow(new RuntimeException("Exception occurred during the business logic execution!"));

        // when
        RuntimeException runtimeException = assertThrows(RuntimeException.class, monitorController::monitorError);

        // then
        assertThat(runtimeException.getMessage()).isEqualTo("Exception occurred during the business logic execution!");
    }

    @Test
    void monitorErrorControllerTest() {
        // given & when
        RuntimeException runtimeException = assertThrows(RuntimeException.class, monitorController::monitorErrorController);

        // then
        assertThat(runtimeException.getMessage()).isEqualTo("Exception occurred on the controller!");
    }

    @Test
    void monitorRedisHelperTest() {
        // given & when
        String returnedValue = "RedisHelper test executed successfully!";
        given(monitorService.monitorRedisHelper()).willReturn(returnedValue);

        // then
        assertThat(monitorController.monitorRedisHelper()).isEqualTo(returnedValue);
    }
}