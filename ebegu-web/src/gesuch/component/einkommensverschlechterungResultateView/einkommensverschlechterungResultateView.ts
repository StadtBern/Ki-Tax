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
export class EinkommensverschlechterungResultateViewController extends AbstractGesuchViewController {

    gesuchsteller1EkvCont: TSEinkommensverschlechterungContainer;
    gesuchsteller2EkvCont: TSEinkommensverschlechterungContainer;
    parsedBasisJahrPlusNum: number;
    resultatVorjahr: TSFinanzielleSituationResultateDTO;
    resultatProzent: string;

    static $inject: string[] = ['$stateParams', 'GesuchModelManager', 'BerechnungsManager', 'CONSTANTS', 'ErrorService',
        'WizardStepManager', '$q'];
    /* @ngInject */
    constructor($stateParams: IEinkommensverschlechterungResultateStateParams, gesuchModelManager: GesuchModelManager,
                berechnungsManager: BerechnungsManager, private CONSTANTS: any, private errorService: ErrorService,
                wizardStepManager: WizardStepManager, private $q: IQService) {
        super(gesuchModelManager, berechnungsManager, wizardStepManager);
        this.parsedBasisJahrPlusNum = parseInt($stateParams.basisjahrPlus, 10);
        this.gesuchModelManager.setBasisJahrPlusNumber(this.parsedBasisJahrPlusNum);
        this.initViewModel();
        this.calculate();
        this.resultatVorjahr = null;
        this.calculateResultateVorjahr();
    }

    private initViewModel() {
        this.wizardStepManager.setCurrentStep(TSWizardStepName.EINKOMMENSVERSCHLECHTERUNG);
    }

    showGemeinsam(): boolean {
        return this.gesuchModelManager.isGesuchsteller2Required() &&
            this.gesuchModelManager.getGemeinsameSteuererklaerungToWorkWith() === true;
    }

    showGS1(): boolean {
        return !this.showGemeinsam();
    }

    showGS2(): boolean {
        return this.gesuchModelManager.isGesuchsteller2Required() &&
            this.gesuchModelManager.getGemeinsameSteuererklaerungToWorkWith() === false;
    }

    showResult(): boolean {
        if (this.parsedBasisJahrPlusNum === 1) {
            let ekvFuerBasisJahrPlus1 = this.gesuchModelManager.getGesuch().einkommensverschlechterungInfo.ekvFuerBasisJahrPlus1
                && this.gesuchModelManager.getGesuch().einkommensverschlechterungInfo.ekvFuerBasisJahrPlus1 === true;
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
        if (this.gesuchModelManager.getGesuch() && this.parsedBasisJahrPlusNum) {
            this.berechnungsManager
                .calculateEinkommensverschlechterung(this.gesuchModelManager.getGesuch(), this.parsedBasisJahrPlusNum)
                .then(() => {
                    this.resultatProzent = this.calculateVeraenderung();
                });
        } else {
            console.log('No gesuch and Basisjahr to calculate');
        }
    }

    public getEinkommensverschlechterungContainerGS1(): TSEinkommensverschlechterungContainer {
        if (!this.gesuchsteller1EkvCont) {
            if (this.gesuchModelManager.getGesuch().gesuchsteller1) {
                this.gesuchsteller1EkvCont = this.gesuchModelManager.getGesuch().gesuchsteller1.einkommensverschlechterungContainer;
            } else {
                this.gesuchsteller1EkvCont = new TSEinkommensverschlechterungContainer();
            }
        }
        return this.gesuchsteller1EkvCont;
    }

    public getEinkommensverschlechterungGS1_GS(): TSEinkommensverschlechterung {
        if (this.parsedBasisJahrPlusNum === 2) {
            return this.getEinkommensverschlechterungContainerGS1().ekvGSBasisJahrPlus2;
        } else {
            return this.getEinkommensverschlechterungContainerGS1().ekvGSBasisJahrPlus1;
        }
    }

    public getEinkommensverschlechterungGS1_JA(): TSEinkommensverschlechterung {
        if (this.parsedBasisJahrPlusNum === 2) {
            return this.getEinkommensverschlechterungContainerGS1().ekvJABasisJahrPlus2;
        } else {
            return this.getEinkommensverschlechterungContainerGS1().ekvJABasisJahrPlus1;
        }
    }

    public getEinkommensverschlechterungContainerGS2(): TSEinkommensverschlechterungContainer {
        if (!this.gesuchsteller2EkvCont) {
            if (this.gesuchModelManager.getGesuch().gesuchsteller2) {
                this.gesuchsteller2EkvCont = this.gesuchModelManager.getGesuch().gesuchsteller2.einkommensverschlechterungContainer;
            } else {
                this.gesuchsteller2EkvCont = new TSEinkommensverschlechterungContainer();
            }
        }
        return this.gesuchsteller2EkvCont;
    }

    public getEinkommensverschlechterungGS2_GS(): TSEinkommensverschlechterung {
        if (this.parsedBasisJahrPlusNum === 2) {
            return this.getEinkommensverschlechterungContainerGS2().ekvGSBasisJahrPlus2;
        } else {
            return this.getEinkommensverschlechterungContainerGS2().ekvGSBasisJahrPlus1;
        }
    }

    public getEinkommensverschlechterungGS2_JA(): TSEinkommensverschlechterung {
        if (this.parsedBasisJahrPlusNum === 2) {
            return this.getEinkommensverschlechterungContainerGS2().ekvJABasisJahrPlus2;
        } else {
            return this.getEinkommensverschlechterungContainerGS2().ekvJABasisJahrPlus1;
        }
    }

    public getResultate(): TSFinanzielleSituationResultateDTO {
        if (this.parsedBasisJahrPlusNum === 2) {
            return this.berechnungsManager.einkommensverschlechterungResultateBjP2;
        } else {
            return this.berechnungsManager.einkommensverschlechterungResultateBjP1;
        }
    }

    public calculateResultateVorjahr() {

        if (this.parsedBasisJahrPlusNum === 2) {
            this.berechnungsManager.calculateEinkommensverschlechterung(this.gesuchModelManager.getGesuch(), 1).then((resultatVorjahr) => {
                this.resultatVorjahr = resultatVorjahr;
                this.resultatProzent = this.calculateVeraenderung();
            });
        } else {
            this.berechnungsManager.calculateFinanzielleSituation(this.gesuchModelManager.getGesuch()).then((resultatVorjahr) => {
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
