(function() {
    'use strict';

    angular
        .module('libraryappApp')
        .controller('BorrowDialogController', BorrowDialogController);

    BorrowDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'Borrow', 'Copy', 'User'];

    function BorrowDialogController ($timeout, $scope, $stateParams, $uibModalInstance, $q, entity, Borrow, Copy, User) {
        var vm = this;

        vm.borrow = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.copies = Copy.query({filter: 'borrow-is-null'});
        $q.all([vm.borrow.$promise, vm.copies.$promise]).then(function() {
            if (!vm.borrow.copy || !vm.borrow.copy.id) {
                return $q.reject();
            }
            return Copy.get({id : vm.borrow.copy.id}).$promise;
        }).then(function(copy) {
            vm.copies.push(copy);
        });
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.borrow.id !== null) {
                Borrow.update(vm.borrow, onSaveSuccess, onSaveError);
            } else {
                Borrow.save(vm.borrow, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('libraryappApp:borrowUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.borrowDate = false;
        vm.datePickerOpenStatus.returnDate = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
