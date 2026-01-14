package com.kt.like.message;

import com.kt.like.repository.ArticleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class MessageSinkBean {
    private Logger logger = LoggerFactory.getLogger(MessageSinkBean.class);

    @Autowired
    private ArticleRepository repository;


    @Transactional
    @StreamListener(Sink.INPUT)
    public void messageSink(ArticleMessage message) {
        if (logger.isInfoEnabled()) {
            logger.info("message arrived - type : {}, id : {}", message.getType(), message.getId());
        }

        repository.increaseLooks(message.getId());
    }
}
