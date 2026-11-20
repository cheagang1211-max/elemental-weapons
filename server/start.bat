@echo off
chcp 65001 > nul
title 원소 무기 서버
cd /d "%~dp0"

rem ===== 서버 메모리 (PC 램이 16GB면 4G, 8GB면 2G 정도) =====
set MEMORY=4G

rem ===== 자동 업데이트 주소 (GitHub 저장소 이름이 다르면 여기만 수정) =====
set PACK_URL=https://raw.githubusercontent.com/cheagang1211-max/elemental-weapons/main/pack/pack.toml

set JAR=
for %%f in (fabric-server*.jar) do set JAR=%%f
if "%JAR%"=="" (
    echo [오류] fabric-server 로 시작하는 .jar 파일이 이 폴더에 없습니다.
    echo https://fabricmc.net/use/server/ 에서 Minecraft 26.3 서버 런처를 받아 이 폴더에 넣어 주세요.
    pause
    exit /b
)

rem ----- 1. 자동 업데이트 도구 준비 (처음 한 번만 다운로드) -----
if not exist packwiz-installer-bootstrap.jar (
    echo 자동 업데이트 도구를 다운로드합니다...
    curl -L -f -o packwiz-installer-bootstrap.jar https://github.com/packwiz/packwiz-installer-bootstrap/releases/latest/download/packwiz-installer-bootstrap.jar
)

rem ----- 2. 최신 모드 받기 (원소 무기 + Fabric API) -----
echo GitHub에서 최신 모드를 확인합니다...
java -jar packwiz-installer-bootstrap.jar -g -s server %PACK_URL%
if errorlevel 1 (
    echo [경고] 업데이트 확인에 실패했습니다. 인터넷 연결을 확인하세요. 지금 있는 모드로 서버를 켭니다.
)

rem ----- 3. 서버 시작 -----
echo.
echo 서버 시작: %JAR%  (메모리 %MEMORY%)
echo 끄려면 이 창에 stop 을 입력하세요. 창을 X로 닫으면 월드가 저장 안 될 수 있어요!
echo 패치를 적용하려면: stop 으로 끄고 start.bat 을 다시 실행하면 됩니다.
echo.
java -Xms%MEMORY% -Xmx%MEMORY% -XX:+UseG1GC -XX:+ParallelRefProcEnabled -XX:MaxGCPauseMillis=200 -XX:+UnlockExperimentalVMOptions -XX:+DisableExplicitGC -XX:G1NewSizePercent=30 -XX:G1MaxNewSizePercent=40 -XX:G1HeapRegionSize=8M -XX:G1ReservePercent=20 -XX:InitiatingHeapOccupancyPercent=15 -jar "%JAR%" nogui
echo.
echo 서버가 꺼졌습니다.
pause
