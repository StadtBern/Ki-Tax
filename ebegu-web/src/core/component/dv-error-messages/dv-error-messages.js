(function () {
    'use strict';

    var componentConfig = {
        transclude: false,
        bindings: {
            errorObject: '<for'
        },
        templateUrl: 'src/core/component/dv-error-messages/dv-error-messages.html',
        controller: DvErrorMessages,
        controllerAs: 'vm'
    };

    angular.module('ebeguWeb.core').component('dvErrorMessages', componentConfig);

    DvErrorMessages.$inject = [];

    function DvErrorMessages() {
        var vm = this;

    }
})();
