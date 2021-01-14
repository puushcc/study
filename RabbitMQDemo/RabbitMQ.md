## 一、RabbitMQ简介

AMQP，即Advanced Message Queuing Protocol，高级消息队列协议，是应用层协议的一个开放标准，为面向消息的[中间件](http://www.diggerplus.org/archives/tag/中间件)设计。消息中间件主要用于组件之间的解耦，消息的发送者无需知道消息使用者的存在，反之亦然。
AMQP的主要特征是面向消息、队列、路由（包括点对点和发布/订阅）、可靠性、安全。
RabbitMQ是一个开源的AMQP实现，服务器端用Erlang语言编写，支持多种客户端，如：Python、Ruby、.NET、Java、JMS、C、PHP、ActionScript、XMPP、STOMP等，支持AJAX。用于在分布式系统中存储转发消息，在易用性、扩展性、高可用性等方面表现不俗。
下面将重点介绍RabbitMQ中的一些基础概念，了解了这些概念，是使用好RabbitMQ的基础。

### ConnectionFactory、Connection、Channel

ConnectionFactory、Connection、Channel都是RabbitMQ对外提供的API中最基本的对象。Connection是RabbitMQ的socket链接，它封装了socket协议相关部分逻辑。ConnectionFactory为Connection的制造工厂。
Channel是我们与RabbitMQ打交道的最重要的一个接口，我们大部分的业务操作是在Channel这个接口中完成的，包括定义Queue、定义Exchange、绑定Queue与Exchange、发布消息等。

### Queue

Queue（队列）是RabbitMQ的内部对象，用于存储消息，用下图表示。
![img](https://img2018.cnblogs.com/blog/774371/201908/774371-20190819103814085-804287529.png)

RabbitMQ中的消息都只能存储在Queue中，生产者（下图中的P）生产消息并最终投递到Queue中，消费者（下图中的C）可以从Queue中获取消息并消费。

![img](https://img2018.cnblogs.com/blog/774371/201908/774371-20190819103830954-867723738.png)

多个消费者可以订阅同一个Queue，这时Queue中的消息会被平均分摊给多个消费者进行处理，而不是每个消费者都收到所有的消息并处理。



### Message acknowledgment

在实际应用中，可能会发生消费者收到Queue中的消息，但没有处理完成就宕机（或出现其他意外）的情况，这种情况下就可能会导致消息丢失。为了避免这种情况发生，我们可以要求消费者在消费完消息后发送一个回执给RabbitMQ，RabbitMQ收到消息回执（Message acknowledgment）后才将该消息从Queue中移除；如果RabbitMQ没有收到回执并检测到消费者的RabbitMQ连接断开，则RabbitMQ会将该消息发送给其他消费者（如果存在多个消费者）进行处理。这里不存在timeout概念，一个消费者处理消息时间再长也不会导致该消息被发送给其他消费者，除非它的RabbitMQ连接断开。
这里会产生另外一个问题，如果我们的开发人员在处理完业务逻辑后，忘记发送回执给RabbitMQ，这将会导致严重的bug——Queue中堆积的消息会越来越多；消费者重启后会重复消费这些消息并重复执行业务逻辑…

### Message durability

如果我们希望即使在RabbitMQ服务重启的情况下，也不会丢失消息，我们可以将Queue与Message都设置为可持久化的（durable），这样可以保证绝大部分情况下我们的RabbitMQ消息不会丢失。但依然解决不了小概率丢失事件的发生（比如RabbitMQ服务器已经接收到生产者的消息，但还没来得及持久化该消息时RabbitMQ服务器就断电了），如果我们需要对这种小概率事件也要管理起来，那么我们要用到事务。由于这里仅为RabbitMQ的简单介绍，所以这里将不讲解RabbitMQ相关的事务。

### Prefetch count

前面我们讲到如果有多个消费者同时订阅同一个Queue中的消息，Queue中的消息会被平摊给多个消费者。这时如果每个消息的处理时间不同，就有可能会导致某些消费者一直在忙，而另外一些消费者很快就处理完手头工作并一直空闲的情况。我们可以通过设置prefetchCount来限制Queue每次发送给每个消费者的消息数，比如我们设置prefetchCount=1，则Queue每次给每个消费者发送一条消息；消费者处理完这条消息后Queue会再给该消费者发送一条消息。

![img](https://img2018.cnblogs.com/blog/774371/201908/774371-20190819104007647-1027286628.png)

### Exchange

在上一节我们看到生产者将消息投递到Queue中，实际上这在RabbitMQ中这种事情永远都不会发生。实际的情况是，生产者将消息发送到Exchange（交换器，下图中的X），由Exchange将消息路由到一个或多个Queue中（或者丢弃）。

![img](https://img2018.cnblogs.com/blog/774371/201908/774371-20190819104028789-412276700.png)

Exchange是按照什么逻辑将消息路由到Queue的？这个将在Binding一节介绍。
RabbitMQ中的Exchange有四种类型，不同的类型有着不同的路由策略，这将在Exchange Types一节介绍。

### routing key

生产者在将消息发送给Exchange的时候，一般会指定一个routing key，来指定这个消息的路由规则，而这个routing key需要与Exchange Type及binding key联合使用才能最终生效。
在Exchange Type与binding key固定的情况下（在正常使用时一般这些内容都是固定配置好的），我们的生产者就可以在发送消息给Exchange时，通过指定routing key来决定消息流向哪里。
RabbitMQ为routing key设定的长度限制为255 bytes。

### Binding

RabbitMQ中通过Binding将Exchange与Queue关联起来，这样RabbitMQ就知道如何正确地将消息路由到指定的Queue了。
![img](https://img2018.cnblogs.com/blog/774371/201908/774371-20190819104128931-1338459538.png)

 

### Binding key

在绑定（Binding）Exchange与Queue的同时，一般会指定一个binding key；消费者将消息发送给Exchange时，一般会指定一个routing key；当binding key与routing key相匹配时，消息将会被路由到对应的Queue中。这个将在Exchange Types章节会列举实际的例子加以说明。
在绑定多个Queue到同一个Exchange的时候，这些Binding允许使用相同的binding key。
binding key 并不是在所有情况下都生效，它依赖于Exchange Type，比如fanout类型的Exchange就会无视binding key，而是将消息路由到所有绑定到该Exchange的Queue。

### Exchange Types

RabbitMQ常用的Exchange Type有fanout、direct、topic、headers这四种（AMQP规范里还提到两种Exchange Type，分别为system与自定义，这里不予以描述），下面分别进行介绍。

### fanout

fanout类型的Exchange路由规则非常简单，它会把所有发送到该Exchange的消息路由到所有与它绑定的Queue中。
![img](https://img2018.cnblogs.com/blog/774371/201908/774371-20190819104152177-2053988251.png)

 

上图中，生产者（P）发送到Exchange（X）的所有消息都会路由到图中的两个Queue，并最终被两个消费者（C1与C2）消费。

### direct

direct类型的Exchange路由规则也很简单，它会把消息路由到那些binding key与routing key完全匹配的Queue中。



![img](https://img2018.cnblogs.com/blog/774371/201908/774371-20190819104210818-1771762193.png)

 

以上图的配置为例，我们以routingKey=”error”发送消息到Exchange，则消息会路由到Queue1（amqp.gen-S9b…，这是由RabbitMQ自动生成的Queue名称）和Queue2（amqp.gen-Agl…）；如果我们以routingKey=”info”或routingKey=”warning”来发送消息，则消息只会路由到Queue2。如果我们以其他routingKey发送消息，则消息不会路由到这两个Queue中。

### topic

前面讲到direct类型的Exchange路由规则是完全匹配binding key与routing key，但这种严格的匹配方式在很多情况下不能满足实际业务需求。topic类型的Exchange在匹配规则上进行了扩展，它与direct类型的Exchage相似，也是将消息路由到binding key与routing key相匹配的Queue中，但这里的匹配规则有些不同，它约定：

- routing key为一个句点号“. ”分隔的字符串（我们将被句点号“. ”分隔开的每一段独立的字符串称为一个单词），如“stock.usd.nyse”、“nyse.vmw”、“quick.orange.rabbit”
- binding key与routing key一样也是句点号“. ”分隔的字符串
- binding key中可以存在两种特殊字符“*”与“#”，用于做模糊匹配，其中“*”用于匹配一个单词，“#”用于匹配多个单词（可以是零个）

 

![img](https://img2018.cnblogs.com/blog/774371/201908/774371-20190819104233644-253637000.png)

 

以上图中的配置为例，routingKey=”quick.orange.rabbit”的消息会同时路由到Q1与Q2，routingKey=”lazy.orange.fox”的消息会路由到Q1，routingKey=”lazy.brown.fox”的消息会路由到Q2，routingKey=”lazy.pink.rabbit”的消息会路由到Q2（只会投递给Q2一次，虽然这个routingKey与Q2的两个bindingKey都匹配）；routingKey=”quick.brown.fox”、routingKey=”orange”、routingKey=”quick.orange.male.rabbit”的消息将会被丢弃，因为它们没有匹配任何bindingKey。

### headers

headers类型的Exchange不依赖于routing key与binding key的匹配规则来路由消息，而是根据发送的消息内容中的headers属性进行匹配。
在绑定Queue与Exchange时指定一组键值对；当消息发送到Exchange时，RabbitMQ会取到该消息的headers（也是一个键值对的形式），对比其中的键值对是否完全匹配Queue与Exchange绑定时指定的键值对；如果完全匹配则消息会路由到该Queue，否则不会路由到该Queue。
该类型的Exchange没有用到过（不过也应该很有用武之地），所以不做介绍。

### RPC

MQ本身是基于异步的消息处理，前面的示例中所有的生产者（P）将消息发送到RabbitMQ后不会知道消费者（C）处理成功或者失败（甚至连有没有消费者来处理这条消息都不知道）。
但实际的应用场景中，我们很可能需要一些同步处理，需要同步等待服务端将我的消息处理完成后再进行下一步处理。这相当于RPC（Remote Procedure Call，远程过程调用）。在RabbitMQ中也支持RPC。

![img](https://img2018.cnblogs.com/blog/774371/201908/774371-20190819104253450-1490098886.png)

 


RabbitMQ中实现RPC的机制是：

- 客户端发送请求（消息）时，在消息的属性（MessageProperties，在AMQP协议中定义了14中properties，这些属性会随着消息一起发送）中设置两个值replyTo（一个Queue名称，用于告诉服务器处理完成后将通知我的消息发送到这个Queue中）和correlationId（此次请求的标识号，服务器处理完成后需要将此属性返还，客户端将根据这个id了解哪条请求被成功执行了或执行失败）
- 服务器端收到消息并处理
- 服务器端处理完消息后，将生成一条应答消息到replyTo指定的Queue，同时带上correlationId属性
- 客户端之前已订阅replyTo指定的Queue，从中收到服务器的应答消息后，根据其中的correlationId属性分析哪条请求被执行了，根据执行结果进行后续业务处理

## 二、RabbitMQ安装

官网：https://www.rabbitmq.com/download.html

### 使用docker安装rabbitMQ

常用的docker镜像操作：

| 操作 | 命令                                                        | 说明                                      |
| ---- | ----------------------------------------------------------- | ----------------------------------------- |
| 检索 | docker search keyword     eg：docker search tomcat          | 去dockers Hub 搜索镜像的详细信息          |
| 拉取 | docker pull keyword：tag     eg：docker pull tomcat：latest | tag是可选的，不指定默认拉取latest最新版本 |
| 列表 | docker images                                               | 查看本地docker仓库的所以镜像              |
| 删除 | docker rmi imageID eg：docker rmi 666666666                 | 删除本地docker仓库镜像                    |

拉取镜像：

```shell
docker pull rabbitmq:management
```

查看docker镜像列表：

```shell
docker images
```

Docker容器操作：
ok，上面命令执行后，镜像就已经拉取到本地仓库了，然后可以进行容器操作，启动rabbitMQ

简单版

```shell
docker run -d -p 5672:5672 -p 15672:15672 --name rabbitmq d4489446dfcc
```

- -d 后台运行
- -p 隐射端口
- --name 指定rabbitMQ名称

复杂版（设置账户密码，hostname）

```powershell
docker run -d -p 15672:15672  -p  5672:5672  -e RABBITMQ_DEFAULT_USER=admin -e RABBITMQ_DEFAULT_PASS=admin --name rabbitmq --hostname=rabbitmqhostone  d4489446dfcc
```

- -d 后台运行
- -p 隐射端口
- --name 指定rabbitMQ名称
- RABBITMQ_DEFAULT_USER 指定用户账号
- RABBITMQ_DEFAULT_PASS 指定账号密码

执行如上命令后访问：http://ip:15672/

默认账号密码：guest/guest

其它常用容器命令：

查看运行中的容器

```shell
# 查看所有的容器用命令docker ps -a
docker ps
```

启动容器

```
# eg: docker start 9781cb2e64bd
docker start CONTAINERID[容器ID]
```

stop容器

```shell
docker stop CONTAINERID[容器ID]
```

删除一个容器

```shell
 docker rm CONTAINERID[容器ID]
```

查看Docker容器日志

```
# eg：docker logs 9781cb2e64bd
docker logs container‐name[容器名]/container‐id[容器ID]
```

## 三、Web管理界面

http://120.24.21.98:15672/

## 四、RabbitMQ第一个程序

### 4.1引入依赖

```xml
<dependency>
      <groupId>com.rabbitmq</groupId>
      <artifactId>amqp-client</artifactId>
      <version>5.9.0</version>
</dependency>
```

### 4.2第一种模型（直连)

#### 	4.2.1开发生产者

```java
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
```

#### 	4.2.2开发消费者

```java
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
```

工具类

```java
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
```

### 4.3第二种模型（Work quene)

Work queues，也被称为（Task queues)，任务模型。当消息处理比较耗时的时候，可能生产消息的速度会远远大于消息的消费速度。长此以往，消息就会堆积越来越多无法及时处理。此时就可以使用work模型:让多个消费者绑定到一个队列，共同消费队列中的消息。队列中的消息一旦消费，就会消失，因此任务是不会被重复执行的.

![img](https://img2018.cnblogs.com/blog/774371/201908/774371-20190819103830954-867723738.png)

角色:

- P:   生产者:任务的发布者
- C1∶消费者-1，领取任务并且完成任务，假设完成速度较慢
- C2∶消费者-2,   领取任务并完成任务，假设完成速度快

#### 4.3.1开发生产者

```java
for (int i = 0; i < 10; i++) {
            //生成消息
            channel.basicPublish("","word",null,"hello word quene".getBytes());
        }
```

#### 4.3.2开发消费者-1

```java
package wordQuene;


import com.rabbitmq.client.*;
import utils.RabbitMQUtils;

import java.io.IOException;

public class Customer1 {

    public static void main(String[] args) throws IOException {
        //获取连接对象
        Connection connection = RabbitMQUtils.getConnection();
        //获取通道
        Channel channel = connection.createChannel();
        //获取通声明队列
        channel.queueDeclare("word",false,false,false,null);
        channel.basicConsume("word",true,new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("++++++++"+new String(body));
            }
        });
    }
}

```

#### 4.3.3开发消费者-2

```java
package wordQuene;


import com.rabbitmq.client.*;
import utils.RabbitMQUtils;

import java.io.IOException;

public class Customer2 {

    public static void main(String[] args) throws IOException {
        //获取连接对象
        Connection connection = RabbitMQUtils.getConnection();
        //获取通道
        Channel channel = connection.createChannel();
        //获取通声明队列
        channel.queueDeclare("word",false,false,false,null);
        channel.basicConsume("word",true,new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("=========="+new String(body));
            }
        });
    }
}
```

### 4.4第三种模型（fanout)

![img](https://img2018.cnblogs.com/blog/774371/201908/774371-20190819104152177-2053988251.png)

在广播模式下，消息发送流程是这样的:

- 可以有多个消费者
- 每个消费者有自己的queue(队列)
- 每个队列都要绑定到Exchange (交换机)
- 生产者发送的消息，只能发送到交换机，交换机来决定要发给哪个队列，生产者无法决定。
- 交换机把消息发送给绑定过的所有队列
- 队列的消费者都能拿到消息。实现一条消息被多个消费者消费

#### 4.4.1开发生产者

```java
//获取连接对象
Connection connection = RabbitMQUtils.getConnection();
//获取通道
Channel channel = connection.createChannel();
//将通道声明指定交换机――//参数1:交换机名称―参数2:交换机类型fanout广播类型
channel.exchangeDeclare(  "logs" ,  "fanout" ) ;
//发送消息
channel.basicPublish( "logs" , "" ,  null, "fanout type message " . getBytes());
//释放资源
RabbitMQUtils.closeConnectionAndChannel(channel, connection );
```

#### 4.4.2开发消费者-1

```java
//通道绑定交换机
        channel.exchangeDeclare(  "logs" , "fanout" );
        //临时队列
        String queueName = channel.queueDeclare( ) .getQueue( ) ;
        //绑定交换机和队列
        channel.queueBind(queueName,"logs" ,"" );
        //消费消息
        channel.basicConsume(queueName,true,new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("消费者1"+new String(body));
            }
        });
```

#### 4.4.2开发消费者-2

```java
//通道绑定交换机
        channel.exchangeDeclare(  "logs" , "fanout" );
        //临时队列
        String queueName = channel.queueDeclare( ) .getQueue( ) ;
        //绑定交换机和队列
        channel.queueBind(queueName,"logs" ,"" );
        //消费消息
        channel.basicConsume(queueName,true,new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("消费者2"+new String(body));
            }
        });
```

### 4.5第四种模型（Routing)

#### 1.Routing之订阅模型-Direct(直连)

在F anout模式中，一条消息，会被所有订阅的队列都消费。但是，在某些场景下，我们希望不同的消息被不同的队列消费。这时就要用到Direct类型的Exchange。

在Direct模型下:

- 队列与交换机的绑定，不能是任意绑定了，而是要指定一个RoutingKey(路由key)
- 消息的发送方在向Exchange发送消息时，也必须指定消息的RoutingKey 。
- oExchange不再把消息交给每一个绑定的队列，而是根据消息的Routing Key进行判断，只有队列的 Routingkey与消息的 Routing key完全一致，才会接收到消息

![img](https://img2018.cnblogs.com/blog/774371/201908/774371-20190819104210818-1771762193.png)

- P:生产者，向Exchange发送消息，发送消息时，会指定一个routing key。
- X: Exchange (交换机)，接收生产者的消息，然后把消息递交给与routing key完全匹配的队列
- c1:消费者，其所在队列指定了需要routing key为error的消息
- C2:消费者，其所在队列指定了需要routing key 为info、error、warning 的消息

##### 开发生产者

```java
//通过通道声明交换机,参数1交换机名称，参数2：direct 路由模式
        channel.exchangeDeclare(  "logs_direct" ,  "direct" );
        //发送消息
        String routingkey ="" ;
        channel.basicPublish("logs_direct",routingkey,null,("这是基于路由key：["+routingkey+"]发送的消息").getBytes());
```

##### 开发消费者

```java
//通道声明交换机以及交换的类型
        channel.exchangeDeclare( "logs_direct" ,  "direct" );
        //创建一个临时队列
        String queue = channel.queueDeclare( ).getQueue( ) ;
        // 基于route key绑定队列和交换机
        channel.queueBind(queue,"logs_direct" ,  "info" );
        channel.queueBind(queue,"logs_direct" ,  "error" );
        channel.queueBind(queue,"logs_direct" ,  "warring" );
        //消费消息
        channel.basicConsume(queue,true,new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("消费者1"+new String(body));
            }
        });
```

#### 2.Routing之订阅模型-Topic

Topic类型的Exchange与Direct相比，都是可以根据Routingey把消息路由到不同的队列。只不过Topic 类型 Exchange可以让队列在绑定Routing key 的时候使用通配符!这种模型Routingkey一般都是由一个或多个单词组成，多个单词之间以"."分割，例如: item.insert

![img](https://img2018.cnblogs.com/blog/774371/201908/774371-20190819104233644-253637000.png)

```java
#统配符
	* (star) can substitute for exactly one word.匹配不多不少恰好1个词
    # (hash) can substitute for zero or more words.匹配一个或多个词
#如:
	audit.#		匹配audit.irs.corporate或者audit.irs等
	audit.*		只能匹配audit.irs
```

##### 开发生成者

 ```java
//通过通道声明交换机,参数1交换机名称，参数2：direct 路由模式
channel.exchangeDeclare(  "topic" ,  "topic" );
//发送消息
String routingkey ="user.save" ;
channel.basicPublish("topic",routingkey,null,("这是topic动态路由：["+routingkey+"]发送的消息").getBytes());
 ```

##### 开发消费者

```java
//通道声明交换机以及交换的类型
        channel.exchangeDeclare( "topic" ,  "topic" );
        //创建一个临时队列
        String queue = channel.queueDeclare( ).getQueue( ) ;
        // 基于route key绑定队列和交换机
        channel.queueBind(queue,"topic" ,  "user.#" );
		//channel.queueBind(queue,"topic" ,  "user.*" );
        //消费消息
        channel.basicConsume(queue,true,new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("消费者1"+new String(body));
            }
        });
```

## 五、在springboot中使用RabbitMQ

### 1、环境搭建

#### （1）引入依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

#### （2）配置配置文件

```yaml
spring:
  application:
    name: springboot_rabbitmq
  rabbitmq:
    host: 120.24.21.98
    port: 5672
    username: guest
    password: guest
    virtual-host: /
```

rabbitTemplate同来简化操作RabbitMQ，直接在项目注入即可使用

### 2、第一种模型

生产者

```java
@Autowired
private RabbitTemplate rabbitTemplate;

@Test
public void test(){
    rabbitTemplate.convertAndSend("hello","hello word");
}
```

消费者

```java
@Component
@RabbitListener(queuesToDeclare = @Queue("hello"))
public class helloCustomer {

    @RabbitHandler
    public void receivel(String message){
        System.out.println( "message:" + message );
    }


}
```

### 3、第二种模型 work

生产者 

```java
@Test
public void testwork(){
    for (int i = 0; i < 10; i++) {
        rabbitTemplate.convertAndSend("work","work模型"+i);
    }

}
```

消费者

```java
@Component
public class WorkCustomer {

    @RabbitListener(queuesToDeclare = @Queue("work"))
    public void receivel(String message){
        System.out.println("message1:"+ message);
    }

    @RabbitListener(queuesToDeclare = @Queue("work"))
    public void receivel2(String message){
        System.out.println("message2:"+ message);
    }

}
```

### 4、第三种模型 fanout

生产者

```java
/**
 * 广播
 */
@Test
public void faout(){
    rabbitTemplate.convertAndSend("logs","","Faout 模型");
}
```

消费者

```java
@Component
public class FaoutCustomer {

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue,
                    exchange = @Exchange(value = "logs",type = "fanout")
            )
    })
    public void receivel(String message){
        System.out.println("message1:"+ message);
    }

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue,
                    exchange = @Exchange(value = "logs",type = "fanout")
            )
    })
    public void receivel2(String message){
        System.out.println("message2:"+ message);
    }
}
```

### 5、第四种模型 direct

生产者

```java
/**
 * router 路由模式
 */
@Test
public void router(){
    rabbitTemplate.convertAndSend("directs","info","发送info的路由消息");
}
```

消费者

```java
@Component
public class RouterCustomer {

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue,
                    exchange = @Exchange(value = "directs",type = "direct"),
                    key = {"info","error","waring"}
            )
    })
    public void receivel(String message){
        System.out.println("message1 :" + message);
    }


    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue,
                    exchange = @Exchange(value = "directs",type = "direct"),
                    key = {"error"}
            )
    })
    public void receivel2(String message){
        System.out.println("message2 :" + message);
    }
}
```

### 6、第五种模型 Topic

生产者

```java
/**
 * router 路由模式topic
 */
@Test
public void topic(){
    rabbitTemplate.convertAndSend("topics","user.save","user.save");
}
```

消费者

```java
@Component
public class TopicCustomer {

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue,
                    exchange = @Exchange(type = "topic",name = "topics"),
                    key = {"user.save","user.*"}
            )
    })
    public void receivel(String message){
        System.out.println("message1 :" + message);
    }

    
    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue,
                    exchange = @Exchange(type = "topic",name = "topics"),
                    key = {"order.#","user.#"}
            )
    })
    public void receivel2(String message){
        System.out.println("message2 :" + message);
    }

}
```

## 六、MQ的应用场景

2.1异步处理
场景说明：用户注册后，需要发注册邮件和注册短信。传统的做法有两种 1.串行的方式；2.并行方式
a、串行方式：将注册信息写入数据库成功后，发送注册邮件，再发送注册短信。以上三个任务全部完成后，返回给客户端。
![img](https://images2015.cnblogs.com/blog/270324/201607/270324-20160730141220778-784471498.png)

b、并行方式：将注册信息写入数据库成功后，发送注册邮件的同时，发送注册短信。以上三个任务完成后，返回给客户端。与串行的差别是，并行的方式可以提高处理的时间
![img](https://images2015.cnblogs.com/blog/270324/201607/270324-20160730141228575-679122919.png)

假设三个业务节点每个使用50毫秒钟，不考虑网络等其他开销，则串行方式的时间是150毫秒，并行的时间可能是100毫秒。
因为CPU在单位时间内处理的请求数是一定的，假设CPU1秒内吞吐量是100次。则串行方式1秒内CPU可处理的请求量是7次（1000/150）。并行方式处理的请求量是10次（1000/100）
小结：如以上案例描述，传统的方式系统的性能（并发量，吞吐量，响应时间）会有瓶颈。如何解决这个问题呢？

引入消息队列，将不是必须的业务逻辑，异步处理。改造后的架构如下：
![img](https://images2015.cnblogs.com/blog/270324/201607/270324-20160730141236169-1140938329.png)
按照以上约定，用户的响应时间相当于是注册信息写入数据库的时间，也就是50毫秒。注册邮件，发送短信写入消息队列后，直接返回，因此写入消息队列的速度很快，基本可以忽略，因此用户的响应时间可能是50毫秒。因此架构改变后，系统的吞吐量提高到每秒20 QPS。比串行提高了3倍，比并行提高了两倍。

2.2应用解耦
场景说明：用户下单后，订单系统需要通知库存系统。传统的做法是，订单系统调用库存系统的接口。如下图：
![img](https://images2015.cnblogs.com/blog/270324/201607/270324-20160730143219809-1948583125.png)
传统模式的缺点：假如库存系统无法访问，则订单减库存将失败，从而导致订单失败，订单系统与库存系统耦合

如何解决以上问题呢？引入应用消息队列后的方案，如下图：
![img](https://images2015.cnblogs.com/blog/270324/201607/270324-20160730143228325-953675504.png)
订单系统：用户下单后，订单系统完成持久化处理，将消息写入消息队列，返回用户订单下单成功
库存系统：订阅下单的消息，采用拉/推的方式，获取下单信息，库存系统根据下单信息，进行库存操作
假如：在下单时库存系统不能正常使用。也不影响正常下单，因为下单后，订单系统写入消息队列就不再关心其他的后续操作了。实现订单系统与库存系统的应用解耦

2.3流量削锋
流量削锋也是消息队列中的常用场景，一般在秒杀或团抢活动中使用广泛。
应用场景：秒杀活动，一般会因为流量过大，导致流量暴增，应用挂掉。为解决这个问题，一般需要在应用前端加入消息队列。
a、可以控制活动的人数
b、可以缓解短时间内高流量压垮应用
![img](https://images2015.cnblogs.com/blog/270324/201607/270324-20160730151710106-2043115158.png)
用户的请求，服务器接收后，首先写入消息队列。假如消息队列长度超过最大数量，则直接抛弃用户请求或跳转到错误页面。
秒杀业务根据消息队列中的请求信息，再做后续处理

2.4日志处理
日志处理是指将消息队列用在日志处理中，比如Kafka的应用，解决大量日志传输的问题。架构简化如下
![img](https://images2015.cnblogs.com/blog/270324/201607/270324-20160730152810934-1818295010.png)
日志采集客户端，负责日志数据采集，定时写受写入Kafka队列
Kafka消息队列，负责日志数据的接收，存储和转发
日志处理应用：订阅并消费kafka队列中的日志数据 

2.5消息通讯
消息通讯是指，消息队列一般都内置了高效的通信机制，因此也可以用在纯的消息通讯。比如实现点对点消息队列，或者聊天室等
点对点通讯：
![img](https://images2015.cnblogs.com/blog/270324/201607/270324-20160730153544294-1894255488.png)
客户端A和客户端B使用同一队列，进行消息通讯。

聊天室通讯：
![img](https://images2015.cnblogs.com/blog/270324/201607/270324-20160730153550184-1160563716.png)
客户端A，客户端B，客户端N订阅同一主题，进行消息发布和接收。实现类似聊天室效果。

**消息中间件示例** 
电商系统
![img](https://images2015.cnblogs.com/blog/270324/201608/270324-20160801102300309-25949110.jpg)
消息队列采用高可用，可持久化的消息中间件。比如Active MQ，Rabbit MQ，Rocket Mq。
（1）应用将主干逻辑处理完成后，写入消息队列。消息发送是否成功可以开启消息的确认模式。（消息队列返回消息接收成功状态后，应用再返回，这样保障消息的完整性）
（2）扩展流程（发短信，配送处理）订阅队列消息。采用推或拉的方式获取消息并处理。
（3）消息将应用解耦的同时，带来了数据一致性问题，可以采用最终一致性方式解决。比如主数据写入数据库，扩展应用根据消息队列，并结合数据库方式实现基于消息队列的后续处理。

日志收集系统
![img](https://images2015.cnblogs.com/blog/270324/201608/270324-20160801102309481-1983324345.jpg)
分为Zookeeper注册中心，日志收集客户端，Kafka集群和Storm集群（OtherApp）四部分组成。
Zookeeper注册中心，提出负载均衡和地址查找服务
日志收集客户端，用于采集应用系统的日志，并将数据推送到kafka队列
Kafka集群：接收，路由，存储，转发等消息处理
Storm集群：与OtherApp处于同一级别，采用拉的方式消费队列中的数据

## 七、RabbitMQ的集群





















