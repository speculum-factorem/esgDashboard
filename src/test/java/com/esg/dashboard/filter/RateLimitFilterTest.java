package com.esg.dashboard.filter;

import com.esg.dashboard.config.RateLimitConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RateLimitFilterTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private RateLimitConfig rateLimitConfig;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private RateLimitFilter rateLimitFilter;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(rateLimitConfig.isRateLimitEnabled()).thenReturn(true);
        when(rateLimitConfig.getRequestsPerMinute()).thenReturn(60);
        when(rateLimitConfig.getRequestsPerHour()).thenReturn(1000);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        rateLimitFilter = new RateLimitFilter(redisTemplate, rateLimitConfig);
    }

    @Test
    void testRateLimitDisabled() throws Exception {
        when(rateLimitConfig.isRateLimitEnabled()).thenReturn(false);

        rateLimitFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(valueOperations, never()).increment(anyString());
    }

    @Test
    void testRateLimitWithinLimit() throws Exception {
        when(valueOperations.increment(anyString())).thenReturn(1L);
        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));

        rateLimitFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(response).setHeader(eq("X-RateLimit-Remaining-Minute"), anyString());
        verify(response).setHeader(eq("X-RateLimit-Limit-Minute"), anyString());
    }

    @Test
    void testRateLimitExceeded() throws Exception {
        when(valueOperations.increment(anyString())).thenReturn(61L); // Превышен лимит
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        rateLimitFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, never()).doFilter(request, response);
        verify(response).setStatus(429); // TOO_MANY_REQUESTS
        verify(response).setHeader(eq("Retry-After"), anyString());
    }
}

