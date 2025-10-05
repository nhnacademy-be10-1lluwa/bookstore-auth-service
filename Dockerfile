FROM eclipse-temurin:21
ARG JAR_FILE=./target/1lluwa-auth-service.jar
COPY ${JAR_FILE} /app/1lluwa-auth-service.jar

ENV SERVER_PORT=10303
EXPOSE 10303

ENTRYPOINT ["java", "-jar", "/app/1lluwa-auth-service.jar", "--server.port=${SERVER_PORT}"]
