package helloword;

import com.rabbitmq.client.*;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Description: 开发消费者 （直连）
 * @author: scott
 * @date: 2021年01月12日 14:22
 */
public class Customer {

    public static void main(String[] args) throws IOException, TimeoutException {
        //创建连接mq的连接工厂对象
        ConnectionFactory connectionFactory = new ConnectionFactory();
        //创建连接rabbitmq主机
        connectionFactory.setHost("120.24.21.98");
        //设置连接那个虚拟主机
        connectionFactory.setPort(5672);
        //设置连接那个虚拟主机
        connectionFactory.setVirtualHost("/ems");
        //设置访问虚拟主机的用户名和密码
        connectionFactory.setUsername("ems");
        connectionFactory.setPassword("123456");
        //获取连接对象
        Connection connection = connectionFactory.newConnection();
        //获取连接中通道
        Channel channel = connection.createChannel();
        //通道绑定对应消息队列
        channel.queueDeclare("hello",false,false,false,null);
        //消费消息
        //参数1:消费那个队列的消息队列名称
        //参数2:开始消息的自动确认机制
        //参数3:消费时的回调接口
        channel.basicConsume("hello",true,new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("++++++++++++++++"+new String(body));
            }
        });
        channel.close();
        connection.close();
    }

}
