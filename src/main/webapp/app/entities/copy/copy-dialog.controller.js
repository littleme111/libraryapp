(function() {
    'use strict';

    angular
        .module('libraryappApp')
        .controller('CopyDialogController', CopyDialogController);

    CopyDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Copy', 'Book'];

    function CopyDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Copy, Book) {
        var vm = this;

        vm.copy = entity;
        vm.clear = clear;
        vm.save = save;
        vm.books = Book.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.copy.id !== null) {
                Copy.update(vm.copy, onSaveSuccess, onSaveError);
            } else {
                Copy.save(vm.copy, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('libraryappApp:copyUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
