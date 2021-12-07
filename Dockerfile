FROM openjdk:11-jdk AS builder

RUN mkdir app
COPY . .
COPY ./rebuild&run.sh /usr/local/bin
ENTRYPOINT ["rebuild&run.sh"]
WORKDIR /app
COPY ./build/libs/BoltBotJava-all.jar /app/BoltBotJava-all.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/build/libs/BoltBotJava-all.jar"]
