#!/bin/sh

set -vx

rm ./bin/ph-client-test.jar

rm -rf ./bin/*

rm -rf ./target/*

cd ./src/

javac -classpath ../lib/log4j-1.2.17.jar -d ../bin/ com/hortonworks/phoenix/PhClientTest.java

cp ../conf/*.* ../bin/

rm ../bin/krb5.conf
rm ../bin/jaas.config


cd ../bin

jar cvf ph-client-test.jar * 

cp ph-client-test.jar ../target/

cd ..