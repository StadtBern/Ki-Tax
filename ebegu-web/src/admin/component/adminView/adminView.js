(function () {
    'use strict';

    var componentConfig = {
        transclude: false,
        bindings: {},
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
        vm.applicationProperty = {key: '', value: ''};
        vm.submit = submit;

        function submit() {
            applicationPropertyRS.create(vm.applicationProperty.key, vm.applicationProperty.value)
                .then(function (response) {
                    vm.applicationProperty = {key: '', value: ''};

                });
        }

    }
})();
