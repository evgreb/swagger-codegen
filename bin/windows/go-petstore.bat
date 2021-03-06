set executable=.\modules\swagger-codegen-cli\target\swagger-codegen-cli.jar

If Not Exist %executable% (
  mvn clean package
)

REM set JAVA_OPTS=%JAVA_OPTS% -Xmx1024M -Dlogback.configurationFile=bin/logback.xml
set ags=generate -t modules\swagger-codegen\src\main\resources\go -i modules\swagger-codegen\src\test\resources\2_0\petstore-with-fake-endpoints-models-for-testing.yaml -l go -o samples\client\petstore\go\go-petstore -DpackageName=petstore

REM java %JAVA_OPTS% -jar %executable% %ags%
