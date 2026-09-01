# AGENTS.md

## 1. 프로젝트 개요

이 프로젝트는 **SK 인텔릭스 데이터를 수집 및 전처리하고 Elasticsearch에 색인한 후 검색 API를 제공하는 Spring Boot 기반 프로젝트**이다.

Codex는 이 프로젝트를 수정할 때 기존 기능의 안정성을 최우선으로 고려해야 한다.

### 주요 처리 흐름

```text
외부 데이터 / DB / API
        ↓
데이터 수집
        ↓
데이터 전처리
        ↓
메타데이터 생성 및 변환
        ↓
Elasticsearch 색인
        ↓
검색
        ↓
검색 API
        ↓
외부 서비스
```

### 주요 개발 영역

* 데이터 수집
* 데이터 전처리
* 메타데이터 가공
* Elasticsearch Document 생성
* Elasticsearch 색인
* 색인 관리
* 검색 Query 구성
* 검색 결과 가공
* REST API 제공
* DB 및 외부 API 연계
* 예외 처리 및 로그 관리

---

# 2. 기본 개발 원칙

Codex는 모든 작업에서 다음 원칙을 따른다.

1. 기존 코드를 먼저 충분히 분석한 후 수정한다.
2. 요청받은 범위 내에서만 코드를 수정한다.
3. 정상 동작 중인 기존 기능을 최대한 유지한다.
4. 필요 이상의 리팩토링을 하지 않는다.
5. 관련 없는 파일을 수정하지 않는다.
6. 기존 클래스, 메서드, 설정을 임의로 삭제하지 않는다.
7. 기존 프로젝트의 코딩 스타일을 우선적으로 따른다.
8. 새로운 라이브러리 추가는 꼭 필요한 경우에만 한다.
9. 기존 의존성 버전을 임의로 변경하지 않는다.
10. 대규모 구조 변경이 필요한 경우 바로 수정하지 말고 영향 범위를 먼저 설명한다.

**가장 중요한 원칙**

> 요청받은 문제를 해결하기 위해 필요한 최소 범위만 수정한다.

---

# 3. Git 안전 규칙

## 매우 중요

Codex는 사용자의 기존 소스와 Git 이력을 보호해야 한다.

작업 시작 전 반드시 현재 Git 상태를 확인한다.

```bash
git status
git branch --show-current
git log --oneline -5
```

기존에 커밋되지 않은 변경사항이 있다면 해당 변경사항을 사용자 작업으로 간주한다.

이를 임의로 삭제하거나 되돌려서는 안 된다.

## 절대 금지 Git 명령

사용자가 명시적으로 요청하지 않는 한 다음 명령 또는 동일한 효과를 내는 작업을 실행하지 않는다.

```bash
git reset --hard
git clean -f
git clean -fd
git checkout -- .
git restore .
git restore --staged .
git push --force
git push -f
```

또한 다음 작업도 임의로 하지 않는다.

* 기존 commit 삭제
* 기존 commit amend
* rebase
* branch 삭제
* tag 삭제
* 강제 push
* remote 변경
* `.git` 디렉터리 수정 또는 삭제
* 사용자 변경사항 discard
* 사용자 stash 삭제

## Git Commit 규칙

Codex는 사용자가 명시적으로 요청하지 않는 한 자동으로 commit하지 않는다.

특히 다음 명령을 임의로 실행하지 않는다.

```bash
git add .
git commit
git commit --amend
git push
```

작업 완료 후에는 commit 대신 변경 내용을 사용자에게 보고한다.

---

# 4. Checkpoint 운영 규칙

이 프로젝트는 Codex 작업 중 언제든 이전 정상 상태로 돌아갈 수 있도록 **Checkpoint 기반으로 개발한다.**

Checkpoint는 대화 단위로 생성하지 않는다.

컴파일과 관련 테스트를 통과하고 사용자가 정상 동작을 확인한 시점에 생성한다.

테스트가 실패했거나 검증이 완료되지 않은 상태에는 checkpoint를 생성하지 않는다.

권장 흐름:

