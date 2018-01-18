(function() {
    'use strict';

    angular
        .module('libraryappApp')
        .controller('BorrowDetailController', BorrowDetailController);

    BorrowDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Borrow', 'Copy', 'User'];

    function BorrowDetailController($scope, $rootScope, $stateParams, previousState, entity, Borrow, Copy, User) {
        var vm = this;

        vm.borrow = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('libraryappApp:borrowUpdate', function(event, result) {
            vm.borrow = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
