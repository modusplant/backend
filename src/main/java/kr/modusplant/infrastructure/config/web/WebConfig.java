package kr.modusplant.infrastructure.config.web;

import kr.modusplant.domains.account.social.domain.vo.enums.SocialProvider;
import kr.modusplant.domains.search.domain.enums.SearchPostSortCondition;
import kr.modusplant.domains.search.domain.enums.SearchPostTarget;
import kr.modusplant.framework.web.converter.StringEnumConverter;
import kr.modusplant.shared.enums.NotificationStatusType;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(String.class, SearchPostTarget.class, new StringEnumConverter<>(SearchPostTarget.class));
        registry.addConverter(String.class, SearchPostSortCondition.class, new StringEnumConverter<>(SearchPostSortCondition.class));
        registry.addConverter(String.class, NotificationStatusType.class, new StringEnumConverter<>(NotificationStatusType.class));
        registry.addConverter(String.class, SocialProvider.class, new StringEnumConverter<>(SocialProvider.class));
    }
}
