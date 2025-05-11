package kr.modusplant.domains.common.scan;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Service;

import static kr.modusplant.domains.common.vo.Reference.NOTATION_DOMAINS;

@Configuration
@ComponentScan(
        basePackages = NOTATION_DOMAINS,
        includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Service.class)
)
public abstract class ScanDomainsService {
}