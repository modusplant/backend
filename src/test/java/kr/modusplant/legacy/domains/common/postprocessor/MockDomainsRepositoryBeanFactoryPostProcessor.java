package kr.modusplant.legacy.domains.common.postprocessor;

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

import java.util.List;
import java.util.Objects;

import static kr.modusplant.legacy.domains.common.vo.Reference.NOTATION_DOMAINS;

@NonNullApi
public class MockDomainsRepositoryBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

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

        for (String reference: List.of(NOTATION_DOMAINS, "kr.modusplant.framework")) {
            for (BeanDefinition repositoryDef : scanner.findCandidateComponents(reference)) {
                Class<?> clazz;
                try {
                    clazz = ClassUtils.forName(Objects.requireNonNull(repositoryDef.getBeanClassName()), classLoader);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("Fail to load the repository interface: " + repositoryDef.getBeanClassName());
                }
                String simpleName = clazz.getSimpleName();
                beanFactory.registerSingleton(String.valueOf(simpleName.charAt(0)).toLowerCase() + simpleName.substring(1), Mockito.mock(clazz));
            }
        }
    }
}