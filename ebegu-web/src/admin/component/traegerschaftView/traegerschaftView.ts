import {IComponentOptions} from 'angular';
import './traegerschaftView.less';
import {TSTraegerschaft} from '../../../models/TSTraegerschaft';
import ErrorService from '../../../core/errors/service/ErrorService';
import {TraegerschaftRS} from '../../../core/service/traegerschaftRS.rest';
import {OkDialogController} from '../../../gesuch/dialog/OkDialogController';
import {DvDialog} from '../../../core/directive/dv-dialog/dv-dialog';
import {OkHtmlDialogController} from '../../../gesuch/dialog/OkHtmlDialogController';
import {RemoveDialogController} from '../../../gesuch/dialog/RemoveDialogController';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import AbstractAdminViewController from '../../abstractAdminView';
import IPromise = angular.IPromise;
import IFormController = angular.IFormController;
import EbeguUtil from '../../../utils/EbeguUtil';
let template = require('./traegerschaftView.html');
let style = require('./traegerschaftView.less');
let okDialogTempl = require('../../../gesuch/dialog/okDialogTemplate.html');
let okHtmlDialogTempl = require('../../../gesuch/dialog/okHtmlDialogTemplate.html');
let removeDialogTemplate = require('../../../gesuch/dialog/removeDialogTemplate.html');

export class TraegerschaftViewComponentConfig implements IComponentOptions {
    transclude: boolean = false;
    bindings: any = {
        traegerschaften: '<'
    };
    template: string = template;
    controller: any = TraegerschaftViewController;
    controllerAs: string = 'vm';
}

export class TraegerschaftViewController extends AbstractAdminViewController {

    traegerschaftRS: TraegerschaftRS;
    traegerschaften: TSTraegerschaft[];
    traegerschaft: TSTraegerschaft = undefined;

    static $inject = ['TraegerschaftRS', 'ErrorService', 'DvDialog', 'AuthServiceRS', 'EbeguUtil'];
    /* @ngInject */
    constructor(TraegerschaftRS: TraegerschaftRS, private errorService: ErrorService, private dvDialog: DvDialog,
                authServiceRS: AuthServiceRS, private ebeguUtil: EbeguUtil) {
        super(authServiceRS);
        this.traegerschaftRS = TraegerschaftRS;
    }

    getTraegerschaftenList(): TSTraegerschaft[] {
        return this.traegerschaften;
    }

    removeTraegerschaft(traegerschaft: any): void {
        this.dvDialog.showDialog(removeDialogTemplate, RemoveDialogController, {
            deleteText: '',
            title: 'LOESCHEN_DIALOG_TITLE'
        })
            .then(() => {   //User confirmed removal
                this.traegerschaft = undefined;
                this.traegerschaftRS.removeTraegerschaft(traegerschaft.id).then((response) => {
                    let index = EbeguUtil.getIndexOfElementwithID(traegerschaft, this.traegerschaften);
                    if (index > -1) {
                        this.traegerschaften.splice(index, 1);
                    }
                });
            });
    }

    createTraegerschaft(): void {
        this.traegerschaft = new TSTraegerschaft();
        this.traegerschaft.active = true;
    }

    saveTraegerschaft(form: IFormController): void {
        if (form.$valid) {
            this.errorService.clearAll();
            let newTraegerschaft: boolean = this.traegerschaft.isNew();
            this.traegerschaftRS.createTraegerschaft(this.traegerschaft).then((traegerschaft: TSTraegerschaft) => {
                if (newTraegerschaft) {
                    this.traegerschaften.push(traegerschaft);
                } else {
                    let index = EbeguUtil.getIndexOfElementwithID(traegerschaft, this.traegerschaften);
                    if (index > -1) {
                        this.traegerschaften[index] = traegerschaft;
                        this.ebeguUtil.handleSmarttablesUpdateBug(this.traegerschaften);
                    }
                }
                this.traegerschaft = undefined;
                if (!traegerschaft.synchronizedWithOpenIdm) {
                    this.dvDialog.showDialog(okDialogTempl, OkDialogController, {
                        title: 'TRAEGERSCHAFT_CREATE_SYNCHRONIZE'
                    });
                }
            });
        }
    }

    cancelTraegerschaft(): void {
        this.traegerschaft = undefined;
    }

    setSelectedTraegerschaft(selected: TSTraegerschaft): void {
        this.traegerschaft = angular.copy(selected);
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
