# 📮 CS 문의 자동 트리아지 시스템 (cs-inquiry-triage)

## 📋 개요
반복적인 CS 문의를 LLM으로 자동 분류하고 답변 초안을 생성하되,
민감한 문의는 반드시 사람이 검토하도록 분기하는 자동화 시스템입니다.
"AI가 처리할 부분과 사람이 개입해야 할 부분을 구분한다"는 원칙을
직접 구현해보기 위한 사이드 프로젝트입니다.

## 🤔 왜 만들었나
이커머스 CS는 문의량이 많고 반복적인 유형(배송조회, 사이즈문의 등)이
큰 비중을 차지합니다. 모든 문의를 사람이 처리하는 대신,
AI가 자신 있게 처리할 수 있는 문의와 사람이 반드시 봐야 하는
문의를 자동으로 구분해보고 싶어 시작했습니다.

## 🧠 핵심 로직
1. 문의가 들어오면 Claude API가 카테고리 / confidence(0~1) / 답변 초안을 생성
2. 아래 기준으로 자동발송 여부를 분기

| 카테고리 | 처리 방식 |
|---|---|
| 환불(REFUND), 배송지연·분실(SHIPPING_ISSUE), 컴플레인(COMPLAINT) | confidence와 무관하게 **항상 사람 검토** |
| 일반 문의(GENERAL) | confidence ≥ **0.85** 면 자동발송, 미만이면 사람 검토 |

3. 사람 검토가 필요한 문의는 Slack으로 알림 발송

## 🛠 기술 스택
- **Backend**: Java 21, Spring Boot, Gradle (Kotlin DSL)
- **DB**: MySQL, Spring Data JPA
- **LLM**: Claude API (Anthropic)
- **알림**: Slack Incoming Webhook

## 📡 API

| Method | Endpoint | 설명 |
|---|---|---|
| `POST` | `/api/inquiries` | 문의 접수 → 자동 분류/분기 |
| `GET` | `/api/inquiries/{id}` | 문의 단건 조회 |
| `GET` | `/api/inquiries?status={status}` | 상태별 문의 목록 조회 |

## 🚀 실행 방법

1. MySQL에 DB 생성
   ```sql
   CREATE DATABASE cs_inquiry_triage CHARACTER SET utf8mb4;
   ```
2. `src/main/resources/application-local.yaml` 생성 (git에 커밋되지 않는 로컬 전용 설정)
   ```yaml
   spring:
     datasource:
       password: <내 MySQL 비밀번호>

   claude:
     api-key: <Anthropic API 키>

   slack:
     webhook-url: <Slack Incoming Webhook URL>
   ```
3. 앱 실행
   ```bash
   ./gradlew bootRun
   ```
   기본적으로 `local` 프로필이 활성화되어 위 설정이 자동 반영됩니다.

## 🐛 트러블슈팅

문제와 원인, 해결(예정) 상태를 기록해두는 칸입니다. 새로운 이슈를 만나면 여기에 추가합니다.

### Claude 응답 파싱 실패 시 문의가 통째로 사라짐
- **증상**: Claude가 요청한 JSON 형식과 다르게 응답하는 경우가 간헐적으로 발생하고, 이때 문의 접수(`POST /api/inquiries`)가 500 에러로 실패함
- **원인**: `InquiryTriageService.triage()`가 `@Transactional`인데, 파싱 실패 시 예외를 던지면 그 시점까지 실행된 `inquiryRepository.save()`도 함께 롤백됨. 즉 CLAUDE.md 원칙("파싱 실패는 사람 검토로 폴백")과 다르게 문의 자체가 유실됨
- **해결**:
  1. 프롬프트로 "JSON만 출력해라"라고 지시하던 방식 대신, Claude API의 **tool use(함수 호출)** 로 전환해 `category`/`confidence`/`draftAnswer` 스키마를 강제함 (`ClaudeApiConstants.CLASSIFY_TOOL`). 모델이 자유 텍스트로 응답하다 형식이 어긋나는 경우 자체를 구조적으로 줄임
  2. 그래도 실패할 수 있으니 `ClaudeApiClient.classify()`에서 최대 3회(최초 1회 + 재시도 2회)까지 재시도
  3. 재시도까지 모두 실패하면 예외를 던지는 대신 `Optional.empty()`를 반환하고, `InquiryTriageService`는 분류 정보 없이 바로 `NEEDS_REVIEW`로 저장 + Slack 알림을 보내 사람이 직접 확인하도록 폴백 (문의 자체는 항상 저장됨)

## 💡 고려했지만 보류한 것

### Claude 응답 정확도 반복 측정
같은 문의를 여러 번 반복 요청해서 분류 정확도(confidence 대비 실제 정답률)를 통계적으로 검증해보고 싶었음. 하지만 반복 요청 한 번 한 번이 전부 API 과금으로 이어져서, 유의미한 샘플 수를 확보할 만큼 테스트를 돌려보지 못함. 추후 비용 여유가 생기면 소량 샘플로라도 진행 예정.
