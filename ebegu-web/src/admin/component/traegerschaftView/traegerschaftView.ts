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

    static $inject = ['TraegerschaftRS', 'ErrorService'];
    /* @ngInject */
    constructor(TraegerschaftRS: TraegerschaftRS, private errorService: ErrorService) {
        this.traegerschaftRS = TraegerschaftRS;
    }

    getTraegerschaftenList(): TSTraegerschaft[] {
        return this.traegerschaften;
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
                this.newTraegerschaft = null;
            });
        }
    }

}
