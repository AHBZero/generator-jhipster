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
package <%=packageName%>.web.rest;

import com.codahale.metrics.annotation.Timed;
import <%=packageName%>.security.AuthoritiesConstants;
<%_ if (dto !== 'mapstruct' || service === 'no') { _%>
import <%=packageName%>.domain.<%= entityClass %>;
<%_ } _%>
<%_ if (service !== 'no') { _%>
import <%=packageName%>.service.<%= entityClass %>Service;<% } else { %>
import <%=packageName%>.repository.<%= entityClass %>Repository;<% if (searchEngine === 'elasticsearch') { %>
import <%=packageName%>.repository.search.<%= entityClass %>SearchRepository;<% }} %>
import <%=packageName%>.web.rest.util.HeaderUtil;<% if (pagination !== 'no') { %>
import <%=packageName%>.web.rest.util.PaginationUtil;<% } %>
<%_ if(facadeForService === 'facadeClass') { _%>
import <%=packageName%>.facade.<%= entityClass %>Facade;<% } %>
<%_ if (dto === 'mapstruct') { _%>
    <%_ if(facadeForService === 'facadeClass') { %>
import <%=packageName%>.facade.dto.<%= entityClass %>DTO;
    <%_ } else { %>
import <%=packageName%>.service.dto.<%= entityClass %>DTO;
<%_ } if (service === 'no') { _%>
import <%=packageName%>.service.mapper.<%= entityClass %>Mapper;
<%_ } } _%>
<%_ if (pagination !== 'no') { _%>
import io.swagger.annotations.ApiParam;
<%_ } _%>
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
<%_ if (pagination !== 'no') { _%>
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
<%_ } _%>
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
<% if (validation) { %>
import javax.validation.Valid;<% } %>
import java.net.URI;
import java.net.URISyntaxException;
<%_ const viaService = service !== 'no';
    const viaFacade = facadeForService === 'facadeClass';
    const isFacade = false;
    if (pagination === 'no' && dto === 'mapstruct' && !viaService && fieldsContainNoOwnerOneToOne === true) { _%>
import java.util.LinkedList;<% } %>
import java.util.List;
import java.util.Optional;<% if (databaseType === 'cassandra') { %>
import java.util.UUID;<% } %><% if (!viaService && (searchEngine === 'elasticsearch' || fieldsContainNoOwnerOneToOne === true)) { %>
import java.util.stream.Collectors;<% } %><% if (searchEngine === 'elasticsearch' || fieldsContainNoOwnerOneToOne === true) { %>
import java.util.stream.StreamSupport;<% } %><% if (searchEngine === 'elasticsearch') { %>

import static org.elasticsearch.index.query.QueryBuilders.*;<% } %>

@RestController
@RequestMapping("/api")
public class <%= entityClass %>Resource {

    private final Logger log = LoggerFactory.getLogger(<%= entityClass %>Resource.class);

    private static final String ENTITY_NAME = "<%= entityInstance %>";
    <%_
    const instanceType = (dto === 'mapstruct') ? entityClass + 'DTO' : entityClass;
    const instanceName = (dto === 'mapstruct') ? entityInstance + 'DTO' : entityInstance;
    _%><%- include('../../common/inject_template', {viaFacade: viaFacade, viaService: viaService, constructorName: entityClass + 'Resource'}); -%>

    @PostMapping("/<%= entityApiUrl %>")
    @Timed
    @Secured({AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER})
    public ResponseEntity<<%= instanceType %>> create<%= entityClass %>(<% if (validation) { %>@Valid <% } %>@RequestBody <%= instanceType %> <%= instanceName %>) throws URISyntaxException {
        log.debug("REST request to save <%= entityClass %> : {}", <%= instanceName %>);
        if (<%= instanceName %>.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new <%= entityInstance %> cannot already have an ID")).body(null);
        }<%- include('../../common/save_template', {isFacade: isFacade, viaService: viaService, returnDirectly: false}); -%>
        return ResponseEntity.created(new URI("/api/<%= entityApiUrl %>/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId()))
            .body(result);
    }

    @PutMapping("/<%= entityApiUrl %>/{id}")
    @Timed
    @Secured({AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER})
    public ResponseEntity<<%= instanceType %>> update<%= entityClass %>(<% if (validation) { %>@Valid <% } %>@RequestBody <%= instanceType %> <%= instanceName %>, @PathVariable("id") String id) throws URISyntaxException {
        log.debug("REST request to update <%= entityClass %> : {}", <%= instanceName %>);
        if (<%= instanceName %>.getId() == null || id == null) {
            return create<%= entityClass %>(<%= instanceName %>);
        }<%- include('../../common/update_template', {isFacade: isFacade, viaService: viaService, returnDirectly: false}); -%>
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, <%= instanceName %>.getId()))
            .body(result);
    }

    @GetMapping("/<%= entityApiUrl %>")
    @Timed
    @Secured({AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER})<%- include('../../common/get_all_template', {viaFacade: viaFacade, viaService: viaService}); -%>

    @GetMapping("/<%= entityApiUrl %>/{id}")
    @Timed
    @Secured({AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER})
    public ResponseEntity<<%= instanceType %>> get<%= entityClass %>(@PathVariable <%= pkType %> id) {
        log.debug("REST request to get <%= entityClass %> : {}", id);<%- include('../../common/get_template', {isFacade: isFacade, viaService: viaService, returnDirectly:false}); -%>
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(<%= instanceName %>));
    }

    @DeleteMapping("/<%= entityApiUrl %>/{id}")
    @Timed
    @Secured({AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER})
    public ResponseEntity<Void> delete<%= entityClass %>(@PathVariable <%= pkType %> id) {
        log.debug("REST request to delete <%= entityClass %> : {}", id);<%- include('../../common/delete_template', {isFacade: isFacade, viaService: viaService}); -%>
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id<% if (pkType !== 'String') { %>.toString()<% } %>)).build();
    }<% if (searchEngine === 'elasticsearch') { %>

    @GetMapping("/_search/<%= entityApiUrl %>")
    @Timed
    @Secured({AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER})<%- include('../../common/search_template', {isFacade: isFacade, viaService: viaService}); -%><% } %>
}
