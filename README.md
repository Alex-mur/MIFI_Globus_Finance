# Веб-приложение для учёта финансов

Проект для дисциплины "Проектный практикум".
Магистратура МИФИ. Специальность "Программная инженерия". 2-й семестр.

## О проекте

Кейс 2. Глобус IT
Веб-приложение для учёта финансов

- Задача:
  создать веб-интерфейс для ввода и просмотра доходов и расходов, а также генерации простых отчётов.
- Технологии:
  Java HTTP Server или Python для бэкенда. Фронтенд можно делать опционально, используйте в этом случае решение на
  HTML/CSS.
- Особенности:
  необходимо реализовать формы, таблицы, фильтрацию, суммирование и базовую визуализацию.
- Ожидаемый результат:
  веб-прототип с базовыми функциями учёта и отчётности; инструкция по запуску.

---

# 💸 Globus Finance API

REST API для управления банковскими транзакциями, генерации отчетов и получения истории изменений.  
Проект разработан на Java 17 с использованием Spring Boot 3 и PostgreSQL.

## 🚀 Быстрый старт

### 🛠️ Требования

- Java 17+
- Maven 3.8+
- PostgreSQL 15+
- Docker (опционально)

### 📦 Установка и запуск

1. **Клонируйте репозиторий:**
   ```bash
   git clone https://github.com/Alex-mur/MIFI_Globus_Finance.git
   cd globus-finance
    ```

2. **Настройте базу данных (PostgreSQL):**

* Создайте БД: `finance`
* Укажите параметры подключения в `.env` или `application.yaml`:

  ```yaml
  spring.datasource.url=jdbc:postgresql://localhost:5432/finance
  spring.datasource.username=your_username
  spring.datasource.password=your_password
  ```

3. **Соберите и запустите проект:**

   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

4. **Веб версия (UI) доступна по адресу:**

   ```
   http://localhost:8080
   ```

5. **Swagger-документация будет доступна по адресу:**

   ```
   http://localhost:8080/swagger-ui/index.html
   ```

---

## 🧪 Тестирование

Вы можете воспользоваться [web-версией](http://localhost:8080) для ознакомления.   
</br>Вы можете протестировать API с помощью Postman.   
Импортируйте коллекцию:   
📁 [Postman Collection](./postman/FinanceApp.json)

Или ознакомьтесь с отчетом о тестировании   
🧾 [Отчёт о тестировании](https://github.com/Alex-mur/MIFI_Globus_Finance/wiki/Отчёт-о-тестировании)

---

## 🔐 Аутентификация

Аутентификация реализована через JWT.

1. Зарегистрируйтесь: `POST /api/auth/register`
2. Войдите: `POST /api/auth/login` → получите `token`
3. Используйте токен:

   ```
   Authorization: Bearer <ваш токен>
   ```

---

## 🧾 Основные возможности

| Метод  | Endpoint                           | Назначение                   |
|--------|------------------------------------|------------------------------|
| POST   | `/api/auth/register`               | Регистрация пользователя     |
| POST   | `/api/auth/login`                  | Аутентификация и JWT         |
| POST   | `/api/transactions`                | Создание транзакции          |
| GET    | `/api/transactions/{id}`           | Получение транзакции по ID   |
| PUT    | `/api/transactions/{id}`           | Обновление транзакции        |
| DELETE | `/api/transactions/{id}`           | Удаление транзакции          |
| POST   | `/api/transactions/filter`         | Фильтрация транзакций        |
| POST   | `/api/transactions/history`        | История изменений транзакции |
| POST   | `/api/reports/generate`            | Создание PDF-отчёта          |
| GET    | `/api/reports/{filename}/download` | Загрузка отчёта              |

---

## 📚 Технологический стек

* Java 17+, Spring Boot 3.x
* Spring Security, JWT
* PostgreSQL 15+
* Hibernate JPA
* OpenPDF, XHTMLRenderer (отчёты PDF)
* Maven

---

## 📁 Структура проекта

```
globus-finance/
│
├── postman/                        # Коллекции Postman для тестирования API
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── it/globus/finance/
│   │   │       ├── component/                 # Инициализация тестовых данных
│   │   │       ├── configuration/             # Конфигурации (Security, Swagger, JWT)
│   │   │       │   └── exception/             # Обработка исключений
│   │   │       ├── logging/                   # Фильтры логирования
│   │   │       ├── model/
│   │   │       │   ├── entity/                # JPA-сущности
│   │   │       │   ├── repo/                  # Репозитории Spring Data
│   │   │       │   ├── CategoryType.java      # Enum категорий
│   │   │       │   └── Role.java              # Enum ролей
│   │   │       ├── rest/
│   │   │       │   ├── controller/            # REST-контроллеры
│   │   │       │   ├── dto/                   # DTO-классы
│   │   │       │   └── service/               # Слой бизнес-логики
│   │   │       └── FinanceApplication.java    # Главный класс Spring Boot
│   │
│   │   └── resources/
│   │       ├── fonts/                         # Шрифты
│   │       ├── static/                        # Статика
│   │       ├── templates/                     # HTML-шаблоны для рендеринга
│   │       └── application.properties         # Конфигурационный файл
│
│   └── test/
│       └── java/it/globus/finance/
│           └── FinanceApplicationTests.java   # Тесты приложения
```

---

## 👨‍💻 Авторы

* Бондаренко Артём
  </br> tg: [@whysobluebunny](https://t.me/whysobluebunny) github: [whysobluebunny](https://github.com/whysobluebunny)
* Кирюхин Андрей
  </br> tg: [@andrew\_kir](https://t.me/andrew_kir) github: [andrewkir](https://github.com/andrewkir)
* Никоненко Лев
  </br> tg: [@w3bpr0g3r](https://t.me/w3bpr0g3r) github: [lewebcode](https://github.com/lewebcode)
* Алексей Муравьевём
  </br> tg: [@AlexeyMuraviev](https://t.me/AlexeyMuraviev) github: [Alex-mur](https://github.com/Alex-mur)

---



  
