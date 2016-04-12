export class DvErrorMessagesComponentConfig implements angular.IComponentOptions {
    transclude = false;
    bindings: any = {
        errorObject: '<for'
    };
    templateUrl = 'src/core/component/dv-error-messages/dv-error-messages.html';
    controller = DvErrorMessages;
    controllerAs = 'vm';
}

export class DvErrorMessages {
}



