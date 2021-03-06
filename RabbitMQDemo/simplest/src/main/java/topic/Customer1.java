package topic;


import com.rabbitmq.client.*;
import utils.RabbitMQUtils;

import java.io.IOException;

public class Customer1 {
    public static void main(String[] args) throws IOException {
        //获取连接对象
        Connection connection = RabbitMQUtils.getConnection();
        //获取通道
        Channel channel = connection.createChannel();
        //通道声明交换机以及交换的类型
        channel.exchangeDeclare( "topic" ,  "topic" );
        //创建一个临时队列
        String queue = channel.queueDeclare( ).getQueue( ) ;
        // 基于route key绑定队列和交换机
        channel.queueBind(queue,"topic" ,  "user.*" );
        //消费消息
        channel.basicConsume(queue,true,new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("消费者1"+new String(body));
            }
        });
    }
}
