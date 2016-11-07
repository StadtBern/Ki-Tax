import AbstractGesuchViewController from '../abstractGesuchView';
import {IComponentOptions, IPromise} from 'angular';
import GesuchModelManager from '../../service/gesuchModelManager';
import BerechnungsManager from '../../service/berechnungsManager';
import ErrorService from '../../../core/errors/service/ErrorService';
import WizardStepManager from '../../service/wizardStepManager';
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
import {TSWizardStepStatus} from '../../../models/enums/TSWizardStepStatus';
import {TSBetroffene} from '../../../models/enums/TSBetroffene';
import TSAdresse from '../../../models/TSAdresse';
import {TSAdressetyp} from '../../../models/enums/TSAdressetyp';
import TSUmzugAdresse from '../../../models/TSUmzugAdresse';
import TSGesuchsteller from '../../../models/TSGesuchsteller';
import ITranslateService = angular.translate.ITranslateService;
let template = require('./umzugView.html');
require('./umzugView.less');
let removeDialogTemplate = require('../../dialog/removeDialogTemplate.html');


export class UmzugViewComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {};
    template = template;
    controller = UmzugViewController;
    controllerAs = 'vm';
}


export class UmzugViewController extends AbstractGesuchViewController {

    private umzugAdressen: Array<TSUmzugAdresse> = [];


    static $inject = ['GesuchModelManager', 'BerechnungsManager', 'WizardStepManager', 'ErrorService', '$translate'];
    /* @ngInject */
    constructor(gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager,
                wizardStepManager: WizardStepManager, private errorService: ErrorService, private $translate: ITranslateService) {

        super(gesuchModelManager, berechnungsManager, wizardStepManager);
        this.initViewModel();
    }

    private initViewModel(): void {
        this.umzugAdressen = [];
        this.wizardStepManager.setCurrentStep(TSWizardStepName.UMZUG);
        this.wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.OK);
        this.getAdressenListFromGS1();
        this.getAdressenListFromGS2();
    }

    public getUmzugAdressenList(): Array<TSUmzugAdresse> {
        return this.umzugAdressen;
    }

    public save(form: angular.IFormController): IPromise<void> {
        if (form.$valid) {
            this.errorService.clearAll();
            this.saveAdresseInGS();
            return this.gesuchModelManager.updateGesuchsteller().then((response) => {
            });
        }
        return undefined;
    }

    /**
     * Hier schauen wir wie viele GS es gibt und dementsprechen fuellen wir die Liste aus.
     * Bei Mutationen wird es nur geschaut ob der GS existiert (!=null), da die Familiensituation nicht relevant ist.
     * Es koennte einen GS2 geben obwohl die neue Familiensituation "ledig" sagt
     */
    public getBetroffenenList(): Array<TSBetroffene> {
        let betroffenenList: Array<TSBetroffene> = [];
        if (this.gesuchModelManager.getGesuch()) {
            if (this.gesuchModelManager.getGesuch().gesuchsteller1) {
                betroffenenList.push(TSBetroffene.GESUCHSTELLER_1);
            }
            if (this.gesuchModelManager.getGesuch().gesuchsteller2) {
                betroffenenList.push(TSBetroffene.GESUCHSTELLER_2);
            }
            if (this.gesuchModelManager.getGesuch().gesuchsteller2 && this.gesuchModelManager.getGesuch().gesuchsteller1) {
                // Dies koennte auch direkt beim Push des GS2 gemacht werden, da es keinen GS2 geben darf wenn es keinen GS1 gibt.
                // Allerdings sind wir mit diesem IF sicher dass GS1 und GS2 wirklich existieren.
                betroffenenList.push(TSBetroffene.BEIDE_GESUCHSTELLER);
            }
        }
        return betroffenenList; // empty list wenn die Daten nicht richtig sind
    }

    public getNameFromBetroffene(betroffene: TSBetroffene): string {
        if (TSBetroffene.GESUCHSTELLER_1 === betroffene && this.gesuchModelManager.getGesuch().gesuchsteller1) {
            return this.gesuchModelManager.getGesuch().gesuchsteller1.getFullName();

        } else if (TSBetroffene.GESUCHSTELLER_2 === betroffene && this.gesuchModelManager.getGesuch().gesuchsteller2) {
            return this.gesuchModelManager.getGesuch().gesuchsteller2.getFullName();

        } else if (TSBetroffene.BEIDE_GESUCHSTELLER === betroffene) {
            return this.$translate.instant(TSBetroffene[betroffene]);
        }

        return '';
    }

    private getAdressenListFromGS1(): void {
        if (this.gesuchModelManager.getGesuch() && this.gesuchModelManager.getGesuch().gesuchsteller1) {
            this.gesuchModelManager.getGesuch().gesuchsteller1.getUmzugAdressen().forEach(umzugAdresse => {
                umzugAdresse.showDatumVon = true; // wird benoetigt weil es vom Server nicht kommt
                this.umzugAdressen.push(new TSUmzugAdresse(TSBetroffene.GESUCHSTELLER_1, umzugAdresse));
            });
        }
    }

    private getAdressenListFromGS2(): void {
        if (this.gesuchModelManager.getGesuch() && this.gesuchModelManager.getGesuch().gesuchsteller2) {
            this.gesuchModelManager.getGesuch().gesuchsteller2.getUmzugAdressen().forEach(umzugAdresse => {
                umzugAdresse.showDatumVon = true; // wird benoetigt weil es vom Server nicht kommt
                this.umzugAdressen.push(new TSUmzugAdresse(TSBetroffene.GESUCHSTELLER_2, umzugAdresse));
            });
        }
    }

    public removeUmzugAdresse(adresse: TSUmzugAdresse): void {
        var indexOf = this.umzugAdressen.lastIndexOf(adresse);
        if (indexOf >= 0) {
            this.umzugAdressen.splice(indexOf, 1);
        }
    }

    /**
     * Erstellt eine neue leere Adresse vom Typ WOHNADRESSE
     */
    public createUmzugAdresse(): void {
        let adresse: TSAdresse = new TSAdresse();
        adresse.showDatumVon = true;
        adresse.adresseTyp = TSAdressetyp.WOHNADRESSE;
        let umzugAdresse: TSUmzugAdresse = new TSUmzugAdresse(undefined, adresse);
        this.umzugAdressen.push(umzugAdresse);
    }

    private saveAdresseInGS(): void {
        this.umzugAdressen.forEach(umzugAdresse => {

            if (TSBetroffene.GESUCHSTELLER_1 === umzugAdresse.betroffene) {
                this.addAdresseToGS(this.gesuchModelManager.getGesuch().gesuchsteller1, umzugAdresse.adresse);

            } else if (TSBetroffene.GESUCHSTELLER_2 === umzugAdresse.betroffene) {
                this.addAdresseToGS(this.gesuchModelManager.getGesuch().gesuchsteller2, umzugAdresse.adresse);

            } else if (TSBetroffene.BEIDE_GESUCHSTELLER === umzugAdresse.betroffene) {
                this.addAdresseToGS(this.gesuchModelManager.getGesuch().gesuchsteller1, umzugAdresse.adresse);
                this.addAdresseToGS(this.gesuchModelManager.getGesuch().gesuchsteller2, umzugAdresse.adresse);
            }
        });
    }

    private addAdresseToGS(gesuchsteller: TSGesuchsteller, adresse: TSAdresse) {
        if (gesuchsteller) {
            if (gesuchsteller.adressen.indexOf(adresse) < 0) {
                gesuchsteller.addAdresse(adresse);
            } else {
                //update old adresse
            }
        }
    }
}
