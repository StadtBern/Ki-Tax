import {IDirective, IDirectiveFactory, IQService} from 'angular';
import {IStateService} from 'angular-ui-router';
import WizardStepManager from '../../../gesuch/service/wizardStepManager';
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
import GesuchModelManager from '../../../gesuch/service/gesuchModelManager';
import ErrorService from '../../errors/service/ErrorService';
import {TSWizardStepStatus} from '../../../models/enums/TSWizardStepStatus';
import ITranslateService = angular.translate.ITranslateService;
let template = require('./dv-navigation.html');
let style = require('./dv-navigation.less');


/**
 * Diese Direktive wird benutzt, um die Navigation Buttons darzustellen. Folgende Parameter koennen benutzt werden,
 * um die Funktionalitaet zu definieren:
 *
 * -- dvPrevious: function      Wenn true wird der Button "previous" angezeigt (nicht gleichzeitig mit dvCancel benutzen)
 * -- dvNext: function          Wenn true wird der Button "next" angezeigt
 * -- dvNextDisabled: function  Wenn man eine extra Pruefung braucht, um den Button Next zu disablen
 * -- dvSubStep: number         Manche Steps haben sog. SubSteps (z.B. finanzielle Situation). Dieses Parameter wird benutzt,
 *                              um zwischen SubSteps unterscheiden zu koennen
 * -- dvSave: function          Die callback Methode, die man aufrufen muss, wenn der Button geklickt wird. Verwenden nur um die Daten zu speichern
 * -- dvCancel: function        Die callback Methode, um alles zurueckzusetzen (nicht gleichzeitig mit dvPrevious benutzen)
 */
export class DVNavigation implements IDirective {
    restrict = 'E';
    scope = {
        dvPrevious: '&?',
        dvNext: '&?',
        dvCancel: '&?',
        dvNextDisabled: '&?',
        dvSubStep: '<',
        dvSave: '&?',
        dvSavingPossible: '<?',
        dvTranslateNext: '@'
    };
    controller = NavigatorController;
    controllerAs = 'vm';
    bindToController = true;
    template = template;

    static factory(): IDirectiveFactory {
        const directive = () => new DVNavigation();
        directive.$inject = [];
        return directive;
    }
}
/**
 * Direktive  der initial die smart table nach dem aktuell eingeloggtem user filtert
 */
export class NavigatorController {

    dvPrevious: () => any;
    dvNext: () => any;
    dvSave: () => any;
    dvCancel: () => any;
    dvNextDisabled: () => any;
    dvSavingPossible: boolean;
    dvSubStep: number;
    dvTranslateNext: string;
    isRequestInProgress: boolean = false; // this semaphore will prevent a navigation button to be called again until the prozess is not finished

    performSave: boolean;

    static $inject: string[] = ['WizardStepManager', '$state', 'GesuchModelManager', '$translate', 'ErrorService', '$q'];
    /* @ngInject */
    constructor(private wizardStepManager: WizardStepManager, private state: IStateService, private gesuchModelManager: GesuchModelManager,
                private $translate: ITranslateService, private errorService: ErrorService, private $q: IQService) {
    }

    //wird von angular aufgerufen
    $onInit() {
        //initial nach aktuell eingeloggtem filtern
        this.dvSavingPossible = this.dvSavingPossible || false;

    }


    public doesCancelExist(): boolean {
        return this.dvCancel !== undefined && this.dvCancel !== null;
    }

    public doesdvTranslateNextExist(): boolean {
        return this.dvTranslateNext !== undefined && this.dvTranslateNext !== null;
    }

    /**
     * Wenn die function save uebergeben wurde, dann heisst der Button Speichern und weiter. Sonst nur weiter
     * @returns {string}
     */
    public getPreviousButtonName(): string {
        if (this.gesuchModelManager.isGesuchReadonly()) {
            return this.$translate.instant('ZURUECK_ONLY_UPPER');
        } else if (this.dvSave) {
            return this.$translate.instant('ZURUECK_UPPER');
        } else {
            return this.$translate.instant('ZURUECK_ONLY_UPPER');
        }
    }

