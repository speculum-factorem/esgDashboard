# ESG Dashboard

![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![MongoDB](https://img.shields.io/badge/MongoDB-4EA94B?style=for-the-badge&logo=mongodb&logoColor=white)
![Redis](https://img.shields.io/badge/redis-%23DD0031.svg?&style=for-the-badge&logo=redis&logoColor=white)
![Gradle](https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2CA5E0?style=for-the-badge&logo=docker&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-2088FF?style=for-the-badge&logo=github-actions&logoColor=white)

Платформа для мониторинга ESG (Environmental, Social, Governance) показателей в реальном времени. Система предоставляет возможность финансовым институтам отслеживать влияние их инвестиций и кредитов на ESG-рейтинги компаний.

## Содержание

- [Описание проекта](#описание-проекта)
- [Архитектура](#архитектура)
- [Сущности и их взаимодействие](#сущности-и-их-взаимодействие)
- [Технологический стек](#технологический-стек)
- [Установка и запуск](#установка-и-запуск)
- [Конфигурация](#конфигурация)
- [API документация](#api-документация)
- [Безопасность](#безопасность)
- [Мониторинг](#мониторинг)
- [Разработка](#разработка)
- [CI/CD](#cicd)
- [Развертывание](#развертывание)

## Описание проекта

ESG Dashboard - это комплексная платформа для управления и мониторинга ESG показателей компаний. Система позволяет:

- Управлять данными компаний и их ESG рейтингами
- Создавать и управлять инвестиционными портфелями
- Отслеживать изменения ESG показателей в реальном времени
- Анализировать исторические данные и тренды
- Экспортировать данные для отчетности
- Получать уведомления об изменениях рейтингов

### Основные возможности

**Real-Time Мониторинг**
- Отслеживание углеродного следа в реальном времени
- Мониторинг социального воздействия
- Показатели корпоративного управления
- WebSocket обновления без перезагрузки страницы

**Аналитика и Отчетность**
- Расчет совокупных ESG показателей инвестиционных портфелей
- Анализ исторических изменений рейтингов
- Сравнительный анализ по отраслям
- Динамическое ранжирование компаний

**Управление данными**
- CRUD операции для компаний и портфелей
- Система обновления ESG рейтингов
- Экспорт данных в JSON/CSV форматах
- Batch операции для массовой обработки

## Архитектура

### Общая архитектура

Система построена на основе многослойной архитектуры с четким разделением ответственности:

```
┌─────────────────────────────────────────────────────────┐
│                    Presentation Layer                   │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐   │
│  │ REST API     │  │ WebSocket    │  │ Swagger UI   │   │
│  │ Controllers  │  │ Controller   │  │              │   │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘   │
└─────────┼─────────────────┼─────────────────┼────────--─┘
          │                 │                 │
┌─────────┼─────────────────┼─────────────────┼──────────┐
│         │                 │                 │          │
│  ┌──────▼───────┐  ┌──────▼─────-─┐  ┌──────▼──────┐   │
│  │   Service    │  │   Service    │  │   Service   │   │
│  │    Layer     │  │    Layer     │  │    Layer    │   │
│  └──────┬───────┘  └──────┬───────┘  └──────┬──────┘   │
│         │                 │                 │          │
│  ┌──────▼─────────────────▼───────────────-─▼───────┐  │
│  │            Event-Driven Architecture             │  │
│  │  ┌──────────────┐         ┌──────────────┐       │  │
│  │  │EventPublisher│────────▶│EventListener │       │  │
│  │  └──────────────┘         └──────┬───────┘       │  │
│  │                                  │               │  │
│  │                           ┌──────▼───────┐       │  │
│  │                           │RealTimeUpdate│       │  │
│  │                           │    Service   │       │  │
│  │                           └──────────────┘       │  │
│  └──────────────────────────────────────────────────┘  │
│                                                        │
└─────────┼──────────────────────────────────────────────┘
          │
┌─────────┼──────────────────────────────────────────────┐
│         │                                              │
│  ┌──────▼───────┐  ┌──────────────┐  ┌──────────────┐  │
│  │  Repository  │  │    Cache     │  │   External   │  │
│  │    Layer     │  │   Service    │  │    APIs      │  │
│  └──────┬───────┘  └───────┬──────┘  └──────────────┘  │
│         │                  │                           │
└─────────┼──────────────────┼───────────────────────────┘
          │                  │
┌─────────┼──────────────────┼───────────────────────────┐
│         │                  │                           │
│  ┌──────▼───────┐  ┌───────▼──────┐                    │
│  │   MongoDB    │  │    Redis     │                    │
│  │  Database    │  │    Cache     │                    │
│  └──────────────┘  └──────────────┘                    │
│                                                        │
└────────────────────────────────────────────────────────┘
```

### Слои архитектуры

**Presentation Layer (Слой представления)**
- REST API Controllers - обработка HTTP запросов
- WebSocket Controller - обработка WebSocket соединений
- Swagger UI - интерактивная документация API

**Service Layer (Слой бизнес-логики)**
- CompanyService - управление компаниями
- PortfolioService - управление портфелями
- AnalyticsService - аналитические расчеты
- HistoricalDataService - работа с историческими данными
- EventService - управление событиями

**Repository Layer (Слой доступа к данным)**
- CompanyRepository - доступ к данным компаний
- PortfolioRepository - доступ к данным портфелей
- HistoricalDataRepository - доступ к историческим данным

**Infrastructure Layer (Инфраструктурный слой)**
- MongoDB - основное хранилище данных
- Redis - кэширование и pub/sub
- External APIs - интеграция с внешними источниками данных

### Event-Driven Architecture

Система использует событийную архитектуру для развязки компонентов:

1. **EventPublisher** - публикует события при изменениях
2. **CompanyEventListener** - обрабатывает события компаний
3. **RealTimeUpdateService** - рассылает обновления через WebSocket
4. **Redis Pub/Sub** - обеспечивает распределенную рассылку событий

### Паттерны проектирования

- **Repository Pattern** - абстракция доступа к данным
- **Service Layer Pattern** - разделение бизнес-логики
- **Observer Pattern** - обработка событий
- **Strategy Pattern** - различные стратегии расчета ESG
- **Factory Pattern** - создание объектов данных

## Сущности и их взаимодействие

### Основные сущности

#### Company (Компания)

Основная сущность, представляющая компанию с ESG показателями.

```java
@Document(collection = "companies")
public class Company {
    private String id;                    // MongoDB ID
    private String companyId;             // Уникальный идентификатор компании
    private String name;                  // Название компании
    private String sector;                // Сектор экономики
    private String industry;              // Отрасль
    private ESGRating currentRating;      // Текущий ESG рейтинг
    private Map<String, Object> additionalMetrics;  // Дополнительные метрики
    private LocalDateTime createdAt;      // Дата создания
    private LocalDateTime updatedAt;      // Дата обновления
}
```

**Связи:**
- Содержит ESGRating (1:1)
- Связана с HistoricalData (1:N)
- Связана с PortfolioItem (N:M через Portfolio)

**Индексы:**
- `companyId` - уникальный индекс
- `sector` - индекс для фильтрации по секторам
- `currentRating.overallScore` - индекс для сортировки по рейтингу
- `currentRating.ranking` - индекс для ранжирования
- `updatedAt` - индекс для сортировки по дате обновления

#### ESGRating (ESG Рейтинг)

Значение объекта, содержащее ESG показатели компании.

```java
public class ESGRating {
    private Double overallScore;          // Общий ESG балл (0-100)
    private Double environmentalScore;    // Экологический балл (0-100)
    private Double socialScore;            // Социальный балл (0-100)
    private Double governanceScore;       // Балл управления (0-100)
    private Double carbonFootprint;        // Углеродный след (tCO2e)
    private Double socialImpactScore;      // Балл социального воздействия (0-100)
    private String ratingGrade;           // Буквенная оценка (AAA, AA, A, etc.)
    private LocalDateTime calculationDate; // Дата расчета
    private Integer ranking;              // Позиция в рейтинге
}
```

**Расчет рейтинга:**
- Overall Score = (Environmental + Social + Governance) / 3
- Rating Grade определяется на основе Overall Score:
  - AAA: 90-100
  - AA: 80-89
  - A: 70-79
  - BBB: 60-69
  - BB: 50-59
  - B: 40-49
  - CCC: 30-39
  - CC: 20-29
  - C: 10-19
  - D: 0-9

#### Portfolio (Портфель)

Инвестиционный портфель, содержащий набор компаний.

```java
@Document(collection = "portfolios")
public class Portfolio {
    private String id;                    // MongoDB ID
    private String portfolioId;           // Уникальный идентификатор портфеля
    private String portfolioName;         // Название портфеля
    private String clientId;              // Идентификатор клиента
    private String clientName;            // Название клиента
    private List<PortfolioItem> items;    // Список компаний в портфеле
    private PortfolioAggregate aggregateScores;  // Агрегированные ESG показатели
    private LocalDateTime createdAt;      // Дата создания
    private LocalDateTime updatedAt;       // Дата обновления
}
```

**Связи:**
- Содержит PortfolioItem (1:N)
- Содержит PortfolioAggregate (1:1)

**Индексы:**
- `portfolioId` - уникальный индекс
- `clientId` - индекс для фильтрации по клиентам
- `clientId + portfolioName` - составной индекс для поиска

#### PortfolioItem (Элемент портфеля)

Элемент портфеля, связывающий компанию с портфелем.

```java
public class PortfolioItem {
    private String companyId;             // Идентификатор компании
    private String companyName;           // Название компании
    private Double investmentAmount;      // Сумма инвестиций
    private Double weight;                // Вес в портфеле (процент)
    private ESGRating currentRating;      // Текущий ESG рейтинг компании
}
```

**Связи:**
- Связан с Company (N:1)
- Связан с Portfolio (N:1)

#### PortfolioAggregate (Агрегированные показатели)

Агрегированные ESG показатели портфеля.

```java
public class PortfolioAggregate {
    private Double totalEsgScore;         // Средневзвешенный общий ESG балл
    private Double carbonFootprint;       // Средневзвешенный углеродный след
    private Double socialImpactScore;     // Средневзвешенный балл социального воздействия
    private String averageRating;         // Средняя буквенная оценка
    private Integer totalCompanies;       // Количество компаний в портфеле
    private Double totalInvestment;       // Общая сумма инвестиций
}
```

**Расчет:**
- Средневзвешенные баллы рассчитываются на основе инвестиционных сумм
- Formula: Σ(Score_i × Investment_i) / Σ(Investment_i)

#### HistoricalData (Исторические данные)

Исторические записи ESG показателей.

```java
@Document(collection = "historical_data")
public class HistoricalData {
    private String id;                    // MongoDB ID
    private String companyId;              // Идентификатор компании
    private String dataType;               // Тип данных (ESG_RATING, CARBON_FOOTPRINT, etc.)
    private Map<String, Object> metrics;   // Метрики данных
    private LocalDateTime recordDate;      // Дата записи
    private LocalDateTime createdAt;       // Дата создания записи
    private DataQuality quality;           // Качество данных (HIGH, MEDIUM, LOW)
}
```

**Связи:**
- Связан с Company (N:1)

**Индексы:**
- `companyId + dataType + recordDate` - составной индекс для запросов по типу и дате
- `companyId + recordDate` - составной индекс для временных запросов
- `recordDate` - индекс для сортировки по дате

#### ESGUpdateEvent (Событие обновления ESG)

Событие, фиксирующее изменение ESG рейтинга.

```java
public class ESGUpdateEvent {
    private String eventId;                // Уникальный идентификатор события
    private String companyId;              // Идентификатор компании
    private String companyName;            // Название компании
    private ESGRating previousRating;      // Предыдущий рейтинг
    private ESGRating newRating;           // Новый рейтинг
    private EventType eventType;           // Тип события (RATING_UPDATE, RANKING_CHANGE, etc.)
    private LocalDateTime timestamp;       // Время события
    private String triggeredBy;            // Инициатор события
}
```

**Типы событий:**
- RATING_UPDATE - обновление рейтинга
- RANKING_CHANGE - изменение позиции в рейтинге
- PORTFOLIO_UPDATE - обновление портфеля
- MANUAL_UPDATE - ручное обновление

### Взаимодействие сущностей

#### Сценарий 1: Обновление ESG рейтинга компании

```
1. CompanyController.updateRating()
   │
   ├─▶ CompanyService.updateESGRating()
   │   │
   │   ├─▶ Валидация данных
   │   ├─▶ Загрузка компании из БД
   │   ├─▶ Обновление рейтинга
   │   ├─▶ Сохранение в MongoDB
   │   ├─▶ Обновление кэша Redis
   │   ├─▶ Обновление ранжирования (Redis Sorted Set)
   │   └─▶ EventPublisher.publishRatingUpdated()
   │       │
   │       ├─▶ CompanyEventListener.onRatingUpdated()
   │       │   │
   │       │   ├─▶ Сохранение в HistoricalData
   │       │   └─▶ Сохранение события в EventService
   │       │
   │       └─▶ RealTimeUpdateService.publishCompanyUpdate()
   │           │
   │           └─▶ Redis Pub/Sub → WebSocket клиенты
```

#### Сценарий 2: Создание портфеля

```
1. PortfolioController.createPortfolio()
   │
   ├─▶ PortfolioService.createPortfolio()
   │   │
   │   ├─▶ Валидация данных портфеля
   │   ├─▶ Batch загрузка компаний (CompanyService.batchLoadCompanies())
   │   │   │
   │   │   └─▶ Оптимизация N+1 проблемы
   │   │
   │   ├─▶ PortfolioService.calculateAggregateScores()
   │   │   │
   │   │   ├─▶ Расчет средневзвешенных ESG баллов
   │   │   └─▶ Создание PortfolioAggregate
   │   │
   │   ├─▶ Сохранение в MongoDB
   │   └─▶ Кэширование в Redis
```

#### Сценарий 3: Получение топ компаний с пагинацией

```
1. CompanyController.getTopRankedCompanies(page, size)
   │
   ├─▶ CompanyService.getTopRankedCompanies(Pageable)
   │   │
   │   ├─▶ Попытка получить из Redis Sorted Set
   │   │   │
   │   │   └─▶ Если найдено: возврат из кэша
   │   │
   │   └─▶ Если не найдено: CompanyRepository.findTopRankedCompanies(Pageable)
   │       │
   │       └─▶ MongoDB запрос с использованием индекса
   │           │
   │           └─▶ Возврат Page<Company>
```

#### Сценарий 4: Real-Time обновления через WebSocket

```
1. Клиент подключается к WebSocket
   │
   ├─▶ WebSocketController.connect()
   │
2. При обновлении рейтинга:
   │
   ├─▶ EventPublisher.publishRatingUpdated()
   │   │
   │   └─▶ RealTimeUpdateService.publishCompanyUpdate()
   │       │
   │       ├─▶ Публикация в Redis Pub/Sub
   │       │
   │       └─▶ WebSocketEventListener.onMessage()
   │           │
   │           └─▶ Рассылка всем подключенным клиентам
```

### Потоки данных

**Запись данных:**
```
Client → REST API → Controller → Service → Repository → MongoDB
                                    │
                                    ├─▶ Cache Service → Redis
                                    │
                                    └─▶ Event Publisher → Event Listener → Real-Time Service → WebSocket
```

**Чтение данных:**
```
Client → REST API → Controller → Service → Cache Service (Redis) → Repository (MongoDB)
```

**Real-Time обновления:**
```
Service → Event Publisher → Redis Pub/Sub → WebSocket Service → WebSocket Clients
```

## Технологический стек

### Backend Framework
- **Java 17** - основной язык разработки
- **Spring Boot 3.2** - основной фреймворк
- **Spring Web MVC** - REST API
- **Spring WebSocket** - real-time коммуникация
- **Spring Data MongoDB** - работа с MongoDB
- **Spring Data Redis** - работа с Redis
- **Spring Security** - безопасность и аутентификация
- **Spring Validation** - валидация данных
- **Spring Boot Actuator** - мониторинг приложения
- **Spring AOP** - аспектно-ориентированное программирование

### Data Layer
- **MongoDB 7.0** - основное хранилище данных
- **Redis 7.2** - кэширование и pub/sub
- **Redis Sorted Sets** - для ранжирования компаний

### Security
- **JWT (JSON Web Tokens)** - аутентификация
- **BCrypt** - хеширование паролей
- **Spring Security** - защита endpoints

### Real-Time Features
- **WebSocket/STOMP** - двусторонняя коммуникация
- **Redis Pub/Sub** - рассылка обновлений

### Monitoring & Observability
- **Micrometer** - сбор метрик приложения
- **Prometheus** - хранение метрик
- **Spring Boot Actuator** - health checks и метрики
- **Custom Health Indicators** - мониторинг БД и кэша
- **MDC Logging** - контекстное логирование

### Development & Quality
- **JUnit 5** - unit тестирование
- **Mockito** - мокирование зависимостей
- **Testcontainers** - интеграционное тестирование
- **Lombok** - уменьшение boilerplate кода
- **Gradle** - система сборки

### API & Documentation
- **OpenAPI 3** - спецификация API
- **Swagger UI** - интерактивная документация
- **RESTful Design** - REST архитектура

## Установка и запуск

### Требования

- Java 17 или выше
- Gradle 8.4 или выше
- MongoDB 7.0 или выше
- Redis 7.2 или выше
- Docker и Docker Compose (опционально)

### Локальная установка

1. Клонирование репозитория:
```bash
git clone https://github.com/speculum-factorem/esgDashboard.git
cd esgDashboard
```

2. Запуск MongoDB и Redis:
```bash
docker-compose -f src/docker/docker-compose.yml up -d mongodb redis
```

3. Настройка конфигурации:
```bash
cp src/main/resources/application.yml src/main/resources/application-local.yml
# Отредактируйте application-local.yml при необходимости
```

4. Сборка проекта:
```bash
./gradlew clean build
```

5. Запуск приложения:
```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

### Запуск с Docker

1. Сборка Docker образа:
```bash
docker build -f src/docker/Dockerfile -t esg-dashboard:latest .
```

2. Запуск всех сервисов:
```bash
cd src/docker
docker-compose up -d
```

3. Проверка статуса:
```bash
docker-compose ps
```

4. Просмотр логов:
```bash
docker-compose logs -f app
```

### Проверка работоспособности

После запуска приложение доступно по адресу:
- API: http://localhost:8080/api/v1
- Swagger UI: http://localhost:8080/swagger-ui.html
- Health Check: http://localhost:8080/actuator/health

## Конфигурация

### Профили Spring Boot

- **default** - конфигурация по умолчанию
- **local** - для локальной разработки
- **dev** - для development окружения
- **prod** - для production окружения
- **docker** - для Docker контейнеров
- **test** - для тестирования

### Основные параметры конфигурации

**application.yml:**
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/esg-dashboard
    redis:
      host: localhost
      port: 6379

app:
  jwt:
    secret: your-secret-key
    expiration: 86400000  # 24 часа
  rate-limit:
    enabled: true
    requests-per-minute: 60
    requests-per-hour: 1000
  cache:
    ttl-minutes: 30
```

### Переменные окружения

- `SPRING_PROFILES_ACTIVE` - активный профиль
- `SPRING_DATA_MONGODB_URI` - URI MongoDB
- `SPRING_REDIS_HOST` - хост Redis
- `APP_JWT_SECRET` - секретный ключ JWT
- `APP_RATE_LIMIT_ENABLED` - включение rate limiting

## API документация

### Базовый URL

```
http://localhost:8080/api/v1
```

### Аутентификация

Большинство endpoints требуют JWT токен в заголовке:
```
Authorization: Bearer <token>
```

Получение токена:
```bash
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin"
}
```

Ответ:
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400000
  }
}
```

### Основные endpoints

**Компании:**
- `GET /api/v1/companies/{companyId}` - получение компании
- `POST /api/v1/companies` - создание компании
- `PUT /api/v1/companies/{companyId}/rating` - обновление рейтинга
- `GET /api/v1/companies/top-ranked?page=0&size=20` - топ компаний (с пагинацией)
- `GET /api/v1/companies/sector/{sector}?page=0&size=20` - компании по сектору (с пагинацией)

**Портфели:**
- `GET /api/v1/portfolios/{portfolioId}` - получение портфеля
- `POST /api/v1/portfolios` - создание портфеля
- `GET /api/v1/portfolios/client/{clientId}?page=0&size=20` - портфели клиента (с пагинацией)

**Исторические данные:**
- `GET /api/v1/history/company/{companyId}?page=0&size=20` - история компании (с пагинацией)
- `GET /api/v1/history/company/{companyId}/type/{dataType}?page=0&size=20` - история по типу (с пагинацией)

**События:**
- `GET /api/v1/events?page=0&size=20` - последние события (с пагинацией)
- `GET /api/v1/events/company/{companyId}?page=0&size=20` - события компании (с пагинацией)

Полная документация доступна в Swagger UI: http://localhost:8080/swagger-ui.html

## Безопасность

### JWT Аутентификация

Система использует JWT токены для аутентификации. Токены содержат:
- Username
- Роли пользователя
- Время истечения

### Security Headers

Приложение автоматически добавляет следующие заголовки безопасности:
- `X-Content-Type-Options: nosniff`
- `X-Frame-Options: DENY`
- `Content-Security-Policy`
- `Strict-Transport-Security`
- `Cache-Control: no-cache, no-store, must-revalidate`

### Rate Limiting

Система ограничивает частоту запросов:
- По умолчанию: 60 запросов в минуту
- По умолчанию: 1000 запросов в час

При превышении лимита возвращается HTTP 429 Too Many Requests.

### CORS

Настроены разрешенные origins через конфигурацию. В production рекомендуется ограничить список разрешенных доменов.

## Мониторинг

### Health Checks

Endpoint для проверки здоровья приложения:
```
GET /actuator/health
```

Доступные проверки:
- Database connectivity
- Redis connectivity
- Application status

### Метрики

Endpoint для получения метрик:
```
GET /actuator/metrics
```

Prometheus метрики:
```
GET /actuator/prometheus
```

### Логирование

Логи структурированы с использованием MDC (Mapped Diagnostic Context):
- `traceId` - идентификатор запроса
- `companyId` - идентификатор компании
- `portfolioId` - идентификатор портфеля
- `operation` - тип операции

Логи сохраняются в:
- Консоль
- `logs/esg-dashboard.log`
- `logs/esg-dashboard-error.log` (только ошибки)

### Структура проекта

```
esg-dashboard/
├── src/
│   ├── main/
│   │   ├── java/com/esg/dashboard/
│   │   │   ├── controller/     # REST контроллеры
│   │   │   ├── service/        # Бизнес-логика
│   │   │   ├── repository/    # Доступ к данным
│   │   │   ├── model/          # Модели данных
│   │   │   ├── config/         # Конфигурация
│   │   │   ├── security/       # Безопасность
│   │   │   ├── exception/      # Обработка ошибок
│   │   │   └── ...
│   │   └── resources/
│   │       ├── application.yml # Конфигурация
│   │       └── ...
│   └── test/                   # Тесты
├── src/docker/                 # Docker файлы
├── .github/workflows/          # CI/CD
└── build.gradle               # Конфигурация сборки
```

### Запуск тестов

```bash
# Все тесты
./gradlew test

# Конкретный тест
./gradlew test --tests CompanyServiceTest

# С покрытием кода
./gradlew test jacocoTestReport
```

## CI/CD

### GitHub Actions

Проект использует GitHub Actions для автоматизации CI/CD.

**Workflow включает:**
1. **Test** - запуск всех тестов
2. **Build** - сборка приложения и Docker образа
3. **Security Scan** - сканирование на уязвимости
4. **Deploy** - развертывание в production (только для main ветки)

### Локальная проверка CI

Для проверки CI локально можно использовать [act](https://github.com/nektos/act).

## Развертывание

### Docker Compose

Для развертывания с Docker Compose:

```bash
cd src/docker
docker-compose up -d
```
