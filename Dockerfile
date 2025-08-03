# Dockerfile

# 베이스 이미지 (Java 21용)
FROM eclipse-temurin:21-jdk

# 작업 디렉토리
WORKDIR /app

# JAR 복사
COPY target/1lluwa-auth-service.jar app.jar

# 포트 노출 (서비스 포트에 맞게 조정)
EXPOSE 8080

# 컨테이너 실행 시 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]
