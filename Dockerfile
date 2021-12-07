FROM openjdk:11-jdk AS builder

RUN mkdir app
RUN ./rebuild&run.sh
COPY ./build/libs/BoltBotJava-all.jar /app/bot.jar
WORKDIR /app

ENTRYPOINT ["java", "-jar", "/app/bot.jar"]
