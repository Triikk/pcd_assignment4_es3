package assignment4.ex3;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class ClientImpl implements Client {

    private static final String QUEUE_NAME = "A";
    private Resource resource;

    public static void main(String[] args) throws Exception {
        boolean first = args.length == 2 && Boolean.parseBoolean(args[1]);

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (
                Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()) {

            channel.queueDeclare(QUEUE_NAME, true, false, false, null);

            if(first){
                channel.basicPublish("", QUEUE_NAME, null, "msg".getBytes("UTF-8"));
            }

            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [x] Received '" + message + "' by thread: " + Thread.currentThread().getName());

                System.out.println("I'M IN CRITICAL SECTION");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
                System.out.println("I'M IN CRITICAL SECTION");
                System.out.println(" [x] Sent '" + message + "'");
            };

            boolean autoAck = true;
            String consumerTag = channel.basicConsume(QUEUE_NAME, autoAck, deliverCallback,
                    /* cancellation callback */ consTag -> {
                    });

            System.out.println("Consumer configured - tag: " + consumerTag);
        }
    }

    @Override
    public void send(String queueName) {

    }

    @Override
    public void recv(String queueName) {

    }
}
