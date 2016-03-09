(function () {
    'use strict';

    var componentConfig = {
        transclude: false,
        bindings: {
            applicationProperties: '<'
        },
        templateUrl: 'src/admin/component/adminView/adminView.html',
        controller: AdminView,
        controllerAs: 'vm'
    };

    angular.module('ebeguWeb.admin').component('adminView', componentConfig);

    AdminView.$inject = ['moment', 'applicationPropertyRS', 'MAX_LENGTH'];

    function AdminView(moment, applicationPropertyRS, MAX_LENGTH) {
        var vm = this;
        vm.length = MAX_LENGTH;
        vm.now = moment();
        vm.applicationProperty = null;
        vm.submit = submit;
        vm.removeRow = removeRow;
        vm.createItem = createItem;

        function fetchList() {
            return applicationPropertyRS.getAllApplicationProperties();
        }

        function submit() {
            applicationPropertyRS.create(vm.applicationProperty.key, vm.applicationProperty.value)
                .then(function (response) {
                    vm.applicationProperty = null;
                    vm.applicationProperties.push(response.data);

                });
        }

        function removeRow(row) {
            applicationPropertyRS.remove(row.name).then(function (reponse) {
                var index = vm.applicationProperties.indexOf(row);
                if (index !== -1) {
                    vm.applicationProperties.splice(index, 1);
                }

            });

        }

        function createItem() {
            vm.applicationProperty = {key: '', value: ''};
        }

    }
})();
