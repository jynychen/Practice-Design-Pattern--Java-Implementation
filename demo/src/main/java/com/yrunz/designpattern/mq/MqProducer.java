package com.yrunz.designpattern.mq;

// 生产者接口，向消息队列生产消费数据
public interface MqProducer {
    void produce(Message message);
}
