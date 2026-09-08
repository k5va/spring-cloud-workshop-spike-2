# Service discovery через Eureka и клиентская балансировка нагрузки

Продолжение [ADR-0001](0001-direct-http-no-discovery.md): дух документа сохраняется (OpenFeign, API Gateway и Resilience4j по-прежнему вне скоупа), но жёстко заданный адрес `product-service` заменяется на service discovery, чтобы можно было масштабировать `product-service` на несколько экземпляров.

## Контекст

`docker-compose up --scale product-service=N` при N>1 не работал: `order-service` всё равно обращался бы только по одному захардкоженному адресу (`product-service.base-url`), а проброс порта на хост конфликтовал бы между экземплярами. Нужен механизм, которым `order-service` находит все живые экземпляры `product-service` и распределяет запросы между ними.

## Решение

- **Eureka (Spring Cloud Netflix)**, а не Consul и не статический `spring.cloud.discovery.client.simple`. Eureka — самый распространённый выбор в экосистеме Spring Cloud и не требует поднимать дополнительную внешнюю систему (в отличие от Consul); статический список инстансов противоречит самой цели задачи — динамическому обнаружению экземпляров при масштабировании.
- Новый независимый Maven-модуль `discovery-server` (в стиле существующих `order-service`/`product-service`) — Eureka-сервер (`@EnableEurekaServer`), single-node, без peer awareness.
- `order-service` и `product-service` — оба Eureka-клиенты (симметрично, даже при том, что `order-service` пока никто не discover-ит — задел под будущий API Gateway).
- Клиентский вызов `order-service` → `product-service` остаётся на `RestClient` (не переходим на OpenFeign — см. ADR-0001): `RestClient.Builder` становится `@LoadBalanced`, базовый URL — логическое имя `http://product-service` вместо адреса. Spring Cloud LoadBalancer резолвит имя в реальный адрес одного из зарегистрированных в Eureka экземпляров.
- Отсутствие живых экземпляров `product-service` в реестре сознательно не обрабатывается как отдельный доменный случай (не добавляем новую ветку в `ProductClient`) — вне скоупа этой задачи.

## Настройки Eureka для демо-режима (осознанно не production-safe)

Ради живой демонстрации на воркшопе интервалы Eureka намеренно ускорены, а self-preservation выключен:

- `eureka.server.enable-self-preservation: false`, `eureka.server.eviction-interval-timer-in-ms: 5000` — иначе остановленные экземпляры зависали бы в реестре десятки секунд/минуты, что выглядело бы как "зависшее" демо.
- `eureka.instance.lease-renewal-interval-in-seconds: 5`, `eureka.instance.lease-expiration-duration-in-seconds: 10`, `eureka.client.registry-fetch-interval-seconds: 5` — быстрая сходимость реестра при старте/остановке экземпляра.

Это компромисс, неприемлемый в проде (Eureka в проде специально имеет консервативные дефолты, чтобы переживать сетевые разделения без массового вымывания инстансов из реестра — self-preservation существует именно для этого). Проект учебный и не идёт в прод с этими настройками; отдельного demo-профиля не заводим, чтобы не вводить конфигурационное ветвление.

## Вне скоупа (не меняется по сравнению с ADR-0001)

OpenFeign, API Gateway, Resilience4j (retry/circuit breaker) — остаются отдельными будущими задачами.

## Оформление

Discovery без load balancing и load balancing без discovery по отдельности не имеют смысла — вся работа оформлена одним issue (#9) и одним PR.
