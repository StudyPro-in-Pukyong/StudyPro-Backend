# 1. 베이스 이미지 설정
FROM openjdk:17-jdk-slim

# 2. 작업 디렉토리 생성
WORKDIR /app

# 3. 미리 빌드된 .jar 파일 복사
COPY build/libs/*.jar app.jar

# 4. 환경 변수 파일 복사
COPY .env /app/.env
#ARG ENV_FILE
#COPY ${ENV_FILE} .env

# 5. 포트 노출
EXPOSE 8080

# 6. 애플리케이션 실행
CMD ["java", "-jar", "app.jar"]
