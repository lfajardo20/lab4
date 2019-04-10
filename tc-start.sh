#!/bin/bash

#Change tomcat8081 ports
tc_conf="/usr/local/tomcat8080/conf"
sed 's/8080/8081/' $tc_conf/server.xml
sed 's/8009/8010/' $tc_conf/server.xml
sed 's/8443/9442/' $tc_conf/server.xml

#tomcats start
sh /usr/local/tomcat8080/bin/startup.sh
sh /usr/local/tomcat8081/bin/startup.sh
