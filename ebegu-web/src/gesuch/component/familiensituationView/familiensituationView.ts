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
let template = require('./familiensituationView.html');

export class FamiliensituationViewComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {};
    template = template;   //todo low prio evtl mit require statt mit import
    controller = FamiliensituationViewController;
    controllerAs = 'vm';
}


export class FamiliensituationViewController extends AbstractGesuchViewController {
    familienstatusValues: Array<TSFamilienstatus>;
    gesuchstellerKardinalitaetValues: Array<TSGesuchstellerKardinalitaet>;

    static $inject = ['$state', 'GesuchModelManager', 'BerechnungsManager'];
    /* @ngInject */
    constructor($state: IStateService, gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager) {
        super($state, gesuchModelManager, berechnungsManager);
        this.familienstatusValues = getTSFamilienstatusValues();
        this.gesuchstellerKardinalitaetValues = getTSGesuchstellerKardinalitaetValues();
    }

    submit($form: IFormController) {
        if ($form.$valid) {
            this.gesuchModelManager.updateFamiliensituation().then((response: any) => {
                this.state.go('gesuch.stammdaten', {gesuchstellerNumber: 1});
            });
        }
    }

    showGesuchstellerKardinalitaet(): boolean {
        return this.getFamiliensituation().familienstatus === TSFamilienstatus.ALLEINERZIEHEND
            || this.getFamiliensituation().familienstatus === TSFamilienstatus.WENIGER_FUENF_JAHRE;
    }

    public getFamiliensituation(): TSFamiliensituation {
        return this.gesuchModelManager.familiensituation;
    }


}
