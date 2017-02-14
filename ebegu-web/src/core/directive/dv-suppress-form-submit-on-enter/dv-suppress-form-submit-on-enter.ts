import {
    IDirective,
    IDirectiveFactory,
    IDirectiveLinkFn,
    IScope,
    IAugmentedJQuery,
    IAttributes,
    ILogService
} from 'angular';

/**
 * This directive is a hack to suppress the enter handler that is defined by angular-material on the md-radio-group.
 * It is a problem because in our case we rely on angular behaving as described in
 * https://docs.angularjs.org/api/ng/directive/form where it specifically says if there are buttons with
 * type=submit in a form they should be triggered on enter.
 * Since the radio-group component does not do this and triggers a form submitt event instead we have to
 * work-around that prevents this. (Otherwise the unsavedChanges plugin sets the form back to pristine which is wrong since no save
 * was triggered).
 *
 * See also https://github.com/angular/material/issues/577
 *
 * @see EBEGU-897
 */
export default class DVSuppressFormSubmitOnEnter implements IDirective {
    static $inject: string[] = [];

    restrict = 'A';
    link: IDirectiveLinkFn;
    priority = -400;
    controller = DVSuppressFormSubmitOnEnterController;
    require: any = {mdRadioGroupCtrl: 'mdRadioGroup', myCtrl: 'dvSuppressFormSubmitOnEnter'};

    /* @ngInject */
    constructor() {
        this.link = (scope: IScope, element: IAugmentedJQuery, attrs: IAttributes, controllers: any) => {
            controllers['myCtrl'].mdRadioGroupCtrl = controllers.mdRadioGroupCtrl;
            element.off('keydown'); //alle keydown listener auf dem element abhaengen
            element.bind('keydown', (event) => { //unseren eigenen listener definieren
                controllers.myCtrl.keydownListener(event, element);

            });
        };
    }

    static factory(): IDirectiveFactory {
        const directive = () => new DVSuppressFormSubmitOnEnter();
        return directive;
    }
}

/**
 * Direktive  die verhindert dass das form submitted wird wenn man enter drueckt auf einem radio-button
 */
export class DVSuppressFormSubmitOnEnterController {

    mdRadioGroupCtrl: any; //see radioButton.js of angular material: mdRadioGroup

    static $inject: string[] = ['$mdConstant', '$mdUtil', '$log'];
    /* @ngInject */
    constructor(private $mdConstant: any, private $mdUtil: any, private $log: ILogService) {

    }

    keydownListener(ev: any, element: IAugmentedJQuery) {
        let keyCode = ev.which || ev.keyCode;

        // Only listen to events that we originated ourselves
        // so that we don't trigger on things like arrow keys in
        // inputs.

        if (keyCode != this.$mdConstant.KEY_CODE.ENTER &&
            ev.currentTarget != ev.target) {
            return;
        }

        switch (keyCode) {
            case this.$mdConstant.KEY_CODE.LEFT_ARROW:
            case this.$mdConstant.KEY_CODE.UP_ARROW:
                ev.preventDefault();
                this.mdRadioGroupCtrl.selectPrevious();
                this.setFocus(element);
                break;

            case this.$mdConstant.KEY_CODE.RIGHT_ARROW:
            case this.$mdConstant.KEY_CODE.DOWN_ARROW:
                ev.preventDefault();
                this.mdRadioGroupCtrl.selectNext();
                this.setFocus(element);
                break;
            case this.$mdConstant.KEY_CODE.ENTER:
                // event.stopPropagation();    //we do not want to submit the form on enter
                // event.preventDefault();
                this.triggerNextButton(element);
                break;
        }
    }

    private setFocus(element: IAugmentedJQuery) {
        if (!element.hasClass('md-focused')) {
            element.addClass('md-focused');
        }
    }

    private triggerNextButton(element: IAugmentedJQuery) {
        let nextButtons: IAugmentedJQuery;
        let formElement: IAugmentedJQuery = angular.element(this.$mdUtil.getClosest(element[0], 'form'));
        if (formElement) {
            nextButtons = formElement.children().find('input[type="submit"], button[type="submit"]');
            if (nextButtons) {
                nextButtons.first().click();
            } else {
                this.$log.debug('no ".next" button found to click on enter');
            }
        }

    }
}

