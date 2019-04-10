#Base image
FROM tomcat:7

#Working Directory
#WORKDIR d:/School/SER 422/Labs/lab4_lfajardo

RUN mkdir /usr/local/tomcat8080/webapps

COPY d:/School/SER 422/tomcat-8.5.37-8080/webapps/RestExampleAPI.war /usr/local/tomcat/webapps/

EXPOSE 8080