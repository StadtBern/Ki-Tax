import AbstractGesuchViewController from '../abstractGesuchView';
import {IComponentOptions, IFormController} from 'angular';
import {IStateService} from 'angular-ui-router';
import GesuchForm from '../../service/gesuchForm';
import TSFamiliensituation from '../../../models/TSFamiliensituation';
import './familiensituationView.less';
import {TSFamilienstatus, getTSFamilienstatusValues} from '../../../models/enums/TSFamilienstatus';
import {
    TSGesuchstellerKardinalitaet,
    getTSGesuchstellerKardinalitaetValues
} from '../../../models/enums/TSGesuchstellerKardinalitaet';
let template = require('./familiensituationView.html');

export class FamiliensituationViewComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {};
    template = template;   //todo low prio evtl mit require statt mit import
    controller = FamiliensituationViewController;
    controllerAs = 'vm';
}


export class FamiliensituationViewController extends AbstractGesuchViewController {
    gesuchForm: GesuchForm;

    familienstatusValues: Array<TSFamilienstatus>;
    gesuchstellerKardinalitaetValues: Array<TSGesuchstellerKardinalitaet>;

    static $inject = ['$state', 'GesuchForm'];
    /* @ngInject */
    constructor($state: IStateService, gesuchForm: GesuchForm) {
        super($state);
        this.gesuchForm = gesuchForm;
        this.familienstatusValues = getTSFamilienstatusValues();
        this.gesuchstellerKardinalitaetValues = getTSGesuchstellerKardinalitaetValues();
    }

    submit($form: IFormController) {
        if ($form.$valid) {
            this.gesuchForm.updateFamiliensituation().then((response: any) => {
                this.state.go('gesuch.stammdaten', {gesuchstellerNumber: 1});
            });
        }
    }

    showGesuchstellerKardinalitaet(): boolean {
        return this.getFamiliensituation().familienstatus === TSFamilienstatus.ALLEINERZIEHEND
            || this.getFamiliensituation().familienstatus === TSFamilienstatus.WENIGER_FUENF_JAHRE;
    }
    
    public getFamiliensituation(): TSFamiliensituation {
        return this.gesuchForm.familiensituation;
    }

}
