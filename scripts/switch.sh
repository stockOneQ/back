#!/bin/bash

ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH)
source ${ABSDIR}/profile.sh

# .env 파일의 경로
ENV_FILE="/home/ec2-user/app/stockoneq/.env"

# .env 파일이 존재하는지 확인하고 읽습니다.
if [ -f $ENV_FILE ]; then
  echo "Loading environment variables from $ENV_FILE"
  source $ENV_FILE
else
  echo "$ENV_FILE not found."
fi

function switch_proxy() {
  IDLE_PORT=$(find_idle_port)

  echo "> 전환할 port: $IDLE_PORT"
  echo "> port 전환"
  # 하나의 문장을 만들어 파이프라인으로 넘겨주기 위해 echo를 사용
  echo "set \$service_url http://$PUBLIC_IP:${IDLE_PORT};" | sudo tee /etc/nginx/conf.d/service-url.inc

  echo "> Nginx Reload"
  sudo nginx -s reload
}