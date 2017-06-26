import GesuchModelManager from './service/gesuchModelManager';
import BerechnungsManager from './service/berechnungsManager';
import DateUtil from '../utils/DateUtil';
import WizardStepManager from './service/wizardStepManager';
import {TSWizardStepName} from '../models/enums/TSWizardStepName';
import {TSWizardStepStatus} from '../models/enums/TSWizardStepStatus';
import EbeguUtil from '../utils/EbeguUtil';
import {IN_BEARBEITUNG_BASE_NAME, TSAntragStatus} from '../models/enums/TSAntragStatus';
import AntragStatusHistoryRS from '../core/service/antragStatusHistoryRS.rest';
import TSGesuch from '../models/TSGesuch';
import {TSRoleUtil} from '../utils/TSRoleUtil';
import {TSRole} from '../models/enums/TSRole';
import AuthServiceRS from '../authentication/service/AuthServiceRS.rest';
import ITranslateService = angular.translate.ITranslateService;
import TSGesuchstellerContainer from '../models/TSGesuchstellerContainer';
import TSEWKPerson from '../models/TSEWKPerson';
import GesuchstellerRS from '../core/service/gesuchstellerRS.rest';
import {ILogService, IRootScopeService} from '@types/angular';
import TSEWKResultat from '../models/TSEWKResultat';
import {TSGesuchEvent} from '../models/enums/TSGesuchEvent';
import {TSAntragTyp} from '../models/enums/TSAntragTyp';
import EwkRS from '../core/service/ewkRS.rest';

export class GesuchRouteController {

    TSRole: any;
    TSRoleUtil: any;
    openEwkSidenav: boolean;

