#!/bin/sh

BASEDIR=$(cd "$(dirname "$0")"; pwd -P)
WRAPPER_JAR="$BASEDIR/.mvn/wrapper/maven-wrapper.jar"

if [ ! -f "$WRAPPER_JAR" ]; then
  echo "Maven wrapper JAR not found: $WRAPPER_JAR"
  exit 1
fi

exec java "-Dmaven.multiModuleProjectDirectory=$BASEDIR" -classpath "$WRAPPER_JAR" org.apache.maven.wrapper.MavenWrapperMain "$@"
