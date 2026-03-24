package kr.modusplant.infrastructure.config.web;

import kr.modusplant.domains.post.usecase.enums.SearchOption;
import kr.modusplant.domains.post.usecase.enums.SearchSort;
import kr.modusplant.framework.web.converter.StringEnumConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(String.class, SearchOption.class, new StringEnumConverter<>(SearchOption.class));
        registry.addConverter(String.class, SearchSort.class, new StringEnumConverter<>(SearchSort.class));
    }
}
