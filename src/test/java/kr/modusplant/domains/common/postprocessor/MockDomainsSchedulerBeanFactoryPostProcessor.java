package kr.modusplant.domains.common.postprocessor;

import io.micrometer.common.lang.NonNullApi;
import org.mockito.Mockito;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.util.Objects;
import java.util.regex.Pattern;

import static kr.modusplant.domains.common.vo.Reference.NOTATION_DOMAINS;

@NonNullApi
public class MockDomainsSchedulerBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                return beanDefinition.getMetadata().isConcrete() && beanDefinition.getMetadata().hasAnnotation(Component.class.getName());
            }
        };
        scanner.addIncludeFilter(new RegexPatternTypeFilter(Pattern.compile("^.*Scheduler$")));
        ClassLoader classLoader = this.getClass().getClassLoader();

        for (BeanDefinition schedulerDef : scanner.findCandidateComponents(NOTATION_DOMAINS)) {
            Class<?> clazz;
            try {
                clazz = ClassUtils.forName(Objects.requireNonNull(schedulerDef.getBeanClassName()), classLoader);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Fail to load the scheduler class: " + schedulerDef.getBeanClassName());
            }
            String simpleName = clazz.getSimpleName();
            beanFactory.registerSingleton(String.valueOf(simpleName.charAt(0)).toLowerCase() + simpleName.substring(1), Mockito.mock(clazz));
        }
    }
}