#!/bin/sh

SCRIPT="$0"

while [ -h "$SCRIPT" ] ; do
  ls=`ls -ld "$SCRIPT"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    SCRIPT="$link"
  else
    SCRIPT=`dirname "$SCRIPT"`/"$link"
  fi
done

if [ ! -d "${APP_DIR}" ]; then
  APP_DIR=`dirname "$SCRIPT"`/..
  APP_DIR=`cd "${APP_DIR}"; pwd`
fi

executable="./modules/swagger-codegen-cli/target/swagger-codegen-cli.jar"

if [ ! -f "$executable" ]
then
  mvn clean package
fi

# if you've executed sbt assembly previously it will use that instead.
export JAVA_OPTS="${JAVA_OPTS} -XX:MaxPermSize=256M -Xmx1024M -Dlogback.configurationFile=bin/logback.xml"
ags="$@ generate --artifact-id swagger-jaxrs-resteasy-eap-joda-server -i modules/swagger-codegen/src/test/resources/2_0/petstore.yaml -l jaxrs-resteasy-eap -o samples/server/petstore/jaxrs-resteasy/eap-joda -DhideGenerationTimestamp=true -c ./bin/jaxrs-resteasy-eap-joda-petstore-server.json"

echo "Removing files and folders under samples/server/petstore/jaxrs-resteasy/eap-joda/src/main"
rm -rf samples/server/petstore/jaxrs-resteasy/eap-joda/src/main
rm -rf samples/server/petstore/jaxrs-resteasy/eap-joda/src/gen
find samples/server/petstore/jaxrs-resteasy/eap-joda -maxdepth 1 -type f ! -name "README.md" -exec rm {} +

java $JAVA_OPTS -jar $executable $ags
