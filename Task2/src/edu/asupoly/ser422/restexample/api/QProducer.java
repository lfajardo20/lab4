import javax.jms.*;

/**
   This simple producer pushes a single text message to a Queue. The Queue
   is command-line argument 0 and the text message is command-line argument 1.
   Run the corresponding QConsumer to consume the message.
 */
public class QProducer {

    public static void main(String args[]) throws Exception {
	try {
	    Connection connection = JMSHelperActiveMQ.getJMSConnection();
	    connection.start();
	    Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	    Destination destination = session.createQueue("Lab4Log");
 
	    // Create a MessageProducer from the Session to the Topic or Queue
	    MessageProducer producer = session.createProducer(destination);
	    producer.setDeliveryMode(DeliveryMode.PERSISTENT);

	    TextMessage msg = session.createTextMessage("Yo bitch is a hoe");
	    producer.send(msg);
	
	    session.close();
	    connection.stop();
	    connection.close();
	} catch (Throwable tw) {
	    tw.printStackTrace();
	}
	//}
    }
}
