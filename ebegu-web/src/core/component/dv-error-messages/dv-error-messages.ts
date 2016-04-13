import * as errTemplate from './dv-error-messages.html';
export class DvErrorMessagesComponentConfig implements angular.IComponentOptions {
    transclude = false;
    bindings: any = {
        errorObject: '<for'
    };
    template = errTemplate;
    controller = DvErrorMessages;
    controllerAs = 'vm';
}

export class DvErrorMessages {
}



