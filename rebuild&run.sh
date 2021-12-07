#!/usr/bin/env bash

rm ./BoltBotJava-all.jar
chmod +x ./gradlew
./gradlew clean runShadow

