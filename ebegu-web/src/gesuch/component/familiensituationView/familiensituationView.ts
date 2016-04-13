import AbstractGesuchViewController from '../abstractGesuchView';
import {IComponentOptions, IFormController} from 'angular';
import {IStateService} from 'angular-ui-router';
import GesuchForm from '../../service/gesuchForm';
import TSGesuch from '../../../models/TSGesuch';
import TSFamiliensituation from '../../../models/TSFamiliensituation'
import * as template from './familiensituationView.html';
import './familiensituationView.less';
import FallRS from '../../service/fallRS.rest';
import GesuchRS from '../../service/gesuchRS.rest';
import FamiliensituationRS from '../../service/familiensituationRS.rest';
import {TSFamilienstatus, getTSFamilienstatusValues} from '../../../models/enums/TSFamilienstatus';
import {TSGesuchstellerKardinalitaet, getTSGesuchstellerKardinalitaetValues} from '../../../models/enums/TSGesuchstellerKardinalitaet';

export class FamiliensituationViewComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {};
    template = template;
    controller = FamiliensituationViewController;
    controllerAs = 'vm';
}


export class FamiliensituationViewController extends AbstractGesuchViewController {
    gesuchForm: GesuchForm;
    fallRS: FallRS;
    gesuchRS: GesuchRS;
    familiensituationRS: FamiliensituationRS;
    familienstatusValues: Array<TSFamilienstatus>;
    gesuchstellerKardinalitaetValues: Array<TSGesuchstellerKardinalitaet>;

    static $inject = ['$state', 'FamiliensituationRS', 'FallRS', 'GesuchRS', 'GesuchForm'];
    /* @ngInject */
    constructor($state: angular.ui.IStateService, familiensituationRS: FamiliensituationRS,
                fallRS: FallRS, gesuchRS: GesuchRS, gesuchForm: GesuchForm) {
        super($state);
        this.gesuchForm = gesuchForm;
        this.fallRS = fallRS;
        this.gesuchRS = gesuchRS;
        this.familiensituationRS = familiensituationRS;
        this.familienstatusValues = getTSFamilienstatusValues();
        this.gesuchstellerKardinalitaetValues = getTSGesuchstellerKardinalitaetValues();
    }

    submit ($form: angular.IFormController) {
        if ($form.$valid) {
            //testen ob aktuelles familiensituation schon gespeichert ist
            if (this.getFamiliensituation().timestampErstellt) {
                this.familiensituationRS.update(this.getFamiliensituation()).then((familienResponse: any) => {
                    this.gesuchForm.familiensituation = familienResponse.data;
                    this.state.go("gesuch.stammdaten", {gesuchstellerNumber:1});
                });
            } else {
                //todo team. Fall und Gesuch sollten in ihren eigenen Services gespeichert werden
                this.fallRS.create(this.gesuchForm.fall).then((fallResponse: any) => {
                    this.gesuchForm.fall = fallResponse.data;
                    this.gesuchForm.gesuch.fall = fallResponse.data;
                    this.gesuchRS.create(this.gesuchForm.gesuch).then((gesuchResponse: any) => {
                        this.gesuchForm.gesuch = gesuchResponse.data;
                        this.getFamiliensituation().gesuch = gesuchResponse.data;
                        this.familiensituationRS.create(this.getFamiliensituation()).then((familienResponse: any) => {
                            this.gesuchForm.familiensituation = familienResponse.data;
                            this.state.go("gesuch.stammdaten", {gesuchstellerNumber:1});
                        });
                    });
                });
            }
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
