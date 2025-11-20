package com.esg.dashboard.cache;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CacheManagerServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private CacheManagerService cacheManagerService;

    @Test
    void evictCompanyCache_ShouldDeleteKey() {
        // Arrange
        when(redisTemplate.delete(anyString())).thenReturn(true);

        // Act
        cacheManagerService.evictCompanyCache("TEST001");

        // Assert
        verify(redisTemplate, times(1)).delete("company:TEST001");
    }

    @Test
    void clearAllCache_ShouldDeleteAllKeys() {
        // Arrange
        when(redisTemplate.keys(anyString())).thenReturn(java.util.Set.of("key1", "key2"));

        // Act
        cacheManagerService.clearAllCache();

        // Assert
        verify(redisTemplate, times(1)).delete(anySet());
    }
}