# OnLog_Post_Server

## 🌐 프로젝트 개요

해당 프로젝트는 Onlog 서비스의 핵심 서버로서, 주요 기능인 게시글 및 댓글 관리, 사용자 인증 등의 대부분 서비스를 담당하고 있습니다.

## 🛠️ 프로젝트 개발 환경

프로젝트는 아래 환경에서 개발되었습니다.

> OS: macOS Sonoma   
> IDE: Intellij IDEA  
> Java 17

## ✅ 프로젝트 실행

해당 프로젝트를 추가로 개발 혹은 실행시켜보고 싶으신 경우 아래의 절차에 따라 진행해주세요

#### 1. `secret.yml` 생성

```commandline
cd ./src/main/resources
touch secret.yml
```

#### 2. `secret.yml` 작성

```text
spring:
  datasource:
    writer:
      driver-class-name: {WRITER_DATABASE_DRIVER_CLASS_NAME}
      jdbc-url: {WRITER_DATABASE_URL}
      username: {WRITER_DATABASE_USERNAME}
      password: {WRITER_DATABASE_PASSWORD}

    reader:
      driver-class-name: {SLAVE_DATABASE_DRIVER_CLASS_NAME}
      jdbc-url: {SLAVE_DATABASE_URL}
      username: {SLAVE_DATABASE_USERNAME}
      password: {SLAVE_DATABASE_PASSWORD}

  security:
    oauth2:
      client:
        registration:
          kakao:
            client-id: {KAKAO_CLIENT_ID}

jwt:
  secret-key: {SECRET_KEY}

```

#### 3. 프로젝트 실행

```commandline
./gradlew bootrun
```

**참고) 프로젝트가 실행 중인 환경에서 아래 URL을 통해 API 명세서를 확인할 수 있습니다**

```commandline
http://localhost:8080/swagger-ui/index.html
```