```text
main
 │
 └── develop
       │
       ├── checkpoint
       │
       ├── Codex 작업
       │
       ├── 테스트
       │
       ├── checkpoint
       │
       ├── Codex 작업
       │
       └── ...
```

중요한 Codex 작업을 시작하기 전에 마지막 정상 상태가 commit되어 있는지 확인하는 것을 기본 원칙으로 한다.

예:

```bash
git add <변경한 파일>
git commit -m "feat: 검색 API 구현"
```

기능 구현 commit과 검증 완료 checkpoint는 구분한다.

작업이 정상적으로 완료되고 사용자가 검토한 후 필요하면 checkpoint를 생성한다.

```bash
git commit --allow-empty -m "checkpoint: 검색 API 정상 동작"
```

Codex는 기존 checkpoint를 임의로 변경하거나 삭제하지 않는다.

---

# 5. Branch 운영 전략

기본 브랜치 전략은 다음과 같다.

```text
main
 │
 └── develop
       │
       ├── feature/*
       ├── fix/*
       └── checkpoint
```

## main

`main`은 안정적인 버전을 유지한다.

Codex는 사용자가 명시적으로 요청하지 않는 한 `main`에서 직접 개발하지 않는다.

다음 작업을 임의로 수행하지 않는다.

```bash
git switch main
git merge
git rebase
git push
```

## develop

일반적인 개발 통합 브랜치이다.

검증된 개발 결과를 통합하며, 원칙적으로 `develop`에서 직접 코드를 수정하지 않는다.

코드 변경 작업은 `develop`에서 분기한 `feature/*` 또는 `fix/*` 브랜치에서 수행한다.

단, 사용자가 특정 브랜치에서 작업하도록 명시적으로 요청한 경우는 예외로 한다.

## feature branch

규모가 있는 기능은 별도 branch 사용을 권장한다.

예:

```text
feature/data-preprocessing
feature/elasticsearch-indexing
feature/search-api
feature/metadata
feature/db-integration
```

## fix branch

버그 수정은 필요할 경우 별도 branch를 사용한다.

예:

```text
fix/search-query
fix/indexing-error
fix/preprocessing-null
```

## 실패 작업 복구

이미 commit 또는 push된 실패 작업은 `reset`이나 강제 push로 이력을 삭제하지 않는다.

공유 브랜치에 반영된 실패 작업은 `git revert`로 취소한다.

미커밋 변경을 되돌려야 할 때는 사용자 변경과 Codex 변경을 먼저 구분한다.

사용자 변경이 섞여 있다면 임의로 `restore`하지 않고 사용자에게 보고한다.

---

# 6. Codex 작업 시작 절차

Codex는 코드 수정 전에 다음 순서로 작업한다.

### STEP 1. Git 상태 확인

```bash
git status
git branch --show-current
git log --oneline -5
```

### STEP 2. 기존 변경사항 확인

사용자가 이미 수정한 파일이 있는지 확인한다.

기존 변경사항이 존재한다면 해당 내용을 보호한다.

### STEP 3. 관련 코드 분석

수정 대상과 관련된 다음 항목을 확인한다.

* Controller
* Service
* Repository
* DTO
* Entity / Document
* Config
* Utility
* Exception
* Elasticsearch Query
* DB Query
* 테스트 코드

### STEP 4. 영향 범위 확인

변경으로 인해 영향을 받을 수 있는 기능을 파악한다.

### STEP 5. 최소 범위 수정

분석 후 요청받은 기능에 필요한 최소 범위만 수정한다.

---

# 7. Java 개발 규칙

기존 프로젝트에서 별도의 규칙이 확인되면 기존 규칙을 우선한다.

기본적으로 다음 원칙을 따른다.

* 의미가 명확한 클래스명과 메서드명을 사용한다.
* 메서드는 하나의 책임을 가지도록 한다.
* 불필요하게 긴 메서드는 분리한다.
* 중복 코드는 가능한 범위에서 제거한다.
* 단, 요청과 관계없는 대규모 리팩토링은 하지 않는다.
* `null` 가능성을 고려한다.
* 예외를 무시하지 않는다.
* 의미 없는 `catch (Exception e)` 남용을 피한다.
* 하드코딩을 최소화한다.
* 운영 환경별 값은 가능한 한 설정으로 관리한다.

