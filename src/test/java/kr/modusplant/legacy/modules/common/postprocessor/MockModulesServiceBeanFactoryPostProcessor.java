package kr.modusplant.legacy.modules.common.postprocessor;

import io.micrometer.common.lang.NonNullApi;
import org.mockito.Mockito;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;

import java.util.List;
import java.util.Objects;

import static kr.modusplant.legacy.domains.common.constant.Reference.NOTATION_DOMAINS;
import static kr.modusplant.legacy.modules.common.constant.Reference.NOTATION_MODULES;

@NonNullApi
public class MockModulesServiceBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                return beanDefinition.getMetadata().isConcrete() && beanDefinition.getMetadata().hasAnnotation(Service.class.getName());
            }
        };
        scanner.addIncludeFilter(new AnnotationTypeFilter(Service.class));
        ClassLoader classLoader = this.getClass().getClassLoader();

        for (String reference: List.of(NOTATION_DOMAINS, NOTATION_MODULES, "kr.modusplant.infrastructure.security")) {
            for (BeanDefinition serviceDef : scanner.findCandidateComponents(reference)) {
                Class<?> clazz;
                try {
                    clazz = ClassUtils.forName(Objects.requireNonNull(serviceDef.getBeanClassName()), classLoader);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("Fail to load the service class: " + serviceDef.getBeanClassName());
                }
                String simpleName = clazz.getSimpleName();
                beanFactory.registerSingleton(String.valueOf(simpleName.charAt(0)).toLowerCase() + simpleName.substring(1), Mockito.mock(clazz));
            }
        }
    }
}