/**
 * Created by Pichau on 28/06/2017.
 */
(function() {
    'use strict';

    angular
        .module('<%=angularAppName%>')
        .config(stateConfig);

    function stateConfig($stateProvider) {
        $stateProvider.state('login', {
            parent: 'account',
            url: '/login',
            data: {
                authorities: [],
                pageTitle: 'login.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/components/login/login.html',
                    controller: 'NewLoginController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('login');
                    return $translate.refresh();
                }]
            }
        });
    }
})();