---

# 8. Spring Boot 개발 규칙

기존 Layer 구조를 유지한다.

기본적인 구조는 다음을 기준으로 한다.

```text
Controller
     ↓
Service
     ↓
Repository / Client
     ↓
DB / Elasticsearch / External API
```

Controller에 비즈니스 로직을 과도하게 작성하지 않는다.

비즈니스 로직은 Service 계층에서 처리한다.

DB 접근은 Repository 계층을 사용한다.

외부 시스템 연동이 별도 Client 구조로 구현되어 있다면 해당 구조를 유지한다.

---

# 9. 패키지 구조 규칙

현재 프로젝트의 기존 패키지 구조를 가장 우선한다.

Codex는 단순히 자신의 판단만으로 전체 package 구조를 변경하지 않는다.

예를 들어 프로젝트가 다음 구조를 사용하고 있다면 이를 유지한다.

```text
controller
service
repository
domain
dto
config
exception
util
client
elasticsearch
preprocess
```

새로운 package가 필요하면 기존 구조와 중복되는 package가 없는지 먼저 확인한다.

---

# 10. Gradle 규칙

Gradle 관련 파일은 신중하게 수정한다.

주요 대상:

```text
build.gradle
settings.gradle
gradle.properties
gradle/
gradlew
gradlew.bat
```

Codex는 요청과 직접 관련이 없는 경우 다음을 임의로 변경하지 않는다.

* Gradle 버전
* Spring Boot 버전
* Java 버전
* dependency 버전
* repository 설정
* plugin 버전

새로운 dependency가 필요한 경우 기존 dependency로 해결할 수 있는지 먼저 확인한다.

dependency를 추가했다면 추가 이유를 작업 결과에 명시한다.

---

# 11. DB 개발 규칙

DB 연동 코드를 수정할 때 기존 Schema와 데이터 구조를 우선적으로 확인한다.

다음을 임의로 변경하지 않는다.

* 테이블 삭제
* 컬럼 삭제
* 컬럼명 변경
* 데이터 삭제
* 대량 UPDATE
* 대량 DELETE
* 운영 DB 데이터 수정

특히 다음과 같은 SQL은 사용자의 명시적인 요청 없이 실행하지 않는다.

```sql
DROP TABLE
TRUNCATE TABLE
DELETE FROM
ALTER TABLE ... DROP
```

Schema 변경이 필요하다면 먼저 변경 필요성과 영향 범위를 설명한다.

---

# 12. 데이터 전처리 규칙

데이터 전처리는 검색 품질에 직접적인 영향을 주므로 신중하게 수정한다.

전처리 과정에서 다음 사항을 고려한다.

```text
원본 데이터
    ↓
NULL 처리
    ↓
문자열 정제
    ↓
불필요 문자 처리
    ↓
필드 변환
    ↓
메타데이터 구성
    ↓
Document 생성
    ↓
Elasticsearch 색인
```

원본 데이터를 가능한 한 보존한다.

전처리 과정에서 데이터가 유실될 가능성이 있는 변경은 신중하게 처리한다.

특히 다음 변경은 영향 범위를 먼저 확인한다.

* HTML 제거
* 특수문자 제거
* 공백 정규화
* 줄바꿈 제거
* 날짜 변환
* 숫자 변환
* 필드 병합
* 필드 분리
* NULL 값 치환
* 대용량 텍스트 자르기

---

# 13. Elasticsearch 개발 규칙

Elasticsearch 관련 코드는 검색 품질과 운영 데이터에 직접 영향을 줄 수 있으므로 특히 신중하게 수정한다.

다음 항목을 임의로 변경하지 않는다.

* index 삭제
* mapping 변경
* analyzer 변경
* tokenizer 변경
* shard 설정
* replica 설정
* alias 변경
* index template 변경

