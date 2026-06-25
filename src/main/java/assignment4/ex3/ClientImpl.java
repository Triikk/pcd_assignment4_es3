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

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, true, false, false, null);

        if (first) {
            channel.basicPublish("", QUEUE_NAME, null, Integer.toString(1).getBytes("UTF-8"));
        }

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            long deliveryTag = delivery.getEnvelope().getDeliveryTag();
            int value = Integer.parseInt(new String(delivery.getBody(), "UTF-8"));

            System.out.println(" [x] Received token " + value
                    + " by thread: " + Thread.currentThread().getName());

            System.out.println("---> ENTRO IN SEZIONE CRITICA (token " + value + ")");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("<--- ESCO DALLA SEZIONE CRITICA");

            int next = value + 1;
            channel.basicPublish("", QUEUE_NAME, null, Integer.toString(next).getBytes("UTF-8"));
            System.out.println(" [x] Inviato token " + next);

            channel.basicAck(deliveryTag, false);
        };

        channel.basicQos(1);  // un solo messaggio non-ackato per consumer alla volta

        boolean autoAck = false;//se crasha non toglie il messaggio
        String consumerTag = channel.basicConsume(QUEUE_NAME, autoAck, deliverCallback,
                consTag -> {});

        System.out.println("Consumer configured - tag: " + consumerTag);
        // No close, no latch — the consumer thread keeps the JVM running.
        // When the queue is empty, that thread simply blocks waiting for
        // the next delivery. CTRL+C to exit.
    }

    @Override
    public void send(String queueName) {

    }

    @Override
    public void recv(String queueName) {

    }
}
