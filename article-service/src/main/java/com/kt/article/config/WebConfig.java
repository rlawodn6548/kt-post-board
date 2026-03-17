package com.kt.article.config;

import com.kt.security.filter.XSSFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean<XSSFilter> xssFilter() {
        FilterRegistrationBean<XSSFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new XSSFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
}
