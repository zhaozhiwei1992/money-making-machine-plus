#!/bin/sh
cd /root/applications
# 备份包
rm z-public-server-bak.jar
cp z-public-server.jar z-public-server-bak.jar
# 停服务
ps -ef|grep z-public-server|grep -v grep|awk '{print $2}'|xargs kill -9
echo starting......
#nohup java -Xmx512m -Xms512m -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -jar z-public-server.jar &>nohup.out & tail -f nohup.out
nohup java -Xmx512m -Xms512m -Dspring.mvc.static-path-pattern=/mmmp/** -jar z-public-server.jar &>nohup.out & tail -f nohup.out
echo success......
