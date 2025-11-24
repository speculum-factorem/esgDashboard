package com.esg.dashboard.config;

import com.esg.dashboard.interceptor.RequestLoggingInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final RequestLoggingInterceptor requestLoggingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestLoggingInterceptor)
                .addPathPatterns("/api/**");

        log.info("Request logging interceptor configured");
    }

    @Override
    public void addArgumentResolvers(java.util.List<org.springframework.web.method.support.HandlerMethodArgumentResolver> resolvers) {
        // Добавляем поддержку Pageable для пагинации
        org.springframework.data.web.PageableHandlerMethodArgumentResolver pageableResolver = 
                new org.springframework.data.web.PageableHandlerMethodArgumentResolver();
        pageableResolver.setMaxPageSize(100);
        pageableResolver.setFallbackPageable(org.springframework.data.domain.PageRequest.of(0, 20));
        resolvers.add(pageableResolver);
    }

    @Bean
    public FilterRegistrationBean<SecurityHeadersConfig> securityHeadersFilter() {
        FilterRegistrationBean<SecurityHeadersConfig> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new SecurityHeadersConfig());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);
        log.info("Security headers filter configured");
        return registrationBean;
    }
}