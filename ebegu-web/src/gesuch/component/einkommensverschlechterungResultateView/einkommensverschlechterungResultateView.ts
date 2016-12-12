import {IComponentOptions, IPromise} from 'angular';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import {IEinkommensverschlechterungResultateStateParams} from '../../gesuch.route';
import BerechnungsManager from '../../service/berechnungsManager';
import TSFinanzielleSituationResultateDTO from '../../../models/dto/TSFinanzielleSituationResultateDTO';
import ErrorService from '../../../core/errors/service/ErrorService';
import TSEinkommensverschlechterungContainer from '../../../models/TSEinkommensverschlechterungContainer';
import TSEinkommensverschlechterung from '../../../models/TSEinkommensverschlechterung';
import WizardStepManager from '../../service/wizardStepManager';
import {TSWizardStepStatus} from '../../../models/enums/TSWizardStepStatus';
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
import TSFinanzModel from '../../../models/TSFinanzModel';
import IQService = angular.IQService;
let template = require('./einkommensverschlechterungResultateView.html');
require('./einkommensverschlechterungResultateView.less');

export class EinkommensverschlechterungResultateViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = EinkommensverschlechterungResultateViewController;
    controllerAs = 'vm';
}

/**
 * Controller fuer die Finanzielle Situation
 */
export class EinkommensverschlechterungResultateViewController extends AbstractGesuchViewController<TSFinanzModel> {


    resultatVorjahr: TSFinanzielleSituationResultateDTO;
    resultatProzent: string;

    static $inject: string[] = ['$stateParams', 'GesuchModelManager', 'BerechnungsManager', 'CONSTANTS', 'ErrorService',
        'WizardStepManager', '$q'];
    /* @ngInject */
    constructor($stateParams: IEinkommensverschlechterungResultateStateParams, gesuchModelManager: GesuchModelManager,
                berechnungsManager: BerechnungsManager, private CONSTANTS: any, private errorService: ErrorService,
                wizardStepManager: WizardStepManager, private $q: IQService) {
        super(gesuchModelManager, berechnungsManager, wizardStepManager);
        let parsedBasisJahrPlusNum = parseInt($stateParams.basisjahrPlus, 10);
        this.model = new TSFinanzModel(this.gesuchModelManager.getBasisjahr(), this.gesuchModelManager.isGesuchsteller2Required(), null, parsedBasisJahrPlusNum);
        this.model.copyEkvDataFromGesuch(this.gesuchModelManager.getGesuch());
        this.model.copyFinSitDataFromGesuch(this.gesuchModelManager.getGesuch());
        this.gesuchModelManager.setBasisJahrPlusNumber(parsedBasisJahrPlusNum);
        this.initViewModel();
        this.calculate();
        this.resultatVorjahr = null;
        this.calculateResultateVorjahr();
    }

    private initViewModel() {
        this.wizardStepManager.setCurrentStep(TSWizardStepName.EINKOMMENSVERSCHLECHTERUNG);
    }

    showGemeinsam(): boolean {
        return this.model.isGesuchsteller2Required() &&
            this.model.getGemeinsameSteuererklaerungToWorkWith() === true;
    }

    showGS1(): boolean {
        return !this.showGemeinsam();
    }

    showGS2(): boolean {
        return this.model.isGesuchsteller2Required() &&
            this.model.getGemeinsameSteuererklaerungToWorkWith() === false;
    }

    showResult(): boolean {
        if (this.model.getBasisJahrPlus() === 1) {
            let ekvFuerBasisJahrPlus1 = this.model.einkommensverschlechterungInfo.ekvFuerBasisJahrPlus1
                && this.model.einkommensverschlechterungInfo.ekvFuerBasisJahrPlus1 === true;
            return ekvFuerBasisJahrPlus1 === true;

        } else {
            return true;
        }
    }

    private save(form: angular.IFormController): IPromise<void> {
        if (form.$valid) {
            //todo team refactoren so dass nur eine resource methode aufgerufen wird (fuer transaktionssicherzheit)
            if (!form.$dirty) {
                // If there are no changes in form we don't need anything to update on Server and we could return the
                // promise immediately
                // Update wizardStepStatus also if the form is empty and not dirty
                return this.updateStatus();
            }

            this.model.copyEkvSitDataToGesuch(this.gesuchModelManager.getGesuch());
            this.errorService.clearAll();
            if (this.gesuchModelManager.getGesuch().gesuchsteller1) {
                this.gesuchModelManager.setGesuchstellerNumber(1);
                if (this.gesuchModelManager.getGesuch().gesuchsteller2) {
                    return this.gesuchModelManager.saveEinkommensverschlechterungContainer().then(() => {
                        this.gesuchModelManager.setGesuchstellerNumber(2);
                        return this.gesuchModelManager.saveEinkommensverschlechterungContainer().then(() => {
                            return this.updateStatus();
                        });
                    });
                } else {
                    return this.gesuchModelManager.saveEinkommensverschlechterungContainer().then(() => {
                        return this.updateStatus();
                    });
                }
            }
        }
        return undefined;
    }

