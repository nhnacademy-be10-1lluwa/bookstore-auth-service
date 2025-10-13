# ✅ 1. Java 21 베이스 이미지 사용
FROM eclipse-temurin:21-jdk

# ✅ 2. JAR 복사
ARG JAR_FILE=./target/1lluwa-auth-service.jar
COPY ${JAR_FILE} /app/1lluwa-auth-service.jar

# ✅ 3. 기본 환경 설정
WORKDIR /app
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=10303
ENV CONFIG_URL=https://book1lluwa.store/config/

# ✅ 4. expose는 v1/v2 포트 모두
EXPOSE 10303 10304

# ✅ 5. 실행 명령 — 반드시 sh -c 로 감싸야 env 변수가 치환됨
ENTRYPOINT ["sh", "-c", "java -jar /app/1lluwa-auth-service.jar \
    --server.port=${SERVER_PORT} \
    --spring.profiles.active=${SPRING_PROFILES_ACTIVE} \
    --spring.config.import=optional:configserver:${CONFIG_URL}"]