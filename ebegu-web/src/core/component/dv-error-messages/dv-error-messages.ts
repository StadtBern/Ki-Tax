import * as errTemplate from './dv-error-messages.html';
import {IComponentOptions} from 'angular';

export class DvErrorMessagesComponentConfig implements IComponentOptions {
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



