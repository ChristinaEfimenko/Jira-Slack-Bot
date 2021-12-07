FROM openjdk:11-jdk AS builder

RUN mkdir app
COPY . .
COPY ./rebuild&run.sh /usr/local/bin

WORKDIR /app
COPY ./ /app

EXPOSE 8080

ENTRYPOINT ["rebuild&run.sh"]
