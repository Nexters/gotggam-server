# CLAUDE.md

이 문서는 Claude Code(및 팀원)가 이 저장소에서 작업할 때 따라야 할 공통 규칙을 정의합니다.

## 프로젝트 개요

- 패키지: `com.nexters.death`
- Gradle 멀티/단일 모듈 여부: 단일 모듈 (`settings.gradle` 기준)

## 기술 스택

- Java 25
- Spring Boot 4.1.0
- PostgreSQL 18
- Spring Data JPA
- Lombok
- Swagger (springdoc-openapi) — API 명세 문서화
- Gradle (Groovy DSL)

## 빌드 & 실행

```bash
./gradlew build          # 빌드
./gradlew test           # 테스트 실행
./gradlew bootRun        # 로컬 실행
```

## 패키지 구조 & 아키텍처

- 최상위는 도메인 단위로 패키지를 나누고, 그 하위에 레이어드 아키텍처를 적용한다.

```
com.nexters.death
├── member
│   ├── controller
│   ├── service
│   ├── repository
│   └── entity
├── order
│   ├── controller
│   ├── service
│   ├── repository
│   └── entity
└── ...
```

- 의존 흐름은 `Controller → Service → Repository → Entity` 단방향으로만 고정한다.
    - Controller는 Service만 호출하고, Repository/Entity에 직접 접근하지 않는다.
    - Service는 자신이 속한 도메인의 Repository를 통해서만 Entity에 접근한다.
    - 다른 도메인의 데이터가 필요한 경우 해당 도메인의 Service를 통해서만 접근한다. (다른 도메인의 Repository/Entity 직접 참조 금지)
- 특정 도메인에 속하지 않고 프로젝트 전체에서 공통으로 쓰이는 것(예: Security, CORS, Swagger 등 전역 설정)만 `global` 패키지에 둔다. 도메인 로직이나 특정 도메인에서만 쓰이는 코드는 `global`에 두지 않는다.

```
com.nexters.death
├── global
│   └── config
├── member
│   ├── controller
│   ├── service
│   ├── repository
│   └── entity
└── ...
```

## 코드 스타일 컨벤션

### 클래스 / 메서드

- CRUD 순서(Create → Read → Update → Delete)에 맞추어 메서드를 정렬한다.
- 클래스 최상단은 1줄 개행 후 필드/코드를 작성한다.
- 상수 선언 아래에는 개행을 추가해 필드/생성자와 구분한다.

```java

public class Member {

    private static final int MAX_NAME_LENGTH = 20;

    private String name;
    ...
}
```

### Lombok

- 단순 Getter는 `@Getter`를 사용한다. (Setter는 지양)
- 기본 생성자는 `@NoArgsConstructor(access = AccessLevel.PROTECTED)`를 사용한다.
- 생성자는 `@Builder`로 통일한다. 생성자를 `private`으로 선언해 Builder를 거치지 않고는 생성할 수 없게 한다.
- 생성자 파라미터가 한 줄을 넘어가면 파라미터마다 개행한다.

```java
@Builder
private Member(
    String name,
    String email,
    Role role
) {
    ...
}
```

```java
// name/description처럼 같은 String 타입이 여러 개라 Builder 사용
@Builder
private Category(String name, String description) {
    ...
}
```

### DTO

- DTO는 `record`로 정의한다.
- 요청 DTO는 `XxxRequest`, 응답 DTO는 `XxxResponse`로 네이밍한다.
- 입력값 검증(형식, 필수값 등)은 DTO에서 Bean Validation으로 처리한다.
- 비즈니스 정책 검증(도메인 규칙)은 도메인 객체 내부에서 처리한다.

### 임베디드 타입

- 임베디드 필드명과 클래스명을 동일하게 짓지 않는다.
    - 예: `Nickname` 클래스를 사용할 때 필드명을 `nickname`이 아닌 다른 이름(예: `value`, 혹은 의미 있는 이름)으로 짓는다.

## API 문서화

- API 명세는 Swagger(springdoc-openapi)로 관리한다.
- 컨트롤러/DTO에 어노테이션을 추가해 문서가 자동 생성되도록 유지한다.

## 브랜치 & PR

- `develop`을 기본 브랜치로 사용하며, 작업은 `type/설명` 형식의 브랜치에서 진행한다. (예: `feat/member-signup`, `fix/login-bug`, `chore/gradle-update`)
- PR은 `.github/pull_request_template.md` 양식을 따른다.
- 리뷰어: `@kyer5`, `@gihhyeon` (CODEOWNERS 기준 자동 지정)

## 커밋 메시지

- 형식: `타입: 작업 내용` (예: `feat: 회원가입 API 추가`, `fix: 로그인 세션 만료 버그 수정`)
- 타입은 Angular 컨벤션을 따른다: `feat`, `fix`, `refactor`, `chore`, `docs`, `test`, `style` 등
- 바디(body)는 제목만으로 "왜" 이렇게 변경했는지 알 수 없을 때만 작성한다.
    - 작성 필요: 버그의 원인, 여러 대안 중 하나를 선택한 이유, 브레이킹 체인지의 영향 범위 등
    - 작성 불필요: 오타 수정, 포맷팅, 제목만으로 의도가 명확한 단순 변경
