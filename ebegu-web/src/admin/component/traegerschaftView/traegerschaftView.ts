import {IComponentOptions} from 'angular';
import './traegerschaftView.less';
import {TSTraegerschaft} from '../../../models/TSTraegerschaft';
import EbeguUtil from '../../../utils/EbeguUtil';
import ErrorService from '../../../core/errors/service/ErrorService';
import {TraegerschaftRS} from '../../../core/service/traegerschaftRS.rest';
import IPromise = angular.IPromise;
import IFormController = angular.IFormController;
let template = require('./traegerschaftView.html');
let style = require('./traegerschaftView.less');

export class TraegerschaftViewComponentConfig implements IComponentOptions {
    transclude: boolean = false;
    bindings: any = {
        traegerschaften: '<',
        mandant: '<'
    };
    template: string = template;
    controller: any = TraegerschaftViewController;
    controllerAs: string = 'vm';
}

export class TraegerschaftViewController {

    traegerschaftRS: TraegerschaftRS;
    ebeguUtil: EbeguUtil;
    traegerschaften: TSTraegerschaft[];


    newTraegerschaft: TSTraegerschaft = null;
    isSelected: boolean = false;


    static $inject = ['TraegerschaftRS', 'EbeguUtil', 'ErrorService'];
    /* @ngInject */
    constructor(TraegerschaftRS: TraegerschaftRS, ebeguUtil: EbeguUtil, private errorService: ErrorService) {
        this.traegerschaftRS = TraegerschaftRS;
        this.ebeguUtil = ebeguUtil;


    }

    getTraegerschaftenList(): TSTraegerschaft[] {
        return this.traegerschaften;
    }

    getSelectedTraegerschaft(): TSTraegerschaft {
        return this.newTraegerschaft;
    }

    isSelectedTraegerschaft(): boolean {
        return this.isSelected;
    }

    removeTraegerschaft(traegerschaft: any): void {
        this.newTraegerschaft = undefined;
        this.traegerschaftRS.removeTraegerschaft(traegerschaft.id).then((response) => {
            var index = EbeguUtil.getIndexOfElementwithID(traegerschaft, this.traegerschaften);
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

            this.traegerschaftRS.createTraegerschaft(this.newTraegerschaft).then((Traegerschaft: TSTraegerschaft) => {
                this.traegerschaften.push(Traegerschaft);
                this.resetTraegerschaftSelection();
            });
        }

    }

    private resetTraegerschaftSelection() {
        this.newTraegerschaft = null;
        this.isSelected = false;
    }

}
