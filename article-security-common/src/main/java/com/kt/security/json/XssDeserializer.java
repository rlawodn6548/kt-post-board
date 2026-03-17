package com.kt.security.json;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.HtmlUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.kt.security.wrapper.XSSRequestWrapper;

/**
* XSS 공격으로부터 JSON 문자열을 안전하게 역직렬화하는 데스리얼라이저입니다.
* 이 클래스는 JSON 입력에서 XSS 공격으로부터 보호하기 위해 OWASP Java HTML Sanitizer를 사용합니다.
*/
public class XssDeserializer extends JsonDeserializer<String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(XssDeserializer.class);

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        String value = p.getValueAsString();
        if (value == null) return null;

        String sanitizerizedValue = XSSRequestWrapper.POLICY.sanitize(value); 

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Value [{}] is encoded to [{}]", value, sanitizerizedValue);
        }
        return sanitizerizedValue;
    }
}