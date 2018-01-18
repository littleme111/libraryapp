(function() {
    'use strict';

    angular
        .module('libraryappApp')
        .controller('BookController', BookController);

    BookController.$inject = ['Book'];

    function BookController(Book) {

        var vm = this;

        vm.books = [];

        loadAll();

        function loadAll() {
            Book.query(function(result) {
                vm.books = result;
                vm.searchQuery = null;
            });
        }
    }
})();
