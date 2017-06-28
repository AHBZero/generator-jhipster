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
package <%=packageName%>.config.push;

import <%=packageName%>.domain.push.Platform;
import <%=packageName%>.service.push.PushSenderService;
import <%=packageName%>.service.push.SenderType;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
public class PushConfiguration {

      @Autowired
      Environment environment;

      @Autowired
      ApplicationContext appContext;

      /**
      * Valida os beans de envio de push, que devem ser para cada tipo de provider
      */
      @PostConstruct
      public void validatePushServices() {

        Set<Platform> platforms =
            appContext.getBeansOfType(PushSenderService.class)
            .entrySet()
            .stream()
            .map(entry ->
                AopProxyUtils
                    .ultimateTargetClass(entry.getValue())
                    .getAnnotation(SenderType.class)
                    .values())
            .flatMap(Stream::of)
            .collect(Collectors.toSet());

        Arrays.stream(Platform.values()).forEach(p -> {
            if (!platforms.contains(p)) {
                throw new IllegalArgumentException("Platform Enum need to have the same number of respective PushSenderService beans. Did you create a new Platform type and don't create the PushSenderService implementation for this type?");
            }
        });
      }

      @Bean
      public FCMCredentials getFCMCredentials() {
        String key = environment.getProperty("push.fcm.key");
        return new FCMCredentials(key);
      }

}
