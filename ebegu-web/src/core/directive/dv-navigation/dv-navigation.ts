import {IDirective, IDirectiveFactory} from 'angular';
import {IStateService} from 'angular-ui-router';
import WizardStepManager from '../../../gesuch/service/wizardStepManager';
import Moment = moment.Moment;
import INgModelController = angular.INgModelController;
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
import GesuchModelManager from '../../../gesuch/service/gesuchModelManager';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import {TSRole} from '../../../models/enums/TSRole';
import ITranslateService = angular.translate.ITranslateService;
import ErrorService from '../../errors/service/ErrorService';
import {TSWizardStepStatus} from '../../../models/enums/TSWizardStepStatus';
let template = require('./dv-navigation.html');

/**
 * Diese Direktive wird benutzt, um die Navigation Buttons darzustellen. Folgende Parameter koennen benutzt werden,
 * um die Funktionalitaet zu definieren:
 *
 * -- dvPrevious: function      Wenn true wird der Button "previous" angezeigt (nicht gleichzeitig mit dvCancel benutzen)
 * -- dvNext: function          Wenn true wird der Button "next" angezeigt
 * -- dvNextDisabled: boolean
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

    static $inject: string[] = ['WizardStepManager', '$state', 'GesuchModelManager', 'AuthServiceRS', '$translate', 'ErrorService'];
    /* @ngInject */
    constructor(private wizardStepManager: WizardStepManager, private state: IStateService, private gesuchModelManager: GesuchModelManager,
        private authServiceRS: AuthServiceRS, private $translate: ITranslateService, private errorService: ErrorService) {
    }

    public doesCancelExist(): boolean {
        return this.dvCancel !== undefined && this.dvCancel !== null;
    }

    /**
     * Wenn die function save uebergeben wurde, dann heisst der Button Speichern und weiter. Sonst nur weiter
     * @returns {string}
     */
    public getPreviousButtonName(): string {
        if (this.dvSave) {
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
        if (this.dvSave) {
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
        if (this.dvSave) {
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
        if (this.dvSave) {
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
        if (TSWizardStepName.GESUCH_ERSTELLEN === this.wizardStepManager.getCurrentStepName()) {
            this.state.go('gesuch.familiensituation');

        } else if (TSWizardStepName.FAMILIENSITUATION === this.wizardStepManager.getCurrentStepName()) {
            this.state.go('gesuch.stammdaten');

        } else if (TSWizardStepName.GESUCHSTELLER === this.wizardStepManager.getCurrentStepName()) {
            if ((this.gesuchModelManager.getGesuchstellerNumber() === 1) && this.gesuchModelManager.isGesuchsteller2Required()) {
                this.state.go('gesuch.stammdaten', {gesuchstellerNumber: '2'});
            } else {
                if (this.authServiceRS.isRole(TSRole.SACHBEARBEITER_INSTITUTION)
                    || this.authServiceRS.isRole(TSRole.SACHBEARBEITER_TRAEGERSCHAFT)) {
                    this.state.go('gesuch.betreuungen');
                } else {
                    this.state.go('gesuch.kinder');
                }
            }
        } else if (TSWizardStepName.KINDER === this.wizardStepManager.getCurrentStepName() && this.dvSubStep === 1) {
            this.state.go('gesuch.betreuungen');

        } else if (TSWizardStepName.KINDER === this.wizardStepManager.getCurrentStepName() && this.dvSubStep === 2) {
            this.state.go('gesuch.kinder');

        } else if (TSWizardStepName.BETREUUNG === this.wizardStepManager.getCurrentStepName() && this.dvSubStep === 1) {
            if (this.authServiceRS.isRole(TSRole.SACHBEARBEITER_INSTITUTION)
                || this.authServiceRS.isRole(TSRole.SACHBEARBEITER_TRAEGERSCHAFT)) {
                this.state.go('gesuch.verfuegen');
            } else {
                this.state.go('gesuch.erwerbsPensen');
            }

        } else if (TSWizardStepName.BETREUUNG === this.wizardStepManager.getCurrentStepName() && this.dvSubStep === 2) {
            // Diese Logik ist ziemlich kompliziert. Deswegen bleibt sie noch in betreuungView.ts -> Hier wird dann nichts gemacht

        } else if (TSWizardStepName.ERWERBSPENSUM === this.wizardStepManager.getCurrentStepName() && this.dvSubStep === 1) {
            this.errorService.clearAll();
            if (this.gesuchModelManager.isGesuchsteller2Required()) {
                this.state.go('gesuch.finanzielleSituationStart');
            } else {
                this.state.go('gesuch.finanzielleSituation', {gesuchstellerNumber: 1});
            }

        } else if (TSWizardStepName.ERWERBSPENSUM === this.wizardStepManager.getCurrentStepName() && this.dvSubStep === 2) {
            this.state.go('gesuch.erwerbsPensen');

        } else if (TSWizardStepName.FINANZIELLE_SITUATION === this.wizardStepManager.getCurrentStepName() && this.dvSubStep === 1) {
            if ((this.gesuchModelManager.getGesuchstellerNumber() === 1) && this.gesuchModelManager.isGesuchsteller2Required()) {
                this.state.go('gesuch.finanzielleSituation', {gesuchstellerNumber: '2'});
            } else {
                this.state.go('gesuch.finanzielleSituationResultate');
            }

        } else if (TSWizardStepName.FINANZIELLE_SITUATION === this.wizardStepManager.getCurrentStepName() && this.dvSubStep === 2) {
            this.state.go('gesuch.finanzielleSituation', {gesuchstellerNumber: '1'});

        } else if (TSWizardStepName.FINANZIELLE_SITUATION === this.wizardStepManager.getCurrentStepName() && this.dvSubStep === 3) {
            this.state.go('gesuch.einkommensverschlechterungInfo');

        } else if (TSWizardStepName.EINKOMMENSVERSCHLECHTERUNG === this.wizardStepManager.getCurrentStepName() && this.dvSubStep === 1) {
            if (this.gesuchModelManager.getEinkommensverschlechterungsInfo().einkommensverschlechterung) { // was muss hier sein?
                if (this.gesuchModelManager.isGesuchsteller2Required()) {
                    this.state.go('gesuch.einkommensverschlechterungSteuern');
                } else {
                    this.state.go('gesuch.einkommensverschlechterung', {gesuchstellerNumber: '1'});
                }
            } else {
                this.state.go('gesuch.dokumente');
            }

        } else if (TSWizardStepName.EINKOMMENSVERSCHLECHTERUNG === this.wizardStepManager.getCurrentStepName() && this.dvSubStep === 2) {
            this.navigateNextEVSubStep2();

        } else if (TSWizardStepName.EINKOMMENSVERSCHLECHTERUNG === this.wizardStepManager.getCurrentStepName() && this.dvSubStep === 3) {
            this.state.go('gesuch.einkommensverschlechterung', {gesuchstellerNumber: '1', basisjahrPlus: '1'});

        } else if (TSWizardStepName.EINKOMMENSVERSCHLECHTERUNG === this.wizardStepManager.getCurrentStepName() && this.dvSubStep === 4) {
            this.navigateNextEVSubStep4();

        } else if (TSWizardStepName.DOKUMENTE === this.wizardStepManager.getCurrentStepName()) {
            this.state.go('gesuch.verfuegen');
        }
    }

    /**
     * Berechnet fuer den aktuellen Benutzer und Step, welcher der previous Step ist und wechselt zu diesem.
     */
    private navigateToPreviousStep() {
        this.errorService.clearAll();
        if (TSWizardStepName.FAMILIENSITUATION === this.wizardStepManager.getCurrentStepName()) {
            this.state.go('gesuch.fallcreation');

        } else if (TSWizardStepName.GESUCHSTELLER === this.wizardStepManager.getCurrentStepName()) {
            if ((this.gesuchModelManager.getGesuchstellerNumber() === 2)) {
                this.state.go('gesuch.stammdaten', {gesuchstellerNumber: '1'});
            } else {
                this.state.go('gesuch.familiensituation');
            }

        } else if (TSWizardStepName.KINDER === this.wizardStepManager.getCurrentStepName() && this.dvSubStep === 1) {
            this.moveBackToGesuchsteller();

        } else if (TSWizardStepName.KINDER === this.wizardStepManager.getCurrentStepName() && this.dvSubStep === 2) {
            this.state.go('gesuch.kinder');

        } else if (TSWizardStepName.BETREUUNG === this.wizardStepManager.getCurrentStepName() && this.dvSubStep === 1) {
            if (this.authServiceRS.isRole(TSRole.SACHBEARBEITER_INSTITUTION )
                || this.authServiceRS.isRole(TSRole.SACHBEARBEITER_TRAEGERSCHAFT)) {
                this.moveBackToGesuchsteller();
            } else {
                this.state.go('gesuch.kinder');
            }

        } else if (TSWizardStepName.BETREUUNG === this.wizardStepManager.getCurrentStepName() && this.dvSubStep === 2) {
            this.state.go('gesuch.betreuungen');

        } else if (TSWizardStepName.ERWERBSPENSUM === this.wizardStepManager.getCurrentStepName() && this.dvSubStep === 1) {
            this.state.go('gesuch.betreuungen');

        } else if (TSWizardStepName.ERWERBSPENSUM === this.wizardStepManager.getCurrentStepName() && this.dvSubStep === 2) {
            this.state.go('gesuch.erwerbsPensen');

        } else if (TSWizardStepName.FINANZIELLE_SITUATION === this.wizardStepManager.getCurrentStepName() && this.dvSubStep === 1) {
            if ((this.gesuchModelManager.getGesuchstellerNumber() === 2)) {
                this.state.go('gesuch.finanzielleSituation', {gesuchstellerNumber: 1});
            } else if ((this.gesuchModelManager.gesuchstellerNumber === 1) && this.gesuchModelManager.isGesuchsteller2Required()) {
                this.state.go('gesuch.finanzielleSituationStart');
            } else {
                this.state.go('gesuch.kinder');
            }

        } else if (TSWizardStepName.FINANZIELLE_SITUATION === this.wizardStepManager.getCurrentStepName() && this.dvSubStep === 2) {
            this.state.go('gesuch.erwerbsPensen');

        } else if (TSWizardStepName.FINANZIELLE_SITUATION === this.wizardStepManager.getCurrentStepName() && this.dvSubStep === 3) {
            if ((this.gesuchModelManager.gesuchstellerNumber === 2)) {
                this.state.go('gesuch.finanzielleSituation', {gesuchstellerNumber: 2});
            } else {
                this.state.go('gesuch.finanzielleSituation', {gesuchstellerNumber: 1});
            }

        } else if (TSWizardStepName.EINKOMMENSVERSCHLECHTERUNG === this.wizardStepManager.getCurrentStepName() && this.dvSubStep === 1) {
            this.state.go('gesuch.finanzielleSituation', {gesuchstellerNumber: '1'});

        } else if (TSWizardStepName.EINKOMMENSVERSCHLECHTERUNG === this.wizardStepManager.getCurrentStepName() && this.dvSubStep === 2) {
            this.navigatePreviousEVSubStep2();

        } else if (TSWizardStepName.EINKOMMENSVERSCHLECHTERUNG === this.wizardStepManager.getCurrentStepName() && this.dvSubStep === 3) {
            this.state.go('gesuch.einkommensverschlechterungInfo');

        } else if (TSWizardStepName.EINKOMMENSVERSCHLECHTERUNG === this.wizardStepManager.getCurrentStepName() && this.dvSubStep === 4) {
            this.navigatePreviousEVSubStep4();

        } else if (TSWizardStepName.DOKUMENTE === this.wizardStepManager.getCurrentStepName()) {
            this.navigatePreviousDokumente();

        } else if (TSWizardStepName.VERFUEGEN === this.wizardStepManager.getCurrentStepName() && this.dvSubStep === 1) {
            if (this.authServiceRS.isRole(TSRole.SACHBEARBEITER_INSTITUTION)
                || this.authServiceRS.isRole(TSRole.SACHBEARBEITER_TRAEGERSCHAFT)) {
                this.state.go('gesuch.betreuungen');
            } else {
                this.state.go('gesuch.dokumente');
            }

        } else if (TSWizardStepName.VERFUEGEN === this.wizardStepManager.getCurrentStepName() && this.dvSubStep === 2) {
            this.state.go('gesuch.verfuegen');
        }
    }

    /**
     * Checks whether the button should be disable for the current conditions. By default enabled
     * @returns {boolean}
     */
    public isNextDisabled(): boolean {
        if (TSWizardStepName.KINDER === this.wizardStepManager.getCurrentStepName() && this.dvSubStep === 1) {
            return !this.gesuchModelManager.isThereAnyKindWithBetreuungsbedarf() && !this.wizardStepManager.isNextStepAvailable();
        }
        if (TSWizardStepName.BETREUUNG === this.wizardStepManager.getCurrentStepName() && this.dvSubStep === 1) {
            return !this.gesuchModelManager.isThereAnyBetreuung() && !this.wizardStepManager.isNextStepAvailable();
        }
        if (TSWizardStepName.ERWERBSPENSUM === this.wizardStepManager.getCurrentStepName() && this.dvSubStep === 1) {
            return this.dvNextDisabled() && !this.wizardStepManager.isNextStepAvailable();
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
                    this.state.go('gesuch.einkommensverschlechterung', {
                        gesuchstellerNumber: '1',
                        basisjahrPlus: '2'
                    });
                } else if (this.gesuchModelManager.isGesuchsteller2Required()) {
                    this.state.go('gesuch.einkommensverschlechterung', {
                        gesuchstellerNumber: '2',
                        basisjahrPlus: '1'
                    });
                } else {
                    this.state.go('gesuch.einkommensverschlechterungResultate', {basisjahrPlus: '1'});
                }

            } else { //gesuchsteller ===2
                // 2 , 1
                if (this.gesuchModelManager.isBasisJahr2Required()) {
                    this.state.go('gesuch.einkommensverschlechterung', {
                        gesuchstellerNumber: '2',
                        basisjahrPlus: '2'
                    });
                } else {
                    this.state.go('gesuch.einkommensverschlechterungResultate', {basisjahrPlus: '1'});
                }

            }

        } else if ((this.gesuchModelManager.getBasisJahrPlusNumber() === 2)) {
            if (this.gesuchModelManager.getGesuchstellerNumber() === 1) {
                // 1 , 2
                if (this.gesuchModelManager.isGesuchsteller2Required()) {
                    this.state.go('gesuch.einkommensverschlechterung', {
                        gesuchstellerNumber: '2',
                        basisjahrPlus: '1'
                    });
                } else {
                    this.state.go('gesuch.einkommensverschlechterungResultate', {basisjahrPlus: '1'});
                }

            } else { //gesuchsteller ===2
                // 2 , 2
                this.state.go('gesuch.einkommensverschlechterungResultate', {basisjahrPlus: '1'});
            }
        }
    };

    private navigatePreviousEVSubStep2(): void {
        if ((this.gesuchModelManager.getBasisJahrPlusNumber() === 1)) {
            if (this.gesuchModelManager.getGesuchstellerNumber() === 1) {
                // 1 , 1
                if (this.gesuchModelManager.isGesuchsteller2Required()) {
                    this.state.go('gesuch.einkommensverschlechterungSteuern');
                } else {
                    this.state.go('gesuch.einkommensverschlechterungInfo');
                }
            } else { //gesuchsteller ===2
                // 2 , 1
                if (this.gesuchModelManager.isBasisJahr2Required()) {
                    this.state.go('gesuch.einkommensverschlechterung', {
                        gesuchstellerNumber: '1',
                        basisjahrPlus: '2'
                    });
                } else {
                    this.state.go('gesuch.einkommensverschlechterung', {
                        gesuchstellerNumber: '1',
                        basisjahrPlus: '1'
                    });
                }
            }
        } else if ((this.gesuchModelManager.getBasisJahrPlusNumber() === 2)) {
            if (this.gesuchModelManager.getGesuchstellerNumber() === 1) {
                // 1 , 2
                this.state.go('gesuch.einkommensverschlechterung', {
                    gesuchstellerNumber: '1',
                    basisjahrPlus: '1'
                });
            } else { //gesuchsteller ===2
                // 2 , 2
                this.state.go('gesuch.einkommensverschlechterung', {
                    gesuchstellerNumber: '2',
                    basisjahrPlus: '1'
                });
            }
        }
    };

    private navigatePreviousEVSubStep4(): void {
        if (this.gesuchModelManager.basisJahrPlusNumber === 2) {
            this.state.go('gesuch.einkommensverschlechterungResultate', {basisjahrPlus: '1'});
        } else {
            // baisjahrPlus1

            let gesuchsteller2Required: boolean = this.gesuchModelManager.isGesuchsteller2Required();
            let basisJahr2Required: boolean = this.gesuchModelManager.isBasisJahr2Required();

            if (gesuchsteller2Required && basisJahr2Required) {
                this.state.go('gesuch.einkommensverschlechterung', {
                    gesuchstellerNumber: '2',
                    basisjahrPlus: '2'
                });
            } else if (gesuchsteller2Required) {
                this.state.go('gesuch.einkommensverschlechterung', {
                    gesuchstellerNumber: '2',
                    basisjahrPlus: '1'
                });
            } else if (basisJahr2Required) {
                this.state.go('gesuch.einkommensverschlechterung', {
                    gesuchstellerNumber: '1',
                    basisjahrPlus: '2'
                });
            } else {
                this.state.go('gesuch.einkommensverschlechterung', {
                    gesuchstellerNumber: '1',
                    basisjahrPlus: '1'
                });
            }
        }
    };

    //muss als instance arrow function definiert werden statt als prototyp funktionw eil sonst this undefined ist
    private navigateNextEVSubStep4(): void {
        if (this.gesuchModelManager.basisJahrPlusNumber === 2) {
            this.goToDokumenteView();
        } else {
            let ekvFuerBasisJahrPlus2 = this.gesuchModelManager.getGesuch().einkommensverschlechterungInfo.ekvFuerBasisJahrPlus2
                && this.gesuchModelManager.getGesuch().einkommensverschlechterungInfo.ekvFuerBasisJahrPlus2 === true;
            if (ekvFuerBasisJahrPlus2) {
                this.state.go('gesuch.einkommensverschlechterungResultate', {basisjahrPlus: '2'});
            } else {
                this.goToDokumenteView();
            }
        }
    };

    /**
     * Goes to the view of documents and updates before the status of the WizardStep Einkommensverschlechterung to OK
     */
    private goToDokumenteView() {
        this.wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.OK).then(() => {
            this.state.go('gesuch.dokumente');
        });
    }

    private navigatePreviousDokumente(): void {
        let ekvFuerBasisJahrPlus2 = this.gesuchModelManager.getGesuch().einkommensverschlechterungInfo.ekvFuerBasisJahrPlus2
            && this.gesuchModelManager.getGesuch().einkommensverschlechterungInfo.ekvFuerBasisJahrPlus2 === true;
        let ekvFuerBasisJahrPlus1 = this.gesuchModelManager.getGesuch().einkommensverschlechterungInfo.ekvFuerBasisJahrPlus1
            && this.gesuchModelManager.getGesuch().einkommensverschlechterungInfo.ekvFuerBasisJahrPlus1 === true;
        if (ekvFuerBasisJahrPlus2) {
            this.state.go('gesuch.einkommensverschlechterungResultate', {basisjahrPlus: '2'});
        } else if (ekvFuerBasisJahrPlus1) {
            this.state.go('gesuch.einkommensverschlechterungResultate', {basisjahrPlus: '1'});
        } else {
            this.state.go('gesuch.einkommensverschlechterungInfo');
        }
    }

    private moveBackToGesuchsteller() {
        if ((this.gesuchModelManager.getGesuchstellerNumber() === 2)) {
            this.state.go('gesuch.stammdaten', {gesuchstellerNumber: 2});
        } else {
            this.state.go('gesuch.stammdaten', {gesuchstellerNumber: 1});
        }
    }

}
