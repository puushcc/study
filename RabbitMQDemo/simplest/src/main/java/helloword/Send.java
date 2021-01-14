package helloword;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Description: 开发生产者 （直连）
 * @author: scott
 * @date: 2021年01月12日 13:56
 */
public class Send {


    //生产消息
    @Test
    public void testSendMessage() throws IOException, TimeoutException {

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
        //参数1:队列名称如果队列不存在自动创建
        //参数2:用来定义队列特性是否要持久化 true 持久化队列  false 不持久化|
        //参数3: 是否独占队列   true 独占  false 不独占
        //参数4: 是否消费完自动删除队列  true 自动删除  false 不自动删除
        //参数5: 额外附加参数
        channel.queueDeclare("hello",false,false,false,null);

        //发布消息
        //参数1：交换机名称 参数2：队列民称 参数3：额外设置 参数4：消息的具体内容
        channel.basicPublish("","hello",null,"hello rabbitmq".getBytes());
        channel.close();
        connection.close();
    }

}
