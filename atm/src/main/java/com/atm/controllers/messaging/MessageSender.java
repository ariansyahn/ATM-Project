package com.atm.controllers.messaging;

import com.atm.controllers.rest.BalanceController;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.jms.*;
import java.security.SecureRandom;
public class MessageSender {
    private static final Logger logger = LoggerFactory.getLogger(MessageSender.class);
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
            logger.info("Sending {} to queue", msg);
            producer.send(msg);
            session.close();
            connection.close();
        } catch (JMSException e) {
            logger.error("Sender createTask method error", e);
            System.out.println(e.getMessage());
        }
    }
//        new Thread(sendTask).start();
}
