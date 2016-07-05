import {IComponentOptions} from 'angular';
import './institutionView.less';
import TSInstitution from '../../../models/TSInstitution';
import TSInstitutionStammdaten from '../../../models/TSInstitutionStammdaten';
import {InstitutionRS} from '../../../core/service/institutionRS.rest';
import {InstitutionStammdatenRS} from '../../../core/service/institutionStammdatenRS.rest';
import {TSTraegerschaft} from '../../../models/TSTraegerschaft';
import {TSMandant} from '../../../models/TSMandant';
import TSAdresse from '../../../models/TSAdresse';
import {getTSBetreuungsangebotTypValues, TSBetreuungsangebotTyp} from '../../../models/enums/TSBetreuungsangebotTyp';
import EbeguUtil from '../../../utils/EbeguUtil';
import IPromise = angular.IPromise;
import IFormController = angular.IFormController;
let template = require('./institutionView.html');
let style = require('./institutionView.less');

export class InstitutionViewComponentConfig implements IComponentOptions {
    transclude: boolean = false;
    bindings: any = {
        institutionen: '<',
        traegerschaften: '<',
        mandant: '<'
    };
    template: string = template;
    controller: any = InstitutionViewController;
    controllerAs: string = 'vm';
}

export class InstitutionViewController {

    institutionRS: InstitutionRS;
    institutionStammdatenRS: InstitutionStammdatenRS;
    ebeguUtil: EbeguUtil;
    institutionen: TSInstitution[];
    traegerschaften: TSTraegerschaft[];
    mandant: TSMandant;
    instStammdatenList: TSInstitutionStammdaten[] = [];
    selectedInstitution: TSInstitution = null;
    isSelected: boolean = false;
    selectedInstitutionStammdaten: TSInstitutionStammdaten = null;
    isSelectedStammdaten: boolean = false;
    betreuungsangebotValues: Array<any>;
    selectedInstitutionStammdatenBetreuungsangebot: any = null;


    static $inject = ['InstitutionRS', 'EbeguUtil', 'InstitutionStammdatenRS'];
    /* @ngInject */
    constructor(institutionRS: InstitutionRS, ebeguUtil: EbeguUtil, institutionStammdatenRS: InstitutionStammdatenRS) {
        this.institutionRS = institutionRS;
        this.ebeguUtil = ebeguUtil;
        this.institutionStammdatenRS = institutionStammdatenRS;
        this.setBetreuungsangebotTypValues();

    }

    getInstitutionenList(): TSInstitution[] {
        return this.institutionen;
    }

    getTreagerschaftList(): Array<TSTraegerschaft> {
        return this.traegerschaften;
    }

    setSelectedInstitution(institution: any): void {
        this.selectedInstitution = institution;
        this.isSelected = true;
        this.selectedInstitutionStammdaten = null;
        this.isSelectedStammdaten = false;
        this.institutionStammdatenRS.getAllInstitutionStammdatenByInstitution(this.selectedInstitution.id).then((loadedInstStammdaten) => {
            this.instStammdatenList = loadedInstStammdaten;
        });
    }

    isCreateInstitutionsMode(): boolean {
        return this.selectedInstitution.isNew();

    }

    isCreateStammdatenMode(): boolean {
        return this.selectedInstitutionStammdaten.isNew();
    }

    getSelectedInstitution(): TSInstitution {
        return this.selectedInstitution;
    }

    isSelectedInstitution(): boolean {
        return this.isSelected;
    }

    removeInstitution(institution: any): void {
        this.selectedInstitution = null;
        this.isSelected = false;
        this.institutionRS.removeInstitution(institution.id).then((response) => {
            var index = EbeguUtil.getIndexOfElementwithID(institution, this.institutionen);
            if (index > -1) {
                this.institutionen.splice(index, 1);
            }
        });

    }

    createInstitution(): void {
        this.selectedInstitution = new TSInstitution();
        this.selectedInstitution.mandant = this.mandant;
        this.isSelected = true;
        this.selectedInstitutionStammdaten = null;
        this.isSelectedStammdaten = false;
        this.instStammdatenList = [];
    }

