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
package <%=packageName%>.facade;
<%  const facadeClassName = entityClass + 'Facade';
    let viaService = false;
    const isFacade = true;
    const instanceType = (dto === 'mapstruct') ? entityClass + 'DTO' : entityClass;
    const instanceName = (dto === 'mapstruct') ? entityInstance + 'DTO' : entityInstance;
    const mapper = entityInstance  + 'Mapper';
    const dtoToEntity = mapper + '.'+ 'toEntity';
    const entityToDto = 'toDto';
    const entityToDtoReference = mapper + '::'+ 'toDto';
    const repository = entityInstance  + 'Repository';%>
import <%=packageName%>.service.<%= entityClass %>Service;
import <%=packageName%>.domain.<%= entityClass %>;<% if (dto === 'mapstruct') { %>
import <%=packageName%>.facade.dto.<%= entityClass %>DTO;
import <%=packageName%>.facade.mapper.<%= entityClass %>Mapper;<% } %>
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
<%_ if (pagination !== 'no') { _%>
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
<%_ } _%>
import org.springframework.stereotype.Service;
<%_ if (databaseType === 'sql') { _%>
import org.springframework.transaction.annotation.Transactional;
<%_ } _%>
<% if (dto === 'mapstruct' && (pagination === 'no' ||  fieldsContainNoOwnerOneToOne === true)) { %>
import java.util.LinkedList;<% } %><% if (pagination === 'no' ||  fieldsContainNoOwnerOneToOne === true) { %>
import java.util.List;<% } %><% if (databaseType === 'cassandra') { %>
import java.util.UUID;<% } %><% if (fieldsContainNoOwnerOneToOne === true || (pagination === 'no' && ((searchEngine === 'elasticsearch' && !viaService) || (dto === 'mapstruct')))) { %>
import java.util.stream.Collectors;<% } %><% if (fieldsContainNoOwnerOneToOne === true || (pagination === 'no' && searchEngine === 'elasticsearch' && !viaService)) %>

@Service
public class <%= facadeClassName %> {

    private final Logger log = LoggerFactory.getLogger(<%= facadeClassName %>.class);

    private final <%= entityClass %>Service <%= entityInstance %>Service;
    <%_ if (dto === 'mapstruct') { _%>

    private final <%= entityClass %>Mapper <%= entityInstance %>Mapper;
    <%_ } _%>

    public <%= facadeClassName %>(<%= entityClass %>Service <%= entityInstance %>Service<% if (dto === 'mapstruct') { %>, <%= entityClass %>Mapper <%= entityInstance %>Mapper<% } %>) {
            this.<%= entityInstance %>Service = <%= entityInstance %>Service;
        <%_ if (dto === 'mapstruct') { _%>
            this.<%= entityInstance %>Mapper = <%= entityInstance %>Mapper;
        <%_ } _%>
    }

    <%_ if (databaseType === 'sql') { _%>
    @Transactional
    <%_ } _%>
    public <%= instanceType %> save(<%= instanceType %> <%= instanceName %>) {
        log.debug("Request to save <%= entityClass %> : {}",<%= instanceName %>);<%- include('../common/save_template', {isFacade: isFacade, viaService: viaService, returnDirectly: true}); -%>
    }

    <%_ if (databaseType === 'sql') { _%>
    @Transactional
    <%_ } _%>
    public <%= instanceType %> update(<%= instanceType %> <%= instanceName %>) {
        log.debug("Request to update <%= entityClass %> : {}", <%= instanceName %>);<%- include('../common/update_template', {isFacade: isFacade, viaService: viaService, returnDirectly: true}); -%>
    }

    <%_ if (databaseType === 'sql') { _%>
    @Transactional(readOnly = true)
    <%_ } _%>
    public <% if (pagination !== 'no') { %>Page<<%= instanceType %><% } else { %>List<<%= instanceType %><% } %>> findAll(<% if (pagination !== 'no') { %>Pageable pageable<% } %>) {
        log.debug("Request to get all <%= entityClassPlural %>");
        <%_ if (pagination === 'no') { _%>
        return <%= entityInstance %>Service.<% if (fieldsContainOwnerManyToMany === true) { %>findAllWithEagerRelationships<% } else { %>findAll<% } %>()<% if (dto === 'mapstruct') { %>.stream()
            .map(<%= entityToDtoReference %>)
            .collect(Collectors.toCollection(LinkedList::new))<% } %>;
        <%_ } else { _%>
        return <%= entityInstance %>Service.findAll(pageable)<% if (dto !== 'mapstruct') { %>;<% } else { %>
            .map(<%= entityToDtoReference %>);<% } %>
        <%_ } _%>
    }
<%- include('../common/get_filtered_template', {isFacade: isFacade}); -%>

    <%_ if (databaseType === 'sql') { _%>
    @Transactional(readOnly = true)
    <%_ } _%>
    public <%= instanceType %> findOne(<%= pkType %> id) {
        log.debug("Request to get <%= entityClass %> : {}", id);<%- include('../common/get_template', {isFacade: isFacade, viaService: viaService, returnDirectly:true}); -%>
    }


    public void delete(<%= pkType %> id) {
        log.debug("Request to delete <%= entityClass %> : {}", id);<%- include('../common/delete_template', {isFacade: isFacade, viaService: viaService}); -%>
    }
    <%_ if (searchEngine === 'elasticsearch') { _%>


    <%_ if (databaseType === 'sql') { _%>
    @Transactional(readOnly = true)
    <%_ } _%>
    public <% if (pagination !== 'no') { %>Page<<%= instanceType %><% } else { %>List<<%= instanceType %><% } %>> search(String query<% if (pagination !== 'no') { %>, Pageable pageable<% } %>) {
        <%_ if (pagination === 'no') { _%>
        log.debug("Request to search <%= entityClassPlural %> for query {}", query);
        <%- include('../common/search_stream_template', {viaService: viaService}); -%>
        <%_ } else { _%>
        log.debug("Request to search for a page of <%= entityClassPlural %> for query {}", query);
        Page<<%= entityClass %>> result = <%= entityInstance %>Service.search(query, pageable);
            <%_ if (dto === 'mapstruct') { _%>
        return result.map(<%= entityToDtoReference %>);
            <%_ } else { _%>
        return result;
        <%_ } } _%>
    }
    <%_ } _%>
}
