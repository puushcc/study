package wordQuene;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import utils.RabbitMQUtils;

import java.io.IOException;

public class Provider {

    public static void main(String[] args) throws IOException {
        //获取连接对象
        Connection connection = RabbitMQUtils.getConnection();
        //获取通道
        Channel channel = connection.createChannel();
        //获取通声明队列
        channel.queueDeclare("word",false,false,false,null);
        for (int i = 0; i < 10; i++) {
            //生成消息
            channel.basicPublish("","word",null,"hello word quene".getBytes());
        }
        //关闭资源
        RabbitMQUtils.closeConnectionAndChannel(channel,connection);
    }
}
