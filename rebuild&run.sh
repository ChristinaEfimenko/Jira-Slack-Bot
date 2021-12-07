#!/usr/bin/env bash

rm ./bot.jar
chmod +x ./gradlew
./gradlew clean runShadow
mv ./build/libs/BoltBotJava-all.jar ./bot.jar

