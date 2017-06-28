<%#
 Copyright 2013-2017 the original author or authors from the JHipster project.

 This file is part of the JHipster project, see https://jhipster.github.io/
 for more information.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-%>
package <%=packageName%>.config;

<%_ if (databaseType === 'sql') { _%>
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
<%_ } _%>

import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

@Configuration
public class JacksonConfiguration {
<%_ if (databaseType === 'sql') { _%>

    /**
     * Support for Hibernate types in Jackson.
     */
    @Bean
    public Hibernate5Module hibernate5Module() {
        return new Hibernate5Module();
    }
<%_ } _%>

    /**
     * Jackson Afterburner module to speed up serialization/deserialization.
     */
    @Bean
    public AfterburnerModule afterburnerModule() {
        return new AfterburnerModule();
    }

    @Autowired
    private Jackson2ObjectMapperBuilder builder;

    @Bean
    public MappingJackson2HttpMessageConverter getMessageConverter(ObjectMapper mapper) {
       return new MappingJackson2HttpMessageConverter(mapper);
    }

    @Bean
    @Primary
    public ObjectMapper jacksonObjectMapper() {
       return this.builder
           .createXmlMapper(false)
           .build()
          //  .registerModule(new JtsModule()) enable if JTS is used
           .registerModule(new JavaTimeModule())
           .setSerializationInclusion(JsonInclude.Include.NON_NULL)
           .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
           .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
           .setDefaultPrettyPrinter(new MinimalPrettyPrinter());
    }

    @Bean
    @Qualifier("PUSH")
    public ObjectMapper pushJacksonObjectMapper() {
       return this.builder
           .createXmlMapper(false)
           .build()
           .registerModule(new JavaTimeModule())
           .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
           .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
           .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
           .setDefaultPrettyPrinter(new MinimalPrettyPrinter());
    }
}
