<<<<<<<< HEAD:src/test/java/kr/modusplant/domains/common/scan/ScanDomainService.java
package kr.modusplant.domains.common.scan;
========
package kr.modusplant.api.crud.common.scan;
>>>>>>>> 8263f89 (MP-92 :goal_net: Catch: 응답 구조 상태코드 수정):src/test/java/kr/modusplant/api/crud/common/scan/ScanCrudService.java

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Service;

<<<<<<<< HEAD:src/test/java/kr/modusplant/domains/common/scan/ScanDomainService.java
import static kr.modusplant.domains.commons.vo.Reference.NOTATION_DOMAINS;

@Configuration
@ComponentScan(
        basePackages = NOTATION_DOMAINS,
        includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Service.class)
)
public abstract class ScanDomainService {
========
import static kr.modusplant.api.crud.common.vo.Reference.NOTATION_CRUD_API;

@Configuration
@ComponentScan(
        basePackages = NOTATION_CRUD_API,
        includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Service.class)
)
public abstract class ScanCrudService {
>>>>>>>> 8263f89 (MP-92 :goal_net: Catch: 응답 구조 상태코드 수정):src/test/java/kr/modusplant/api/crud/common/scan/ScanCrudService.java
}