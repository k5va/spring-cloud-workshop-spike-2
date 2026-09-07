# Демо интернет-магазин

Учебный проект из двух независимых Spring Boot микросервисов:

- **product-service** — владеет каталогом товаров (read-only), порт `8081` снаружи.
- **order-service** — оформляет заказы, синхронно обращаясь к `product-service` по HTTP, порт `8082` снаружи.

Подробности домена — `CONTEXT.md`, архитектурные решения — `docs/adr/`.

## Требования

- Docker + Docker Compose (BuildKit включён по умолчанию в современных версиях Docker Desktop).
- [`just`](https://github.com/casey/just) для запуска задач ниже.

## Задачи `justfile`

| Команда      | Что делает                                                                                     |
|--------------|-------------------------------------------------------------------------------------------------|
| `just up`    | Собирает образы и поднимает `product-service` и `order-service`, дожидаясь их готовности (actuator health). |
| `just smoke` | Поднимает инфраструктуру (`just up`) и выполняет `POST /orders`, проверяя, что ответ — `201 Created`. |
| `just down`  | Останавливает и удаляет инфраструктуру.                                                         |

Типичный локальный прогон:

```sh
just smoke
just down
```

## Запуск сервисов локально без Docker

Каждый сервис — независимый Maven-модуль со своим Maven Wrapper:

```sh
cd product-service && ./mvnw spring-boot:run   # слушает :8080
cd order-service && ./mvnw spring-boot:run     # слушает :8080, ждёт product-service на PRODUCT_SERVICE_BASE_URL
```

По умолчанию `order-service` обращается к `product-service` по `http://localhost:8081` (см. `product-service.base-url` в `application.yml`) — при локальном запуске переопределите порт `product-service` через `--server.port=8081`, либо задайте `PRODUCT_SERVICE_BASE_URL`.

## Тесты

```sh
cd product-service && ./mvnw test
cd order-service && ./mvnw test
```
