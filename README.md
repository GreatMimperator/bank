Запускать сначала `docker compose` (для postgres - см. `docker-scripts/postgres/init.sql`), затем рабочий профиль `prod`:
```shell
docker-compose up
./gradlew bootRun --args='--spring.profiles.active=prod'
```
Есть также профиль `dev` с неустаревающими токенами
Для запуска тестов:
```shell
docker-compose up
./gradlew test
```
