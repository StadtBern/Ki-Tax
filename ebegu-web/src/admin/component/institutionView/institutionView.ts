import EbeguRestUtil from '../../../utils/EbeguRestUtil';
import {IHttpPromiseCallbackArg, IComponentOptions} from 'angular';
import './institutionView.less';
import TSInstitution from '../../../models/TSInstitution';
import TSInstitutionStammdaten from '../../../models/TSInstitutionStammdaten';
import {InstitutionRS} from '../../../core/service/institutionRS.rest';
import {InstitutionStammdatenRS} from '../../../core/service/institutionStammdatenRS.rest';
import IPromise = angular.IPromise;
import {TSTraegerschaft} from '../../../models/TSTraegerschaft';
import {TSMandant} from '../../../models/TSMandant';
import TSAdresse from '../../../models/TSAdresse';
import {getTSBetreuungsangebotTypValues, TSBetreuungsangebotTyp} from '../../../models/enums/TSBetreuungsangebotTyp';
import EbeguUtil from '../../../utils/EbeguUtil';
let template = require('./institutionView.html');

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

    static $inject = ['InstitutionRS', 'MAX_LENGTH', 'EbeguUtil', 'InstitutionStammdatenRS'];

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
    createInstitutionMode : boolean = false;
    createInstitutionStammdatenMode : boolean = false;
    betreuungsangebotValues: Array<any>;
    selectedInstitutionStammdatenBetreuungsangebot: any = null;

    //
    /* @ngInject */
    constructor(institutionRS: InstitutionRS,  MAX_LENGTH: number, ebeguUtil: EbeguUtil, institutionStammdatenRS: InstitutionStammdatenRS) {
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
        this.createInstitutionMode = false;
    }

    isCreateInstitutionsMode(): boolean {
        return this.createInstitutionMode;
    }

    getSelectedInstitution(): TSInstitution {
        return this.selectedInstitution;
    }

    isSelectedInstitution(): boolean {
        return this.isSelected;
    }

    removeInstitution(institution: any): void {
        this.institutionRS.removeInstitution(institution.id);
        var index = this.institutionen.indexOf(institution, 0);
        if (index > -1) {
            this.institutionen.splice(index, 1);
        }
    }

    createInstitution(): void {
        this.selectedInstitution = new TSInstitution();
        this.selectedInstitution.mandant = this.mandant;
        this.isSelected = true;
        this.selectedInstitutionStammdaten = null;
        this.isSelectedStammdaten = false;
        this.instStammdatenList = [];
        this.createInstitutionMode = true;
    }

    saveInstitution() : void {
        this.isSelected = false;

        if (this.createInstitutionMode === true) {
            this.institutionRS.createInstitution(this.selectedInstitution).then((institution: TSInstitution) => {
                this.institutionen.push(institution);
            });
        }else {
            this.institutionRS.updateInstitution(this.selectedInstitution).then((institution: TSInstitution) => {
                var index = this.institutionen.indexOf(institution, 0);
                if (index > -1) {
                    this.institutionen[index] = institution;
                }
            });
        }
        this.createInstitutionMode = false;
    }

    getSelectedInstitutionStammdatenList(): TSInstitutionStammdaten[] {
        return this.instStammdatenList;
    }

    setSelectedInstitutionStammdaten(institutionStammdaten: any): void {
        this.selectedInstitutionStammdaten = institutionStammdaten;
        this.selectedInstitutionStammdatenBetreuungsangebot = this.getBetreuungsangebotFromInstitutionList(institutionStammdaten.betreuungsangebotTyp);
        this.isSelectedStammdaten = true;
        this.createInstitutionStammdatenMode = false;
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
        this.createInstitutionStammdatenMode = true;
    }

    saveInstitutionStammdaten() : void {
        this.isSelectedStammdaten = false;
        this.selectedInstitutionStammdaten.betreuungsangebotTyp = this.selectedInstitutionStammdatenBetreuungsangebot.key;

        if (this.createInstitutionStammdatenMode === true) {
            this.institutionStammdatenRS.createInstitutionStammdaten(this.selectedInstitutionStammdaten).then((institutionStammdaten: TSInstitutionStammdaten) => {
                this.instStammdatenList.push(institutionStammdaten);
            });
        }else {
            this.institutionStammdatenRS.updateInstitutionStammdaten(this.selectedInstitutionStammdaten).then((institutionStammdaten: TSInstitutionStammdaten) => {
                var index = this.instStammdatenList.indexOf(institutionStammdaten, 0);
                if (index > -1) {
                    this.instStammdatenList[index] = institutionStammdaten;
                }
            });
        }
        this.createInstitutionStammdatenMode = false;
    }

    removeInstitutionStammdaten(institutionStammdaten: TSInstitutionStammdaten): void {
        this.institutionStammdatenRS.removeInstitutionStammdaten(institutionStammdaten.id);
        var index = this.instStammdatenList.indexOf(institutionStammdaten, 0);
        if (index > -1) {
            this.instStammdatenList.splice(index, 1);
        }
        this.isSelectedStammdaten = false;
        this.createInstitutionStammdatenMode = false;
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
        if (this.selectedInstitutionStammdatenBetreuungsangebot != null && this.selectedInstitutionStammdatenBetreuungsangebot.key === TSBetreuungsangebotTyp.KITA) {
            return true;
        }else {
            return false;
        }
    }


}