    /**
     * Wenn die function save uebergeben wurde, dann heisst der Button Speichern und zurueck. Sonst nur zurueck
     * @returns {string}
     */
    public getNextButtonName(): string {
        if (this.dvTranslateNext) {
            return this.$translate.instant(this.dvTranslateNext);
        } else {
            if (this.gesuchModelManager.isGesuchReadonly()) {
                return this.$translate.instant('WEITER_ONLY_UPPER');
            } else if (this.dvSave) {
                return this.$translate.instant('WEITER_UPPER');
            } else {
                return this.$translate.instant('WEITER_ONLY_UPPER');
            }
        }
    }

    /**
     * Diese Methode prueft zuerst ob eine Function in dvSave uebergeben wurde. In diesem Fall wird diese Methode aufgerufen und erst
     * als callback zum naechsten Step gefuehrt. Wenn dvSave keine gueltige Function enthaelt und deshalb keine Promise zurueckgibt,
     * wird dann direkt zum naechsten Step geleitet.
     */
    public nextStep(): void {
        if (!this.isRequestInProgress) { //do nothing if we are already saving
            this.isRequestInProgress = true;
            if (this.isSavingEnabled() && this.dvSave) {
                let returnValue: any = this.dvSave();  //callback ausfuehren, could return promise
                if (returnValue !== undefined) {
                    this.$q.when(returnValue).then(() => {
                        this.navigateToNextStep();
                    }).finally(() => {
                        this.isRequestInProgress = false;
                    });
                } else {
                    this.isRequestInProgress = false;
                }
            } else {
                this.isRequestInProgress = false;
                this.navigateToNextStep();
            }
        } else {
            console.log('doubleclick suppressed'); //for testing
        }
    }

    private isSavingEnabled(): boolean {
        if (this.dvSavingPossible) {
            return true;
        } else {
            return !this.gesuchModelManager.isGesuchReadonly();
        }
    }

    /**
     * Diese Methode prueft zuerst ob eine Function in dvSave uebergeben wurde. In diesem Fall wird diese Methode aufgerufen und erst
     * als callback zum vorherigen Step gefuehrt. Wenn dvSave keine gueltige Function enthaelt und deshalb keine Promise zurueckgibt,
     * wird dann direkt zum vorherigen Step geleitet.
     */
    public previousStep(): void {
        if (!this.isRequestInProgress) { //do nothing if we are already saving
            if (this.isSavingEnabled() && this.dvSave) {
                let returnValue: any = this.dvSave();  //callback ausfuehren, could return promise
                if (returnValue !== undefined) {
                    this.$q.when(returnValue).then(() => {
                        this.navigateToPreviousStep();
                    }).finally(() => {
                        this.isRequestInProgress = false;
                    });
                } else {
                    this.isRequestInProgress = false;
                }
            } else {
                this.isRequestInProgress = false;
                this.navigateToPreviousStep();
            }
        }
    }

    /**
     * Diese Methode ist aehnlich wie previousStep() aber wird verwendet, um die Aenderungen NICHT zu speichern
     */
    public cancel(): void {
        if (this.dvCancel) {
            this.dvCancel();
        }
        this.navigateToPreviousStep();
    }

