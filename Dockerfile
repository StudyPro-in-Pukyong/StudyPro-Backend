# 1. 베이스 이미지 설정
FROM openjdk:17-jdk-slim

# 2. 작업 디렉토리 생성
WORKDIR /app

# 3. 미리 빌드된 .jar 파일 복사
COPY build/libs/*.jar app.jar

# 6. 포트 노출
EXPOSE 8080

# 7. 애플리케이션 실행
CMD ["java", "-jar", "app.jar"]
