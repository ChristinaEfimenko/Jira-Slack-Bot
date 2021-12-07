FROM openjdk:11-jdk AS builder

RUN mkdir app

WORKDIR /app
COPY ./ /app
COPY ./build /app/build
EXPOSE 8080

COPY env_file .

ENTRYPOINT ["java", "-jar", "/app/build/libs/BoltBotJava-all.jar"]
