import {IDirective, IDirectiveFactory} from 'angular';
import {TSHTTPEvent} from '../../events/TSHTTPEvent';
import INgModelController = angular.INgModelController;
import IHttpService = angular.IHttpService;
import ITimeoutService = angular.ITimeoutService;
import IFormController = angular.IFormController;
import IScope = angular.IScope;
import IAugmentedJQuery = angular.IAugmentedJQuery;
import IAttributes = angular.IAttributes;
import IAngularEvent = angular.IAngularEvent;
let template = require('./dv-loading-button.html');


interface IDVLoadingButtonController {
    isDisabled: boolean;
    buttonDisabled: boolean;
}

export class DVLoadingButton implements IDirective {
    transclude = true;
    restrict = 'E';
    require: any = {dvLoadingButtonCtrl: 'dvLoadingButton', formCtrl: '^?form'};
    replace = true;
    scope = {
        type: '@',
        delay: '@',
        buttonClass: '@',
        forceWaitService: '@',
        buttonDisabled: '<',
        ariaLabel: '@',
        buttonClick: '&'

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
 * @example:
 *
 <dv-loading-button type="submit"
 button-click="vm.mySaveFunction()"
 button-class="btn btn-sm btn-success"
 button-disabled="!vm.isButtonDisabled()">
 <i class="glyphicon glyphicon-plus"></i>
 <span data-translate="SAVE"></span>
 </dv-loading-button>

 *
 */
export class DVLoadingButtonController implements IDVLoadingButtonController {
    static $inject: string[] = ['$http', '$scope', '$timeout'];

    buttonClicked: () => void;
    isDisabled: boolean;
    formCtrl: IFormController;
    delay: string;
    type: string;
    forceWaitService: string;
    buttonDisabled: boolean; //true wenn unser element programmatisch disabled wird
    buttonClick: () => void;

    /* @ngInject */
    constructor(private $http: IHttpService, private $scope: any, private $timeout: ITimeoutService) {
    }

    //wird von angular aufgerufen
    $onInit() {
        if (!this.type) {
            this.type = 'button'; //wenn kein expliziter type angegeben wurde nehmen wir default button
        }

        this.buttonClicked = () => {
            //wenn der button disabled ist machen wir mal gar nichts
            if (this.buttonDisabled || this.isDisabled) {
                return;
            }
            this.buttonClick();  //falls ein button-click callback uebergeben wurde ausfuehren

            //timeout wird gebraucht damit der request nach dem disablen ueberhaupt uebermittelt wird
            this.$timeout(() => {
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
                    this.isDisabled = true;
                }
            }, 0);

        };

        this.$scope.$on(TSHTTPEvent.REQUEST_FINISHED, (event: any) => {
            this.isDisabled = false;
        });

    }


    // beispiel wie man auf changes eines attributes von aussen reagieren kann
    $onChanges(changes: any) {
        if (changes.buttonDisabled && !changes.buttonDisabled.isFirstChange()) {
            this.buttonDisabled = changes.buttonDisabled.currentValue;
        }

    }


    private getDelay(): number {
        if (this.delay) {
            let parsedNum = parseInt(this.delay);
            if (parsedNum) {
                return parsedNum;
            }
        }
        return 4000;   //default delay = 400 MS
    }

    /**
     * disabled den Button fuer "delay" millisekunden
     */
    private disableForDelay(): void {
        this.isDisabled = true;
        this.$timeout(() => {
            this.isDisabled = false;
        }, this.getDelay());

    }
}
