package kr.modusplant.api.crud.common.scan;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Service;

import static kr.modusplant.api.crud.common.vo.Reference.NOTATION_CRUD_API;

@Configuration
@ComponentScan(
        basePackages = NOTATION_CRUD_API,
        includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Service.class)
)
public abstract class ScanCrudService {
}