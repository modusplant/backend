<<<<<<<< HEAD:src/test/java/kr/modusplant/domains/common/postprocessor/MockDomainRepositoryBeanFactoryPostProcessor.java
package kr.modusplant.domains.common.postprocessor;
========
package kr.modusplant.api.crud.common.postprocessor;
>>>>>>>> 8263f89 (MP-92 :goal_net: Catch: 응답 구조 상태코드 수정):src/test/java/kr/modusplant/api/crud/common/postprocessor/MockCrudRepositoryBeanFactoryPostProcessor.java

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

<<<<<<<< HEAD:src/test/java/kr/modusplant/domains/common/postprocessor/MockDomainRepositoryBeanFactoryPostProcessor.java
import static kr.modusplant.domains.commons.vo.Reference.NOTATION_DOMAINS;

@NonNullApi
public class MockDomainRepositoryBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
========
import static kr.modusplant.api.crud.common.vo.Reference.NOTATION_CRUD_API;

@NonNullApi
public class MockCrudRepositoryBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
>>>>>>>> 8263f89 (MP-92 :goal_net: Catch: 응답 구조 상태코드 수정):src/test/java/kr/modusplant/api/crud/common/postprocessor/MockCrudRepositoryBeanFactoryPostProcessor.java

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

<<<<<<<< HEAD:src/test/java/kr/modusplant/domains/common/postprocessor/MockDomainRepositoryBeanFactoryPostProcessor.java
        for (BeanDefinition repositoryDef : scanner.findCandidateComponents(NOTATION_DOMAINS)) {
========
        for (BeanDefinition repositoryDef : scanner.findCandidateComponents(NOTATION_CRUD_API)) {
>>>>>>>> 8263f89 (MP-92 :goal_net: Catch: 응답 구조 상태코드 수정):src/test/java/kr/modusplant/api/crud/common/postprocessor/MockCrudRepositoryBeanFactoryPostProcessor.java
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