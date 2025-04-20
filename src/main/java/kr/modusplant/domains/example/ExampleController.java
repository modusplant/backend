package kr.modusplant.domains.example;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Example API")
@RestController
@RequestMapping("/api/example")
@RequiredArgsConstructor
public class ExampleController {

    private final ExampleService exampleService;

    @Operation(summary = "성공적인 비즈니스 로직 실행", description = "비즈니스 로직을 정상적으로 실행합니다.")
    @GetMapping("/test-success")
    public String testSuccess() {
        return exampleService.performBusinessLogic(true); // 정상 흐름
    }

    @Operation(summary = "예외가 발생하는 비즈니스 로직 실행", description = "비즈니스 로직을 실행하다가 예외가 발생합니다.")
    @GetMapping("/test-error")
    public String testError() {
        return exampleService.performBusinessLogic(false); // 예외 발생
    }

    @Operation(summary = "컨트롤러에서 예외가 발생하는 실행", description = "컨트롤러에서 예외가 발생합니다.")
    @GetMapping("/test-error-controller")
    public String testErrorController() {
        throw new RuntimeException("컨트롤러에서 예외 발생"); // 예외 발생
    }
}
