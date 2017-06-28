
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
(function() {
    'use strict';

    angular
        .module('<%=angularAppName%>')
        .controller('<%= entityAngularName %>ManageController', <%= entityAngularName %>ManageController);

    function <%= entityAngularName %>ManageController (previousState, $state<% if (fieldsContainOwnerOneToOne) { %>, $q<% } %><% if (fieldsContainBlob) { %>, DataUtils<% } %>, entity, <%= entityClass %><% for (idx in differentTypes) { if (differentTypes[idx] !== entityClass) {%>, <%= differentTypes[idx] %><% } } %>) {
        var vm = this;

        vm.<%= entityInstance %> = entity;
        vm.clear = clear;
        <%_ if (fieldsContainInstant || fieldsContainZonedDateTime || fieldsContainLocalDate) { _%>
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        <%_ } _%>
        <%_ if (fieldsContainBlob) { _%>
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
        <%_ } _%>
        vm.save = save;
        <%
            var queries = [];
            for (idx in relationships) {
                var query;
                if (relationships[idx].selectize) {
                  var prepareArray = 'vm.' + relationships[idx].relationshipFieldNamePlural.toLowerCase() + ' = [];\n'
                  + '        if (vm.'+ entityInstance +'.'+ relationships[idx].relationshipFieldName +' !== undefined) {\n'
                  + '            vm.' + relationships[idx].relationshipFieldNamePlural.toLowerCase() + '.push(vm.' + entityInstance + '.'+ relationships[idx].relationshipFieldName + ');\n'
                  + '        }\n';

                  if (!contains(queries, prepareArray)) {
                      queries.push(prepareArray);
                  }

                  query = relationships[idx].otherEntityNameCapitalized + '.get(function(result) { \n'
                  + '            vm.' + relationships[idx].relationshipFieldNamePlural.toLowerCase() + ' = vm.' + relationships[idx].relationshipFieldNamePlural.toLowerCase() + '.concat(result.content);\n'
                  + '        });\n';

                } else {
                    if (relationships[idx].relationshipType === 'one-to-one' && relationships[idx].ownerSide === true && relationships[idx].otherEntityName !== 'user') {
                        query = 'vm.' + relationships[idx].relationshipFieldNamePlural.toLowerCase() + ' = ' + relationships[idx].otherEntityNameCapitalized + ".query({filter: '" + relationships[idx].otherEntityRelationshipName.toLowerCase() + "-is-null'});"
                    + "\n        $q.all([vm." + entityInstance + ".$promise, vm." + relationships[idx].relationshipFieldNamePlural.toLowerCase() + ".$promise]).then(function() {";
                            query += "\n            if (!vm." + entityInstance + "." + relationships[idx].relationshipFieldName + " || !vm." + entityInstance + "." + relationships[idx].relationshipFieldName + ".id) {"
                        query += "\n                return $q.reject();"
                    + "\n            }"
                    + "\n            return " + relationships[idx].otherEntityNameCapitalized + ".get({id : vm." + entityInstance + "." + relationships[idx].relationshipFieldName + ".id" + "}).$promise;"
                    + "\n        }).then(function(" + relationships[idx].relationshipFieldName + ") {"
                    + "\n            vm." + relationships[idx].relationshipFieldNamePlural.toLowerCase() + ".push(" + relationships[idx].relationshipFieldName + ");"
                    + "\n        });";
                    } else {
                        query = 'vm.' + relationships[idx].otherEntityNameCapitalizedPlural.toLowerCase() + ' = ' + relationships[idx].otherEntityNameCapitalized + '.query();';
                    }
                }

                if (!contains(queries, query)) {
                    queries.push(query);
                }
            } %><% for (idx in queries) { %>
        <%- queries[idx] %><% } %>

        function clear () {
            $state.go(previousState.name || '^');
        }

        function save () {
            vm.isSaving = true;
            if (vm.<%= entityInstance %>.id !== undefined) {
                <%= entityClass %>.update(vm.<%= entityInstance %>, onSaveSuccess, onSaveError);
            } else {
                <%= entityClass %>.save(vm.<%= entityInstance %>, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $state.go(previousState.name || '^');
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        <%_ for (idx in fields) {
            if (['Instant', 'ZonedDateTime', 'LocalDate'].includes(fields[idx].fieldType)) { _%>
        vm.datePickerOpenStatus.<%= fields[idx].fieldName %> = false;
        <%_ } else if ((fields[idx].fieldType === 'byte[]' || fields[idx].fieldType === 'ByteBuffer') && fields[idx].fieldTypeBlobContent !== 'text') { _%>

        vm.set<%= fields[idx].fieldNameCapitalized %> = function ($file, <%= entityInstance %>) {
            <%_ if (fields[idx].fieldTypeBlobContent === 'image') { _%>
            if ($file && $file.$error === 'pattern') {
                return;
            }
            <%_ } _%>
            if ($file) {
                DataUtils.toBase64($file, function(base64Data) {
                    $scope.$apply(function() {
                        <%= entityInstance %>.<%= fields[idx].fieldName %> = base64Data;
                        <%= entityInstance %>.<%= fields[idx].fieldName %>ContentType = $file.type;
                    });
                });
            }
        };
        <%_ } } _%>

        <%_ if (fieldsContainInstant || fieldsContainZonedDateTime || fieldsContainLocalDate) { _%>
        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
        <%_ } _%>

        <%_ for (idx in relationships) {
        if (relationships[idx].selectize) { _%>
        vm.load<%=relationships[idx].relationshipFieldName%> = function (query, callback) {
            <%=otherEntityFieldCapitalized %>.get({filter: query}, function(result) {
                vm.<% relationships[idx].relationshipFieldNamePlural.toLowerCase() %> = vm.<%= relationships[idx].relationshipFieldNamePlural.toLowerCase() %>.concat(result.content);
                callback(result.content);
            }, function(error) {
                console.log(error);
                callback();
            });
        };

        vm.<%=relationships[idx].relationshipFieldName%>Selected = function ($id, $model) {
            vm.<%= entityInstance %>.<%= relationships[idx].relationshipFieldName %> = $model;
        };
        <%_ if (relationships[idx].selectizeCreate) { _%>

        vm.create<%=relationships[idx].relationshipFieldName%> = function ($name, $callback) {
            if ($name === undefined || $name === '') {
                return;
            }

            <%= relationships[idx].otherEntityNameCapitalized %>.save({
                name: $name
            }, function (result) {
                $callback(result);
            }, function () {
                $callback(error);
            })
        };
        <%_ } _%>
        <%_ if (relationships[idx].selectizeRender) { _%>

        vm.render<%=relationships[idx].relationshipFieldName%> = function ($name, $callback) {
            var template = '<div><strong>' + escape(item.name) +'</strong></div>';
            return template;
        };

        <%_ } _%>
        <%_ } } _%>

    }
})();
