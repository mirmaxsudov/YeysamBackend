package uz.yeysam.Yeysam.runner;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import uz.yeysam.Yeysam.anotations.OpenAuth;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@SuppressWarnings("all")
public class EndpointAnnotationVerifier implements ApplicationRunner {

    private final RequestMappingHandlerMapping handlerMapping;
    private final ConfigurableApplicationContext context;

    public EndpointAnnotationVerifier(RequestMappingHandlerMapping handlerMapping, ConfigurableApplicationContext context) {
        this.handlerMapping = handlerMapping;
        this.context = context;
    }

    @Override
    public void run(ApplicationArguments args) {
        List<String> violations = new ArrayList<>();

        handlerMapping.getHandlerMethods().forEach((info, handler) -> {
            Class<?> beanType = handler.getBeanType();
            String pkg = beanType.getPackageName();

            if (!pkg.startsWith("uz.resume.resume.controller"))
                return;

            Method method = handler.getMethod();
            boolean hasSecurity = AnnotationUtils.findAnnotation(method, PreAuthorize.class) != null
                    || AnnotationUtils.findAnnotation(method, OpenAuth.class) != null;

            if (!hasSecurity) {
                Set<String> patterns = Optional.ofNullable(info.getPatternsCondition())
                        .map(pc -> pc.getPatterns())
                        .orElseGet(() -> {
                            return info.getPathPatternsCondition()
                                    .getPatternValues()
                                    .stream()
                                    .collect(Collectors.toSet());
                        });

                String pattern = patterns.stream().findFirst().orElse("<?>");
                violations.add(method.getDeclaringClass().getSimpleName()
                        + "#" + method.getName()
                        + " [" + pattern + "]");
            }
        });

        if (!violations.isEmpty()) {
            violations.forEach(v ->
                    System.err.println("⚠️  Missing @PreAuthorize/@OpenAuth on: " + v)
            );
            int exitCode = SpringApplication.exit(context, () -> 0);
            System.exit(exitCode);
        }
    }
}
