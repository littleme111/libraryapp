(function() {
    'use strict';

    angular
        .module('libraryappApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('borrow', {
            parent: 'entity',
            url: '/borrow',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Borrows'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/borrow/borrows.html',
                    controller: 'BorrowController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('borrow-detail', {
            parent: 'borrow',
            url: '/borrow/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Borrow'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/borrow/borrow-detail.html',
                    controller: 'BorrowDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Borrow', function($stateParams, Borrow) {
                    return Borrow.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'borrow',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('borrow-detail.edit', {
            parent: 'borrow-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/borrow/borrow-dialog.html',
                    controller: 'BorrowDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Borrow', function(Borrow) {
                            return Borrow.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('borrow.new', {
            parent: 'borrow',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/borrow/borrow-dialog.html',
                    controller: 'BorrowDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                borrowDate: null,
                                returnDate: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('borrow', null, { reload: 'borrow' });
                }, function() {
                    $state.go('borrow');
                });
            }]
        })
        .state('borrow.edit', {
            parent: 'borrow',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/borrow/borrow-dialog.html',
                    controller: 'BorrowDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Borrow', function(Borrow) {
                            return Borrow.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('borrow', null, { reload: 'borrow' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('borrow.delete', {
            parent: 'borrow',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/borrow/borrow-delete-dialog.html',
                    controller: 'BorrowDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Borrow', function(Borrow) {
                            return Borrow.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('borrow', null, { reload: 'borrow' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
