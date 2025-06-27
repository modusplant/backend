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
import org.springframework.stereotype.Repository;
import org.springframework.util.ClassUtils;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static kr.modusplant.domains.common.vo.Reference.NOTATION_DOMAINS;
import static kr.modusplant.global.vo.Reference.NOTATION_GLOBAL;

@NonNullApi
public class MockDomainsClientBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().hasAnnotation(Repository.class.getName());
            }
        };
        scanner.addIncludeFilter(new RegexPatternTypeFilter(Pattern.compile("^.*Client$")));
        ClassLoader classLoader = this.getClass().getClassLoader();

        for (String reference: List.of(NOTATION_DOMAINS, NOTATION_GLOBAL)) {
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