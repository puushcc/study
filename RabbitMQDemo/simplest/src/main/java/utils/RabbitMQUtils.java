package utils;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @Description: RabbitMQ创建连接工具类
 * @author: scott
 * @date: 2021年01月12日 14:34
 */
public class RabbitMQUtils {

    private static ConnectionFactory connectionFactory;

    static {
        connectionFactory = new ConnectionFactory();
        //创建连接rabbitmq主机
        connectionFactory.setHost("120.24.21.98");
        //设置连接那个虚拟主机
        connectionFactory.setPort(5672);
        //设置连接那个虚拟主机
        connectionFactory.setVirtualHost("/ems");
        //设置访问虚拟主机的用户名和密码
        connectionFactory.setUsername("ems");
        connectionFactory.setPassword("123456");
    }

    public static Connection getConnection(){
        try {
            //获取连接对象
            return connectionFactory.newConnection();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static void closeConnectionAndChannel(Channel channel, Connection connection){
        try {
            if (channel != null) {
                channel.close();
            }
            if (connection != null) {
                connection.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
