# My Box 챌린지

----

## 개요

네이버의 mybox 백엔드 서버를 구성하는 프로젝트 입니다.

----

## 실행 환경

- Java 11 version
- Spring boot 2.7.9
- Gradle
- Java AWS SDK 2.2.6
- Postgresql 12+ version

----

## 요구사항

- Ncloud Object Storage
  - 본 프로젝트는 Ncloud를 기반으로 구성되었습니다.
  - AWS의 s3와 동일하게 AWS SDK를 기반으로 사용됩니다.

----

## 설치 및 실행

> git clone https://github.com/deonii/mybox_challenge.git
>
> cd mybox_challenge
> 
> vi src/main/resources/application.yml

application.yml 에 아래 내용을 추가합니다.

```yaml
cloud:
  aws:
    credentials:
      access-key: {엑세스 키}
      secret-key: {시크릿 키}
    stack:
      auto: false
    region:
      static: ap-northeast-2 # 지역 정보 
    s3:
      endpoint: {aws 또는 ncloud의 엔드포인트}
      bucket: {버킷 이름}

# aws환경이 아닌 로컬환경에서 실행시 발생하는 에러 메세지 생략 
logging:
  level:
    com:
      amazonaws:
        util:
          EC2MetadataUtils: error

# DB 설정
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/{DB 이름}
    username: {DB 사용자 이름}
    password: {DB 사용자 비밀번호}
    driver-class-name: org.postgresql.Driver # 본인이 사용하는 DB 설정
```

실행을 위해서는 데이터베이스와 AWS 계정이 필요합니다.

설치 및 설정 후 실행합니다.

> ./gradlew bootRun

----

## 아키텍쳐

```
.
├── main
│   ├── java
│   │   └── deonii
│   │       └── mybox
│   │           ├── config # 설정 파일
│   │           ├── controller # 컨트롤러
│   │           ├── data # DB 관련
│   │           │   ├── dao # 트랜잭션을 통해 repository을 사용  
│   │           │   │   └── impl # dao의 구현체
│   │           │   ├── dto # 계층간 데이터 이동을 위한 dto
│   │           │   ├── entity 
│   │           │   └── repository 
│   │           ├── error # 에러 처리 관련
│   │           ├── functions # 전반적으로 사용되는 함수 모음
│   │           ├── interceptor # 인가를 위한 인터셉터
│   │           └── service # 서비스
│   │               └── impl # 서비스 구현체
│   └── resources # 환경 변수 및 설정
└── test # 테스트
```

----

## ERD

![ERD](https://velog.velcdn.com/images/deonii/post/27164666-70f0-4aa9-a8f9-5e1137981c31/image.png)

----

## API 구성

### 유저 관련 api

| api        |  method  | body                                 | 설명    |
|:-----------|:--------:|:-------------------------------------|-------|
| /signup    | **POST** | email : String<br/>password : String | 회원가입  |
| /login     | **POST** | email : String<br/>password : String | 로그인   | 
| /signout   | **POST** |                                      | 로그아웃  |
| /user-info | **GET**  |                                      | 유저 정보 |

### 폴더 관련 api

| api                      |   method   | body          | PathVariable      | 설명                            |
|:-------------------------|:----------:|:--------------|:------------------|-------------------------------|
| /folder/{folderUuid}     |  **POST**  | name : String | folderUuid : UUID | 폴더 생성                         |
| /folder/{folderUuid}     |  **GET**   |               | folderUuid : UUID | 하위 폴더, 파일 <br/>탐색             | 
| /folder/{folderUuid}     | **DELETE** |               | folderUuid : UUID | 해당 폴더, 하위 폴더, 파일 <br/>삭제      |
| /folder/{folderUuid}/zip |  **GET**   |               | folderUuid : UUID | 해당 폴더, 하위 폴더, 파일 <br/>압축 다운로드 |

### 파일 관련 api

| api                                  |   method   | body                 | PathVariable                          | 설명         |
|:-------------------------------------|:----------:|:---------------------|:--------------------------------------|------------|
| /folder/{folderUuid}/file            |  **POST**  | file : MultipartFile | folderUuid : UUID                     | 파일 업로드     |
| /folder/{folderUuid}/file/{fileUuid} |  **GET**   |                      | folderUuid : UUID<br/>fileUuid : UUID | 해당 파일 다운로드 | 
| /folder/{folderUuid}/file/{fileUuid} | **DELETE** |                      | folderUuid : UUID<br/>fileUuid : UUID | 해당 파일 삭제   |

----

## 주요 기능

- 각 사용자에게 30GB의 용량이 주어집니다.
  - 사용자가 업로드할 파일의 사이즈가 현재 사용가능한 공간보다 클 경우 즉시 실패합니다.
  - 파일을 삭제할 경우 즉시 사용 가능한 공간이 확보됩니다.
- 각 사용자는 서로간의 폴더, 파일에 접근할 수 없습니다.
- 같은 경로에는 동일한 파일, 폴더가 존재할 수 없습니다.
- 폴더 생성의 depth 제한이 없습니다.
- 브라우저에서 파일 다운로드가 가능하며 다운로드 진행상황을 확인할 수 있습니다.(파일 다운로드)
- 폴더 다운로드 시 폴더 이름으로 된 zip 파일이 다운로드 됩니다.

----

## 기타 사항

- 폴더 다운로드의 경우 서버 로컬에 파일을 다운로드 하지 않고 압축과 동시에 클라이언트가 다운로드 하기 때문에 압축파일의 크기를 예측할 수 없어 진행상황 확인이 불가능 합니다.
- 편의상 byte가 아닌 KB단위로 계산해 DB에 반영됩니다.
