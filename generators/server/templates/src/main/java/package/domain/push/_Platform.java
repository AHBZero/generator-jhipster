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
package <%=packageName%>.domain.push;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Platform {

    ANDROID("android"),
    IOS("ios"),
    WEB("web");

    private final String value;

    Platform(String value) {
        this.value = value;
    }

    @JsonCreator
    public static Platform findByValue(String value) {
        for (Platform p : values()) {
            if (p.getValue().equalsIgnoreCase(value)) {
                return p;
            }
        }
        return null;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
