import {IComponentOptions} from 'angular';
import {DvDialog} from '../../directive/dv-dialog/dv-dialog';
import {ShowTooltipController} from '../../../gesuch/dialog/ShowTooltipController';
import {IDVFocusableController} from '../IDVFocusableController';
import ITranslateService = angular.translate.ITranslateService;

let template = require('./dv-tooltip.html');
require('./dv-tooltip.less');
let showTooltipTemplate = require('../../../gesuch/dialog/showTooltipTemplate.html');

export class DvTooltipComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = DvTooltipController;
    controllerAs = 'vm';
    bindings: any = {
        text: '<',
        inputId: '@'
    };
}

export class DvTooltipController implements IDVFocusableController {

    private inputId: string;

    static $inject: any[] = ['$translate', 'DvDialog'];
    /* @ngInject */
    constructor(private $translate: ITranslateService, private DvDialog: DvDialog) {
    }

    showTooltip(info: any): void {
        this.DvDialog.showDialogFullscreen(showTooltipTemplate, ShowTooltipController, {
            title: '',
            text: info,
            parentController: this
        });
    }

    /**
     * Sets the focus back to the tooltip icon.
     */
    public setFocusBack(elementID: string): void {
        angular.element('#' + this.inputId + '.fa.fa-info-circle').first().focus();
    }
}