특히 사용자의 명시적인 요청 없이 다음과 같은 파괴적 작업을 실행하지 않는다.

```text
DELETE /index-name
DELETE /*
```

대량 reindex 작업 역시 임의로 실행하지 않는다.

---

# 14. Elasticsearch Mapping 규칙

Mapping 변경이 필요한 경우 기존 Mapping을 먼저 분석한다.

특히 다음 타입 변경은 신중하게 처리한다.

```text
text
keyword
date
integer
long
boolean
nested
object
```

이미 생성된 index의 field type을 단순 변경할 수 있다고 가정하지 않는다.

필요하다면 다음 방식의 migration을 검토한다.

```text
기존 Index
   ↓
새로운 Mapping
   ↓
신규 Index
   ↓
Reindex
   ↓
검증
   ↓
Alias 전환
```

하지만 실제 reindex나 alias 전환은 사용자의 명시적인 요청 없이 수행하지 않는다.

---

# 15. Elasticsearch 검색 Query 규칙

검색 Query 변경 시 기존 검색 결과와 검색 품질에 미치는 영향을 고려한다.

다음 항목을 확인한다.

* match
* multi_match
* term
* terms
* bool
* must
* should
* filter
* minimum_should_match
* range
* sort
* highlight
* aggregation

검색 결과가 달라질 수 있는 Query 변경은 작업 결과에 반드시 설명한다.

가능하다면 기존 Query와 변경 Query의 차이를 명확하게 설명한다.

---

# 16. 검색 API 개발 규칙

검색 API 수정 시 기존 API 호환성을 최대한 유지한다.

특히 다음 항목을 임의로 변경하지 않는다.

```text
URL
HTTP Method
Request Parameter
Request Body
Response JSON 구조
HTTP Status Code
```

기존 API Spec을 변경해야 한다면 영향 범위를 먼저 설명한다.

Response DTO 변경 시 기존 Client에 영향을 줄 수 있는지 확인한다.

---

# 17. 예외 처리 규칙

예외를 단순히 숨기지 않는다.

다음과 같은 코드를 피한다.

```java
try {
    ...
} catch (Exception e) {
}
```

예외 발생 시 적절한 로그와 예외 처리를 한다.

단, 로그에 다음과 같은 민감정보를 출력하지 않는다.

* Password
* API Secret
* Access Token
* Authorization Header
* 개인정보
* DB 접속 비밀번호

---

# 18. 로그 규칙

운영 환경에서 문제를 추적할 수 있도록 의미 있는 로그를 작성한다.

예:

```text
INFO  : 주요 처리 시작/완료
DEBUG : 개발 및 상세 추적 정보
WARN  : 예상 가능한 비정상 상황
ERROR : 실제 처리 실패
```

대량 데이터 처리 시 레코드마다 INFO 로그를 출력하여 로그가 과도하게 증가하지 않도록 한다.

필요하다면 처리 건수 단위로 진행 상황을 기록한다.

예:

```text
색인 시작
총 대상: 100,000건

10,000건 처리
20,000건 처리
...
100,000건 처리

색인 완료
성공: 99,950
실패: 50
```

---

# 19. 설정 파일 보호

다음 파일은 특히 신중하게 수정한다.

```text
application.yml
application.properties
application-dev.yml
application-prod.yml
logback.xml
Dockerfile
docker-compose.yml
```

환경별 설정을 임의로 통합하거나 삭제하지 않는다.

Secret이나 Password를 소스에 하드코딩하지 않는다.

---

# 20. 절대로 노출하면 안 되는 정보

다음 정보는 코드, 로그, 테스트 코드, 문서 등에 직접 작성하지 않는다.

```text
DB Password
AWS Access Key
AWS Secret Key
API Secret
JWT Secret
Elasticsearch Password
Authorization Token
개인정보
운영 서버 인증정보
```

이미 코드에 이러한 정보가 발견되더라도 다른 파일에 복사하거나 출력하지 않는다.

---

# 21. 테스트 규칙

코드 수정 후 가능한 범위에서 관련 테스트를 수행한다.

