(function() {
    'use strict';

    angular
        .module('libraryappApp')
        .controller('BorrowDeleteController',BorrowDeleteController);

    BorrowDeleteController.$inject = ['$uibModalInstance', 'entity', 'Borrow'];

    function BorrowDeleteController($uibModalInstance, entity, Borrow) {
        var vm = this;

        vm.borrow = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Borrow.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
