#!/bin/bash

ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH)
source ${ABSDIR}/profile.sh성
source ${ABSDIR}/switch.sh

IDLE_PORT=$(find_idle_port)

# .env 파일의 경로
ENV_FILE="/home/ec2-user/app/stockoneq/.env"

# .env 파일이 존재하는지 확인하고 읽습니다.
if [ -f $ENV_FILE ]; then
  echo "Loading environment variables from $ENV_FILE"
  source $ENV_FILE
else
  echo "$ENV_FILE not found."
fi

echo "> Health Check Start"
echo "> IDLE_PORT: $IDLE_PORT"
echo "> curl -s http://$PUBLIC_IP:$IDLE_PORT/profile"
sleep 10

for RETRY_COUNT in {1..10}
do
  RESPONSE=$(curl -s http://$PUBLIC_IP:${IDLE_PORT}/profile)
  UP_COUNT=$(echo ${RESPONSE} | grep 'real' | wc -l)

  if [ ${UP_COUNT} -ge 1 ]
  then # $up_count >= 1 "real" 문자열이 있는지 검증
    echo "> Health check 성공"
    switch_proxy
    break
  else
    echo "> Health check의 응답을 알 수 없거나 혹은 실행 상태가 아닙니다."
    echo "> Health check: ${RESPONSE}"
  fi

  if [ ${RETRY_COUNT} -eq 10 ]
  then
    echo "> Health check 실패."
    echo "> Nginx에 연결하지 않고 배포를 종료합니다."
    exit 1
  fi

  echo "> Health check 연결 실패. 재시도 ..."
  sleep 10
done