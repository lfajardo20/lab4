FROM ubuntu:latest
RUN apt-get -y update && apt-get -y upgrade
RUN apt-get -y install openjdk-8-jdk wget

RUN mkdir /usr/local/tomcat8080
#RUN mkdir /usr/local/tomcat8081

RUN wget http://www-us.apache.org/dist/tomcat/tomcat-8/v8.5.30/bin/apache-tomcat-8.5.30.tar.gz -O /tmp/tomcat.tar.gz
RUN cd /tmp && tar xvfz tomcat.tar.gz

RUN cp -Rv /tmp/apache-tomcat-8.5.30/* /usr/local/tomcat8080
#RUN cp -Rv /tmp/apache-tomcat-8.5.30/* /usr/local/tomcat8081

EXPOSE 8080
#EXPOSE 8081

CMD /usr/local/tomcat8080/bin/catalina.sh run