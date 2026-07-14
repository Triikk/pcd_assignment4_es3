package assignment4.ex3;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class CriticalSection {

    private static final String QUEUE_NAME = "CriticalSection";

    static void main(String[] args) throws IOException, TimeoutException {

        boolean first = args.length == 1 && Boolean.parseBoolean(args[0]);

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, true, false, true, null);

        if (first) {
            channel.basicPublish("", QUEUE_NAME, null, Integer.toString(1).getBytes(StandardCharsets.UTF_8));
        }

        while (true) {
            System.out.println("--> In non CS");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("<-- Out of non CS");

            System.out.println("trying to enter CS");

            GetResponse response;
            while ((response = channel.basicGet(QUEUE_NAME, true)) == null) {//busy waiting
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            int value = Integer.parseInt(new String(response.getBody(), StandardCharsets.UTF_8));
            System.out.println(" [x] Received token " + value
                    + " by thread: " + Thread.currentThread().getName());

            System.out.println("---> START OF CS (token " + value + ")");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("<--- OUT OF CS");

            System.out.println("Release CS");

            int next = value + 1;
            channel.basicPublish("", QUEUE_NAME, null,
                    Integer.toString(next).getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent token " + next);

        }
    }
}
