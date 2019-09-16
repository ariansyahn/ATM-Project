package com.atm.controllers.messaging;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.jms.*;
import java.security.SecureRandom;
public class MessageSender {

    public void createTask(String message){
//        Runnable sendTask = () -> {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        Connection connection = null;
        try {
            connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue("bank");
            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            TextMessage msg = session.createTextMessage(message);
            producer.send(msg);
            session.close();
            connection.close();
        } catch (JMSException e) {
//            logger.error("Sender createTask method error", e);
            System.out.println(e.getMessage());
        }
    }
//        new Thread(sendTask).start();
}