기본 명령:

```bash
./gradlew test
```

Windows 환경:

```powershell
.\gradlew.bat test
```

필요하면:

```bash
./gradlew clean test
```

단, `clean`으로 인해 개발 중 필요한 산출물이 삭제될 위험이 있다면 먼저 영향을 확인한다.

테스트 실패 시 테스트를 억지로 통과시키기 위해 기존 테스트를 임의로 삭제하거나 의미를 변경하지 않는다.

---

# 22. 테스트 실패 처리

테스트가 실패하면 다음 순서로 처리한다.

```text
테스트 실패
    ↓
원인 분석
    ↓
이번 변경 때문인지 확인
    ↓
기존 문제인지 확인
    ↓
최소 범위 수정
    ↓
재테스트
```

기존부터 실패하던 테스트라면 Codex 작업 때문에 발생한 것처럼 처리하지 않는다.

작업 완료 보고에서 명확히 구분한다.

---

# 23. 대용량 데이터 처리 규칙

SK 인텔릭스 데이터 전처리 및 Elasticsearch 색인은 대용량 데이터 처리를 전제로 한다.

따라서 다음 사항을 고려한다.

* 전체 데이터를 한 번에 메모리에 올리지 않는다.
* 가능한 경우 Batch 단위로 처리한다.
* DB 조회 시 Paging 또는 Streaming을 고려한다.
* Elasticsearch Bulk API 사용을 고려한다.
* Batch Size를 설정 가능하도록 구성하는 것을 우선한다.
* 실패 데이터 재처리 가능성을 고려한다.
* 전체 성공/실패 건수를 확인할 수 있도록 한다.
* 부분 실패 때문에 전체 작업을 처음부터 다시 수행해야 하는 구조를 피한다.

---

# 24. 성능 최적화 규칙

성능 개선 요청을 받은 경우 추측만으로 코드를 변경하지 않는다.

가능하면 다음 순서로 접근한다.

```text
현재 문제 확인
    ↓
병목 구간 확인
    ↓
원인 분석
    ↓
최소 변경
    ↓
성능 비교
```

특히 다음 설정을 근거 없이 임의로 크게 변경하지 않는다.

* Thread Pool
* DB Connection Pool
* Elasticsearch Bulk Size
* JVM Heap
* Timeout
* Retry Count
* Batch Size

---

# 25. Codex가 절대로 임의로 하면 안 되는 작업

사용자의 명시적인 요청 없이 다음 작업을 수행하지 않는다.

### Git

* reset --hard
* clean
* force push
* branch 삭제
* tag 삭제
* commit history 변경

### DB

* DROP
* TRUNCATE
* 대량 DELETE
* 운영 데이터 변경

### Elasticsearch

* index 삭제
* 전체 index 삭제
* mapping 파괴적 변경
* alias 임의 전환
* 대량 reindex 실행

### 프로젝트

* Spring Boot 버전 변경
* Java 버전 변경
* Gradle 버전 변경
* 대규모 package 변경
* 전체 architecture 변경
* 기존 설정파일 삭제
* 기존 테스트 삭제

### 파일

사용자가 만든 파일이나 기존 프로젝트 파일을 요청 없이 삭제하지 않는다.

---

# 26. 대규모 변경 요청 처리

다음과 같은 작업은 바로 코드부터 수정하지 않는다.

```text
전체 구조 리팩토링
Spring Boot 버전 Upgrade
Gradle Upgrade
Elasticsearch Upgrade
DB Schema 변경
Index Mapping 변경
인증 구조 변경
대규모 package 이동
검색 Architecture 변경
```

먼저 다음을 분석한다.

```text
1. 현재 구조
2. 변경 대상
3. 영향 받는 파일
4. 예상 위험
5. 변경 계획
```

그 후 최소한의 안전한 방식으로 작업한다.

---

# 27. Codex 작업 완료 보고 형식

작업이 끝나면 다음 형식으로 사용자에게 결과를 보고한다.

## 작업 요약

무엇을 변경했는지 간단하게 설명한다.

