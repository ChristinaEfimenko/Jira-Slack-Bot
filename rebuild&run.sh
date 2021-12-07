#!/usr/bin/env bash

gradle clean runShadow
mv ./build/libs/* ./
java -jar ./BoltBotJava-all.jar