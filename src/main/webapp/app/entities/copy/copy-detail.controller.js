(function() {
    'use strict';

    angular
        .module('libraryappApp')
        .controller('CopyDetailController', CopyDetailController);

    CopyDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Copy', 'Book'];

    function CopyDetailController($scope, $rootScope, $stateParams, previousState, entity, Copy, Book) {
        var vm = this;

        vm.copy = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('libraryappApp:copyUpdate', function(event, result) {
            vm.copy = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
