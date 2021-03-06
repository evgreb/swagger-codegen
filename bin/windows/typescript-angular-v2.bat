set executable=.\modules\swagger-codegen-cli\target\swagger-codegen-cli.jar

If Not Exist %executable% (
  mvn clean package
)

REM set JAVA_OPTS=%JAVA_OPTS% -Xmx1024M
set ags=generate -i modules\swagger-codegen\src\test\resources\2_0\petstore.yaml -l typescript-angular -o samples\client\petstore\typescript-angular-v2\default --additional-properties ngVersion=2

REM java %JAVA_OPTS% -jar %executable% %ags%
