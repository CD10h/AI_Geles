package lt.aigen.geles.interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    RequestLoggerInterceptor requestLoggerInterceptor;
    @Autowired
    AuthenticationInterceptor authenticationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestLoggerInterceptor).excludePathPatterns("/static/**");
        registry.addInterceptor(authenticationInterceptor).excludePathPatterns("/static/**");
    }
}
