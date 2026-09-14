# Currency Tracker — трекер курсов валют и крипты

Веб-сервис: узнаёшь текущий курс fiat/крипто-пары и ставишь алерт на достижение цены — при срабатывании приходит письмо на почту.

---

## Архитектура

```
Браузер (статический HTML/CSS/JS)
        │  HTTP
        ▼
┌────────────────────────────────────────────┐
│  Spring Boot 4  (порт 8080)                │
│                                            │
│  PriceController      AlertController      │
│         │                    │             │
│         ▼                    ▼             │
│    PriceService        AlertsRepository    │
│         │                    ▲             │
│         │             AlertSchedulerService│
│         ▼                    │             │
│  ┌──────────────┐            │             │
│  │ PriceClient  │◄───┤ NotificationService │
│  │ ├ CoinGecko  │            │             │
│  │ └ Frankfurter│    MonitoringService     │
│  └──────────────┘    (email при сбоях API) │
└───────────────────┬────────────────────────┘
                    │ JDBC / JPA
                    ▼
              PostgreSQL 16
```

Оба сервиса (приложение и БД) поднимаются через Docker Compose.

---

## Стек технологий

| Слой          | Технологии                                                   |
| ------------- |--------------------------------------------------------------|
| Бэкенд        | Java 21, Spring Boot 4 (Web, Validation, Scheduling)         |
| База данных   | PostgreSQL 16, Flyway                                        |
| Внешние API   | CoinGecko, Frankfurter                                       |
| Почта         | Spring Mail / JavaMailSender (SMTP)                          |
| Мониторинг    | Spring Boot Actuator, SLF4J/Logback, email-алерты об ошибках внешних API |
| Тесты         | JUnit 5, Mockito, Testcontainers (Postgres), MockMvc         |
| Фронтенд      | Статический HTML/CSS/JS                                      |
| Деплой        | Docker, Docker Compose                                       |

---

## Быстрый старт (Docker)

### 1. Клонировать репозиторий

```bash
git clone <repository-url>
cd currency_tracker
```

### 2. Заполнить переменные окружения

```bash
cp .env.example .env
```

Открыть `.env` и как минимум задать:

```
MAIL_HOST=
MAIL_USERNAME=
MAIL_PASSWORD=
MAIL_FROM=
MONITORING_EMAIL=

COIN_GECKO_API_KEY=
```


### 3. Запустить

```bash
docker compose up --build
```

- Веб-интерфейс / API: <http://localhost:8080>
- Health check: <http://localhost:8080/actuator/health>

---

## API

| Метод | Путь          | Описание                                   |
| ----- | ------------- | ------------------------------------------- |
| GET   | `/api/price`  | Текущий курс пары `base`/`quote` для `type` (`FIAT`/`CRYPTO`) |
| POST  | `/api/alerts` | Создать алерт на достижение цены             |
| GET   | `/api/alerts` | Список активных алертов                      |


### Тесты бэкенда

```bash
mvn test
```

Интеграционные тесты используют Testcontainers (требуется запущенный Docker).



## Просмотр логов

```bash
# логи приложения
docker compose logs -f app

# все сервисы
docker compose logs -f
```

Логи также пишутся в файл `logs/app.log` внутри контейнера приложения (volume `app_logs`).

---

## Мониторинг ошибок

Любая ошибка при обращении к внешним API (CoinGecko, Frankfurter) логируется и, в зависимости от типа ошибки, отправляется письмом на `MONITORING_EMAIL` (дедупликация одинаковых ошибок — раз в 10 минут, см. `MonitoringService`). Ошибки уровня приложения из планировщика алертов (`AlertSchedulerService`) репортятся туда же.