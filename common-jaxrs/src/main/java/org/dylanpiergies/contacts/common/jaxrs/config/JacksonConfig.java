package org.dylanpiergies.contacts.common.jaxrs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

@Configuration
public class JacksonConfig {

    @Bean
    public JacksonJsonProvider jacksonJsonProvider() {
        final ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.registerModule(new Jdk8Module());

        return new JacksonJsonProvider(objectMapper);
    }
}
