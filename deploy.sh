#!/bin/bash

set -e
set -x

if [ $# -ne 1 ]
then
    echo "Usage: $0 [ Maven's settings file path ]"
    exit 1
fi

if [[ "$BRANCH_NAME" =~ ^(master$|support/.|release/.) ]]
then
    ./gradlew --stacktrace -Pmaven.settings.location=$1 publish
fi

