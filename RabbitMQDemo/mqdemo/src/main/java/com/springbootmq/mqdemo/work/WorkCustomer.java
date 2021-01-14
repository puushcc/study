package com.springbootmq.mqdemo.work;

import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

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
