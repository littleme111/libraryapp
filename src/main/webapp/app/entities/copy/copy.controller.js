(function() {
    'use strict';

    angular
        .module('libraryappApp')
        .controller('CopyController', CopyController);

    CopyController.$inject = ['Copy'];

    function CopyController(Copy) {

        var vm = this;

        vm.copies = [];

        loadAll();

        function loadAll() {
            Copy.query(function(result) {
                vm.copies = result;
                vm.searchQuery = null;
            });
        }
    }
})();
