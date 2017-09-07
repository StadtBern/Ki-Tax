import {IComponentOptions} from 'angular';
require('./dv-error-messages.less');
let errTemplate = require('./dv-error-messages.html');

export class DvErrorMessagesComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {
        errorObject: '<for',
        inputid: '@inputId'
    };
    template = errTemplate;
    controller = DvErrorMessages;
    controllerAs = 'vm';
}

export class DvErrorMessages {
}
