import AbstractGesuchViewController from '../abstractGesuchView';
import {IComponentOptions, IFormController} from 'angular';
import {IStateService} from 'angular-ui-router';
import GesuchModelManager from '../../service/gesuchModelManager';
import TSFamiliensituation from '../../../models/TSFamiliensituation';
import './familiensituationView.less';
import {TSFamilienstatus, getTSFamilienstatusValues} from '../../../models/enums/TSFamilienstatus';
import {
    TSGesuchstellerKardinalitaet,
    getTSGesuchstellerKardinalitaetValues
} from '../../../models/enums/TSGesuchstellerKardinalitaet';
import BerechnungsManager from '../../service/berechnungsManager';
import ErrorService from '../../../core/errors/service/ErrorService';
let template = require('./familiensituationView.html');
require('./familiensituationView.less');


export class FamiliensituationViewComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {};
    template = template;
    controller = FamiliensituationViewController;
    controllerAs = 'vm';
}


export class FamiliensituationViewController extends AbstractGesuchViewController {
    familienstatusValues: Array<TSFamilienstatus>;
    gesuchstellerKardinalitaetValues: Array<TSGesuchstellerKardinalitaet>;

    static $inject = ['$state', 'GesuchModelManager', 'BerechnungsManager', 'ErrorService'];
    /* @ngInject */
    constructor($state: IStateService, gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager, private errorService: ErrorService) {
        super($state, gesuchModelManager, berechnungsManager);
        this.familienstatusValues = getTSFamilienstatusValues();
        this.gesuchstellerKardinalitaetValues = getTSGesuchstellerKardinalitaetValues();
        this.initViewModel();
    }

    private initViewModel(): void {
        this.gesuchModelManager.initFamiliensituation();
    }

    previousStep(form: IFormController): void {
        this.save(form, (response: any) => {
            this.state.go('gesuch.fallcreation');
        });
    }

    nextStep(form: IFormController): void {
        this.save(form, (response: any) => {
            this.state.go('gesuch.stammdaten');
        });
    }

    private save(form: angular.IFormController, navigationFunction: (gesuch: any) => any) {
        if (form.$valid) {
            this.errorService.clearAll();
            this.gesuchModelManager.updateFamiliensituation().then(navigationFunction);
        }
    }

    showGesuchstellerKardinalitaet(): boolean {
        return this.getFamiliensituation().familienstatus === TSFamilienstatus.ALLEINERZIEHEND
            || this.getFamiliensituation().familienstatus === TSFamilienstatus.WENIGER_FUENF_JAHRE;
    }

    public getFamiliensituation(): TSFamiliensituation {
        return this.gesuchModelManager.getFamiliensituation();
    }

}
