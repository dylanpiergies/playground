package org.dylanpiergies.contacts.common.jaxrs.config;

import org.apache.cxf.jaxrs.impl.WebApplicationExceptionMapper;
import org.apache.cxf.jaxrs.validation.JAXRSBeanValidationInInterceptor;
import org.apache.cxf.jaxrs.validation.ValidationExceptionMapper;
import org.dylanpiergies.contacts.common.exception.ThrowableExceptionMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CxfConfig {

    @Bean
    public JAXRSBeanValidationInInterceptor jaxrsBeanValidationInInterceptor() {
        return new JAXRSBeanValidationInInterceptor();
    }

    @Bean
    public ValidationExceptionMapper validationExceptionMapper() {
        return new ValidationExceptionMapper();
    }

    @Bean
    public ThrowableExceptionMapper javaLangExceptionMapper() {
        return new ThrowableExceptionMapper();
    }

    @Bean
    public WebApplicationExceptionMapper webApplicationExceptionMapper() {
        return new WebApplicationExceptionMapper();
    }
}
