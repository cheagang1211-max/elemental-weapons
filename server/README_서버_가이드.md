# 🌈 원소 무기 서버 + 자동 업데이트 가이드

```
[나] 코드 수정 → GitHub에 올림 → Actions에서 "Run workflow" (버전 입력)
        ↓ 자동: 빌드 → Release 생성 → pack/ 갱신
[서버] start.bat 실행할 때마다 최신 모드 자동 설치
[친구] 게임 켤 때마다 최신 모드 자동 설치 (Prism Launcher)
```

> ⚠️ "실시간"은 아니에요. 자바 모드는 게임이 켜진 상태에서 바꿀 수 없어서 **서버는 재시작, 친구는 게임 재실행** 때 적용돼요.
> 서버와 친구의 버전이 다르면 접속이 안 되니, 패치하면 **서버 재시작 → 친구들도 게임 재실행** 하라고 알려주세요.

---

## A. GitHub 준비 (처음 한 번)

1. GitHub에서 **새 저장소** 만들기: 이름 `elemental-weapons`, **Public** (친구들이 로그인 없이 받으려면 공개여야 함)
2. 이 프로젝트를 저장소에 올리기 (Claude에게 맡기거나, GitHub Desktop으로 폴더 통째로 올리기)
3. 저장소 → **Settings → Actions → General → Workflow permissions** → **Read and write permissions** 선택 → Save
4. **Actions** 탭 → `Build (빌드 검사)` 가 초록색 ✅ 인지 확인 (빨간색이면 로그를 Claude에게 보여주세요)
5. **첫 배포**: Actions → `Release (패치 배포)` → **Run workflow** → 버전 `1.0.0` → 실행
   → 몇 분 뒤 Releases에 `v1.0.0`이 생기고 `pack/` 폴더가 자동 갱신됩니다.

> 저장소 이름이 `elemental-weapons`가 아니면 `server/start.bat`, `start.sh` 의 `PACK_URL` 과 아래 친구용 주소를 같이 바꿔 주세요.

## B. 패치하는 법 (매번)

1. 코드 수정 (예: 피해량 변경) → GitHub에 커밋/푸시
2. Actions → `Release (패치 배포)` → **Run workflow** → 새 버전(예: `1.0.1`, 이전보다 크게) + 패치 내용 입력
3. 초록색 ✅ 뜨면 끝 → 서버 재시작 + 친구들에게 "게임 다시 켜" 공지

## C. 서버 켜기 (내 PC)

### 준비물 (처음 한 번)
- **Java 25**: https://adoptium.net → Temurin 25 (설치할 때 PATH 설정 체크)
- **Fabric 서버 런처**: https://fabricmc.net/use/server/ → Minecraft **26.3** → 받은 `fabric-server-....jar` 를 이 `server` 폴더에 넣기

모드 jar는 **직접 넣을 필요 없어요.** `start.bat` 이 GitHub에서 원소 무기 + Fabric API를 자동으로 받아 `mods` 폴더에 설치합니다.

### 처음 실행
1. `start.bat` 더블클릭 → 업데이트 도구 다운로드 → 모드 설치 → 서버가 파일을 만들고 꺼짐
2. 생긴 `eula.txt` 를 열어 EULA(https://aka.ms/MinecraftEULA)를 읽고 동의하면 `eula=true` 로 바꾸고 저장
3. 다시 `start.bat` → `Done (...)!` 이 나오면 켜짐 ✅ (방화벽 창이 뜨면 허용)

### 서버 창 명령어 (앞에 / 없이)
| 명령어 | 의미 |
|---|---|
| `whitelist add 친구닉네임` | 친구 입장 허용 (화이트리스트가 켜져 있어서 꼭 필요) |
| `op 내닉네임` | 관리자 권한 |
| `stop` | 서버 끄기 (**창을 X로 닫지 말 것**) |

패치 적용: `stop` → `start.bat` 다시 실행 (자동으로 새 버전 설치)

## D. 친구가 들어올 주소

- **같은 와이파이**: 서버 PC에서 `ipconfig` → `IPv4 주소` (예: `192.168.0.12`)
- **다른 곳**: **playit.gg** (무료, 공유기 설정 불필요)
  1. https://playit.gg 가입 → 프로그램 받아서 서버 PC에서 실행
  2. **Add Tunnel → Minecraft Java**, 로컬 포트 `25565`
  3. 생긴 주소(예: `xxxx.joinmc.link`)를 친구에게 공유. 서버 켤 때 playit도 같이 켜 두기

## E. 친구용 게임 설정 — Prism Launcher (자동 업데이트)

### 내가 한 번만 만들기
1. **Prism Launcher** 설치: https://prismlauncher.org → 실행 → 마이크로소프트 계정 추가
2. **Add Instance** → 이름 `원소 무기`, 버전 **26.3** → 아래 Mod loader에서 **Fabric** 선택 → OK
3. 업데이트 도구 받기: https://github.com/packwiz/packwiz-installer-bootstrap/releases 에서 `packwiz-installer-bootstrap.jar`
4. 인스턴스 우클릭 → **Folder** → 열린 폴더 안의 `minecraft` (또는 `.minecraft`) 폴더에 그 jar 넣기
5. 인스턴스 우클릭 → **Edit** → **Settings → Custom commands** → **Custom Commands** 체크 → **Pre-launch command** 에 아래 한 줄 입력:
   ```
   "$INST_JAVA" -jar packwiz-installer-bootstrap.jar https://raw.githubusercontent.com/cheagang1211-max/elemental-weapons/main/pack/pack.toml
   ```
6. **Launch** → 업데이트 창이 잠깐 뜨고 모드가 설치된 뒤 게임이 켜지면 성공 ✅ → 내 서버 접속 테스트
7. 인스턴스 우클릭 → **Export** → `.zip` 저장 → 친구들에게 전송 (카톡, 디스코드, 구글 드라이브 등)

### 친구가 할 일 (처음 한 번)
1. Prism Launcher 설치 + 자기 마이크로소프트 계정 추가
2. **Add Instance → Import** → 받은 `.zip` 선택 → OK
3. **Launch** → 끝! 앞으로 게임 켤 때마다 자동으로 최신 패치가 적용됩니다.

---

## 자주 생기는 문제
| 증상 | 해결 |
|---|---|
| start.bat 이 `java` 를 못 찾음 | Java 25 설치 후 PC 재시작 |
| `업데이트 확인에 실패` | 인터넷 확인. A단계 5번(첫 배포)을 했는지 확인 |
| `You are not whitelisted` | 서버 창에 `whitelist add 닉네임` |
| 친구가 "모드 불일치"로 못 들어옴 | 서버 재시작했는지 + 친구가 게임을 다시 켰는지 확인 |
| Release 실행이 빨간색 ❌ | A단계 3번(쓰기 권한) 확인, 그래도 안 되면 로그를 Claude에게 |
| 패치했는데 적용이 안 됨 | GitHub 반영에 최대 몇 분 걸릴 수 있어요. 잠시 후 다시 실행 |

## 멀티플레이 참고
- 지팡이 마법은 적대적 몹만 공격 (단, 불 속성 폭발·근접 공격은 PvP가 켜져 있으면 친구도 맞음 → `server.properties` 에서 `pvp=false` 로 끌 수 있음)
- 카메라 연출(삼인칭, 빛 지팡이 시점 고정)은 시전한 사람 화면에만 적용
- `backup.bat` : 서버를 끈 뒤 실행하면 월드를 날짜별로 백업
