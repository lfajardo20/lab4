*****TASK 1*****
Inside the file of Task1.txt is the proper configurations/steps used to test my queries.

*****TASK 2*****
-ActiveMQ-all-5.15.9.jar is the jar file NECESSARY to run this section. Please place it inside of the "lib" folder.
-For the consumer, place the same jar file OUTSIDE of the Task2 folder (where the Consumer.java is in). It is NECESSARY to have that jar in the same direcory.
-Start the ActiveMQ console without any configurations from the default download on activeMQ's website. (this will start the broker so it is necessary in order to test the lab)
-Start the broker by running "bin/activemq console" under your apachemq folders/installation
-Start the tomcat servers (8080 and 8081)
-Start your apache httpd.exe
-Open google chrome and firefox and type "localhost" on both. (NOTE: dont specify a port just do localhost. This proves thread-safety.)
-NOTE: Change the build.properties tomcat_webapps path to make it to your particular tomcat
-"ant deploy" Will deploy the WAR file into the tomcat server.
-"http://localhost:8080/RestExampleAPI/rest/authors/" This url is the url to be used for the methods on Post Man. (also 8081)
-Every method invocation send the appropriate message according to the specifications on the lab. Every verb is given based on the verbs from the BooktownREST Example.
-After deploying on tomcat on 8080, go to the build.properties and change the tomcat_home to port 8081 and deploy again. This is to have both tomcats have the file.
-Test your endpoints to send messages to the producer.
****NOTE: In order to use the consumer on bash you have to run the commands I provide below****
	*FOR BASH: javac -cp ".;activemq-all-5.15.9.jar" Consumer.java to compile and java -cp ".;activemq-all-5.15.9.jar" Consumer for running it. If this doesnt work, change the " ; " for a " : "
	*In the command line: go to the root of the project where the activemq jar and the consumer.java are at
	*type "javac -cp activemq-all-5.15.9.jar Consumer.java" to compile the class
	*Then type "java -cp .; activemq-all-5.15.9.jar Consumer" to run the consumer class and consume a message
 	*The consumer consumes all messages that are enqueue and will wait for 1000 ms for another message and then terminate.
*NOTE: All the verbs and status codes are implemented from what was natively placed on the BooktownREST Example. No adjustments or enhancements have been made.

*****TASK 3*****
NOTE: I am using docker toolbox (windows 10 version) and some commands/urls might be a bit different and will try my best to give you the right command
-Open docker
-run the activemq broker (this is for the producer to send messages when we hit our endpoints in postman)
-On the default path (mine is D/Program Files/Docker Toolbox) you will clone my repo to create the image of my application.
-run "git clone https://github.com/lfajardo20/lab4.git"
-After you have successfully cloned the repo, cd into Task2 (cd Task2)
-run "ls" and verify that the source files for booktown are there (e.g lib, properties, src ,build.xml, etc.)
-If everything looks good, run 'ant build' (this will create the war file that is necessary for when building the image since it is coded in the Dockerfile.)
-cd back into lab4 (cd ../) where the dockerfile is at.
-Then run "docker build ." No tags necessary or you can place some if you wish
-Get the image ID and do "docker run -p 8089:8080 <Image ID>"
-I currently get an error stating that my sh script couldn't be found evem though its in the same directory. Can't get passed this. No more implementation done.
-Dockerfile was created
-Script file was created
-Appropriate dirs created and copied files appropriately
