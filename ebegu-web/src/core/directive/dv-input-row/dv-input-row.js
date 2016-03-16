(function () {
    'use strict';

    angular.module('ebeguWeb.core').directive('dvInputRow', dvInputRow);

    function dvInputRow() {
        return {
            require: ['^form', 'dvInputRow'],
            transclude: true,
            restrict: 'E',
            replace: true,
            scope: {
                label: '@'
            },
            templateUrl: 'src/core/directive/dv-input-row/dv-input-row.html',
            controller: DvInputRow,
            controllerAs: 'vm',
            bindToController: true,
            link: function (scope, element, attrs, ctrls) {

                var ngFormController = ctrls[0];
                var ctrl = ctrls[1];

                var input = angular.element('ng-transclude').find('input').first();
                var inputProps = {
                    id: input.attr('id'),
                    name: input.attr('name')
                };

                ctrl.$onInit(inputProps, ngFormController);
            }
        };
    }

    DvInputRow.$inject = [];

    function DvInputRow() {
        var vm = this;
        vm.$onInit = init;
        vm.getFormElement = getFormElement;
        vm.formName = '';

        var ngFormController;

        function init(inputProps, formCtrl) {
            if (inputProps) {
                vm.inputId = inputProps.id;
                vm.inputName = inputProps.name;
            }

            if (formCtrl) {
                ngFormController = formCtrl;
                vm.formName = ngFormController.$name;
            }
        }

        function getFormElement() {
            return ngFormController.hasOwnProperty(vm.inputName) ? ngFormController[vm.inputName] : null;
        }
    }
})();
