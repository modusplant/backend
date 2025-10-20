package kr.modusplant.legacy.modules.common.scan;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Controller;

import static kr.modusplant.legacy.modules.common.constant.Reference.NOTATION_MODULES;

@Configuration
@ComponentScan(
        // HACK: 임의로 Configuration 클래스까지 제거 및 인프라 서비스 스캔 추가, 추후 해당 조건 고려한 어노테이션 필요
        basePackages = {NOTATION_MODULES, "kr.modusplant.infrastructure"},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = {Controller.class, Configuration.class})
)
public abstract class ScanModulesService {
}