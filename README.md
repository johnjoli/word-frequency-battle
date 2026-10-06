# Word Frequency Battle

[![CI](https://github.com/johnjoli/word-frequency-battle/actions/workflows/ci.yml/badge.svg)](https://github.com/johnjoli/word-frequency-battle/actions/workflows/ci.yml)
![Java](https://img.shields.io/badge/Java-21-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)

Сервис частотного анализа текста: загружаешь `.txt` — получаешь статистику по словам, историю обработок и топ слов по всей базе.

## Что внутри

- **Загрузка и обработка файла** с валидацией расширения и размера, стримингом строк и настраиваемым минимумом длины слова
- **История обработок**: фильтры по имени файла и диапазону дат, пагинация, сортировка
- **Топ слов и агрегированная статистика** — считается на стороне БД, а не в памяти приложения
- **Оптимистичные блокировки** через `@Version` и обработчик `409 Conflict` при параллельном изменении
- **Единый формат ошибок** на `ProblemDetail` (RFC 7807) + валидация параметров запроса
- **Версионирование API**: `/api/v1/...`
- **Типизированная конфигурация** через `@ConfigurationProperties` с валидацией значений
- **Маппинг DTO** через MapStruct — сущности не утекают в ответы API
- **Наблюдаемость**: Spring Boot Actuator (`health`, `info`, `metrics`) и собственный `HealthIndicator`
- **Swagger UI** для ручной проверки всех эндпоинтов
- **Схема БД через Flyway**, `ddl-auto: validate`
- **Тесты**: MockMvc на контроллеры, unit/service, интеграционные с PostgreSQL в Testcontainers

## Стек

Java 21 · Spring Boot 3.1 · Spring Web · Spring Data JPA · Flyway · PostgreSQL 16 · MapStruct · springdoc-openapi · Spring Boot Actuator · JUnit 5 · Mockito · Testcontainers · Docker · Maven

## Структура

```text
com.bootcamp
├── config       # WordCounterProperties — типизированные настройки
├── controller   # WordCounterController, GlobalExceptionHandler
├── dto          # ответы API (сущности наружу не отдаются)
├── entity       # WordCountResult, WordCount
├── health       # WordCounterHealthIndicator
├── mapper       # MapStruct-мапперы
├── repository   # Spring Data JPA + агрегирующие запросы
└── service      # WordCounter, WordCountService
```

## Быстрый старт

Нужны Docker и JDK 21.

```bash
git clone https://github.com/johnjoli/word-frequency-battle.git
cd word-frequency-battle

docker compose up -d
mvn spring-boot:run
```

- Swagger UI: http://localhost:8080/swagger-ui.html
- Простая веб-форма загрузки: http://localhost:8080/
- Actuator: http://localhost:8080/actuator/health

Параметры подключения к БД при необходимости переопределяются переменными окружения:

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/wordcounter \
SPRING_DATASOURCE_USERNAME=postgres \
SPRING_DATASOURCE_PASSWORD=postgres \
mvn spring-boot:run
```

## Эндпоинты

| Метод | Путь | Описание |
|---|---|---|
| POST | `/api/v1/words/upload` | загрузить `.txt` (multipart, поле `file`) |
| GET | `/api/v1/words/history` | история: `fileName`, `from`, `to`, `page`, `size` |
| GET | `/api/v1/words/history/{id}` | результат по id |
| DELETE | `/api/v1/words/history/{id}` | удалить результат |
| GET | `/api/v1/words/top` | топ слов, `limit` (по умолчанию 10, максимум 100) |
| GET | `/api/v1/words/stats` | агрегированная статистика |

Пример:

```bash
curl -F "file=@sample.txt" http://localhost:8080/api/v1/words/upload
curl "http://localhost:8080/api/v1/words/top?limit=5"
curl "http://localhost:8080/api/v1/words/history?page=0&size=10"
```

## Настройки

[`application.properties`](src/main/resources/application.properties):

| Свойство | Смысл |
|---|---|
| `word-counter.default-top-limit` | размер топа по умолчанию |
| `word-counter.max-top-limit` | верхняя граница `limit` |
| `word-counter.min-word-length` | слова короче игнорируются |
| `word-counter.processing.count-numbers` | считать ли числа словами |
| `word-counter.processing.allowed-extension` | разрешённое расширение файла |

## Миграции

[`src/main/resources/db/migration`](src/main/resources/db/migration):

- `V1__init_schema.sql` — таблицы результатов и слов
- `V2__add_version_column.sql` — колонка версии для оптимистичных блокировок
- `V3__refactor_word_counts.sql` — суррогатный ключ `id` вместо составного

Применённые миграции не переписываются: изменения схемы — только новой версией.

## Тесты

```bash
mvn -B clean verify
```

Интеграционные тесты поднимают PostgreSQL через Testcontainers (нужен Docker).

## Roadmap

- [ ] Docker-образ в multi-stage сборке
- [ ] Метрики по количеству обработок через Micrometer
- [ ] Ограничение размера загружаемого файла и rate limiting
