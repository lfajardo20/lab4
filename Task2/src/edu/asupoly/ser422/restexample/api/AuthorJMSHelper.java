package edu.asupoly.ser422.restexample.api;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;

// This is the ActiveMQ version
public class AuthorJMSHelper {

    public static String jmsURL = "tcp://localhost:61616";

    public Connection getJMSConnection() throws Exception {
        // Create a ConnectionFactory
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(jmsURL);
        return connectionFactory.createConnection();
    }

}