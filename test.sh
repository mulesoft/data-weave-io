#!/bin/bash

set -e
set -x

if [ $# -ne 1 ]
then
    echo "Usage: $0 [ Maven's settings file path ]"
    exit 1
fi

if type -p java; then
    echo found java executable in PATH
    _java=java
elif [[ -n "$JAVA_HOME" ]] && [[ -x "$JAVA_HOME/bin/java" ]];  then
    echo found java executable in JAVA_HOME
    _java="$JAVA_HOME/bin/java"
else
    echo "no java"
fi

if [[ "$_java" ]]; then
    if [[ -z "${TEST_ENVIRONMENT}" ]]; then
      echo "TEST_ENVIRONMENT env variable is not defined using default"
      ./gradlew -Pmaven.settings.location=$1 reportScoverage aggregateScoverage
    else
      ./gradlew -Pmaven.settings.location=$1 clean
      ./gradlew -Pmaven.settings.location=$1 build -x test
      ./gradlew -Pmaven.settings.location=$1 test
    fi
fi