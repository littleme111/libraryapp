(function() {
    'use strict';
    angular
        .module('libraryappApp')
        .factory('Borrow', Borrow);

    Borrow.$inject = ['$resource', 'DateUtils'];

    function Borrow ($resource, DateUtils) {
        var resourceUrl =  'api/borrows/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.borrowDate = DateUtils.convertLocalDateFromServer(data.borrowDate);
                        data.returnDate = DateUtils.convertLocalDateFromServer(data.returnDate);
                    }
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.borrowDate = DateUtils.convertLocalDateToServer(copy.borrowDate);
                    copy.returnDate = DateUtils.convertLocalDateToServer(copy.returnDate);
                    return angular.toJson(copy);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.borrowDate = DateUtils.convertLocalDateToServer(copy.borrowDate);
                    copy.returnDate = DateUtils.convertLocalDateToServer(copy.returnDate);
                    return angular.toJson(copy);
                }
            }
        });
    }
})();
