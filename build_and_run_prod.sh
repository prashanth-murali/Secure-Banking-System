#!/usr/bin/env bash

# kill process
kill -9 `cat /home/ubuntu/CSE545-Project/shared/back-end/pid`

# 1. pull latest

cd /home/ubuntu/CSE545-Project/
eval "$(ssh-agent -s)"
ssh-add ~/.ssh/github_cse545
#git pull --no-edit origin develop

# 2. build deployable
cd /home/ubuntu/CSE545-Project/shared/front-end/my_app
# clear out old built frontend files
rm -rf /home/ubuntu/CSE545-Project/shared/back-end/src/main/resources/static/*
# build new front end files
npm run build

cd /home/ubuntu/CSE545-Project/shared/back-end
mvn clean install -DskipTests
mvn spring-boot:run > application.log &
FOO_PID=$!
echo $FOO_PID > pid

# application pid file
# /home/ubuntu/CSE545-Project/shared/back-end/pid
# application log file
# /home/ubuntu/CSE545-Project/shared/back-end/nohup.out

echo "application is running"
