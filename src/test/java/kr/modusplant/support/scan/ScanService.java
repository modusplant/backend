package kr.modusplant.support.scan;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Service;

import static kr.modusplant.global.vo.Reference.NOTATION_SERVICE_IMPL;

@Configuration
@ComponentScan(
        basePackages = NOTATION_SERVICE_IMPL,
        includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Service.class)
)
public abstract class ScanService {
}