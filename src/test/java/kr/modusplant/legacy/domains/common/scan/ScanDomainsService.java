package kr.modusplant.legacy.domains.common.scan;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Controller;

import static kr.modusplant.legacy.domains.common.vo.Reference.NOTATION_DOMAINS;

@Configuration
@ComponentScan(
        basePackages = {NOTATION_DOMAINS, "kr.modusplant.framework.outbound"},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Controller.class)
)
public abstract class ScanDomainsService {
}