    saveInstitution(form: IFormController): void {
        if (form.$valid) {
            if (this.isCreateInstitutionsMode() === true) {
                this.institutionRS.createInstitution(this.selectedInstitution).then((institution: TSInstitution) => {
                    this.institutionen.push(institution);
                    this.resetInstitutionSelection();
                });
            } else {
                this.institutionRS.updateInstitution(this.selectedInstitution).then((institution: TSInstitution) => {
                    var index = EbeguUtil.getIndexOfElementwithID(institution, this.institutionen);
                    if (index > -1) {
                        this.institutionen[index] = institution;
                        this.resetInstitutionSelection();
                    }
                });
            }
        }

    }

    private resetInstitutionSelection() {
        this.selectedInstitution = null;
        this.isSelected = false;
    }

    getSelectedInstitutionStammdatenList(): TSInstitutionStammdaten[] {
        return this.instStammdatenList;
    }

    setSelectedInstitutionStammdaten(institutionStammdaten: any): void {
        this.selectedInstitutionStammdaten = institutionStammdaten;
        this.selectedInstitutionStammdatenBetreuungsangebot = this.getBetreuungsangebotFromInstitutionList(institutionStammdaten.betreuungsangebotTyp);
        this.isSelectedStammdaten = true;
    }

    getSelectedInstitutionStammdaten(): TSInstitutionStammdaten {
        return this.selectedInstitutionStammdaten;
    }

    isSelectedInstitutionStammdaten(): boolean {
        return this.isSelectedStammdaten;
    }

    createInstitutionStammdaten(): void {
        this.selectedInstitutionStammdaten = new TSInstitutionStammdaten();
        this.selectedInstitutionStammdaten.adresse = new TSAdresse();
        this.selectedInstitutionStammdaten.institution = this.selectedInstitution;
        this.isSelectedStammdaten = true;
    }

    saveInstitutionStammdaten(form: IFormController): void {
        if (form.$valid) {
            this.selectedInstitutionStammdaten.betreuungsangebotTyp = this.selectedInstitutionStammdatenBetreuungsangebot.key;

            if (this.isCreateStammdatenMode()) {
                this.institutionStammdatenRS.createInstitutionStammdaten(this.selectedInstitutionStammdaten).then((institutionStammdaten: TSInstitutionStammdaten) => {
                    this.instStammdatenList.push(institutionStammdaten);
                    this.resetInstitutionStammdatenSelection();
                });
            } else {
                this.institutionStammdatenRS.updateInstitutionStammdaten(this.selectedInstitutionStammdaten).then((institutionStammdaten: TSInstitutionStammdaten) => {
                    var index = EbeguUtil.getIndexOfElementwithID(institutionStammdaten, this.instStammdatenList);
                    if (index > -1) {
                        this.instStammdatenList[index] = institutionStammdaten;
                        this.resetInstitutionStammdatenSelection();
                    }
                });
            }
        }
    }

    private resetInstitutionStammdatenSelection() {
        this.selectedInstitutionStammdaten = null;
        this.isSelectedStammdaten = false;
    }

    removeInstitutionStammdaten(institutionStammdaten: TSInstitutionStammdaten): void {
        this.institutionStammdatenRS.removeInstitutionStammdaten(institutionStammdaten.id).then((result) => {

            var index = EbeguUtil.getIndexOfElementwithID(institutionStammdaten, this.instStammdatenList);
            if (index > -1) {
                this.instStammdatenList.splice(index, 1);
            }
            this.isSelectedStammdaten = false;
        });

    }

    private setBetreuungsangebotTypValues(): void {
        this.betreuungsangebotValues = this.ebeguUtil.translateStringList(getTSBetreuungsangebotTypValues());
    }

    getBetreuungsangebotFromInstitutionList(betreuungsangebotTyp: TSBetreuungsangebotTyp) {
        return $.grep(this.betreuungsangebotValues, (value: any) => {
            return value.key === betreuungsangebotTyp;
        })[0];
    }

    isKita(): boolean {
        if (this.selectedInstitutionStammdatenBetreuungsangebot && this.selectedInstitutionStammdatenBetreuungsangebot.key === TSBetreuungsangebotTyp.KITA) {
            return true;
        } else {
            return false;
        }
    }


}
