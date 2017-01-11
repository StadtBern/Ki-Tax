import {IComponentOptions} from 'angular';
import {DvDialog} from '../../directive/dv-dialog/dv-dialog';
import {ShowTooltipController} from '../../../gesuch/dialog/ShowTooltipController';
import IDialogService = angular.material.IDialogService;
import ITranslateService = angular.translate.ITranslateService;
import IScope = angular.IScope;
let template = require('./dv-tooltip.html');
require('./dv-tooltip.less');
let showTooltipTemplate = require('../../../gesuch/dialog/showTooltipTemplate.html');

export class DvTooltipComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = DvTooltipController;
    controllerAs = 'vm';
    bindings: any = {
        text: '<'
    };
}

export class DvTooltipController {

    static $inject: any[] = ['$translate', 'DvDialog'];
    /* @ngInject */
    constructor(private $translate: ITranslateService, private DvDialog: DvDialog) {
    }

    showTooltip(info: any): void {
        this.DvDialog.showDialogFullscreen(showTooltipTemplate, ShowTooltipController, {
            title: '',
            text: info
        });
    }
}

