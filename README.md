# ESG Dashboard - Платформа мониторинга ESG-показателей в реальном времени

![Java](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green)
![MongoDB](https://img.shields.io/badge/MongoDB-7.0-green)
![Redis](https://img.shields.io/badge/Redis-7.2-red)
![Docker](https://img.shields.io/badge/Docker-Enabled-blue)
![Gradle](https://img.shields.io/badge/Gradle-8.4-yellow)

Комплексная платформа для мониторинга ESG (Environmental, Social, Governance) показателей в реальном времени. Система предоставляет возможность отслеживать влияние их инвестиций и кредитов на ESG-рейтинги.

## Основные возможности

###  Real-Time ESG Мониторинг
- **Live Carbon Footprint Tracking** - отслеживание углеродного следа в реальном времени
- **Social Impact Score Dashboard** - мониторинг социального воздействия
- **Governance Metrics** - показатели корпоративного управления
- **WebSocket Updates** - мгновенные обновления без перезагрузки страницы

###  Аналитика и Отчетность
- **Portfolio Aggregation** - расчет совокупных ESG показателей инвестиционных портфелей
- **Historical Data Analysis** - анализ исторических изменений рейтингов
- **Sector Comparison** - сравнительный анализ по отраслям
- **Real-Time Rankings** - динамическое ранжирование компаний

###  Бизнес-Функциональность
- **Company Management** - управление компаниями и их ESG показателями
- **Portfolio Management** - создание и управление инвестиционными портфелями
- **Rating Updates** - система обновления ESG рейтингов
- **Data Export** - экспорт данных в JSON/CSV форматах

##  Технологический Стек

### Backend & Framework
- **Java 17** - основной язык разработки
- **Spring Boot 3.2** - основной фреймворк
- **Spring Web MVC** - REST API
- **Spring WebSocket** - real-time коммуникация
- **Spring Data MongoDB** - работа с данными
- **Spring Validation** - валидация данных
- **Spring Actuator** - мониторинг приложения

### Data Layer
- **MongoDB 7.0** - основное хранилище данных
- **Redis 7.2** - кэширование и pub/sub
- **Redis Sorted Sets** - для ранжирования компаний
- **Flexible Schema** - поддержка различных ESG метрик

### Real-Time Features
- **WebSocket/STOMP** - двусторонняя коммуникация
- **Redis Pub/Sub** - рассылка обновлений клиентам
- **Event-Driven Architecture** - событийная модель данных

### Monitoring & Observability
- **Micrometer** - сбор метрик приложения
- **Prometheus** - хранение метрик
- **Grafana** - визуализация данных
- **Spring Boot Actuator** - health checks и метрики
- **Custom Health Indicators** - мониторинг БД и кэша

### Development & Quality
- **JUnit 5** - unit тестирование
- **Mockito** - мокирование зависимостей
- **Testcontainers** - интеграционное тестирование
- **JaCoCo** - покрытие кода тестами
- **Lombok** - уменьшение boilerplate кода

### API & Documentation
- **OpenAPI 3** - спецификация API
- **Swagger UI** - интерактивная документация
- **RESTful Design** - REST архитектура
- **JSON Serialization** - Jackson для работы с JSON

## 💡 Ключевые Технические Особенности

### Real-Time Архитектура
```java
// WebSocket конфигурация для real-time обновлений
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }
}
```

## Кэширование и Производительность
- **Многоуровневое кэширование** - Redis + in-memory cache
- **Автоматическое обновление рейтингов** - Redis Sorted Sets
- **TTL настройки** - гибкое управление временем жизни кэша
- **Cache warming** - предзагрузка популярных данных

## Обработка Данных
```java
// Сервис для работы с ESG рейтингами
@Service
@Slf4j
public class CompanyService {
    public void updateESGRating(String companyId, ESGRating newRating) {
        // Валидация и обновление рейтинга
        // Кэширование результатов
        // Real-time уведомления через WebSocket
    }
}
```

## Валидация и Безопасность
- **Bean Validation** - аннотации валидации полей
- **Custom Validators** - кастомные валидаторы для ESG данных
- **Global Exception Handling** - централизованная обработка ошибок
- **CORS Configuration** - настройки кросс-доменных запросов

## Мониторинг и Метрики
- **Custom Metrics** - бизнес-метрики ESG показателей
- **Health Checks** - мониторинг состояния БД и сервисов
- **Performance Monitoring** - метрики производительности
- **Audit Logging** - логирование действий пользователей

## Event-Driven Модель
```java
// Событие обновления ESG рейтинга
@Data
@Builder
public class ESGUpdateEvent {
    private String companyId;
    private ESGRating previousRating;
    private ESGRating newRating;
    private EventType eventType;
    private LocalDateTime timestamp;
}
```

## Функциональные Модули
### Core Module
- Управление компаниями и ESG данными
- Расчет и обновление рейтингов
- Базовые операции CRUD

### Portfolio Module
- Создание и управление портфелями
- Агрегация ESG показателей
- Weighted scoring calculations

### Analytics Module
- Секторальная аналитика
- Исторические тренды
- Сравнительные отчеты

### Real-Time Module
- WebSocket соединения
- Redis Pub/Sub messaging
- Live dashboard updates

### Export Module
- JSON/CSV экспорт данных
- Генерация отчетов
- Bulk operations

## Модели данных
### Company Entity
```java
@Document(collection = "companies")
public class Company {
    private String companyId;
    private String name;
    private String sector;
    private ESGRating currentRating;
    private Map<String, Object> additionalMetrics;
}
```

### ESGRating Value Object
```java
public class ESGRating {
    private Double overallScore;
    private Double environmentalScore;
    private Double socialScore;
    private Double governanceScore;
    private Double carbonFootprint;
    private Double socialImpactScore;
    private String ratingGrade;
}
```

### Portfolio Aggregate
```java
@Document(collection = "portfolios")
public class Portfolio {
    private String portfolioId;
    private String portfolioName;
    private List<PortfolioItem> items;
    private PortfolioAggregate aggregateScores;
}
```

## Development Features

### Feature Flags
```java
@Service
public class FeatureFlagService {
    public boolean isEnabled(String feature) {
        return featureFlags.getOrDefault(feature, false);
    }
}
```

### Configuration Profiles
- **Development** - подробное логирование, H2 база
- **Production** - оптимизированные настройки, мониторинг
- **Docker** - контейнеризованная конфигурация

### Testing Strategy
- **Unit Tests** - бизнес-логика и сервисы
- **Integration Tests** - API endpoints и база данных
- **WebSocket Tests** - real-time функциональность

## Расширяемость

### Поддержка Новых Метрик
Гибкая схема MongoDB позволяет добавлять новые ESG метрики без изменения схемы

### Plugin Architecture
Возможность подключения внешних источников ESG данных

### Microservices Ready
Архитектура готова к разделению на микросервисы при необходимости