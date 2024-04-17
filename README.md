 ## Agroex API

 ## Tags: 
- #web
- #mobile
- #back

## Task
Create an API for agricultural exchange, which provides functionality on buy\sell\auction flow, provides authorization, filters, notifications, reports.

## Used technologies
1. **Java**
2. **Spring Boot / Spring WebFlux / Spring Cloud / Spring Security**
3. **Gradle**
4. **Sping Data JPA, Hibernate Criteria API**
5. **Postgresql**
6. **Flyway**
7. **MinIO**
8. **AWS EC2, AWS RDS, AWS ELB, AWS S3, AWS Cognito**
9. **Apache POI**
10. **Lombok**
11. **MapStruct**
12. **Prometheus / Grafana**

## Setup
1. **Clone this repository**
2. **Install docker**
3. **Set up .env file with your enviroment variables**
4. **Run docker compose up to start application**

## Endpoints:

### User Controller

* #### GET /users/{id}: *Returns user by its id.*

* #### PUT /users/{id}: *Updates existing user by its id. All fields must be sent in the request body.*

* #### DELETE /users/{id}: *Deletes user by its id.*

* #### GET /users: *Returns all created users.*

* #### POST /users: *Creates a new user. Request body must contain creation user entity*

### Lot Controller

* #### GET /lots/{id} * Returns lot by its id*

* #### PUT /lots/{id} *Updates existing lot by its id. All fields must be sent in the request body*

* #### DELETE /lots/{id}: *Deletes lot by its id.*

* #### GET /lots *Returns all lots.*

* #### POST /lots: *Creates a new lot. Request body must contain lot creation entity.*

## Data Models

### UserDTO
#### Properties:
- **id**: integer (int64), example: 1
- **username**: string, example: "123_user_321"
- **email**: string, example: "user_test@gmail.com"
- **password**: string (write-only), example: "user-test123321"
- **phoneNumber**: string, example: "+375448901238"
- **creationDate**: string (date-time, read-only), example: "2024-01-26T15:52:08.8822866Z"
- **emailVerified**: boolean (read-only), example: true

### LocationDTO
#### Properties:
- **id**: integer (int64)
- **countryId**: integer (int64), example: 1
- **region**: string, example: "Minsk region"
- **latitude**: string, example: "53.902284"
- **longitude**: string, example: "27.561831"

### LotDTO
#### Properties:
- **id**: integer (int64), example: 1
- **title**: string, example: "Braeburn Apples"
- **description**: string, example: "Fresh Central Otago Braeburn Apples delivered direct to your door. The best flavoured apple, great for eating. Long lasting."
- **variety**: string, example: "alwa"
- **size**: string, example: "70+"
- **packaging**: string, example: "buckets"
- **enbledByAdmin**: boolean, example: false
- **quantity**: number (float), example: 123.2
- **price**: number (float), example: 1299
- **currency**: string, example: "USD"
- **creationDate**: string (date-time, read-only), example: "2024-01-26T15:52:08.8822866Z"
- **expirationDate**: string (date-time), example: "2024-01-26T15:52:08.8822866Z"
- **productCategoryId**: integer (int64), example: 1
- **lotType**: string (writable only on creation), example: "sell"
- **userId**: integer (int64), example: 1
- **location**: [Location](#locationdto)
- **tags**: array of [Tag](#tagdto)
- **images**: ["a90430c0-fd8f-4da7-821d-34834e7fe6ed.jpg", "c330de51-76dc-4922-97b8-a62afae5fac7.jpg"]

### ProductCategoryDTO
#### Properties:
- **id**: integer (int64), example: 2
- **title**: string, example: "Fruits"
- **parentId**: integer (int64), example: 1

### TagDTO
#### Properties:
- **id**: integer (int64), example: 1
- **title**: string, example: "fresh"

### CountryDTO
#### Properties:
- **id**: integer (int64), example: 1
- **title**: string, example: "fresh"


