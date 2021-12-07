FROM openjdk:11-jdk AS builder

RUN mkdir app
RUN chmod +x rebuild-run.sh
COPY ./rebuild-run.sh /usr/local/bin
RUN rebuild-run.sh
COPY ./build/libs/BoltBotJava-all.jar /app/bot.jar
WORKDIR /app

ENTRYPOINT ["java", "-jar", "/app/bot.jar"]
