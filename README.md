## Описание
Этот проект представляет собой эмулятор кошелька с двумя модулями:
- Wallet Module💰: Модуль для управления кошельком, включая переводы и просмотр баланса.
- ATM Module🏧: Модуль для пополнения баланса, использующий Kafka для передачи информации о пополнении в первый модуль и обновления данных в Redis.
## Стек технологий
- Kotlin
- Spring Boot
- Maven
- Apache Kafka: Для передачи сообщений о пополнении баланса.
- Redis: Для хранения и кэширования данных о балансах.
## Краткое описание логики модулей:
- Wallet Module💰: Обрабатывает запросы на переводы и просмотр баланса. Принимает сообщения из Kafka о пополнении баланса и обновляет Redis.
- ATM Module🏧: Отправляет сообщения о пополнении баланса в Kafka, которые затем обрабатываются первым модулем.
## Переменные окружения:
- ```${JDBC_URL}```: URL для подключения к базе данных PostgreSQL. Если переменная не задана, по умолчанию используется значение jdbc:postgresql://postgres:5432/reactive.
- ```${JDBC_USERNAME}```: Имя пользователя для подключения к базе данных PostgreSQL. По умолчанию — postgres.
- ```${JDBC_PASSWORD}```: Пароль для подключения к базе данных PostgreSQL. По умолчанию — gbhfns.
- ```${KAFKA_BOOTSTRAP}```: Адрес и порт Kafka брокера. По умолчанию — broker:29092.
- ```${REDIS_HOST}```: Хост для подключения к Redis. По умолчанию — redis.
- ```${REDIS_PORT}```: Порт для подключения к Redis. По умолчанию — 6379.
- ```${ENCODER_SECRET}```: Секретный ключ для шифрования паролей. По умолчанию — FZK2DZ82odqS13e8aENggaMbb_fAkl-nJL4AEVBX43g.
- ```${ENCODER_ITERATION}```: Количество итераций, используемых при шифровании паролей. По умолчанию — 64.
- ```${ENCODER_KEY_LENGTH}```: Длина ключа шифрования паролей. По умолчанию — 256.
- ```${JWT_SECRET}```: Секретный ключ для подписи JWT токенов. По умолчанию — 8506876256f3e572b83d7d5a0b86b503c7b7e68c430d69d5307e865d8619d05f.
- ```${JWT_EXPIRATION}```: Время жизни JWT токена в секундах. По умолчанию — 3600.
- ```${JWT_ISSUER}```: Издатель JWT токена. По умолчанию — bank.
- ```${AMOUNT_OF_RETRIES}```: Количество повторных попыток при возникновении ошибки. По умолчанию — 3.
- ```${TIMEOUT_IN_SECONDS}```: Время ожидания ответа в секундах. По умолчанию — 2.
## Как запустить:
1. Склонируйте приложение: ```git clone https://github.com/3eight70/Reactive-bank-account.git```  
2. Перейдите в корень приложения ```cd Reactive-bank-account```
3. Сбилдите проект ```mvn clean install```
4. Поднимайте контейнеры ```docker-compose up -d``` (Есть вероятность, что контейнер с Kafka упадет, т.к в момент запуска zookeeper не был полностью поднят, поэтому возможно придется запустить кафку ручками ```docker-compose up -d kafka```)