    /**
     * Hier wird der Status von WizardStep auf OK (MUTIERT fuer Mutationen) aktualisiert aber nur wenn es die letzt Seite EVResultate
     * gespeichert wird. Sonst liefern wir einfach den aktuellen GS als Promise zurueck.
     */
    private updateStatus(): IPromise<any> {
        if (this.isLastEinkVersStep()) {
            if (this.gesuchModelManager.getGesuch().isMutation()) {
                return this.wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.MUTIERT);
            } else {
                return this.wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.OK);
            }
        } else {
            return this.$q.when(this.gesuchModelManager.getStammdatenToWorkWith()); //wenn nichts gespeichert einfach den aktuellen GS zurueckgeben
        }
    }

    calculate() {
        if (this.model && this.model.getBasisJahrPlus()) {
            this.berechnungsManager
                .calculateEinkommensverschlechterungTemp(this.model, this.model.getBasisJahrPlus())
                .then(() => {
                    this.resultatProzent = this.calculateVeraenderung();
                });
        } else {
            console.log('No gesuch and Basisjahr to calculate');
        }
    }

    public getEinkommensverschlechterungContainerGS1(): TSEinkommensverschlechterungContainer {
        return this.model.einkommensverschlechterungContainerGS1;
    }

    public getEinkommensverschlechterungGS1_GS(): TSEinkommensverschlechterung {
        if (this.model.getBasisJahrPlus() === 2) {
            return this.getEinkommensverschlechterungContainerGS1().ekvGSBasisJahrPlus2;
        } else {
            return this.getEinkommensverschlechterungContainerGS1().ekvGSBasisJahrPlus1;
        }
    }

    public getEinkommensverschlechterungGS1_JA(): TSEinkommensverschlechterung {
        if (this.model.getBasisJahrPlus() === 2) {
            return this.getEinkommensverschlechterungContainerGS1().ekvJABasisJahrPlus2;
        } else {
            return this.getEinkommensverschlechterungContainerGS1().ekvJABasisJahrPlus1;
        }
    }

    public getEinkommensverschlechterungContainerGS2(): TSEinkommensverschlechterungContainer {
        return this.model.einkommensverschlechterungContainerGS2;
    }

    public getEinkommensverschlechterungGS2_GS(): TSEinkommensverschlechterung {
        if (this.model.getBasisJahrPlus() === 2) {
            return this.getEinkommensverschlechterungContainerGS2().ekvGSBasisJahrPlus2;
        } else {
            return this.getEinkommensverschlechterungContainerGS2().ekvGSBasisJahrPlus1;
        }
    }

    public getEinkommensverschlechterungGS2_JA(): TSEinkommensverschlechterung {
        if (this.model.getBasisJahrPlus() === 2) {
            return this.getEinkommensverschlechterungContainerGS2().ekvJABasisJahrPlus2;
        } else {
            return this.getEinkommensverschlechterungContainerGS2().ekvJABasisJahrPlus1;
        }
    }

    public getResultate(): TSFinanzielleSituationResultateDTO {
        if (this.model.getBasisJahrPlus() === 2) {
            return this.berechnungsManager.einkommensverschlechterungResultateBjP2;
        } else {
            return this.berechnungsManager.einkommensverschlechterungResultateBjP1;
        }
    }

    public calculateResultateVorjahr() {

        if (this.model.getBasisJahrPlus() === 2) {
            this.berechnungsManager.calculateEinkommensverschlechterungTemp(this.model, 1).then((resultatVorjahr) => {
                this.resultatVorjahr = resultatVorjahr;
                this.resultatProzent = this.calculateVeraenderung();
            });
        } else {
            this.berechnungsManager.calculateFinanzielleSituationTemp(this.model).then((resultatVorjahr) => {
                this.resultatVorjahr = resultatVorjahr;
                this.resultatProzent = this.calculateVeraenderung();
            });
        }
    }


    /**
     *
     * @returns {string} Veraenderung im Prozent im vergleich zum Vorjahr
     */
    public calculateVeraenderung(): string {
        if (this.resultatVorjahr) {

            let massgebendesEinkVorAbzFamGr = this.getResultate().massgebendesEinkVorAbzFamGr;
            let massgebendesEinkVorAbzFamGrVJ = this.resultatVorjahr.massgebendesEinkVorAbzFamGr;
            if (massgebendesEinkVorAbzFamGr && massgebendesEinkVorAbzFamGrVJ) {

                let promil: number = 1000 - (massgebendesEinkVorAbzFamGr * 1000 / massgebendesEinkVorAbzFamGrVJ);
                let sign: string;
                promil = Math.round(promil);
                if (promil > 0) {
                    sign = '- ';
                } else {
                    sign = '+ ';
                }
                return sign + Math.abs(Math.floor(promil / 10)) + '.' + Math.abs(promil % 10) + ' %';
            } else if (!massgebendesEinkVorAbzFamGr && !massgebendesEinkVorAbzFamGrVJ) {
                // case: Kein Einkommen in diesem Jahr und im letzten Jahr
                return '+ 0 %';
            } else if (!massgebendesEinkVorAbzFamGr) {
                // case: Kein Einkommen in diesem Jahr aber Einkommen im letzten Jahr
                return '- 100 %';
            } else {
                // case: Kein Einkommen im letzten Jahr aber Einkommen in diesem Jahr
                return '+ 100 %';
            }
        }
        return '';
    }

    /**
     * Prueft ob es die letzte Seite von EVResultate ist. Es ist die letzte Seite wenn es zum letzten EV-Jahr gehoert
     * @returns {boolean}
     */
    private isLastEinkVersStep(): boolean {
        // Letztes Jahr haengt von den eingegebenen Daten ab
        return (this.gesuchModelManager.getEinkommensverschlechterungsInfo().ekvFuerBasisJahrPlus2 && this.gesuchModelManager.basisJahrPlusNumber === 2)
            || (!this.gesuchModelManager.getEinkommensverschlechterungsInfo().ekvFuerBasisJahrPlus2 && this.gesuchModelManager.basisJahrPlusNumber === 1);
    }
}
