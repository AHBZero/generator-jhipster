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
package <%=packageName%>.service.dto;

<% if (fieldsContainInstant === true) { %>
import java.time.Instant;<% } %><% if (fieldsContainLocalDate === true) { %>
import java.time.LocalDate;<% } %><% if (fieldsContainZonedDateTime === true) { %>
import java.time.ZonedDateTime;<% } %><% if (validation) { %>
import javax.validation.constraints.*;<% } %>
import java.io.Serializable;<% if (fieldsContainBigDecimal === true) { %>
import java.math.BigDecimal;<% } %><% if (fieldsContainBlob && databaseType === 'cassandra') { %>
import java.nio.ByteBuffer;<% } %><% if (relationships.length > 0) { %>
import java.util.HashSet;
import java.util.Set;<% } %>
import java.util.Objects;<% if (databaseType === 'cassandra') { %>
import java.util.UUID;<% } %><% if (fieldsContainBlob && databaseType === 'sql') { %>
import javax.persistence.Lob;<% } %>
<%_ for (idx in fields) { if (fields[idx].fieldIsEnum === true) { _%>
import <%=packageName%>.domain.enumeration.<%= fields[idx].fieldType %>;
<%_ } } _%>

public class <%= entityClass %>DTO implements Serializable {
<% if (databaseType === 'sql') { %>
    private String id;<% } %><% if (databaseType === 'mongodb') { %>
    private String id;<% } %><% if (databaseType === 'cassandra') { %>
    private UUID id;<% } %>
    <%_ for (idx in fields) {
        const fieldName = fields[idx].fieldName;_%>
    <%_ if (fieldName === 'name') { _%>
    private String name;
    <%_ } } _%>

    public <% if (databaseType === 'sql') { %>String<% } %><% if (databaseType === 'mongodb') { %>String<% } %><% if (databaseType === 'cassandra') { %>UUID<% } %> getId() {
        return id;
    }

    public void setId(<% if (databaseType === 'sql') { %>String<% } %><% if (databaseType === 'mongodb') { %>String<% } %><% if (databaseType === 'cassandra') { %>UUID<% } %> id) {
        this.id = id;
    }

    <%_ for (idx in fields) {
        const fieldName = fields[idx].fieldName;_%>
    <%_ if (fieldName === 'name') { _%>
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    <%_ } } _%>

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        <%= entityClass %>DTO <%= entityInstance %>DTO = (<%= entityClass %>DTO) o;
        if(<%= entityInstance %>DTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), <%= entityInstance %>DTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "<%= entityClass %>SimpleDTO{" +
            "id=" + getId() +
            "}";
    }
}
