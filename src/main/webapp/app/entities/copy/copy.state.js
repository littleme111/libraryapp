(function() {
    'use strict';

    angular
        .module('libraryappApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('copy', {
            parent: 'entity',
            url: '/copy',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Copies'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/copy/copies.html',
                    controller: 'CopyController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('copy-detail', {
            parent: 'copy',
            url: '/copy/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Copy'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/copy/copy-detail.html',
                    controller: 'CopyDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Copy', function($stateParams, Copy) {
                    return Copy.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'copy',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('copy-detail.edit', {
            parent: 'copy-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/copy/copy-dialog.html',
                    controller: 'CopyDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Copy', function(Copy) {
                            return Copy.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('copy.new', {
            parent: 'copy',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/copy/copy-dialog.html',
                    controller: 'CopyDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                available: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('copy', null, { reload: 'copy' });
                }, function() {
                    $state.go('copy');
                });
            }]
        })
        .state('copy.edit', {
            parent: 'copy',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/copy/copy-dialog.html',
                    controller: 'CopyDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Copy', function(Copy) {
                            return Copy.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('copy', null, { reload: 'copy' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('copy.delete', {
            parent: 'copy',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/copy/copy-delete-dialog.html',
                    controller: 'CopyDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Copy', function(Copy) {
                            return Copy.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('copy', null, { reload: 'copy' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
