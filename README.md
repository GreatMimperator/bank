Запускать сначала `docker compose` (для postgres - см. `docker-scripts/postgres/init.sql`), затем рабочий профиль `prod`:
```shell
docker-compose up  
mvn spring-boot:run -Pprod
```
Есть также профиль `dev` с неустаревающими токенами  
Для запуска тестов:
```shell
docker-compose up
mvn test -Ptest
```
