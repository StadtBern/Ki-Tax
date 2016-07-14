import {IDirective, IDirectiveFactory} from 'angular';
import {TSHTTPEvent} from '../../events/TSHTTPEvent';
import Moment = moment.Moment;
import INgModelController = angular.INgModelController;
import IHttpService = angular.IHttpService;
import ITimeoutService = angular.ITimeoutService;
import IFormController = angular.IFormController;
let template = require('./dv-loading-button.html');


interface IDVLoadingButtonController {
    isLoading: () => {};
    buttonDisabled: boolean;
}

export class DVLoadingButton implements IDirective {
    transclude = true;
    restrict = 'E';
    require: any = {formCtrl: '^?form'};
    replace = true;
    scope = {
        type: '@',
        delay: '@',
        buttonClass: '@',
        forceWaitService: '@'
    };
    template = template;
    controller = DVLoadingButtonController;
    controllerAs = 'vm';
    bindToController = true;

    static factory(): IDirectiveFactory {
        const directive = () => new DVLoadingButton();
        directive.$inject = [];
        return directive;
    }
}

/**
 * Button that disables itself after clicking to prevent multiclicks. If embedded in a form-controller it will check if
 * the form is valid first. If not it will not disable itself.
 * By default the button will be disabled till the next REST servicecall returns (not neceserally the one that was triggered
 * by this button) or till 400 ms have expired
 */
export class DVLoadingButtonController implements IDVLoadingButtonController {
    static $inject: string[] = ['$http', '$scope', '$timeout'];

    isLoading: () => boolean;
    buttonClicked: () => void;
    buttonDisabled: boolean;
    formCtrl: IFormController;
    delay: string;
    forceWaitService: string;

    /* @ngInject */
    constructor(private $http: IHttpService, private $scope: any, private $timeout: ITimeoutService) {

        this.buttonClicked = () => {
            //timeout wird gebraucht damit der request nach dem disablen ueberhaupt uebermittelt wird
            $timeout(() => {
                if (!this.forceWaitService) {
                    if (this.formCtrl) {  //wenn form-controller existiert
                        //button wird nur disabled wenn form valid
                        if (this.formCtrl.$valid) {
                            this.disableForDelay();
                        }
                    } else { //wenn kein form einfach mal disablen fuer delay ms
                        this.disableForDelay();
                    }
                } else {
                    //wir warten auf naechsten service return, egal wie lange es dauert
                    this.buttonDisabled = true;
                }
            }, 0);

        };

        this.$scope.$on(TSHTTPEvent.REQUEST_FINISHED, (event: any) => {
            this.buttonDisabled = false;
        });

    }

    private getDelay(): number {
        if (this.delay) {
            let parsedNum = parseInt(this.delay);
            if (parsedNum) {
                return parsedNum;
            }
        }
        return 400;   //default delay = 400 MS
    }

    /**
     * disabled den Button fuer "delay" milisekunden
     */
    private disableForDelay(): void {
        this.buttonDisabled = true;
        this.$timeout(() => {
            this.buttonDisabled = false;
        }, this.getDelay());

    }
}
