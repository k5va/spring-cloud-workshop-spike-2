# Прямая HTTP-интеграция без service discovery и retry (первая итерация)

> **Superseded by [ADR-0002](0002-eureka-discovery-load-balancing.md)** в части service discovery: `order-service` больше не обращается к `product-service` по жёстко заданному в конфиге адресу — вместо этого используется Eureka и `@LoadBalanced RestClient`. Решение не использовать OpenFeign, API Gateway и Resilience4j остаётся в силе (см. ADR-0002).

`order-service` обращается к `product-service` синхронным HTTP-запросом через `RestClient`, по жёстко заданному в конфиге базовому URL (`product-service.base-url` / переменная окружения), с дефолтным таймаутом и без повторных попыток при ошибке. Eureka, API Gateway, OpenFeign и Resilience4j (retry/circuit breaker) сознательно не используются на этом шаге — они появятся отдельными задачами позже. Цель первой итерации — синхронная межсервисная интеграция как таковая, без наслаивания нескольких новых концепций Spring Cloud одновременно.
