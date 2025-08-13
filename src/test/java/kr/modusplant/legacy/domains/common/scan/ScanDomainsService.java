package kr.modusplant.legacy.domains.common.scan;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Controller;

import static kr.modusplant.legacy.domains.common.vo.Reference.NOTATION_DOMAINS;

@Configuration
@ComponentScan(
        // HACK: 임시 방편, 추후 서비스 대상의 더욱 정교한 어노테이션 개발이 필요함
        basePackages = {NOTATION_DOMAINS, "kr.modusplant.framework.outbound.cloud"},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Controller.class)
)
public abstract class ScanDomainsService {
}