FROM gradle:jdk11 AS builder

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle clean runShadow

FROM openjdk:11-jdk 
EXPOSE 8080
RUN mkdir app
COPY --from=builder /home/gradle/src/build/libs/BoltBotJava-all.jar /app/bot.jar
WORKDIR /app
ENTRYPOINT ["java", "-jar", "/app/bot.jar"]
