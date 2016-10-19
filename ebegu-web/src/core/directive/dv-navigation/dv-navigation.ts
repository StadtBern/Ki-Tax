import {IDirective, IDirectiveFactory} from 'angular';
import {IStateService} from 'angular-ui-router';
import WizardStepManager from '../../../gesuch/service/wizardStepManager';
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
import GesuchModelManager from '../../../gesuch/service/gesuchModelManager';
import ErrorService from '../../errors/service/ErrorService';
import {TSWizardStepStatus} from '../../../models/enums/TSWizardStepStatus';
import {TSAntragTyp} from '../../../models/enums/TSAntragTyp';
import ITranslateService = angular.translate.ITranslateService;
let template = require('./dv-navigation.html');

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
        dvSave: '&?'
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
    dvSubStep: number;

    static $inject: string[] = ['WizardStepManager', '$state', 'GesuchModelManager', '$translate', 'ErrorService'];
    /* @ngInject */
    constructor(private wizardStepManager: WizardStepManager, private state: IStateService, private gesuchModelManager: GesuchModelManager,
                private $translate: ITranslateService, private errorService: ErrorService) {
    }

    public doesCancelExist(): boolean {
        return this.dvCancel !== undefined && this.dvCancel !== null;
    }

    /**
     * Wenn die function save uebergeben wurde, dann heisst der Button Speichern und weiter. Sonst nur weiter
     * @returns {string}
     */
    public getPreviousButtonName(): string {
        if (this.gesuchModelManager.isGesuchStatusVerfuegenVerfuegt()) {
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
        if (this.gesuchModelManager.isGesuchStatusVerfuegenVerfuegt()) {
            return this.$translate.instant('WEITER_ONLY_UPPER');
        } else if (this.dvSave) {
            return this.$translate.instant('WEITER_UPPER');
        } else {
            return this.$translate.instant('WEITER_ONLY_UPPER');
        }
    }

    /**
     * Diese Methode prueft zuerst ob eine Function in dvSave uebergeben wurde. In diesem Fall wird diese Methode aufgerufen und erst
     * als callback zum naechsten Step gefuehrt. Wenn dvSave keine gueltige Function enthaelt und deshalb keine Promise zurueckgibt,
     * wird dann direkt zum naechsten Step geleitet.
     */
    public nextStep(): void {
        if (!this.gesuchModelManager.isGesuchStatusVerfuegenVerfuegt() && this.dvSave) {
            this.dvSave().then(() => {
                this.navigateToNextStep();
            });
        } else {
            this.navigateToNextStep();
        }
    }

    /**
     * Diese Methode prueft zuerst ob eine Function in dvSave uebergeben wurde. In diesem Fall wird diese Methode aufgerufen und erst
     * als callback zum vorherigen Step gefuehrt. Wenn dvSave keine gueltige Function enthaelt und deshalb keine Promise zurueckgibt,
     * wird dann direkt zum vorherigen Step geleitet.
     */
    public previousStep(): void {
        if (!this.gesuchModelManager.isGesuchStatusVerfuegenVerfuegt() && this.dvSave) {
            this.dvSave().then(() => {
                this.navigateToPreviousStep();
            });
        } else {
            this.navigateToPreviousStep();
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
     */
    private navigateToNextStep() {

        this.errorService.clearAll();
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
                // if (this.wizardStepManager.isNextStepBesucht(this.gesuchModelManager.getGesuch().typ)
                //     && !this.wizardStepManager.isNextStepEnabled(this.gesuchModelManager.getGesuch().typ)) {
                //     // wenn finanzielle situation besucht aber nicht enabled ist, dann zu Dokumenten
                //     this.navigateToStep(TSWizardStepName.DOKUMENTE);
                // } else
                if (this.gesuchModelManager.isGesuchsteller2Required()) {
                    this.navigateToStep(TSWizardStepName.FINANZIELLE_SITUATION);
                } else {
                    this.navigateToStep(this.wizardStepManager.getNextStep(this.gesuchModelManager.getGesuch().typ));
                }

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
                this.navigateToStep(TSWizardStepName.EINKOMMENSVERSCHLECHTERUNG);
            }

        } else if (TSWizardStepName.EINKOMMENSVERSCHLECHTERUNG === this.wizardStepManager.getCurrentStepName()) {
            if (this.dvSubStep === 1) {
                if (this.gesuchModelManager.getEinkommensverschlechterungsInfo().einkommensverschlechterung) { // was muss hier sein?
                    if (this.gesuchModelManager.isGesuchsteller2Required()) {
                        this.navigateToStepEinkommensverschlechterungSteuern();
                    } else {
                        this.navigateToStepEinkommensverschlechterung('1', undefined);
                    }
                } else {
                    this.navigateToStep(TSWizardStepName.DOKUMENTE);
                }

            } else if (this.dvSubStep === 2) {
                this.navigateNextEVSubStep2();

            } else if (this.dvSubStep === 3) {
                this.navigateToStepEinkommensverschlechterung('1', '1');

            } else if (this.dvSubStep === 4) {
                this.navigateNextEVSubStep4();
            }

        } else { //by default navigieren wir zum naechsten erlaubten Step
            this.navigateToStep(this.wizardStepManager.getNextStep(this.gesuchModelManager.getGesuch().typ));
        }
    }

    /**
     * Berechnet fuer den aktuellen Benutzer und Step, welcher der previous Step ist und wechselt zu diesem.
     */
    private navigateToPreviousStep() {

        this.errorService.clearAll();
        if (TSWizardStepName.GESUCH_ERSTELLEN === this.wizardStepManager.getCurrentStepName()) {
            this.navigateToStep(TSWizardStepName.GESUCH_ERSTELLEN);

        } else if (TSWizardStepName.GESUCHSTELLER === this.wizardStepManager.getCurrentStepName()
            && this.gesuchModelManager.getGesuchstellerNumber() === 2) {

            this.navigateToStep(TSWizardStepName.GESUCHSTELLER, '1');

        } else if (TSWizardStepName.KINDER === this.wizardStepManager.getCurrentStepName() && this.dvSubStep === 1) {
            this.navigateToStep(TSWizardStepName.GESUCHSTELLER, this.gesuchModelManager.getGesuchstellerNumber().toLocaleString());

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
                    this.navigateToStep(this.wizardStepManager.getPreviousStep(this.gesuchModelManager.getGesuch().typ));
                }
            } else if (this.dvSubStep === 3) {
                this.navigateToStepFinanzielleSituation(this.gesuchModelManager.getGesuchstellerNumber() === 2 ? '2' : '1');
            }

        } else if (TSWizardStepName.EINKOMMENSVERSCHLECHTERUNG === this.wizardStepManager.getCurrentStepName()) {
            if (this.dvSubStep === 1) {
                this.navigateToStep(this.wizardStepManager.getPreviousStep(this.gesuchModelManager.getGesuch().typ));

            } else if (this.dvSubStep === 2) {
                this.navigatePreviousEVSubStep2();

            } else if (this.dvSubStep === 3) {
                this.navigateToStep(TSWizardStepName.EINKOMMENSVERSCHLECHTERUNG);
            } else if (this.dvSubStep === 4) {
                this.navigatePreviousEVSubStep4();
            }

        } else if (TSWizardStepName.VERFUEGEN === this.wizardStepManager.getCurrentStepName() && this.dvSubStep === 2) {
            this.navigateToStep(TSWizardStepName.VERFUEGEN);

        } else {
            this.navigateToStep(this.wizardStepManager.getPreviousStep(this.gesuchModelManager.getGesuch().typ));
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
                gesuchId: gesuchId
            });

        } else if (stepName === TSWizardStepName.FAMILIENSITUATION) {
            this.state.go('gesuch.familiensituation', {
                gesuchId: gesuchId
            });

        } else if (stepName === TSWizardStepName.GESUCHSTELLER) {
            this.state.go('gesuch.stammdaten', {
                gesuchstellerNumber: gsNumber,
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

        } else if (stepName === TSWizardStepName.ERWERBSPENSUM) {
            this.state.go('gesuch.erwerbsPensen', {
                gesuchId: gesuchId
            });

        } else if (stepName === TSWizardStepName.FINANZIELLE_SITUATION) {
            this.state.go('gesuch.finanzielleSituationStart', {
                gesuchId: this.getGesuchId()
            });

        } else if (stepName === TSWizardStepName.EINKOMMENSVERSCHLECHTERUNG) {
            this.state.go('gesuch.einkommensverschlechterungInfo', {
                gesuchId: this.getGesuchId()
            });

        } else if (stepName === TSWizardStepName.DOKUMENTE) {
            this.wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.OK).then(() => {
                this.state.go('gesuch.dokumente', {
                    gesuchId: gesuchId
                });
            });

        } else if (stepName === TSWizardStepName.VERFUEGEN) {
            this.state.go('gesuch.verfuegen', {
                gesuchId: gesuchId
            });

        }
    }

    private navigateToStepEinkommensverschlechterung(gsNumber: string, basisjahrPlus: string) {
        this.state.go('gesuch.einkommensverschlechterung', {
            gesuchstellerNumber: gsNumber,
            basisjahrPlus: basisjahrPlus,
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
            basisjahrPlus: basisjahrPlus,
            gesuchId: this.getGesuchId()
        });
    }

    private navigateToStepFinanzielleSituation(gsNumber: string) {
        this.state.go('gesuch.finanzielleSituation', {
            gesuchstellerNumber: gsNumber,
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
    public isNextDisabled(): boolean {
        if (this.gesuchModelManager.getGesuch().typ === TSAntragTyp.GESUCH) {
            if (TSWizardStepName.KINDER === this.wizardStepManager.getCurrentStepName() && this.dvSubStep === 1) {
                return !this.gesuchModelManager.isThereAnyKindWithBetreuungsbedarf()
                    && !this.wizardStepManager.isNextStepBesucht(this.gesuchModelManager.getGesuch().typ);
            }
            if (TSWizardStepName.BETREUUNG === this.wizardStepManager.getCurrentStepName() && this.dvSubStep === 1) {
                return !this.gesuchModelManager.isThereAnyBetreuung()
                    && !this.wizardStepManager.isNextStepBesucht(this.gesuchModelManager.getGesuch().typ);
            }
            if (TSWizardStepName.ERWERBSPENSUM === this.wizardStepManager.getCurrentStepName() && this.dvSubStep === 1) {
                return this.dvNextDisabled() && !this.wizardStepManager.isNextStepBesucht(this.gesuchModelManager.getGesuch().typ);
            }
        }
        return false;
    }

    public getTooltip(): string {
        if (this.isNextDisabled()) {
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

    private navigateNextEVSubStep2(): void {
        if ((this.gesuchModelManager.getBasisJahrPlusNumber() === 1)) {
            if (this.gesuchModelManager.getGesuchstellerNumber() === 1) {
                // 1 , 1
                if (this.gesuchModelManager.isBasisJahr2Required()) {
                    this.navigateToStepEinkommensverschlechterung('1', '2');
                } else if (this.gesuchModelManager.isGesuchsteller2Required()) {
                    this.navigateToStepEinkommensverschlechterung('2', '1');
                } else {
                    this.navigateToStepEinkommensverschlechterungResultate('1');
                }

            } else { //gesuchsteller ===2
                // 2 , 1
                if (this.gesuchModelManager.isBasisJahr2Required()) {
                    this.navigateToStepEinkommensverschlechterung('2', '2');
                } else {
                    this.navigateToStepEinkommensverschlechterungResultate('1');
                }

            }

        } else if ((this.gesuchModelManager.getBasisJahrPlusNumber() === 2)) {
            if (this.gesuchModelManager.getGesuchstellerNumber() === 1) {
                // 1 , 2
                if (this.gesuchModelManager.isGesuchsteller2Required()) {
                    this.navigateToStepEinkommensverschlechterung('2', '1');
                } else {
                    this.navigateToStepEinkommensverschlechterungResultate('1');
                }

            } else { //gesuchsteller ===2
                // 2 , 2
                this.navigateToStepEinkommensverschlechterungResultate('1');
            }
        }
    };

    private navigatePreviousEVSubStep2(): void {
        if ((this.gesuchModelManager.getBasisJahrPlusNumber() === 1)) {
            if (this.gesuchModelManager.getGesuchstellerNumber() === 1) {
                // 1 , 1
                if (this.gesuchModelManager.isGesuchsteller2Required()) {
                    this.navigateToStepEinkommensverschlechterungSteuern();
                } else {
                    this.navigateToStep(TSWizardStepName.EINKOMMENSVERSCHLECHTERUNG);
                }
            } else { //gesuchsteller ===2
                // 2 , 1
                if (this.gesuchModelManager.isBasisJahr2Required()) {
                    this.navigateToStepEinkommensverschlechterung('1', '2');
                } else {
                    this.navigateToStepEinkommensverschlechterung('1', '1');
                }
            }
        } else if ((this.gesuchModelManager.getBasisJahrPlusNumber() === 2)) {
            if (this.gesuchModelManager.getGesuchstellerNumber() === 1) {
                // 1 , 2
                this.navigateToStepEinkommensverschlechterung('1', '1');
            } else { //gesuchsteller ===2
                this.navigateToStepEinkommensverschlechterung('2', '1');
            }
        }
    };

    private navigatePreviousEVSubStep4(): void {
        if (this.gesuchModelManager.getBasisJahrPlusNumber() === 2) {
            this.navigateToStepEinkommensverschlechterungResultate('1');
        } else {
            // baisjahrPlus1

            let gesuchsteller2Required: boolean = this.gesuchModelManager.isGesuchsteller2Required();
            let basisJahr2Required: boolean = this.gesuchModelManager.isBasisJahr2Required();

            if (gesuchsteller2Required && basisJahr2Required) {
                this.navigateToStepEinkommensverschlechterung('2', '2');
            } else if (gesuchsteller2Required) {
                this.navigateToStepEinkommensverschlechterung('2', '1');
            } else if (basisJahr2Required) {
                this.navigateToStepEinkommensverschlechterung('1', '2');
            } else {
                this.navigateToStepEinkommensverschlechterung('1', '1');
            }
        }
    };

    //muss als instance arrow function definiert werden statt als prototyp funktionw eil sonst this undefined ist
    private navigateNextEVSubStep4(): void {
        if (this.gesuchModelManager.getBasisJahrPlusNumber() === 2) {
            this.navigateToStep(TSWizardStepName.DOKUMENTE);
        } else {
            let ekvFuerBasisJahrPlus2 = this.gesuchModelManager.getGesuch().einkommensverschlechterungInfo.ekvFuerBasisJahrPlus2
                && this.gesuchModelManager.getGesuch().einkommensverschlechterungInfo.ekvFuerBasisJahrPlus2 === true;
            if (ekvFuerBasisJahrPlus2) {
                this.navigateToStepEinkommensverschlechterungResultate('2');
            } else {
                this.navigateToStep(TSWizardStepName.DOKUMENTE);
            }
        }
    };

}
