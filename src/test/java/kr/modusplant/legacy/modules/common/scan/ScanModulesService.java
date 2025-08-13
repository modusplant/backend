package kr.modusplant.legacy.modules.common.scan;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Controller;

import static kr.modusplant.legacy.modules.common.vo.Reference.NOTATION_MODULES;

@Configuration
@ComponentScan(
        basePackages = NOTATION_MODULES,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Controller.class)
)
public abstract class ScanModulesService {
}