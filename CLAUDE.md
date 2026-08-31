# CS 문의 자동 트리아지 시스템

## 프로젝트 개요
CS 문의를 LLM으로 분류·답변초안 생성하고, confidence와 카테고리 민감도에
따라 자동발송/사람검토를 분기하는 백엔드 시스템입니다.

## 기술 스택
- Java 21, Spring Boot, Gradle (Kotlin DSL)
- DB: MySQL, Spring Data JPA
- LLM: Claude API
- 알림: Slack Webhook

## 자주 쓰는 명령어
- 빌드: `./gradlew build`
- 테스트 전체 실행: `./gradlew test`
- 테스트 단건 실행: `./gradlew test --tests "패키지.클래스명.메서드명"`
- 로컬 실행: `./gradlew bootRun`
- 의존성 확인: `./gradlew dependencies`

## 핵심 분기 규칙
- 민감 카테고리(환불, 배송지연/분실, 컴플레인) → 항상 사람 검토
- 일반 카테고리 → confidence >= 0.85면 자동발송, 미만이면 사람 검토

## 패키지 구조
기능 단위(`domain`)와 전역 공통 모듈(`global`)로 크게 나눈다. 기능이 늘어나면
`domain` 아래에 새 기능 패키지를 추가하는 방식으로 확장한다.

```
domain/
  inquiry/               ─ 문의 트리아지 기능
    controller/          ─ API 엔드포인트
    dto/                 ─ request/response DTO, 서비스 간 전달용 내부 DTO
    entity/               ─ JPA 엔티티, enum
    repository/           ─ JPA 리포지토리
    service/               ─ LLM/Slack 클라이언트, 분기 로직
    constant/             ─ 해당 기능에서만 쓰는 상수
global/
  advice/                ─ @RestControllerAdvice 전역 예외 처리
  error/                 ─ ErrorCode, ErrorResponse
  exception/             ─ CommonException 등 공통 예외
```

## 아키텍처 원칙
- 컨트롤러에는 로직을 두지 않고 서비스로 위임한다
- 외부 API(Claude, Slack) 호출은 별도 클라이언트/어댑터로 감싸서 서비스 로직과 분리한다
- 엔티티를 API 응답으로 직접 노출하지 않고 DTO로 변환해서 반환한다
- 예외는 `ErrorCode`를 정의하고 `ErrorCode.commonException()`으로 던진다.
  새 기능 추가 시 필요한 에러 케이스는 `global/error/ErrorCode`에 추가한다
- 새 기능은 `domain` 아래 새 패키지로 추가하고, 여러 기능이 공유하는 것만 `global`로 올린다

## 테스트 규칙
- 새 기능/버그 수정에는 반드시 테스트를 함께 작성한다
- 분기 로직(자동발송 vs 사람검토 판단)은 단위 테스트로 경계값(0.85 포함)까지 검증한다
- 외부 API(Claude, Slack)는 목(mock)으로 대체하고, 실제 네트워크 호출은 하지 않는다

## 에러 처리 & 로깅
- 예외는 무시하지 않고 명시적으로 처리하거나 상위로 전파한다
- LLM 응답 파싱 실패, 외부 API 타임아웃 등은 사람 검토로 안전하게 폴백한다
- 민감정보(문의 원문, 개인정보, API 키)는 로그에 그대로 남기지 않는다

## Git / 브랜치 전략
- 브랜치명: `feature/기능명`, `fix/버그명` 형식 권장
- 코드/변수명은 영어, 주석은 필요한 곳에만 한글로
- 커밋 메시지는 `[Type 이모지] 설명` 형식, 설명은 한글로 간결하게

| Type | 설명 |
|---|---|
| `[Start 🎉]` | 처음 시작할 때 커밋 |
| `[Feat ✨]` | 새로운 기능에 대한 커밋 |
| `[Fix 🔧]` | 버그 수정 |
| `[Build 🔨]` | 빌드 관련 파일 수정 |
| `[Docs 📝]` | 문서 추가, 수정, 삭제 |
| `[Style 🎨]` | 코드 formatting, 세미콜론 누락 등 코드 자체의 변경이 없는 경우 |
| `[Design 💄]` | CSS 등 사용자 UI 디자인 변경 |
| `[Types 🏷️]` | 타입 생성 또는 수정 |
| `[Refactor ♻️]` | 코드 리팩토링 |
| `[Chore 🗑️]` | 기타 변경 사항 (빌드 스크립트 수정 등) |
| `[Rename 🚚]` | 파일 또는 폴더명을 수정하거나 옮기는 작업 |
| `[Remove 🔥]` | 파일을 삭제하는 작업만 수행한 경우 |
| `[Bug 🐛]` | 버그 있는 코드 알림 |
| `[Test ✅]` | 테스트 코드 |

## 주의사항
- API 키는 절대 커밋하지 말 것 (.env 또는 application-local.yml로 관리, .gitignore 추가)
- 한국어로 답변해줘
