package com.kt.article.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class MessageSourceBean {
    @Autowired
    private Source source;

    private static final Logger logger = LoggerFactory.getLogger(MessageSourceBean.class);


    public void publishArticleMessage(String type, String id) {
        if (logger.isInfoEnabled()) {
            logger.info("Sending kafka message for {} id : {}", type, id);
        }
        ArticleMessage message = new ArticleMessage(type, id);
        source.output().send(MessageBuilder.withPayload(message).build());
    }
}
