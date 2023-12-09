FROM openjdk:17-jdk-alpine

# 인자 설정 - Jar_File, Environment
ARG JAR_FILE=build/libs/*.jar
ARG ENVIRONMENT

# jar 파일 복제
COPY ${JAR_FILE} config.jar

# 환경 변수 ENVIRONMENT의 값으로 스프링 프로파일을 활성화한다.
ENV SPRING_PROFILES_ACTIVE=${ENVIRONMENT}

# 실행 명령어
ENTRYPOINT ["java","-jar","config.jar"]