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
        .provider('AlertService', AlertService);

    /** @ngInject **/
    function AlertService () {
        this.toast = false;
        /*jshint validthis: true */
        this.$get = getService;

        this.showAsToast = function(isToast) {
            this.toast = isToast;
        };

        function getService ($timeout, noty, $rootScope, $sce<% if (enableTranslation) { %>, $translate<% } %>) {
            var toast = this.toast,
                alertId = 0, // unique id for each alert. Starts from 0.
                alerts = [],
                timeout = 5000; // default timeout

                function addErrorAlert (message, key, data) {
                    <%_ if (enableTranslation) { _%>
                    key = key ? key : message;
                    alerts.push(
                        addAlert(
                            {
                                type: 'error',
                                msg: key,
                                params: data,
                                timeout: 5000,
                                toast: isToast(),
                                scoped: true
                            },
                            alerts
                        )
                    );
                    <%_ } else { _%>
                    alerts.push(
                        AlertService.add(
                            {
                                type: 'error',
                                msg: message,
                                timeout: 5000,
                                scoped: true
                            },
                            alerts
                        )
                    );
                    <%_ } _%>
                }

                $rootScope.$on('<%=angularAppName%>.httpError', function (event, httpResponse) {
                    var i;
                    event.stopPropagation();
                    switch (httpResponse.status) {
                    // connection refused, server not reachable
                    case 0:
                        addErrorAlert('Server not reachable','error.server.not.reachable');
                        break;

                    case 400:
                        var headers = Object.keys(httpResponse.headers()).filter(function (header) {
                            return header.indexOf('app-error', header.length - 'app-error'.length) !== -1 || header.indexOf('app-params', header.length - 'app-params'.length) !== -1;
                        }).sort();
                        var errorHeader = httpResponse.headers(headers[0]);
                        var entityKey = httpResponse.headers(headers[1]);
                        if (angular.isString(errorHeader)) {
                            var entityName = <% if (enableTranslation) { %>$translate.instant('global.menu.entities.' + entityKey)<% }else{ %>entityKey<% } %>;
                            addErrorAlert(errorHeader, errorHeader, {entityName: entityName});
                        } else if (httpResponse.data && httpResponse.data.fieldErrors) {
                            for (i = 0; i < httpResponse.data.fieldErrors.length; i++) {
                                var fieldError = httpResponse.data.fieldErrors[i];
                                // convert 'something[14].other[4].id' to 'something[].other[].id' so translations can be written to it
                                var convertedField = fieldError.field.replace(/\[\d*\]/g, '[]');
                                var fieldName = <% if (enableTranslation) { %>$translate.instant('<%= angularAppName %>.' + fieldError.objectName + '.' + convertedField)<% }else{ %>convertedField.charAt(0).toUpperCase() + convertedField.slice(1)<% } %>;
                                addErrorAlert('Field ' + fieldName + ' cannot be empty', 'error.' + fieldError.message, {fieldName: fieldName});
                            }
                        } else if (httpResponse.data && httpResponse.data.message) {
                            addErrorAlert(httpResponse.data.message, httpResponse.data.message, httpResponse.data);
                        } else {
                            addErrorAlert(httpResponse.data);
                        }
                        break;

                    case 404:
                        addErrorAlert('Not found','error.url.not.found');
                        break;

                    default:
                        if (httpResponse.data && httpResponse.data.message) {
                            addErrorAlert(httpResponse.data.message);
                        } else {
                            addErrorAlert(angular.toJson(httpResponse));
                        }
                    }
                });

            return {
                factory: factory,
                isToast: isToast,
                add: addAlert,
                closeAlert: closeAlert,
                closeAlertByIndex: closeAlertByIndex,
                clear: clear,
                get: get,
                success: success,
                error: error,
                info: info,
                warning : warning
            };

            function isToast() {
                return toast;
            }

            function clear() {
                alerts = [];
            }

            function get() {
                return alerts;
            }

            function success(msg, params, position) {
                return this.add({
                    type: 'success',
                    msg: msg,
                    params: params,
                    timeout: timeout,
                    toast: toast,
                    position: position
                });
            }

            function error(msg, params, position) {
                return this.add({
                    type: 'danger',
                    msg: msg,
                    params: params,
                    timeout: timeout,
                    toast: toast,
                    position: position
                });
            }

            function warning(msg, params, position) {
                return this.add({
                    type: 'warning',
                    msg: msg,
                    params: params,
                    timeout: timeout,
                    toast: toast,
                    position: position
                });
            }

            function info(msg, params, position) {
                return this.add({
                    type: 'info',
                    msg: msg,
                    params: params,
                    timeout: timeout,
                    toast: toast,
                    position: position
                });
            }

            function factory(alertOptions) {
                var alert = {
                    type: alertOptions.type,
                    msg: $sce.trustAsHtml(alertOptions.msg),
                    id: alertOptions.alertId,
                    timeout: alertOptions.timeout,
                    toast: alertOptions.toast,
                    position: alertOptions.position ? alertOptions.position : 'top right',
                    scoped: alertOptions.scoped,
                    close: function (alerts) {
                        return closeAlert(this.id, alerts);
                    }
                };
                if (!alert.scoped) {
                    alerts.push(alert);
                }
                return alert;
            }

            function addAlert(alertOptions, extAlerts) {
                var text = $translate.instant(alertOptions.msg, alertOptions.params);

                switch (alertOptions.type) {
                    case 'error' :
                        noty.error(text, extAlerts);
                        break;
                    case 'success' :
                        noty.success(text, extAlerts);
                        break;
                    default :
                        noty.alert(text, extAlerts);
                        break;

                }

                alertOptions.alertId = alertId++;
            <%_ if (enableTranslation) { _%>
                alertOptions.msg = $translate.instant(alertOptions.msg, alertOptions.params);
            <%_ } _%>
                var alert = factory(alertOptions);
                if (alertOptions.timeout && alertOptions.timeout > 0) {
                    $timeout(function () {
                        closeAlert(alertOptions.alertId, extAlerts);
                    }, alertOptions.timeout);
                }
                return alert;
            }

            function closeAlert(id, extAlerts) {
                var thisAlerts = extAlerts ? extAlerts : alerts;
                return closeAlertByIndex(thisAlerts.map(function(e) { return e.id; }).indexOf(id), thisAlerts);
            }

            function closeAlertByIndex(index, thisAlerts) {
                return thisAlerts.splice(index, 1);
            }
        }
    }
})();
