@echo off
chcp 65001 > nul
cd /d "%~dp0"
rem 서버를 끈 상태에서 실행하세요. world 폴더를 backups 폴더에 날짜별로 복사합니다.
for /f %%i in ('powershell -NoProfile -Command "Get-Date -Format yyyy-MM-dd_HH-mm"') do set STAMP=%%i
if not exist backups mkdir backups
powershell -NoProfile -Command "Compress-Archive -Path 'world' -DestinationPath 'backups\world_%STAMP%.zip'"
echo 백업 완료: backups\world_%STAMP%.zip
pause
