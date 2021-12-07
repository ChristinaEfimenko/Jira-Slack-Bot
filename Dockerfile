FROM openjdk:11-jdk AS builder

RUN mkdir app
COPY ./rebuild&run.sh /usr/local/bin
CMD ["rebuild&run.sh"]
COPY ./BoltBotJava-all.jar /app/bot.jar
WORKDIR /app

ENTRYPOINT ["java", "-jar", "/app/bot.jar"]
