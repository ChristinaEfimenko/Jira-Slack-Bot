FROM openjdk:11-jdk AS builder

RUN mkdir app
COPY ./rebuild-run.sh /usr/local/bin
RUN cd /usr/local/bin
RUN chmod +x rebuild-run.sh
RUN cd ./
RUN rebuild-run.sh
COPY ./build/libs/BoltBotJava-all.jar /app/bot.jar
WORKDIR /app

ENTRYPOINT ["java", "-jar", "/app/bot.jar"]
