#!/bin/bash

export JAVA_HOME=$HOME/android-studio/jre

./gradlew assembleDebug || exit 1

echo -n Output file is found at:
echo ./app/build/outputs/apk/debug/app-debug.apk
