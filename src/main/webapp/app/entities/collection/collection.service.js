(function() {
    'use strict';
    angular
        .module('libraryappApp')
        .factory('Collection', Collection);

    Collection.$inject = ['$resource'];

    function Collection ($resource) {
        var resourceUrl =  'api/collections/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