    static $inject: string[] = ['GesuchModelManager', 'BerechnungsManager', 'WizardStepManager', 'EbeguUtil',
        'AntragStatusHistoryRS', '$translate', 'AuthServiceRS', '$mdSidenav', 'CONSTANTS', 'GesuchstellerRS', 'EwkRS', '$log', '$rootScope'];
    /* @ngInject */
    constructor(private gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager,
                private wizardStepManager: WizardStepManager, private ebeguUtil: EbeguUtil,
                private antragStatusHistoryRS: AntragStatusHistoryRS, private $translate: ITranslateService,
                private authServiceRS: AuthServiceRS, private $mdSidenav: ng.material.ISidenavService, private CONSTANTS: any,
                private gesuchstellerRS: GesuchstellerRS, private ewkRS: EwkRS,
                private $log: ILogService, private $rootScope: IRootScopeService) {
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
                return 'fa-circle green';
            } else if (status === TSWizardStepStatus.OK) {
                if (this.getGesuch().isMutation()) {
                    if (step.wizardStepName === TSWizardStepName.VERFUEGEN ) { // Verfuegung auch bei Mutation mit Hacken (falls verfuegt)
                        return 'fa-check green';
                    }
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
                if (this.getGesuch().isMutation() && this.isWizardStepDisabled(step.wizardStepName)) { // in einer Mutation bekommt icon nur wenn es aktiviert ist
                    return '';
                } else {
                    return 'fa-hourglass orange';
                }
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
        let isUserSTV: boolean = this.authServiceRS.isOneOfRoles(TSRoleUtil.getSteueramtOnlyRoles());

        if (toTranslate === TSAntragStatus.IN_BEARBEITUNG_GS && isUserGesuchsteller
            || toTranslate === TSAntragStatus.IN_BEARBEITUNG_JA && isUserJA) {
            return this.ebeguUtil.translateString(IN_BEARBEITUNG_BASE_NAME);
        }
        switch (toTranslate) {
            case TSAntragStatus.GEPRUEFT_STV:
            case TSAntragStatus.IN_BEARBEITUNG_STV:
            case TSAntragStatus.PRUEFUNG_STV:
                if (!isUserJA && !isUserSTV) {
                    return this.ebeguUtil.translateString('VERFUEGT');
                }
                break;
            default:
                break;

        }

        if ((toTranslate === TSAntragStatus.NUR_SCHULAMT || toTranslate === TSAntragStatus.NUR_SCHULAMT_DOKUMENTE_HOCHGELADEN)
            && isUserGesuchsteller) {
            return this.ebeguUtil.translateString('ABGESCHLOSSEN');
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

    public getGesuchErstellenStepTitle(): string {
        if (this.gesuchModelManager.isGesuch()) {
            if (this.getDateFromGesuch()) {
                let key = (this.gesuchModelManager.getGesuch().typ === TSAntragTyp.ERNEUERUNGSGESUCH) ? 'MENU_ERNEUERUNGSGESUCH_VOM' : 'MENU_ERSTGESUCH_VOM';
                return this.$translate.instant(key, {
                    date: this.getDateFromGesuch()
                });
            } else {
                let key = (this.gesuchModelManager.getGesuch().typ === TSAntragTyp.ERNEUERUNGSGESUCH) ? 'MENU_ERNEUERUNGSGESUCH' : 'MENU_ERSTGESUCH';
                return this.$translate.instant(key);
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

    public getGesuchstellerTitle(gesuchsteller: TSGesuchstellerContainer): string {
        if (gesuchsteller && gesuchsteller.gesuchstellerJA) {
            if (gesuchsteller.gesuchstellerJA.ewkPersonId) {
                return gesuchsteller.gesuchstellerJA.getFullName() + ' (' + gesuchsteller.gesuchstellerJA.ewkPersonId + ')';
            }
            return gesuchsteller.gesuchstellerJA.getFullName();
        }
        return undefined;
    }

    public showAbfrageForGesuchsteller(n: any): boolean {
        return this.ewkRS.ewkSearchAvailable(n);
    }

    public getGesuchsteller(n: number): TSGesuchstellerContainer {
        switch (n) {
            case 1:
            if (this.gesuchModelManager.getGesuch() && this.gesuchModelManager.getGesuch().gesuchsteller1) {
                return this.gesuchModelManager.getGesuch().gesuchsteller1;
            }
                return undefined;
            case 2:
                if (this.gesuchModelManager.getGesuch() && this.gesuchModelManager.getGesuch().gesuchsteller2) {
                    return this.gesuchModelManager.getGesuch().gesuchsteller2;
                }
                return undefined;
            default:
                return undefined;
        }
    }

    public getEWKResultat(n: number): TSEWKResultat {
        switch (n) {
            case 1:
                return this.gesuchModelManager.ewkResultatGS1;
            case 2:
                return this.gesuchModelManager.ewkResultatGS2;
            default:
                return undefined;
        }
    }

    public getEWKPerson(n: number): TSEWKPerson {
        switch (n) {
            case 1:
                return this.gesuchModelManager.ewkPersonGS1;
            case 2:
                return this.gesuchModelManager.ewkPersonGS2;
            default:
                return undefined;
        }
    }

    public checkEWKPerson(person: TSEWKPerson, n: number): boolean {
        switch (n) {
            case 1:
                return (person.personID === this.ewkRS.gesuchsteller1.gesuchstellerJA.ewkPersonId);
            case 2:
                return (person.personID === this.ewkRS.gesuchsteller2.gesuchstellerJA.ewkPersonId);
            default:
                return false;
        }
    }

    public searchGesuchsteller(n: number): void {
        this.ewkRS.suchePerson(n).then(response => {
            switch (n) {
                case 1:
                    this.gesuchModelManager.ewkResultatGS1 = response;
                    if (this.gesuchModelManager.ewkResultatGS1.anzahlResultate === 1) {
                       this.selectPerson(this.gesuchModelManager.ewkResultatGS1.personen[0], n);
                    }
                    break;
                case 2:
                    this.gesuchModelManager.ewkResultatGS2 = response;
                    if (this.gesuchModelManager.ewkResultatGS2.anzahlResultate === 1) {
                        this.selectPerson(this.gesuchModelManager.ewkResultatGS2.personen[0], n);
                    }
                    break;
                default:
                    break;
            }
        }).catch((exception) => {
            this.$log.error('there was an error searching the person in EWK ', exception);
        });
    }

    public selectPerson(person: TSEWKPerson, n: number): void {
        this.ewkRS.selectPerson(n, person.personID);
        switch (n) {
            case 1:
                this.gesuchModelManager.ewkPersonGS1 = person;
                this.$rootScope.$broadcast(TSGesuchEvent[TSGesuchEvent.EWK_PERSON_SELECTED], 1, person.personID);
                break;
            case 2:
                this.gesuchModelManager.ewkPersonGS2 = person;
                this.$rootScope.$broadcast(TSGesuchEvent[TSGesuchEvent.EWK_PERSON_SELECTED], 2, person.personID);
                break;
            default:
                break;
        }
    }

    public isGesuchGesperrt(): boolean {
        if (this.gesuchModelManager.getGesuch()) {
            return this.gesuchModelManager.getGesuch().gesperrtWegenBeschwerde === true;
        }
        return false;
    }

    public isSuperAdmin(): boolean {
        return  this.authServiceRS.isRole(TSRole.SUPER_ADMIN);
    }
}
