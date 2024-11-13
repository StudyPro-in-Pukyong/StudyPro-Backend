# 베이스 이미지 설정
FROM openjdk:17-jdk-slim as app

# 작업 디렉토리 생성
WORKDIR /app

# .jar 파일 복사
COPY build/libs/*.jar app.jar

# PostgreSQL 설치
RUN apt-get update && apt-get install -y postgresql

# .env 파일 복사
COPY .env /app/.env
#RUN export $(cat .env | xargs)
#RUN export $(grep -v '^#' /app/.env | xargs)

# wait-for-it 스크립트 복사
COPY wait-for-it.sh /app/wait-for-it.sh
RUN chmod +x /app/wait-for-it.sh

# Docker 빌드 시 전달받은 환경 변수 설정
ARG STUDYPRO_DB_USER
ARG STUDYPRO_DB_PASSWORD
ARG STUDYPRO_DB_NAME

ENV POSTGRES_USER=${STUDYPRO_DB_USER}
ENV POSTGRES_PASSWORD=${STUDYPRO_DB_PASSWORD}
ENV POSTGRES_DB=${STUDYPRO_DB_NAME}

# 빌드 시 환경 변수 출력 (확인용)
RUN echo "!! Database User: $POSTGRES_USER"
RUN echo "!! Database Password: $POSTGRES_PASSWORD"
RUN echo "!! Database Name: $POSTGRES_DB"

# 포트 노출
EXPOSE 8080

# 멀티 프로세스 실행 (Java 애플리케이션과 PostgreSQL)
CMD ["sh", "-c", "service postgresql start && java -jar app.jar"]
