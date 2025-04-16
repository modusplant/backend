package kr.modusplant.domains.common.postprocessor;

import io.micrometer.common.lang.NonNullApi;
import org.mockito.Mockito;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Repository;
import org.springframework.util.ClassUtils;

import java.util.Objects;

import static kr.modusplant.domains.commons.vo.Reference.NOTATION_DOMAINS;

@NonNullApi
public class MockCrudRepositoryBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().hasAnnotation(Repository.class.getName());
            }
        };
        scanner.addIncludeFilter(new AnnotationTypeFilter(Repository.class));
        ClassLoader classLoader = this.getClass().getClassLoader();

        for (BeanDefinition repositoryDef : scanner.findCandidateComponents(NOTATION_DOMAINS)) {
            Class<?> clazz;
            try {
                clazz = ClassUtils.forName(Objects.requireNonNull(repositoryDef.getBeanClassName()), classLoader);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Fail to load the repository interface: " + repositoryDef.getBeanClassName());
            }
            beanFactory.registerSingleton(clazz.getSimpleName(), Mockito.mock(clazz));
        }
    }
}