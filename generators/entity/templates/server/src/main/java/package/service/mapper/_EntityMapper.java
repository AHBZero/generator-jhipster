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
package <%=packageName%>.service.mapper;

import <%=packageName%>.domain.*;
import <%=packageName%>.service.dto.<%= entityClass %>DTO;
import <%=packageName%>.service.dto.<%= entityClass %>SimpleDTO;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity <%= entityClass %> and its DTO <%= entityClass %>DTO.
 */
@Mapper(componentModel = "spring", uses = {<% var existingMappings = [];
  for (idx in relationships) {
    if ((relationships[idx].relationshipType === 'many-to-many' && relationships[idx].ownerSide === true)|| relationships[idx].relationshipType === 'many-to-one' ||(relationships[idx].relationshipType === 'one-to-one' && relationships[idx].ownerSide === true)){
      // if the entity is mapped twice, we should implement the mapping once
      if (existingMappings.indexOf(relationships[idx].otherEntityNameCapitalized) === -1 && relationships[idx].otherEntityNameCapitalized !== entityClass) {
          existingMappings.push(relationships[idx].otherEntityNameCapitalized);
      %><%= relationships[idx].otherEntityNameCapitalized %>Mapper.class, <% } } } %>})
public interface <%= entityClass %>Mapper extends EntityMapper <<%= entityClass %>DTO, <%= entityClass %>> {

    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    <%= entityClass %> toSimpleEntity(<%= entityClass %>SimpleDTO dto);

    <%= entityClass %>SimpleDTO toSimpleDto(<%= entityClass %> entity);

    List<<%= entityClass %>> toSimpleEntityList(List<<%= entityClass %>SimpleDTO> dtoList);

    List<<%= entityClass %>DTO> toSimpleDtoList(List<<%= entityClass %>> entityList);

}
