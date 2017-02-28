import {IComponentOptions} from 'angular';
import './traegerschaftView.less';
import {TSTraegerschaft} from '../../../models/TSTraegerschaft';
import EbeguUtil from '../../../utils/EbeguUtil';
import ErrorService from '../../../core/errors/service/ErrorService';
import {TraegerschaftRS} from '../../../core/service/traegerschaftRS.rest';
import {OkDialogController} from '../../../gesuch/dialog/OkDialogController';
import {DvDialog} from '../../../core/directive/dv-dialog/dv-dialog';
import {OkHtmlDialogController} from '../../../gesuch/dialog/OkHtmlDialogController';
import IPromise = angular.IPromise;
import IFormController = angular.IFormController;
let template = require('./traegerschaftView.html');
let style = require('./traegerschaftView.less');
let okDialogTempl = require('../../../gesuch/dialog/okDialogTemplate.html');
let okHtmlDialogTempl = require('../../../gesuch/dialog/okHtmlDialogTemplate.html');

export class TraegerschaftViewComponentConfig implements IComponentOptions {
    transclude: boolean = false;
    bindings: any = {
        traegerschaften: '<'
    };
    template: string = template;
    controller: any = TraegerschaftViewController;
    controllerAs: string = 'vm';
}

export class TraegerschaftViewController {

    traegerschaftRS: TraegerschaftRS;
    traegerschaften: TSTraegerschaft[];
    newTraegerschaft: TSTraegerschaft = null;

    static $inject = ['TraegerschaftRS', 'ErrorService', 'DvDialog'];
    /* @ngInject */
    constructor(TraegerschaftRS: TraegerschaftRS, private errorService: ErrorService, private dvDialog: DvDialog) {
        this.traegerschaftRS = TraegerschaftRS;
    }

    getTraegerschaftenList(): TSTraegerschaft[] {
        return this.traegerschaften;
    }

    removeTraegerschaft(traegerschaft: any): void {
        this.newTraegerschaft = undefined;
        this.traegerschaftRS.removeTraegerschaft(traegerschaft.id).then((response) => {
            let index = EbeguUtil.getIndexOfElementwithID(traegerschaft, this.traegerschaften);
            if (index > -1) {
                this.traegerschaften.splice(index, 1);
            }
        });

    }

    createTraegerschaft(): void {
        this.newTraegerschaft = new TSTraegerschaft();
        this.newTraegerschaft.active = true;
    }

    clearNewTraegerschaft(): void {
        this.newTraegerschaft = undefined;
    }

    saveTraegerschaft(form: IFormController): void {
        if (form.$valid) {
            this.errorService.clearAll();
            this.traegerschaftRS.createTraegerschaft(this.newTraegerschaft).then((traegerschaft: TSTraegerschaft) => {
                this.traegerschaften.push(traegerschaft);
                this.newTraegerschaft = null;
                if (!traegerschaft.synchronizedWithOpenIdm) {
                    this.dvDialog.showDialog(okDialogTempl, OkDialogController, {
                        title: 'TRAEGERSCHAFT_CREATE_SYNCHRONIZE'
                    });
                }
            });
        }
    }

    private syncWithOpenIdm(): void {
        this.traegerschaftRS.synchronizeTraegerschaften().then((respone) => {
            let returnString = respone.data.replace(/(?:\r\n|\r|\n)/g, '<br />');
            return this.dvDialog.showDialog(okHtmlDialogTempl, OkHtmlDialogController, {
                title: returnString
            });
        });
    }

}
