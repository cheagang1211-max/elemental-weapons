#!/bin/sh
# Mac / Linux 용
cd "$(dirname "$0")"
MEMORY=4G
PACK_URL=https://raw.githubusercontent.com/cheagang1211-max/elemental-weapons/main/pack/pack.toml
JAR=$(ls fabric-server*.jar 2>/dev/null | head -n 1)
if [ -z "$JAR" ]; then
  echo "fabric-server 로 시작하는 .jar 파일이 없습니다. https://fabricmc.net/use/server/ 에서 받아 주세요."
  exit 1
fi
[ -f packwiz-installer-bootstrap.jar ] || curl -L -f -o packwiz-installer-bootstrap.jar \
  https://github.com/packwiz/packwiz-installer-bootstrap/releases/latest/download/packwiz-installer-bootstrap.jar
java -jar packwiz-installer-bootstrap.jar -g -s server "$PACK_URL" || echo "업데이트 확인 실패 - 기존 모드로 시작"
java -Xms2G -Xmx$MEMORY -jar "$JAR" nogui
