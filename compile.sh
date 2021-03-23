#!/bin/bash

echo "compiling..."
./gradlew shadowJar
./gradlew --stop
mv build/libs/BlueBookScraping*.jar ./BlueBookScraping.jar

echo "compiling finished"
