package com.springbootmq.mqdemo.test;

import com.springbootmq.mqdemo.MqdemoApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = MqdemoApplication.class)
@RunWith(SpringRunner.class)
public class TestMQ {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * hello work
     */
    @Test
    public void test(){
        rabbitTemplate.convertAndSend("hello","hello word");
    }

    /**
     * work 模型
     */
    @Test
    public void testwork(){
        for (int i = 0; i < 10; i++) {
            rabbitTemplate.convertAndSend("work","work模型"+i);
        }

    }

    /**
     * 广播
     */
    @Test
    public void faout(){
        rabbitTemplate.convertAndSend("logs","","Faout 模型");
    }

    /**
     * router 路由模式direct
     */
    @Test
    public void router(){
        rabbitTemplate.convertAndSend("directs","info","发送info的路由消息");
    }

    /**
     * router 路由模式topic
     */
    @Test
    public void topic(){
        rabbitTemplate.convertAndSend("topics","user.save","user.save");
    }

}
