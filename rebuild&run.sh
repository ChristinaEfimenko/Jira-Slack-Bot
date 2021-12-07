#!/usr/bin/env bash

rm ./BoltBot.jar
gradle clean runShadow
mv ./build/libs/BoltBotJava-all.jar ./BoltBot.jar
