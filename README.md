# Hotel Service

REST-API сервис для управления информацией об отелях.

## Архитектура

Проект состоит из следующих модулей:

```
hotel-service/
├── hotel-ejb-api/     # API модуль с интерфейсами EJB и DTO
├── hotel-ejb/         # Реализация EJB с JPA entities и DAO
├── hotel-ear/         # Enterprise Application Archive для WildFly
├── hotel-rest/        # Quarkus REST API сервис
└── docker/            # Docker конфигурации
```

### Технологический стек

- **Backend (EJB)**: WildFly 30, Jakarta EE 10, JPA/Hibernate
- **REST API**: Quarkus 3.6
- **База данных**: MariaDB 10.11
- **Сборка**: Maven 3.9+
- **Контейнеризация**: Docker, Docker Compose

## Быстрый старт

### Предварительные требования

- JDK 17+
- Maven 3.9+
- Docker и Docker Compose
- WildFly 30 (для локальной разработки без Docker)

### Запуск с Docker Compose

1. Скачайте MariaDB JDBC драйвер:
```bash
curl -L -o docker/wildfly/mariadb-java-client-3.3.2.jar \
  https://repo1.maven.org/maven2/org/mariadb/jdbc/mariadb-java-client/3.3.2/mariadb-java-client-3.3.2.jar
```

2. Соберите проект:
```bash
mvn clean package -DskipTests
```

3. Запустите все сервисы:
```bash
docker-compose up --build
```

4. Сервисы будут доступны:
   - REST API: http://localhost:8081
   - Swagger UI: http://localhost:8081/swagger-ui
   - WildFly Admin: http://localhost:9990 (admin/admin123)
   - MariaDB: localhost:3306 (hotel/hotel123)

### Локальная разработка

1. Запустите только MariaDB:
```bash
docker-compose -f docker-compose.dev.yml up -d
```

2. Настройте WildFly локально (см. раздел "Настройка WildFly")

3. Задеплойте EAR:
```bash
mvn clean package -DskipTests
cp hotel-ear/target/hotel-app.ear $WILDFLY_HOME/standalone/deployments/
```

4. Запустите Quarkus в dev режиме:
```bash
cd hotel-rest
mvn quarkus:dev
```

## API Endpoints

### Получить список отелей
```http
GET /api/v1/hotels?page=0&size=10&sort=asc
```

**Параметры:**
- `page` - номер страницы (по умолчанию 0)
- `size` - размер страницы (по умолчанию 10, макс. 100)
- `sort` - порядок сортировки по названию (asc/desc)

**Ответ:**
```json
{
  "content": [
    {
      "id": 1,
      "name": "Grand Hotel Moscow",
      "address": {
        "id": 1,
        "postalCode": "101000",
        "city": "Москва",
        "street": "Тверская улица",
        "building": "15"
      },
      "category": "FIVE_STARS",
      "notes": "Роскошный отель в центре Москвы"
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 5,
  "totalPages": 1,
  "hasNext": false,
  "hasPrevious": false
}
```

### Получить отель по ID
```http
GET /api/v1/hotels/{id}
```

### Создать отель
```http
POST /api/v1/hotels
Content-Type: application/json

{
  "name": "Новый Отель",
  "address": {
    "postalCode": "123456",
    "city": "Москва",
    "street": "Новая улица",
    "building": "1"
  },
  "category": "FOUR_STARS",
  "notes": "Описание отеля"
}
```

### Обновить отель
```http
PUT /api/v1/hotels/{id}
Content-Type: application/json

{
  "name": "Обновлённое название",
  "address": {
    "postalCode": "123456",
    "city": "Москва",
    "street": "Улица",
    "building": "1"
  },
  "category": "FIVE_STARS"
}
```

### Удалить отель
```http
DELETE /api/v1/hotels/{id}
```

## Настройка WildFly

### Добавление MariaDB драйвера

```bash
# Создайте директорию модуля
mkdir -p $WILDFLY_HOME/modules/system/layers/base/org/mariadb/jdbc/main

# Скопируйте драйвер
cp mariadb-java-client-3.3.2.jar $WILDFLY_HOME/modules/system/layers/base/org/mariadb/jdbc/main/

# Скопируйте module.xml
cp docker/wildfly/module.xml $WILDFLY_HOME/modules/system/layers/base/org/mariadb/jdbc/main/
```

### Настройка через CLI

```bash
$WILDFLY_HOME/bin/jboss-cli.sh --connect

# Добавить драйвер
/subsystem=datasources/jdbc-driver=mariadb:add(driver-name=mariadb,driver-module-name=org.mariadb.jdbc,driver-class-name=org.mariadb.jdbc.Driver)

# Добавить datasource
/subsystem=datasources/data-source=HotelDS:add(jndi-name=java:jboss/datasources/HotelDS,driver-name=mariadb,connection-url=jdbc:mariadb://localhost:3306/hoteldb,user-name=hotel,password=hotel123)

reload
```

## Тестирование

### Запуск тестов
```bash
mvn test
```

### Примеры curl запросов

```bash
# Получить список отелей
curl http://localhost:8081/api/v1/hotels

# Получить отель по ID
curl http://localhost:8081/api/v1/hotels/1

# Создать отель
curl -X POST http://localhost:8081/api/v1/hotels \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Hotel","address":{"city":"Moscow","street":"Test St","building":"1"},"category":"THREE_STARS"}'

# Удалить отель
curl -X DELETE http://localhost:8081/api/v1/hotels/1
```
## Лицензия

MIT License
