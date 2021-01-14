package com.springbootmq.mqdemo.hello;

import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queuesToDeclare = @Queue("hello"))
public class helloCustomer {

    @RabbitHandler
    public void receivel(String message){
        System.out.println( "message:" + message );
    }


}
