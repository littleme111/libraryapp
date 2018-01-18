(function() {
    'use strict';

    angular
        .module('libraryappApp')
        .controller('BorrowController', BorrowController);

    BorrowController.$inject = ['Borrow'];

    function BorrowController(Borrow) {

        var vm = this;

        vm.borrows = [];

        loadAll();

        function loadAll() {
            Borrow.query(function(result) {
                vm.borrows = result;
                vm.searchQuery = null;
            });
        }
    }
})();