## 변경 파일

```text
src/main/java/.../SearchController.java
src/main/java/.../SearchService.java
src/main/java/.../ElasticSearchService.java
```

각 파일의 변경 이유를 설명한다.

## 주요 변경 내용

핵심 로직이 어떻게 변경되었는지 설명한다.

## 테스트 결과

예:

```text
./gradlew test

BUILD SUCCESSFUL
```

또는 실패한 경우:

```text
BUILD FAILED

실패 테스트:
SearchServiceTest

원인:
...
```

## 영향 범위

기존 기능에 영향을 줄 가능성이 있는 부분을 설명한다.

## 남은 문제

추가 확인이 필요한 사항이 있다면 설명한다.

---

# 28. Codex 작업 단위 원칙

한 번의 Codex 작업에서는 가능한 한 하나의 목적에 집중한다.

좋은 예:

```text
검색 API pagination 기능 추가
```

```text
Elasticsearch Bulk 색인 실패 처리 추가
```

```text
전처리 NULL 처리 개선
```

좋지 않은 예:

```text
검색 API 수정하고
DB도 변경하고
Elasticsearch mapping도 변경하고
패키지도 정리하고
Spring Boot도 업그레이드
```

큰 작업은 작은 작업으로 분리한다.

---

# 29. 사용자 코드 보호 원칙

Codex가 작업하기 전부터 존재했던 변경사항은 **사용자의 작업**으로 간주한다.

따라서 다음 원칙을 따른다.

> 내가 만들지 않은 변경사항을 임의로 되돌리지 않는다.

다른 파일에서 예상하지 못한 변경사항을 발견해도 임의로 삭제하거나 수정하지 않는다.

현재 작업과 충돌한다면 사용자에게 해당 사실을 보고한다.

---

# 30. 최우선 규칙

규칙이 충돌하거나 판단이 어려운 경우 다음 우선순위를 따른다.

```text
1순위 : 사용자 데이터 및 소스 보호
2순위 : Git 이력 보호
3순위 : 기존 정상 기능 유지
4순위 : 운영 데이터 보호
5순위 : 요청받은 기능 구현
6순위 : 코드 개선 및 리팩토링
```

즉,

> **코드를 더 예쁘게 만드는 것보다 기존 정상 코드를 안전하게 보존하는 것이 우선이다.**

---

# 31. 작업 시작 체크리스트

Codex는 매 작업을 시작할 때 다음 사항을 확인한다.

```text
[ ] 현재 branch 확인
[ ] git status 확인
[ ] 최근 commit 확인
[ ] 사용자 미커밋 변경사항 확인
[ ] 요청사항 분석
[ ] 관련 코드 확인
[ ] 영향 범위 확인
[ ] 최소 변경 계획 수립
```

---

# 32. 작업 완료 체크리스트

```text
[ ] 요청사항 구현 여부 확인
[ ] 관련 없는 파일 수정 여부 확인
[ ] git diff 확인
[ ] 컴파일 확인
[ ] 관련 테스트 실행
[ ] 기존 기능 영향 확인
[ ] 변경 파일 정리
[ ] 테스트 결과 보고
[ ] 남은 위험 요소 보고
```

---

# 33. 핵심 원칙 요약

Codex는 이 프로젝트에서 다음 7가지 원칙을 항상 기억한다.

```text
1. 기존 소스를 보호한다.

2. Git 이력을 보호한다.

3. 작업 전에 git status를 확인한다.

4. 요청한 범위만 최소한으로 수정한다.

5. 파괴적인 Git / DB / Elasticsearch 작업을
   사용자 요청 없이 실행하지 않는다.

6. 수정 후 반드시 검증한다.

7. 언제든 이전 checkpoint로
   돌아갈 수 있는 상태를 유지한다.
```

**안전성이 개발 속도보다 우선한다.**

**정상 동작하는 기존 기능을 보존하는 것이 리팩토링보다 우선한다.**

**Codex는 사용자의 기존 작업을 임의로 삭제하거나 되돌리지 않는다.**
