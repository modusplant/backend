package kr.modusplant.domains.common.scan;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Service;

import static kr.modusplant.global.vo.Reference.NOTATION_GLOBAL;

@Configuration
@ComponentScan(
        basePackages = NOTATION_GLOBAL,
        includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Service.class),
        excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Configuration.class)
)
public abstract class ScanGlobalService {
}