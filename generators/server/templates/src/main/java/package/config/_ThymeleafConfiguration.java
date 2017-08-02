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

    import org.apache.commons.lang3.CharEncoding;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.beans.BeansException;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.autoconfigure.AutoConfigureAfter;
    import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
    import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
    import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
    import org.springframework.boot.context.properties.EnableConfigurationProperties;
    import org.springframework.context.ApplicationContext;
    import org.springframework.context.ApplicationContextAware;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.context.annotation.Primary;
    import org.springframework.web.servlet.ViewResolver;
    import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
    import org.thymeleaf.spring4.SpringTemplateEngine;
    import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
    import org.thymeleaf.spring4.view.ThymeleafViewResolver;
    import org.thymeleaf.templatemode.TemplateMode;

@ConditionalOnClass({SpringTemplateEngine.class})
@EnableConfigurationProperties({ThymeleafProperties.class})
@Configuration
@AutoConfigureAfter({WebMvcAutoConfiguration.class})
public class ThymeleafConfiguration implements ApplicationContextAware {

    @SuppressWarnings("unused")
    private final Logger log = LoggerFactory.getLogger(ThymeleafConfiguration.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ThymeleafProperties properties;

    @Bean @Primary
    public ViewResolver viewResolver() {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setOrder(2147483642);

        resolver.setTemplateEngine(templateEngine());
        resolver.setCharacterEncoding("UTF-8");
        return resolver;
    }

    @Bean
    @Primary
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(templateResolver());
        engine.addDialect(new Java8TimeDialect());
        return engine;
    }

    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setApplicationContext(applicationContext);
        resolver.setPrefix("classpath:mails/");
        resolver.setOrder(100000);
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding(CharEncoding.UTF_8);
        resolver.setCacheable(this.properties.isCache());
        return resolver;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

