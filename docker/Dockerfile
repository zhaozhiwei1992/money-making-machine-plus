FROM ejiyuan/openjdk:21-jdk-alpine
VOLUME /data
ADD maven/oa-0.0.1-SNAPSHOT.war app.war
RUN sh -c 'touch /app.war'
ENV JAVA_OPTS=""
ENV spring.profiles.active="prod"
ENTRYPOINT ["java","-jar","/app.war"]