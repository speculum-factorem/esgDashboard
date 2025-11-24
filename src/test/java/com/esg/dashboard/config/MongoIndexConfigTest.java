package com.esg.dashboard.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexOperations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MongoIndexConfigTest {

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private IndexOperations indexOperations;

    @Test
    void testCreateIndexes() {
        when(mongoTemplate.indexOps(any(Class.class))).thenReturn(indexOperations);
        when(indexOperations.ensureIndex(any())).thenReturn("index_name");

        MongoIndexConfig config = new MongoIndexConfig(mongoTemplate);
        config.createIndexes();

        // Проверяем, что индексы были созданы
        verify(mongoTemplate, atLeastOnce()).indexOps(any(Class.class));
        verify(indexOperations, atLeastOnce()).ensureIndex(any());
    }
}

