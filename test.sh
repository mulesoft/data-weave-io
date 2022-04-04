#!/bin/bash

set -x

if [ $# -ne 1 ]
then
    echo "Usage: $0 [ Maven's settings file path ]"
    exit 1
fi

./gradlew -Pmaven.settings.location=$1 test

./gradlew -Pmaven.settings.location=$1 -PweaveVersion=2.5.0-SNAPSHOT test
