package com.esg.dashboard.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Configuration
public class ApiVersionConfig {

    @Bean
    public FilterRegistrationBean<ApiVersionFilter> apiVersionFilter() {
        FilterRegistrationBean<ApiVersionFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ApiVersionFilter());
        registrationBean.addUrlPatterns("/api/*");
        log.info("API version filter configured");
        return registrationBean;
    }

    public static class ApiVersionFilter extends OncePerRequestFilter {

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                        FilterChain filterChain) throws ServletException, IOException {

            String path = request.getRequestURI();
            if (path.startsWith("/api/") && !path.startsWith("/api/v1/")) {
                log.warn("Deprecated API version accessed: {}", path);
                response.addHeader("X-API-Deprecated", "true");
                response.addHeader("X-API-Current-Version", "v1");
            }

            filterChain.doFilter(request, response);
        }
    }
}