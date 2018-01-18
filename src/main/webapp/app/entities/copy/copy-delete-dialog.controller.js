(function() {
    'use strict';

    angular
        .module('libraryappApp')
        .controller('CopyDeleteController',CopyDeleteController);

    CopyDeleteController.$inject = ['$uibModalInstance', 'entity', 'Copy'];

    function CopyDeleteController($uibModalInstance, entity, Copy) {
        var vm = this;

        vm.copy = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Copy.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
