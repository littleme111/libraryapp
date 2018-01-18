(function() {
    'use strict';

    angular
        .module('libraryappApp')
        .controller('BookDetailController', BookDetailController);

    BookDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Book', 'Collection', 'Copy'];

    function BookDetailController($scope, $rootScope, $stateParams, previousState, entity, Book, Collection, Copy) {
        var vm = this;

        vm.book = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('libraryappApp:bookUpdate', function(event, result) {
            vm.book = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
