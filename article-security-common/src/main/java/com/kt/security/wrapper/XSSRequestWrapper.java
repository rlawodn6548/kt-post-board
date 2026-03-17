package com.kt.security.wrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XSSRequestWrapper extends HttpServletRequestWrapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(XSSRequestWrapper.class);
    public  static final PolicyFactory POLICY = Sanitizers.BLOCKS.and(Sanitizers.FORMATTING).and(Sanitizers.LINKS);

    public XSSRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    /**
     * application/x-www-form-urlencoded 타입의 Http Request Parameter에 입력된 내용을 XSS 공격으로부터 입력을 보호하기 위해 입력을 정제합니다.
     */
    @Override
    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter);
        if (values == null) return null;

        int count = values.length;
        String[] encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            encodedValues[i] = stripXSS(values[i]);
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Parameter [{}] value [{}] is encoded to [{}]", parameter, values[i], encodedValues[i]);
            }
        }
        return encodedValues;
    }

    /**
     * application/x-www-form-urlencoded 타입의 Http Request Parameter에 입력된 내용을 XSS 공격으로부터 입력을 보호하기 위해 입력을 정제합니다.
     */
    @Override
    public String getParameter(String parameter) {
        String value = super.getParameter(parameter);
        String encodedValue = stripXSS(value);
        if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Parameter [{}] value [{}] is encoded to [{}]", parameter, value, encodedValue);
            }
        return encodedValue;
    }

    /**
     * application/json 형식의 Http Header에 입력된 내용을 XSS 공격으로부터 입력을 보호하기 위해 입력을 정제합니다.
     */
    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        String encodedValue = stripXSS(value);
        if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Header [{}] value [{}] is encoded to [{}]", name, value, encodedValue);
            }
        return encodedValue;
    }

    private String stripXSS(String value) {
        if (value == null) return null;
        return POLICY.sanitize(value);
    }
}