    /**
     * Berechnet fuer den aktuellen Benutzer und Step, welcher der naechste Step ist und wechselt zu diesem.
     * Bay default wird es zum nae
     */
    private navigateToNextStep() {

        this.errorService.clearAll();

        // Improvement?: All diese Sonderregel koennten in getNextStep() vom wizardStepManager sein, damit die gleiche
        // Funktionalität für isButtonDisable wie für die Navigation existiert.
        if (TSWizardStepName.GESUCHSTELLER === this.wizardStepManager.getCurrentStepName()
            && (this.gesuchModelManager.getGesuchstellerNumber() === 1) && this.gesuchModelManager.isGesuchsteller2Required()) {
            this.navigateToStep(TSWizardStepName.GESUCHSTELLER, '2');

        } else if (TSWizardStepName.KINDER === this.wizardStepManager.getCurrentStepName() && this.dvSubStep === 2) {
            this.navigateToStep(TSWizardStepName.KINDER);

        } else if (TSWizardStepName.BETREUUNG === this.wizardStepManager.getCurrentStepName() && this.dvSubStep === 2) {
            // Diese Logik ist ziemlich kompliziert. Deswegen bleibt sie noch in betreuungView.ts -> Hier wird dann nichts gemacht

        } else if (TSWizardStepName.ERWERBSPENSUM === this.wizardStepManager.getCurrentStepName()) {
            if (this.dvSubStep === 1) {
                this.errorService.clearAll();
                this.navigateToStep(this.wizardStepManager.getNextStep(this.gesuchModelManager.getGesuch()));

            } else if (this.dvSubStep === 2) {
                this.navigateToStep(TSWizardStepName.ERWERBSPENSUM);
            }

        } else if (TSWizardStepName.FINANZIELLE_SITUATION === this.wizardStepManager.getCurrentStepName()) {
            if (this.dvSubStep === 1) {
                if ((this.gesuchModelManager.getGesuchstellerNumber() === 1) && this.gesuchModelManager.isGesuchsteller2Required()) {
                    this.navigateToStepFinanzielleSituation('2');
                } else {
                    this.navigateToFinanziellSituationResultate();
                }

            } else if (this.dvSubStep === 2) {
                this.navigateToStepFinanzielleSituation('1');

            } else if (this.dvSubStep === 3) {
                this.navigateToStep(this.wizardStepManager.getNextStep(this.gesuchModelManager.getGesuch()));
            }

        } else if (TSWizardStepName.EINKOMMENSVERSCHLECHTERUNG === this.wizardStepManager.getCurrentStepName()) {
            if (this.dvSubStep === 1) {
                if (this.gesuchModelManager.getGesuch().extractEinkommensverschlechterungInfo() &&
                    this.gesuchModelManager.getGesuch().extractEinkommensverschlechterungInfo().einkommensverschlechterung) { // was muss hier sein?
                    if (this.gesuchModelManager.isGesuchsteller2Required()) {
                        if (this.gesuchModelManager.getGesuch().extractEinkommensverschlechterungInfo().ekvFuerBasisJahrPlus1) {
                            this.navigateToStepEinkommensverschlechterungSteuern();
                        } else {
                            this.navigateToStepEinkommensverschlechterung('1', '2');
                        }
                    } else {
                        if (this.gesuchModelManager.getGesuch().extractEinkommensverschlechterungInfo().ekvFuerBasisJahrPlus1) {
                            this.navigateToStepEinkommensverschlechterung('1', undefined);
                        } else {
                            this.navigateToStepEinkommensverschlechterung('1', '2');
                        }
                    }
                } else {
                    this.wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.OK).then(() => {
                        this.navigateToStep(this.wizardStepManager.getNextStep(this.gesuchModelManager.getGesuch()));
                    });
                }

            } else if (this.dvSubStep === 2) {
                if (this.gesuchModelManager.isRequiredEKV_GS_BJ(1, 1)) { // gehe ekv 1/2
                    this.navigateToStepEinkommensverschlechterung('1', '1');
                } else if (this.gesuchModelManager.isRequiredEKV_GS_BJ(1, 2)) { // gehe ekv 1/2
                    this.navigateToStepEinkommensverschlechterung('1', '2');
                }
            } else if (this.dvSubStep === 3) {
                this.navigateNextEVSubStep3();

            } else if (this.dvSubStep === 4) {
                this.navigateNextEVSubStep4();
            }

        } else { //by default navigieren wir zum naechsten erlaubten Step
            this.navigateToStep(this.wizardStepManager.getNextStep(this.gesuchModelManager.getGesuch()));
        }
    }

    /**
     * Berechnet fuer den aktuellen Benutzer und Step, welcher der previous Step ist und wechselt zu diesem.
     * wenn es kein Sonderfall ist wird der letzte else case ausgefuehrt
     */
    private navigateToPreviousStep() {

        this.errorService.clearAll();
        if (TSWizardStepName.GESUCH_ERSTELLEN === this.wizardStepManager.getCurrentStepName()) {
            this.navigateToStep(TSWizardStepName.GESUCH_ERSTELLEN);

        } else if (TSWizardStepName.GESUCHSTELLER === this.wizardStepManager.getCurrentStepName()
            && this.gesuchModelManager.getGesuchstellerNumber() === 2) {

            this.navigateToStep(TSWizardStepName.GESUCHSTELLER, '1');

        } else if (TSWizardStepName.KINDER === this.wizardStepManager.getCurrentStepName() && this.dvSubStep === 2) {
            this.navigateToStep(TSWizardStepName.KINDER);

        } else if (TSWizardStepName.BETREUUNG === this.wizardStepManager.getCurrentStepName() && this.dvSubStep === 2) {
            this.navigateToStep(TSWizardStepName.BETREUUNG);

        } else if (TSWizardStepName.ERWERBSPENSUM === this.wizardStepManager.getCurrentStepName() && this.dvSubStep === 2) {
            this.navigateToStep(TSWizardStepName.ERWERBSPENSUM);

        } else if (TSWizardStepName.FINANZIELLE_SITUATION === this.wizardStepManager.getCurrentStepName()) {
            if (this.dvSubStep === 1) {
                if ((this.gesuchModelManager.getGesuchstellerNumber() === 2)) {
                    this.navigateToStepFinanzielleSituation('1');

                } else if (this.gesuchModelManager.getGesuchstellerNumber() === 1 && this.gesuchModelManager.isGesuchsteller2Required()) {
                    this.navigateToStep(TSWizardStepName.FINANZIELLE_SITUATION);
                } else {
                    this.navigateToStep(this.wizardStepManager.getPreviousStep(this.gesuchModelManager.getGesuch()));
                }
            } else if (this.dvSubStep === 2) {
                this.navigateToStep(this.wizardStepManager.getPreviousStep(this.gesuchModelManager.getGesuch()));
            } else if (this.dvSubStep === 3) {
                this.navigateToStepFinanzielleSituation(this.gesuchModelManager.getGesuchstellerNumber() === 2 ? '2' : '1');
            }

        } else if (TSWizardStepName.EINKOMMENSVERSCHLECHTERUNG === this.wizardStepManager.getCurrentStepName()) {
            if (this.dvSubStep === 1) {
                this.navigateToStep(this.wizardStepManager.getPreviousStep(this.gesuchModelManager.getGesuch()));
            } else if (this.dvSubStep === 2) {
                this.navigateToStep(TSWizardStepName.EINKOMMENSVERSCHLECHTERUNG);
            } else if (this.dvSubStep === 3) {
                this.navigatePreviousEVSubStep3();
            } else if (this.dvSubStep === 4) {
                this.navigatePreviousEVSubStep4();
            }

        } else if (TSWizardStepName.VERFUEGEN === this.wizardStepManager.getCurrentStepName() && this.dvSubStep === 2) {
            this.navigateToStep(TSWizardStepName.VERFUEGEN);

        } else {
            this.navigateToStep(this.wizardStepManager.getPreviousStep(this.gesuchModelManager.getGesuch()));
        }
    }

    /**
     * Diese Methode navigierte zum ersten substep jedes Steps. Fuer die navigation innerhalb eines Steps muss
     * man eine extra Methode machen
     * @param stepName
     * @param gsNumber
     */
    private navigateToStep(stepName: TSWizardStepName, gsNumber?: string) {
        let gesuchId = this.getGesuchId();

        if (stepName === TSWizardStepName.GESUCH_ERSTELLEN) {
            this.state.go('gesuch.fallcreation', {
                createNew: 'false',
                createMutation: 'false',
                eingangsart: this.gesuchModelManager.getGesuch().eingangsart,
                gesuchId: gesuchId,
                gesuchsperiodeId: this.gesuchModelManager.getGesuch().gesuchsperiode.id,
                fallId: this.gesuchModelManager.getGesuch().fall.id
            });

        } else if (stepName === TSWizardStepName.FAMILIENSITUATION) {
            this.state.go('gesuch.familiensituation', {
                gesuchId: gesuchId
            });

        } else if (stepName === TSWizardStepName.GESUCHSTELLER) {
            this.state.go('gesuch.stammdaten', {
                gesuchstellerNumber: gsNumber ? gsNumber : '1',
                gesuchId: gesuchId
            });

        } else if (stepName === TSWizardStepName.UMZUG) {
            this.state.go('gesuch.umzug', {
                gesuchId: gesuchId
            });

        } else if (stepName === TSWizardStepName.KINDER) {
            this.state.go('gesuch.kinder', {
                gesuchId: gesuchId
            });

        } else if (stepName === TSWizardStepName.BETREUUNG) {
            this.state.go('gesuch.betreuungen', {
                gesuchId: gesuchId
            });

        } else if (stepName === TSWizardStepName.ABWESENHEIT) {
            this.state.go('gesuch.abwesenheit', {
                gesuchId: gesuchId
            });

        } else if (stepName === TSWizardStepName.ERWERBSPENSUM) {
            this.state.go('gesuch.erwerbsPensen', {
                gesuchId: gesuchId
            });

        } else if (stepName === TSWizardStepName.FINANZIELLE_SITUATION) {
            if (this.gesuchModelManager.isGesuchsteller2Required()) {
                this.state.go('gesuch.finanzielleSituationStart', {
                    gesuchId: this.getGesuchId()
                });
            } else {
                this.state.go('gesuch.finanzielleSituation', {
                    gesuchstellerNumber: '1',
                    gesuchId: this.getGesuchId()
                });
            }

        } else if (stepName === TSWizardStepName.EINKOMMENSVERSCHLECHTERUNG) {
            this.state.go('gesuch.einkommensverschlechterungInfo', {
                gesuchId: this.getGesuchId()
            });

        } else if (stepName === TSWizardStepName.DOKUMENTE) {
            this.state.go('gesuch.dokumente', {
                gesuchId: gesuchId
            });

        } else if (stepName === TSWizardStepName.FREIGABE) {
            this.state.go('gesuch.freigabe', {
                gesuchId: gesuchId
            });

        } else if (stepName === TSWizardStepName.VERFUEGEN) {
            this.state.go('gesuch.verfuegen', {
                gesuchId: gesuchId
            });

        }
    }

    private navigateToStepEinkommensverschlechterung(gsNumber: string, basisjahrPlus: string) {
        this.state.go('gesuch.einkommensverschlechterung', {
            gesuchstellerNumber: gsNumber ? gsNumber : '1',
            basisjahrPlus: basisjahrPlus ? basisjahrPlus : '1',
            gesuchId: this.getGesuchId()
        });
    }

    private navigateToFinanziellSituationResultate() {
        this.state.go('gesuch.finanzielleSituationResultate', {
            gesuchId: this.getGesuchId()
        });
    }

    private navigateToStepEinkommensverschlechterungSteuern() {
        this.state.go('gesuch.einkommensverschlechterungSteuern', {
            gesuchId: this.getGesuchId()
        });
    }

    private navigateToStepEinkommensverschlechterungResultate(basisjahrPlus: string) {
        this.state.go('gesuch.einkommensverschlechterungResultate', {
            basisjahrPlus: basisjahrPlus ? basisjahrPlus : '1',
            gesuchId: this.getGesuchId()
        });
    }

    private navigateToStepFinanzielleSituation(gsNumber: string) {
        this.state.go('gesuch.finanzielleSituation', {
            gesuchstellerNumber: gsNumber ? gsNumber : '1',
            gesuchId: this.getGesuchId()
        });
    }

    private getGesuchId(): string {
        if (this.gesuchModelManager && this.gesuchModelManager.getGesuch()) {
            return this.gesuchModelManager.getGesuch().id;
        }
        return '';
    }

    /**
     * Checks whether the button should be disable for the current conditions. By default (auch fuer Mutaionen) enabled
     * @returns {boolean}
     */
    public isNextButtonDisabled(): boolean {
        //if step is disabled in manager we can stop here
        if (!this.wizardStepManager.isNextStepEnabled(this.gesuchModelManager.getGesuch())) {
            return true;
        }
        // otherwise check specifics
        if (this.gesuchModelManager.isGesuch()) {
            if (TSWizardStepName.KINDER === this.wizardStepManager.getCurrentStepName() && this.dvSubStep === 1) {
                return !this.gesuchModelManager.isThereAnyKindWithBetreuungsbedarf()
                    && !this.wizardStepManager.isNextStepBesucht(this.gesuchModelManager.getGesuch());
            }
            if (TSWizardStepName.BETREUUNG === this.wizardStepManager.getCurrentStepName() && this.dvSubStep === 1) {
                return !this.gesuchModelManager.getGesuch().isThereAnyBetreuung()
                    && !this.wizardStepManager.isNextStepBesucht(this.gesuchModelManager.getGesuch());
            }
            if (TSWizardStepName.ERWERBSPENSUM === this.wizardStepManager.getCurrentStepName() && this.dvSubStep === 1) {
                return this.dvNextDisabled() && !this.wizardStepManager.isNextStepBesucht(this.gesuchModelManager.getGesuch());
            }
        }

        return false;
    }

    public getTooltip(): string {
        if (this.isNextButtonDisabled()) {
            if (TSWizardStepName.KINDER === this.wizardStepManager.getCurrentStepName() && this.dvSubStep === 1) {
                return this.$translate.instant('KINDER_TOOLTIP_REQUIRED');

            } else if (TSWizardStepName.BETREUUNG === this.wizardStepManager.getCurrentStepName() && this.dvSubStep === 1) {
                return this.$translate.instant('BETREUUNG_TOOLTIP_REQUIRED');

            } else if (TSWizardStepName.ERWERBSPENSUM === this.wizardStepManager.getCurrentStepName() && this.dvSubStep === 1) {
                return this.$translate.instant('ERWERBSPENSUM_TOOLTIP_REQUIRED');
            }
        }
        return undefined;
    }

    private navigateNextEVSubStep3(): void {
        if ((this.gesuchModelManager.getBasisJahrPlusNumber() === 1)) {
            if (this.gesuchModelManager.getGesuchstellerNumber() === 1) { // ist Zustand 1/1
                if (this.gesuchModelManager.isRequiredEKV_GS_BJ(2, 1)) { // gehe ekv 2/1
                    this.navigateToStepEinkommensverschlechterung('2', '1');
                } else if (this.gesuchModelManager.isRequiredEKV_GS_BJ(1, 2)) { // gehe ekv 1/2
                    this.navigateToStepEinkommensverschlechterung('1', '2');
                } else {
                    this.navigateToStepEinkommensverschlechterungResultate('1'); // gehe Resultate Bj 1
                }
            } else { // ist Zustand 2/1
                if (this.gesuchModelManager.isRequiredEKV_GS_BJ(1, 2)) {
                    this.navigateToStepEinkommensverschlechterung('1', '2'); // gehe ekv 1/2
                } else if (this.gesuchModelManager.isRequiredEKV_GS_BJ(2, 2)) {
                    this.navigateToStepEinkommensverschlechterung('2', '2'); // gehe ekv 2/2
                } else {
                    this.navigateToStepEinkommensverschlechterungResultate('1'); // gehe Resultate Bj 1
                }
            }
        } else {
            if (this.gesuchModelManager.getGesuchstellerNumber() === 1) { // ist Zustand 1/2
                if (this.gesuchModelManager.isRequiredEKV_GS_BJ(2, 2)) { // gehe ekv 2/2
                    this.navigateToStepEinkommensverschlechterung('2', '2');
                } else {
                    if (this.gesuchModelManager.getEkvFuerBasisJahrPlus(1)) {
                        this.navigateToStepEinkommensverschlechterungResultate('1'); // gehe Resultate Bj 1
                    } else {
                        this.navigateToStepEinkommensverschlechterungResultate('2'); // gehe Resultate Bj 2
                    }
                }
            } else { // ist Zustand 2/2
                if (this.gesuchModelManager.getEkvFuerBasisJahrPlus(1)) {
                    this.navigateToStepEinkommensverschlechterungResultate('1'); // gehe Resultate Bj 1
                } else {
                    this.navigateToStepEinkommensverschlechterungResultate('2'); // gehe Resultate Bj 2
                }
            }
        }
    }


    private navigatePreviousEVSubStep3(): void {
        if ((this.gesuchModelManager.getBasisJahrPlusNumber() === 1)) {
            if (this.gesuchModelManager.getGesuchstellerNumber() === 1) { // ist Zustand 1/1
                if (this.gesuchModelManager.isGesuchsteller2Required() &&
                    this.gesuchModelManager.getGesuch().extractEinkommensverschlechterungInfo().ekvFuerBasisJahrPlus1) {
                    this.navigateToStepEinkommensverschlechterungSteuern();
                } else {
                    this.navigateToStep(TSWizardStepName.EINKOMMENSVERSCHLECHTERUNG);
                }
            } else { // ist Zustand 2/1
                if (this.gesuchModelManager.isRequiredEKV_GS_BJ(1, 1)) {
                    this.navigateToStepEinkommensverschlechterung('1', '1'); // gehe ekv 1/1
                } else if (this.gesuchModelManager.isGesuchsteller2Required() &&
                    this.gesuchModelManager.getGesuch().extractEinkommensverschlechterungInfo().ekvFuerBasisJahrPlus1) {
                    this.navigateToStepEinkommensverschlechterungSteuern();
                } else {
                    this.navigateToStep(TSWizardStepName.EINKOMMENSVERSCHLECHTERUNG);
                }
            }
        } else {
            if (this.gesuchModelManager.getGesuchstellerNumber() === 1) { // ist Zustand 1/2
                if (this.gesuchModelManager.isRequiredEKV_GS_BJ(2, 1)) { // gehe ekv 2/2
                    this.navigateToStepEinkommensverschlechterung('2', '1');
                } else if (this.gesuchModelManager.isRequiredEKV_GS_BJ(1, 1)) {
                    this.navigateToStepEinkommensverschlechterung('1', '1'); // gehe ekv 1/1
                } else if (this.gesuchModelManager.isGesuchsteller2Required() &&
                    this.gesuchModelManager.getGesuch().extractEinkommensverschlechterungInfo().ekvFuerBasisJahrPlus1) {
                    this.navigateToStepEinkommensverschlechterungSteuern();
                } else {
                    this.navigateToStep(TSWizardStepName.EINKOMMENSVERSCHLECHTERUNG);
                }
            } else { // ist Zustand 2/2
                if (this.gesuchModelManager.isRequiredEKV_GS_BJ(1, 2)) { // gehe ekv 1/2
                    this.navigateToStepEinkommensverschlechterung('1', '2');
                } else if (this.gesuchModelManager.isRequiredEKV_GS_BJ(2, 1)) { // gehe ekv 2/2
                    this.navigateToStepEinkommensverschlechterung('2', '1');
                } else if (this.gesuchModelManager.isRequiredEKV_GS_BJ(1, 1)) {
                    this.navigateToStepEinkommensverschlechterung('1', '1'); // gehe ekv 1/1
                } else if (this.gesuchModelManager.isGesuchsteller2Required() &&
                    this.gesuchModelManager.getGesuch().extractEinkommensverschlechterungInfo().ekvFuerBasisJahrPlus1) {
                    this.navigateToStepEinkommensverschlechterungSteuern();
                } else {
                    this.navigateToStep(TSWizardStepName.EINKOMMENSVERSCHLECHTERUNG);
                }
            }
        }
    };

    private navigatePreviousEVSubStep4(): void {
        if (this.gesuchModelManager.getBasisJahrPlusNumber() === 2) {
            // baisjahrPlus2
            if (this.gesuchModelManager.getEkvFuerBasisJahrPlus(1)) {
                this.navigateToStepEinkommensverschlechterungResultate('1'); // gehe Resultate Bj 1
            } else if (this.gesuchModelManager.isRequiredEKV_GS_BJ(2, 2)) { // gehe ekv 2/2
                this.navigateToStepEinkommensverschlechterung('2', '2');
            } else if (this.gesuchModelManager.isRequiredEKV_GS_BJ(1, 2)) { // gehe ekv 1/2
                this.navigateToStepEinkommensverschlechterung('1', '2');
            } else if (this.gesuchModelManager.isRequiredEKV_GS_BJ(2, 1)) { // gehe ekv 2/1
                this.navigateToStepEinkommensverschlechterung('2', '1');
            } else {
                this.navigateToStepEinkommensverschlechterung('1', '1'); // gehe ekv 1/1
            }
        } else {
            // baisjahrPlus1
            if (this.gesuchModelManager.isRequiredEKV_GS_BJ(2, 2)) { // gehe ekv 2/2
                this.navigateToStepEinkommensverschlechterung('2', '2');
            } else if (this.gesuchModelManager.isRequiredEKV_GS_BJ(1, 2)) { // gehe ekv 1/2
                this.navigateToStepEinkommensverschlechterung('1', '2');
            } else if (this.gesuchModelManager.isRequiredEKV_GS_BJ(2, 1)) { // gehe ekv 2/1
                this.navigateToStepEinkommensverschlechterung('2', '1');
            } else {
                this.navigateToStepEinkommensverschlechterung('1', '1'); // gehe ekv 1/1
            }
        }
    };

    private navigateNextEVSubStep4(): void {
        if (this.gesuchModelManager.getBasisJahrPlusNumber() === 1
            && this.gesuchModelManager.getGesuch().extractEinkommensverschlechterungInfo().ekvFuerBasisJahrPlus2 === true) {
            this.navigateToStepEinkommensverschlechterungResultate('2');
        } else {
            this.wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.OK).then(() => {
                this.navigateToStep(this.wizardStepManager.getNextStep(this.gesuchModelManager.getGesuch()));
            });
        }
    };

}
