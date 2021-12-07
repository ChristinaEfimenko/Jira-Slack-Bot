FROM openjdk:11-jdk AS builder

RUN mkdir app
COPY . .
COPY ./rebuild&run.sh /usr/local/bin
ENTRYPOINT ["rebuild&run.sh"]
WORKDIR /app
COPY ./BoltBot.jar /app/BoltBot.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/BoltBot.jar"]
