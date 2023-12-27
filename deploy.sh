#!/bin/bash

set -e
set -x

if [ $# -ne 1 ]
then
    echo "Usage: $0 [ Maven's settings file path ]"
    exit 1
fi

source gradle.properties

dir=$PWD
echo $dir
echo "Listing file-module classes ..."
ls -l -R file-module/build/classes

echo "Extracting file-module JAR content ..."
mkdir -p file-module/build/extracted-jar
cd file-module/build/extracted-jar
jar -xfv ../libs/file-module-${projectVersion}.jar

cd $dir
if [[ "$BRANCH_NAME" =~ ^(master$|support/.|release/.) ]]
then
    ./gradlew --stacktrace -Pmaven.settings.location=$1 publish
fi

