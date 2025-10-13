FROM eclipse-temurin:21
ARG JAR_FILE=./target/1lluwa-auth-service.jar
COPY ${JAR_FILE} /app/1lluwa-auth-service.jar

WORKDIR /app

EXPOSE 10303 10304

ENV SERVER_PORT=10303
ENV APP_NAME=auth-service-v1

ENTRYPOINT ["sh", "-c", "java -jar /app/1lluwa-auth-service.jar --server.port=${SERVER_PORT} --spring.application.name=${APP_NAME}"]
