import IComponentOptions = angular.IComponentOptions;
import {TSMessageEvent} from '../../../../models/enums/TSErrorEvent';
import ErrorService from '../../service/ErrorService';
import TSExceptionReport from '../../../../models/TSExceptionReport';
import {TSErrorLevel} from '../../../../models/enums/TSErrorLevel';
import IScope = angular.IScope;
let templ = require('./dvb-error-messages-panel.html');
let style = require('./dvb-error-messages-panel.less');

export class DvErrorMessagesPanelComponentConfig implements IComponentOptions {

    scope = {};
    template = templ;
    controller = DvErrorMessagesPanelComponent;
    controllerAs = 'vm';
}

/**
 * component that can display error messages
 */
export class DvErrorMessagesPanelComponent {

    errors: Array<TSExceptionReport> = [];


    static $inject: string[] = ['$scope', 'ErrorService'];
    /* @ngInject */
    constructor(private $scope: IScope, private errorService: ErrorService) {
    }

    $onInit() {
        this.$scope.$on(TSMessageEvent[TSMessageEvent.ERROR_UPDATE], this.displayMessages);
        this.$scope.$on(TSMessageEvent[TSMessageEvent.INFO_UPDATE], this.displayMessages);
        this.$scope.$on(TSMessageEvent[TSMessageEvent.CLEAR], () => {
            this.errors = [];
        });
    }

    displayMessages = (event: any, errors: Array<TSExceptionReport>) => {
        this.errors = errors;
        this.show();
    };

    show() {
        // this.element.show();
        angular.element('dvb-error-messages-panel').show();     //besser als $element injection fuer tests
    }

    clear() {
        this.errorService.clearAll();
    };

    messageStyle(): string {
        for (let error of this.errors) {
            if (error.severity !== TSErrorLevel.INFO) {
                return '';
            }
        }
        return 'info';
    }

}


