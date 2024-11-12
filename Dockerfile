# 1. 베이스 이미지 설정
FROM openjdk:17-jdk-slim as app

# 2. 작업 디렉토리 생성
WORKDIR /app

# 3. .jar 파일 복사
COPY build/libs/*.jar app.jar

# 4. PostgreSQL 설치
RUN apt-get update && apt-get install -y postgresql

# 5. 환경 변수 설정
ENV POSTGRES_USER=${STUDYPRO_DB_USER}
ENV POSTGRES_PASSWORD=${STUDYPRO_DB_PASSWORD}
ENV POSTGRES_DB=${STUDYPRO_DB_NAME}

# 6. 포트 노출
EXPOSE 8080

# 7. 멀티 프로세스 실행 (Java 애플리케이션과 PostgreSQL)
CMD ["sh", "-c", "service postgresql start && java -jar app.jar"]
