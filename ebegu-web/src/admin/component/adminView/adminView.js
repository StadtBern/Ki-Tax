(function () {
    'use strict';

    var componentConfig = {
        transclude: false,
        require: ['^form'],
        bindings: {
            applicationProperties: '<'
        },
        templateUrl: 'src/admin/component/adminView/adminView.html',
        controller: AdminView,
        controllerAs: 'vm'
    };

    angular.module('ebeguWeb.admin').component('adminView', componentConfig);

    AdminView.$inject = ['applicationPropertyRS', 'MAX_LENGTH'];

    function AdminView(applicationPropertyRS, MAX_LENGTH) {
        var vm = this;
        vm.length = MAX_LENGTH;
        vm.applicationProperty = null;
        vm.submit = submit;
        vm.removeRow = removeRow;
        vm.createItem = createItem;
        vm.editRow = editRow;
        vm.resetForm = resetForm;

        function fetchList() {
            return applicationPropertyRS.getAllApplicationProperties();
        }

        function getIndexOfElementwithID(prop) {
            var idToSearch = prop.id;
            for (var i = 0; i < vm.applicationProperties.length; i++) {
                if (vm.applicationProperties[i].id === idToSearch) {
                    return i;
                }
            }
            return -1;

        }

        function submit() {
            //testen ob aktuelles property schon gespeichert ist
            if (vm.applicationProperty.timestampErstellt) {
                applicationPropertyRS.update(vm.applicationProperty.name, vm.applicationProperty.value)
                    .then(function (response) {
                        var index = getIndexOfElementwithID(response.data);
                        vm.applicationProperties[index] = response.data;
                        resetForm();

                    });

            } else {
                applicationPropertyRS.create(vm.applicationProperty.name, vm.applicationProperty.value)
                    .then(function (response) {
                        vm.applicationProperties.push(response.data);

                    });
            }

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
            vm.applicationProperty = {name: '', value: ''};
        }

        function editRow(row) {
            vm.applicationProperty = row;
        }

        function resetForm() {
            vm.applicationProperty = null;
        }

    }
})();
