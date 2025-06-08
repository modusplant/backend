package kr.modusplant.modules.monitor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Monitor API")
@RestController
@RequestMapping("/api/monitor")
@RequiredArgsConstructor
public class MonitorController {

    private final MonitorService monitorService;

    @Operation(summary = "성공적인 비즈니스 로직 실행", description = "비즈니스 로직을 정상적으로 실행합니다.")
    @GetMapping("/monitor-success")
    public String monitorSuccess() {
        return monitorService.performBusinessLogic(true); // 정상 흐름
    }

    @Operation(summary = "예외가 발생하는 비즈니스 로직 실행", description = "비즈니스 로직을 실행하다가 예외가 발생합니다.")
    @GetMapping("/monitor-error")
    public String monitorError() {
        return monitorService.performBusinessLogic(false); // 예외 발생
    }

    @Operation(summary = "컨트롤러에서 예외가 발생하는 실행", description = "컨트롤러에서 예외가 발생합니다.")
    @GetMapping("/monitor-error-controller")
    public String monitorErrorController() {
        throw new RuntimeException("컨트롤러에서 예외 발생"); // 예외 발생
    }

    @Operation(summary = "Redis Helper 테스트", description = "Redis 저장소에 RedisHelper 를 사용하여 저장.")
    @GetMapping("/monitor-redis")
    public String monitorRedisHelper() {
        return monitorService.monitorRedisHelper();
    }
}
