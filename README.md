# Project Documentation

## Database
This project uses **H2** in-memory as the database. The database will be operational when the application starts.

## Back-End
The back-end application runs on port **8080**.

## Security
The application is secured using **JWT (JSON Web Tokens)**

### Initial Customer Credentials
At the beginning, a customer is created with the following credentials:
- **Email:** bentouri.elmehdi@example.com
- **Password:** 123456

## REST API

### Implement HATEOAS
Done via Spring HATEOAS

### API Documentation
- **Swagger:** You can access the API documentation via Swagger at the following address: http://localhost:8080/swagger-ui/index.html#.

### API Client
- **Postman:** The file `OnlineShopingKata.postman_collection` contains the API collection for Postman.

## Persistence
- Propose a data persistence solution : Postgres
- Propose a cache solution : Redis

### Stream
- Propose a data streaming solution : Kafka
- Propose a solution for consuming and/or producing events : Kafka Producer API / Consumers - Rabbit MQ


## CI/CD
- Propose a CI/CD system for the project : Jenkins 
- Propose End-to-End tests for your application : JUnit or TestNG

## Packaging
### Docker
- To build the image : docker build -t online-shopping-kata .
- the image is pushed to dockerhub : docker.io/sekito/online-shopping-kata
  
### Kubernetes
Deploy your application in a pod : kubectl apply -f k8s/online-shopping-kata-deployment.yaml


## Build the Projet:
-mvn clean install -mvn spring-boot:run OU java -jar target/online-shopping-kata.jar



