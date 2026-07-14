package assignment4.ex3;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;

public class Subscriber {

    private static final String QUEUE_NAME = "CriticalSub";

    static void main(String[] args) throws Exception {
        boolean first = args.length == 1 && Boolean.parseBoolean(args[0]);

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, true, false, true, null);

        if (first) {
            channel.basicPublish("", QUEUE_NAME, null, Integer.toString(1).getBytes(StandardCharsets.UTF_8));
        }

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (_, delivery) -> {
            int value = Integer.parseInt(new String(delivery.getBody(), StandardCharsets.UTF_8));

            System.out.println(" [x] Received token " + value + " by thread: " + Thread.currentThread().getName());

            System.out.println("---> START OF CS (token " + value + ")");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("<--- OUT OF CS");

            int next = value + 1;
            channel.basicPublish("", QUEUE_NAME, null, Integer.toString(next).getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent token " + next);
        };

        String consumerTag = channel.basicConsume(QUEUE_NAME, true, deliverCallback, _ -> {
        });

        System.out.println("Consumer configured - tag: " + consumerTag);
    }
}
