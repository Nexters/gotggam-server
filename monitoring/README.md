# 서버 모니터링 (Prometheus + Grafana)

운영(prod) 인스턴스에서만 뜨는 서버 모니터링 스택이다. HTTP 지연/에러율, JVM, HikariCP, 시스템(CPU/RAM/디스크), 컨테이너, PostgreSQL 지표를 수집해 Grafana 대시보드로 본다.

## 구성

| 서비스 | 역할 |
| --- | --- |
| prometheus | 지표 수집/저장 (내부 전용, 외부 미노출) |
| grafana | 대시보드 (127.0.0.1:3000 로만 바인딩) |
| node-exporter | 호스트 CPU/RAM/디스크 |
| cadvisor | 컨테이너별 리소스 |
| postgres-exporter | PostgreSQL 지표 |

앱 지표는 별도 관리 포트(`app:8081/actuator/prometheus`)로 노출되며, 같은 docker 네트워크의 Prometheus만 접근한다.

## 언제 뜨는가

`docker-compose.yml`에서 모니터링 서비스는 `profiles: [monitoring]`으로 게이트된다.

- 운영: prod `.env`에 `COMPOSE_PROFILES=monitoring`이 주입되어 자동으로 뜬다.
- 개발: `COMPOSE_PROFILES`가 없어 모니터링 서비스는 뜨지 않는다.

## 접근 방법 (SSH 터널)

Grafana는 외부에 노출하지 않고 운영 호스트의 `127.0.0.1:3000`에만 바인딩한다. 로컬에서 SSH 터널로 접속한다.

```bash
ssh -L 3000:localhost:3000 <user>@<prod-host>
```

터널을 연 상태에서 브라우저로 접속:

```
http://localhost:3000
```

- 초기 로그인: `admin` / (GitHub prod environment의 secret `GF_SECURITY_ADMIN_PASSWORD` 값)
- secret 미설정 시 기본값 `admin`

## 대시보드

Grafana 프로비저닝으로 자동 등록된다 (별도 import 불필요).

- JVM (Micrometer) — 힙/GC/스레드, HTTP 지연, HikariCP
- Node Exporter Full — 호스트 시스템
- PostgreSQL Database — DB/커넥션
- Cadvisor exporter — 컨테이너 리소스

## 필요한 secret

GitHub 저장소 prod environment에 아래 secret을 등록해야 한다.

- `GF_SECURITY_ADMIN_PASSWORD` — Grafana 관리자 비밀번호

postgres-exporter는 기존 `POSTGRES_USER/PASSWORD/DB`를 재사용하므로 추가 secret이 필요 없다.

## 한계와 후속

모니터링 스택이 운영 앱과 **같은 인스턴스**에 뜨므로, 호스트 자체가 죽으면(커널 패닉, 디스크 풀, 네트워크 단절 등) Grafana/Prometheus도 함께 죽어 그 구간을 관측할 수 없다. 앱만 죽고 호스트가 살아있는 경우나, 살아있을 때의 지연/에러/리소스 추세는 잘 잡는다.

"호스트 죽음" 공백은 **외부 업타임 체크**(호스트 밖에서 운영 URL을 주기적으로 핑)로 메꾸며, 이는 별도 후속 이슈에서 다룬다.

## 로컬에서 스택 확인

운영 배포 없이 로컬에서 전체 스택을 띄워보려면:

```bash
COMPOSE_PROFILES=monitoring docker compose up -d --build
```

- Prometheus 타깃 상태: 컨테이너 내부에서 `http://localhost:9090/api/v1/targets`
- Grafana: `http://localhost:3000`
