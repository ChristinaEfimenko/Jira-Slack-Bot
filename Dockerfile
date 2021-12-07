FROM openjdk:11-jdk AS builder

RUN mkdir app
COPY . .
COPY ./rebuild&run.sh /usr/local/bin
CMD ["rebuild&run.sh"]
WORKDIR /app
COPY ./build/libs/BoltBotJava-all.jar /app/BoltBot.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "BoltBot.jar"]
