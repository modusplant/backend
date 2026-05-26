package kr.modusplant.infrastructure.aop.logging.fixture;

import kr.modusplant.shared.exception.BusinessException;
import kr.modusplant.shared.exception.enums.GeneralErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

public class ServiceExceptionTestFixture {

    @Service
    public static class SysErrorTestService {
        public void throwSysError() {
            throw new RuntimeException("테스트용 시스템 예외");
        }
    }

    @Service
    public static class BizErrorTestService {
        public void throwBizError() {
            throw new BusinessException(GeneralErrorCode.GENERIC_ERROR, "테스트용 비즈니스 예외");
        }
        public void throwBizErrorWithCause() {
            throw new BusinessException(GeneralErrorCode.GENERIC_ERROR, "테스트용 비즈니스 예외",
                    new IllegalStateException("테스트용 원인 예외"));
        }
    }

    @RestController
    @RequestMapping("/test/monitor")
    @RequiredArgsConstructor
    public static class ServiceExceptionTestController {
        private final SysErrorTestService sysErrorTestService;
        private final BizErrorTestService bizErrorTestService;

        @GetMapping("/sys-error")
        public void sysError() { sysErrorTestService.throwSysError(); }

        @GetMapping("/biz-error")
        public void bizError() { bizErrorTestService.throwBizError(); }

        @GetMapping("/biz-error-with-cause")
        public void bizErrorWithCause() { bizErrorTestService.throwBizErrorWithCause(); }
    }
}
