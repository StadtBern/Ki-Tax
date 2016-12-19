import GesuchModelManager from './service/gesuchModelManager';
import BerechnungsManager from './service/berechnungsManager';
import DateUtil from '../utils/DateUtil';
import WizardStepManager from './service/wizardStepManager';
import {TSWizardStepName} from '../models/enums/TSWizardStepName';
import {TSWizardStepStatus} from '../models/enums/TSWizardStepStatus';
import EbeguUtil from '../utils/EbeguUtil';
import {TSAntragStatus, IN_BEARBEITUNG_BASE_NAME} from '../models/enums/TSAntragStatus';
import AntragStatusHistoryRS from '../core/service/antragStatusHistoryRS.rest';
import TSGesuch from '../models/TSGesuch';
import TSUser from '../models/TSUser';
import {TSRoleUtil} from '../utils/TSRoleUtil';
import {TSRole} from '../models/enums/TSRole';
import AuthServiceRS from '../authentication/service/AuthServiceRS.rest';
import ITranslateService = angular.translate.ITranslateService;

export class GesuchRouteController {

    TSRole: any;
    TSRoleUtil: any;

    static $inject: string[] = ['GesuchModelManager', 'BerechnungsManager', 'WizardStepManager', 'EbeguUtil',
        'AntragStatusHistoryRS', '$translate', 'AuthServiceRS', '$mdSidenav', 'CONSTANTS'];
    /* @ngInject */
    constructor(private gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager,
                private wizardStepManager: WizardStepManager, private ebeguUtil: EbeguUtil,
                private antragStatusHistoryRS: AntragStatusHistoryRS, private $translate: ITranslateService,
                private authServiceRS: AuthServiceRS, private $mdSidenav: ng.material.ISidenavService, private CONSTANTS: any) {
        //super(gesuchModelManager, berechnungsManager, wizardStepManager);
        this.antragStatusHistoryRS.loadLastStatusChange(this.gesuchModelManager.getGesuch());
        this.TSRole = TSRole;
        this.TSRoleUtil = TSRoleUtil;
    }

    showFinanzsituationStart(): boolean {
        return this.gesuchModelManager.isGesuchsteller2Required();
    }


    public getDateFromGesuch(): string {
        if (this.gesuchModelManager && this.gesuchModelManager.getGesuch()) {
            return DateUtil.momentToLocalDateFormat(this.gesuchModelManager.getGesuch().eingangsdatum, 'DD.MM.YYYY');
        }
        return undefined;
    }

    public toggleSidenav(componentId: string) {
        this.$mdSidenav(componentId).toggle();
    }

    public closeSidenav(componentId: string) {
        this.$mdSidenav(componentId).close();
    }

    public getIcon(stepName: TSWizardStepName): string {
        let step = this.wizardStepManager.getStepByName(stepName);
        if (step) {
            let status = step.wizardStepStatus;
            if (status === TSWizardStepStatus.MUTIERT) {
                return 'fa-pencil green';
            } else if (status === TSWizardStepStatus.OK) {
                if (this.getGesuch().isMutation()) {
                    return '';
                } else {
                    return 'fa-check green';
                }
            } else if (status === TSWizardStepStatus.NOK) {
                return 'fa-close red';
            } else if (status === TSWizardStepStatus.IN_BEARBEITUNG) {
                if (step.wizardStepName === TSWizardStepName.DOKUMENTE || step.wizardStepName === TSWizardStepName.FREIGABE) { // Dokumenten haben kein Icon wenn nicht alle hochgeladen wurden
                    return '';
                }
                return 'fa-pencil black';
            } else if (status === TSWizardStepStatus.PLATZBESTAETIGUNG || status === TSWizardStepStatus.WARTEN) {
                return 'fa-hourglass orange';
            } else if (status === TSWizardStepStatus.UNBESUCHT) {
                return '';
            }
        }
        return '';
    }

    /**
     * Steps are disabled when the field verfuegbar is false or if they are not allowed for the current role
     * @param stepName
     * @returns {boolean} Sollte etwas schief gehen, true wird zurueckgegeben
     */
    public isWizardStepDisabled(stepName: TSWizardStepName): boolean {
        let step = this.wizardStepManager.getStepByName(stepName);
        if (step) {
            return !this.wizardStepManager.isStepClickableForCurrentRole(step, this.gesuchModelManager.getGesuch());
        }
        return true;
    }

    public isStepVisible(stepName: TSWizardStepName): boolean {
        if (stepName) {
            return this.wizardStepManager.isStepVisible(stepName);
        }
        return true;
    }

    public isElementActive(stepName: TSWizardStepName): boolean {
        return this.wizardStepManager.getCurrentStepName() === stepName;
    }

    /**
     * Uebersetzt den Status des Gesuchs und gibt ihn zurueck. Sollte das Gesuch noch keinen Status haben IN_BEARBEITUNG_JA
     * wird zurueckgegeben
     * @returns {string}
     */
    public getGesuchStatusTranslation(): string {
        let toTranslate: TSAntragStatus = TSAntragStatus.IN_BEARBEITUNG_JA;
        if (this.gesuchModelManager.getGesuch() && this.gesuchModelManager.getGesuch().status) {
            toTranslate = this.gesuchModelManager.calculateNewStatus(this.gesuchModelManager.getGesuch().status);
        }
        let isUserGesuchsteller: boolean = this.authServiceRS.isOneOfRoles(TSRoleUtil.getGesuchstellerOnlyRoles());
        let isUserJA: boolean = this.authServiceRS.isOneOfRoles(TSRoleUtil.getJugendamtRole());

        if (toTranslate === TSAntragStatus.IN_BEARBEITUNG_GS && isUserGesuchsteller
            || toTranslate === TSAntragStatus.IN_BEARBEITUNG_JA && isUserJA) {
            return this.ebeguUtil.translateString(IN_BEARBEITUNG_BASE_NAME);
        }
        return this.ebeguUtil.translateString(TSAntragStatus[toTranslate]);
    }

    public getUserFullname(): string {
        return this.antragStatusHistoryRS.getUserFullname();
    }

    public getGesuchId(): string {
        if (this.getGesuch()) {
            return this.getGesuch().id;
        }
        return undefined;
    }

    public getGesuch(): TSGesuch {
        if (this.gesuchModelManager.getGesuch()) {
            return this.gesuchModelManager.getGesuch();
        }
        return undefined;
    }

    /**
     * Sets the given user as the verantworlicher fuer den aktuellen Fall
     * @param verantwortlicher
     */
    public setVerantwortlicher(verantwortlicher: TSUser): void {
        if (verantwortlicher) {
            this.gesuchModelManager.setUserAsFallVerantwortlicher(verantwortlicher);
            this.gesuchModelManager.updateFall();
        }
    }

    public getGesuchErstellenStepTitle(): string {
        if (this.gesuchModelManager.isErstgesuch()) {
            if (this.getDateFromGesuch()) {
                return this.$translate.instant('MENU_ERSTGESUCH_VOM', {
                    date: this.getDateFromGesuch()
                });
            } else {
                return this.$translate.instant('MENU_ERSTGESUCH');
            }
        } else {
            if (this.getDateFromGesuch()) {
                return this.$translate.instant('MENU_MUTATION_VOM', {
                    date: this.getDateFromGesuch()
                });
            } else {
                return this.$translate.instant('MENU_MUTATION');
            }
        }
    }

    public getGesuchName(): string {
        return this.gesuchModelManager.getGesuchName();
    }

    public getActiveElement(): TSWizardStepName {
        return this.wizardStepManager.getCurrentStepName();
    }

}
