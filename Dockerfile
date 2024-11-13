# 1. 베이스 이미지 설정
FROM openjdk:17-jdk-slim

# 2. 작업 디렉토리 생성
WORKDIR /app

# 3. 미리 빌드된 .jar 파일 복사
COPY build/libs/*.jar app.jar

# 3. 환경 변수 파일 복사
ARG ENV_FILE
COPY ${ENV_FILE} .env

# 4. .env 파일에서 환경 변수 로드
RUN export $(cat .env | xargs)

# 6. 포트 노출
EXPOSE 8080

# 7. 애플리케이션 실행
CMD ["java", "-jar", "app.jar"]
