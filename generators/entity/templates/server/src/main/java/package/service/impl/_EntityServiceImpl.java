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
package <%=packageName%>.service<% if (service === 'serviceImpl') { %>.impl<% } %>;
<%  const serviceClassName = service === 'serviceImpl' ? entityClass + 'ServiceImpl' : entityClass + 'Service';
    let viaService = false;
    let isFacade = false;
    const instanceType = (dto === 'mapstruct' && facadeForService === 'no') ? entityClass + 'DTO' : entityClass;
    const instanceName = (dto === 'mapstruct' && facadeForService === 'no') ? entityInstance + 'DTO' : entityInstance;
    const mapper = entityInstance  + 'Mapper';
    const dtoToEntity = mapper + '.'+ 'toEntity';
    const entityToDto = 'toDto';
    const entityToDtoReference = mapper + '::'+ 'toDto';
    const repository = entityInstance  + 'Repository';
    const searchRepository = entityInstance  + 'SearchRepository';
    if (service === 'serviceImpl') { %>
import <%=packageName%>.service.<%= entityClass %>Service;<% } %>
import <%=packageName%>.domain.<%= entityClass %>;
import <%=packageName%>.repository.<%= entityClass %>Repository;<% if (searchEngine === 'elasticsearch') { %>
import <%=packageName%>.repository.search.<%= entityClass %>SearchRepository;<% } if (dto === 'mapstruct') { %>
<% if(facadeForService === 'no') { %>
import <%=packageName%>.service.dto.<%= entityClass %>DTO;
import <%=packageName%>.service.mapper.<%= entityClass %>Mapper;<% } %>
<% } %>
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
<%_ if (pagination !== 'no') { _%>
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
<%_ } _%>
import org.springframework.stereotype.Service;
<%_ if (databaseType === 'sql' && facadeForService === 'no') { _%>
import org.springframework.transaction.annotation.Transactional;
<%_ } _%>
<% if (dto === 'mapstruct' && facadeForService === 'no' && (pagination === 'no' ||  fieldsContainNoOwnerOneToOne === true)) { %>
import java.util.LinkedList;<% } %><% if (pagination === 'no' ||  fieldsContainNoOwnerOneToOne === true) { %>
import java.util.List;<% } %><% if (databaseType === 'cassandra') { %>
import java.util.UUID;<% } %><% if (fieldsContainNoOwnerOneToOne === true || (pagination === 'no' && ((searchEngine === 'elasticsearch' && !viaService) || (dto === 'mapstruct' && facadeForService === 'no')))) { %>
import java.util.stream.Collectors;<% } %><% if (fieldsContainNoOwnerOneToOne === true || (pagination === 'no' && searchEngine === 'elasticsearch' && !viaService)) { %>
import java.util.stream.StreamSupport;<% } %><% if (searchEngine === 'elasticsearch') { %>

import static org.elasticsearch.index.query.QueryBuilders.*;<% } %>


@Service
public class <%= serviceClassName %> <% if (service === 'serviceImpl') { %>implements <%= entityClass %>Service<% } %>{

    private final Logger log = LoggerFactory.getLogger(<%= serviceClassName %>.class);
<%- include('../../common/service_inject_template', {viaService: viaService, constructorName: serviceClassName}); -%>


    <%_ if (service === 'serviceImpl') { _%>
    @Override
    <%_ } _%>
    <%_ if (databaseType === 'sql' && facadeForService === 'no') { _%>
    @Transactional(readOnly = true)
    <%_ } _%>
    public <%= instanceType %> save(<%= instanceType %> <%= instanceName %>) {
        log.debug("Request to save <%= entityClass %> : {}", <%= instanceName %>);<%- include('../../common/service_save_template', {viaService: viaService, returnDirectly: true}); -%>
    }

    <%_ if (service === 'serviceImpl') { _%>
    @Override
    <%_ } _%>
    <%_ if (databaseType === 'sql' && facadeForService === 'no') { _%>
    @Transactional(readOnly = true)
    <%_ } _%>
    public <%= instanceType %> update(<%= instanceType %> <%= instanceName %>) {
        log.debug("Request to update <%= entityClass %> : {}", <%= instanceName %>);<%- include('../../common/service_update_template', {viaService: viaService, returnDirectly: true}); -%>
    }

    <%_ if (service === 'serviceImpl') { _%>
    @Override
    <%_ } _%>
    <%_ if (databaseType === 'sql' && facadeForService === 'no') { _%>
    @Transactional(readOnly = true)
    <%_ } _%>
    public <% if (pagination !== 'no') { %>Page<<%= instanceType %><% } else { %>List<<%= instanceType %><% } %>> findAll(<% if (pagination !== 'no') { %>Pageable pageable<% } %>) {
        log.debug("Request to get all <%= entityClassPlural %>");
        <%_ if (pagination === 'no') { _%>
        return <%= entityInstance %>Repository.<% if (fieldsContainOwnerManyToMany === true) { %>findAllWithEagerRelationships<% } else { %>findAll<% } %>()<% if (dto === 'mapstruct' && facadeForService === 'no') { %>.stream()
            .map(<%= entityToDtoReference %>)
            .collect(Collectors.toCollection(LinkedList::new))<% } %>;
        <%_ } else { _%>
        return <%= entityInstance %>Repository.findAll(pageable)<% if (dto !== 'mapstruct' || facadeForService !== 'no') { %>;<% } else { %>
            .map(<%= entityToDtoReference %>);<% } %>
        <%_ } _%>
    }
<%- include('../../common/get_filtered_template'); -%>

    <%_ if (service === 'serviceImpl') { _%>
    @Override
    <%_ } _%>
    <%_ if (databaseType === 'sql' && facadeForService === 'no') { _%>
    @Transactional(readOnly = true)
    <%_ } _%>
    public <%= instanceType %> findOne(<%= pkType %> id) {
        log.debug("Request to get <%= entityClass %> : {}", id);<%- include('../../common/service_get_template', {viaService: viaService, returnDirectly:true}); -%>
    }


    <%_ if (service === 'serviceImpl') { _%>
    @Override
    <%_ } _%>
    public void delete(<%= pkType %> id) {
        log.debug("Request to delete <%= entityClass %> : {}", id);<%- include('../../common/delete_template', {isFacade: isFacade, viaService: viaService}); -%>
    }
    <%_ if (searchEngine === 'elasticsearch') { _%>


    <%_ if (service === 'serviceImpl') { _%>
    @Override
    <%_ } _%>
    <%_ if (databaseType === 'sql' && facadeForService === 'no') { _%>
    @Transactional(readOnly = true)
    <%_ } _%>
    public <% if (pagination !== 'no') { %>Page<<%= instanceType %><% } else { %>List<<%= instanceType %><% } %>> search(String query<% if (pagination !== 'no') { %>, Pageable pageable<% } %>) {
        <%_ if (pagination === 'no') { _%>
        log.debug("Request to search <%= entityClassPlural %> for query {}", query);<%- include('../../common/search_stream_template', {viaService: viaService}); -%>
        <%_ } else { _%>
        log.debug("Request to search for a page of <%= entityClassPlural %> for query {}", query);
        Page<<%= entityClass %>> result = <%= entityInstance %>SearchRepository.search(queryStringQuery(query), pageable);
            <%_ if (dto === 'mapstruct' && facadeForService === 'no') { _%>
        return result.map(<%= entityToDtoReference %>);
            <%_ } else { _%>
        return result;
        <%_ } } _%>
    }
    <%_ } _%>
}
