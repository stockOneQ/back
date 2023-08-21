#!/bin/bash

REPOSITORY=/home/ec2-user/app/stockoneq
PROJECT_NAME=StockOneQ

ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH) # 현재 stop.sh에 속해 있는 경로
source ${ABSDIR}/profile.sh # 자바 import와 비슷한 기능. 해당 코드로 인해 profile.sh의 여러 함수를 사용할 수 있다.

echo "> Build 파일 복사"

cp $REPOSITORY/zip/*.jar $REPOSITORY/

# .env 파일의 경로
ENV_FILE="/home/ec2-user/app/stockoneq/.env"

# .env 파일이 존재하는지 확인하고 읽습니다.
if [ -f $ENV_FILE ]; then
  echo "Loading environment variables from $ENV_FILE"
  source $ENV_FILE
else
  echo "$ENV_FILE not found."
fi

echo "> 새 애플리케이션 배포"

JAR_NAME=$(ls -tr $REPOSITORY/*.jar | tail -n 1)

echo "> JAR Name: $JAR_NAME"

echo "> $JAR_NAME 에 실행권한 추가"

chmod +x $JAR_NAME

echo "> $JAR_NAME 실행"

IDLE_PROFILE=$(find_idle_profile)

echo "> $JAR_NAME 를 profile=$IDLE_PROFILE 로 실행합니다."
nohup java -DRDS_HOST=$RDS_HOST \
     -DRDS_USERNAME=$RDS_USERNAME \
     -DRDS_PASSWORD=$RDS_PASSWORD \
     -DS3_BUCKET_NAME=$S3_BUCKET_NAME \
     -DS3_ACCESS_KEY=$S3_ACCESS_KEY \
     -DS3_SECRET_KEY=$S3_SECRET_KEY \
     -DJWT_SECRET=$JWT_SECRET \
     -DAWS_ACCESS_KEY=$AWS_ACCESS_KEY \
     -DAWS_SECRET_KEY=$AWS_SECRET_KEY \
     -DPUBLIC_IP=$PUBLIC_IP \
     -DTYPE=$TYPE \
     -DPROJECT_ID=$PROJECT_ID \
     -DPRIVATE_KEY_ID=$PRIVATE_KEY_ID \
     -DPRIVATE_KEY=$PRIVATE_KEY \
     -DCLIENT_EMAIL=$CLIENT_EMAIL \
     -DCLIENT_ID=$CLIENT_ID \
     -DAUTH_URI=$AUTH_URI \
     -DTOKEN_URI=$TOKEN_URI \
     -DAUTH_PROVIDER_X509_CERT_URL=$AUTH_PROVIDER_X509_CERT_URL \
     -DCLIENT_X509_CERT_URL=$CLIENT_X509_CERT_URL \
     -DUNIVERSE_DOMAIN=$UNIVERSE_DOMAIN \
     -Dspring.config.location=classpath:/application.yml,classpath:/application-$IDLE_PROFILE.yml,classpath:/application-firebase.yml \
     -Dspring.profiles.active=$IDLE_PROFILE \
     -jar /home/ec2-user/app/stockoneq/StockOneQ.jar > /home/ec2-user/app/stockoneq/nohup.out 2>&1 &
