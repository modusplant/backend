package kr.modusplant.infrastructure.security;

import kr.modusplant.infrastructure.security.config.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Every {@code @RestController} endpoint must carry an explicit authorization decision: either a
 * method-security annotation ({@code @PreAuthorize}/{@code @PostAuthorize}/{@code @Secured}) or an
 * entry in {@link SecurityConfig#PUBLIC_ENDPOINTS} or {@link SecurityConfig#LOCAL_PUBLIC_ENDPOINTS}.
 * This is a pure reflection/classpath-scan test (no Spring context, so {@code @Profile} is not
 * evaluated) that fails the build the moment a new endpoint is added without either, instead of
 * silently falling back to whatever {@code SecurityConfig.defaultChain()}'s default happens to be.
 */
class EndpointAuthorizationCoverageTest {

    private static final String BASE_PACKAGE = "kr.modusplant";
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Test
    @DisplayName("모든 REST 컨트롤러 엔드포인트에 명시적 인가 결정이 존재하는지 확인")
    void testEndpointAuthorizationCoverage_givenEveryRestControllerEndpoint_willHaveNoUncoveredEndpoint() {
        List<String> uncovered = new ArrayList<>();

        for (Class<?> controller : findRestControllers()) {
            String basePath = classBasePath(controller);
            for (Method method : controller.getDeclaredMethods()) {
                for (EndpointMapping mapping : mappingsOf(method)) {
                    if (hasMethodSecurityAnnotation(controller, method)) {
                        continue;
                    }
                    String fullPath = joinPaths(basePath, mapping.path());
                    if (!isPublic(mapping.httpMethod(), fullPath)) {
                        uncovered.add(controller.getSimpleName() + "#" + method.getName()
                                + " " + mapping.httpMethod() + " " + fullPath);
                    }
                }
            }
        }

        assertThat(uncovered)
                .as("These endpoints have neither a @PreAuthorize/@PostAuthorize/@Secured annotation "
                        + "nor an entry in SecurityConfig.PUBLIC_ENDPOINTS or LOCAL_PUBLIC_ENDPOINTS. "
                        + "Add one or the other.")
                .isEmpty();
    }

    private static Set<Class<?>> findRestControllers() {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(RestController.class));

        Set<Class<?>> classes = new LinkedHashSet<>();
        scanner.findCandidateComponents(BASE_PACKAGE).forEach(candidate -> {
            String className = candidate.getBeanClassName();
            if (className == null || className.contains(".fixture.")) {
                // Test-only controller fixtures are not part of the production authorization surface.
                return;
            }
            try {
                classes.add(Class.forName(className));
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException(e);
            }
        });
        return classes;
    }

    private static String classBasePath(Class<?> controller) {
        RequestMapping requestMapping = controller.getAnnotation(RequestMapping.class);
        if (requestMapping == null) {
            return "";
        }
        String[] values = requestMapping.value().length > 0 ? requestMapping.value() : requestMapping.path();
        return values.length > 0 ? values[0] : "";
    }

    private record EndpointMapping(HttpMethod httpMethod, String path) {
    }

    private static List<EndpointMapping> mappingsOf(Method method) {
        List<EndpointMapping> mappings = new ArrayList<>();
        addMappings(mappings, method, GetMapping.class, HttpMethod.GET, GetMapping::value, GetMapping::path);
        addMappings(mappings, method, PostMapping.class, HttpMethod.POST, PostMapping::value, PostMapping::path);
        addMappings(mappings, method, PutMapping.class, HttpMethod.PUT, PutMapping::value, PutMapping::path);
        addMappings(mappings, method, DeleteMapping.class, HttpMethod.DELETE, DeleteMapping::value, DeleteMapping::path);
        addMappings(mappings, method, PatchMapping.class, HttpMethod.PATCH, PatchMapping::value, PatchMapping::path);

        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
        if (requestMapping != null) {
            String[] values = requestMapping.value().length > 0 ? requestMapping.value() : requestMapping.path();
            String[] paths = values.length > 0 ? values : new String[]{""};
            RequestMethod[] methods = requestMapping.method();
            if (methods.length == 0) {
                for (String path : paths) {
                    for (HttpMethod httpMethod : HttpMethod.values()) {
                        mappings.add(new EndpointMapping(httpMethod, path));
                    }
                }
            } else {
                for (RequestMethod requestMethod : methods) {
                    for (String path : paths) {
                        mappings.add(new EndpointMapping(HttpMethod.valueOf(requestMethod.name()), path));
                    }
                }
            }
        }
        return mappings;
    }

    private static <A extends Annotation> void addMappings(
            List<EndpointMapping> mappings, Method method, Class<A> annotationType, HttpMethod httpMethod,
            Function<A, String[]> valueExtractor, Function<A, String[]> pathExtractor) {
        A annotation = method.getAnnotation(annotationType);
        if (annotation == null) {
            return;
        }
        String[] values = valueExtractor.apply(annotation);
        String[] paths = values.length > 0 ? values : pathExtractor.apply(annotation);
        if (paths.length == 0) {
            mappings.add(new EndpointMapping(httpMethod, ""));
        } else {
            for (String path : paths) {
                mappings.add(new EndpointMapping(httpMethod, path));
            }
        }
    }

    private static boolean hasMethodSecurityAnnotation(Class<?> controller, Method method) {
        return hasAny(method) || hasAny(controller);
    }

    private static boolean hasAny(AnnotatedElement element) {
        return element.isAnnotationPresent(PreAuthorize.class)
                || element.isAnnotationPresent(PostAuthorize.class)
                || element.isAnnotationPresent(Secured.class);
    }

    private static boolean isPublic(HttpMethod httpMethod, String path) {
        return matchesAny(SecurityConfig.PUBLIC_ENDPOINTS.get(httpMethod), path)
                || matchesAny(SecurityConfig.LOCAL_PUBLIC_ENDPOINTS.get(httpMethod), path);
    }

    private static boolean matchesAny(List<String> patterns, String path) {
        return patterns != null && patterns.stream().anyMatch(pattern -> PATH_MATCHER.match(pattern, path));
    }

    private static String joinPaths(String basePath, String methodPath) {
        String combined = basePath + methodPath;
        if (combined.isEmpty()) {
            return "/";
        }
        return combined.startsWith("/") ? combined : "/" + combined;
    }
}
