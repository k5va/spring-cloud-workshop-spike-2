# Демо интернет-магазин

Учебный проект из трёх независимых Spring Boot микросервисов:

- **discovery-server** — Eureka-сервер, реестр экземпляров сервисов, dashboard на порту `8761`.
- **product-service** — владеет каталогом товаров (read-only), обнаруживается через discovery, порт наружу не пробрасывается.
- **order-service** — оформляет заказы, синхронно обращаясь к `product-service` по HTTP через discovery и клиентскую балансировку нагрузки, порт `8082` снаружи.

Подробности домена — `CONTEXT.md`, архитектурные решения — `docs/adr/`.

## Требования

- Docker + Docker Compose (BuildKit включён по умолчанию в современных версиях Docker Desktop).
- [`just`](https://github.com/casey/just) для запуска задач ниже.

## Задачи `justfile`

| Команда            | Что делает                                                                                                          |
|--------------------|-----------------------------------------------------------------------------------------------------------------------|
| `just up`          | Собирает образы и поднимает весь стек (`discovery-server`, `product-service`, `order-service`), дожидаясь готовности (actuator health). |
| `just up-scaled N` | То же самое, но с N экземплярами `product-service` — для демонстрации масштабирования и балансировки нагрузки.        |
| `just smoke`       | Поднимает инфраструктуру (`just up`) и выполняет `POST /orders`, проверяя, что ответ — `201 Created`.                  |
| `just down`        | Останавливает и удаляет инфраструктуру.                                                                              |

Типичный локальный прогон:

```sh
just smoke
just down
```

Демонстрация масштабирования и балансировки нагрузки:

```sh
just up-scaled 3
# Eureka dashboard: http://localhost:8761 — все 3 экземпляра product-service должны быть UP
# несколько последовательных запросов, чтобы увидеть балансировку между экземплярами:
for i in 1 2 3 4 5 6; do curl -s -X POST http://localhost:8082/orders \
    -H "Content-Type: application/json" -d '{"productId":1,"quantity":1}' > /dev/null; done
docker compose logs product-service   # запросы должны попадать в разные контейнеры
```

## Eureka dashboard

После `just up` (или `just up-scaled N`) реестр сервисов доступен на [http://localhost:8761](http://localhost:8761) — там видно все зарегистрированные экземпляры `order-service` и `product-service`.

## Запуск сервисов локально без Docker

Каждый сервис — независимый Maven-модуль со своим Maven Wrapper. Локальный запуск без Docker требует отдельно поднятого `discovery-server` (иначе `order-service`/`product-service` не смогут зарегистрироваться и найти друг друга), поэтому основной путь запуска — через `docker compose` (см. выше).

```sh
cd discovery-server && ./mvnw spring-boot:run   # слушает :8761, dashboard на http://localhost:8761
cd product-service && ./mvnw spring-boot:run    # слушает :8080, регистрируется в discovery-server
cd order-service && ./mvnw spring-boot:run      # слушает :8080, находит product-service через discovery-server
```

## Тесты

```sh
cd discovery-server && ./mvnw test
cd product-service && ./mvnw test
cd order-service && ./mvnw test
```
