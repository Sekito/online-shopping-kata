FROM openjdk:21-jdk-slim
LABEL authors="BENTOURI El Mehdi"
EXPOSE 8080
ADD target/online-shopping-kata.jar online-shopping-kata.jar

ENTRYPOINT ["java", "-jar","/online-shopping-kata.jar"]