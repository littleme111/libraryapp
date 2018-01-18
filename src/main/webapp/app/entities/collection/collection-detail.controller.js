(function() {
    'use strict';

    angular
        .module('libraryappApp')
        .controller('CollectionDetailController', CollectionDetailController);

    CollectionDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Collection', 'Book'];

    function CollectionDetailController($scope, $rootScope, $stateParams, previousState, entity, Collection, Book) {
        var vm = this;

        vm.collection = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('libraryappApp:collectionUpdate', function(event, result) {
            vm.collection = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
