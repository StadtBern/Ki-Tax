import AbstractGesuchViewController from './component/abstractGesuchView';
import GesuchModelManager from './service/gesuchModelManager';
import BerechnungsManager from './service/berechnungsManager';
import DateUtil from '../utils/DateUtil';
import WizardStepManager from './service/wizardStepManager';
import {TSWizardStepName} from '../models/enums/TSWizardStepName';
import {TSWizardStepStatus} from '../models/enums/TSWizardStepStatus';
import EbeguUtil from '../utils/EbeguUtil';
import {TSAntragStatus} from '../models/enums/TSAntragStatus';
import AntragStatusHistoryRS from '../core/service/antragStatusHistoryRS.rest';
import TSGesuch from '../models/TSGesuch';
import TSUser from '../models/TSUser';
import ITranslateService = angular.translate.ITranslateService;

export class GesuchRouteController extends AbstractGesuchViewController {

    static $inject: string[] = ['GesuchModelManager', 'BerechnungsManager', 'WizardStepManager', 'EbeguUtil',
        'AntragStatusHistoryRS', '$translate'];
    /* @ngInject */
    constructor(gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager,
                wizardStepManager: WizardStepManager, private ebeguUtil: EbeguUtil,
                private antragStatusHistoryRS: AntragStatusHistoryRS, private $translate: ITranslateService) {
        super(gesuchModelManager, berechnungsManager, wizardStepManager);
        this.antragStatusHistoryRS.loadLastStatusChange(this.gesuchModelManager.getGesuch());
    }

    showFinanzsituationStart(): boolean {
        return !!this.gesuchModelManager.isGesuchsteller2Required();
    }


    public getDateFromGesuch(): string {
        if (this.gesuchModelManager && this.gesuchModelManager.getGesuch()) {
            return DateUtil.momentToLocalDateFormat(this.gesuchModelManager.getGesuch().eingangsdatum, 'DD.MM.YYYY');
        }
        return undefined;
    }

    public getIcon(stepName: TSWizardStepName): string {
        var step = this.wizardStepManager.getStepByName(stepName);
        if (step) {
            let status = step.wizardStepStatus;
            if (status === TSWizardStepStatus.OK) {
                return 'fa fa-check green';
            } else if (status === TSWizardStepStatus.NOK) {
                return 'fa fa-close red';
            } else if (status === TSWizardStepStatus.IN_BEARBEITUNG) {
                if (step.wizardStepName === TSWizardStepName.DOKUMENTE) { // Dokumenten haben kein Icon wenn nicht alle hochgeladen wurden
                    return '';
                }
                return 'fa fa-pencil';
            } else if (status === TSWizardStepStatus.PLATZBESTAETIGUNG || status === TSWizardStepStatus.WARTEN) {
                return 'fa fa-hourglass orange';
            } else if (status === TSWizardStepStatus.UNBESUCHT) {
                return '';
            }
        }
        return '';
    }

    /**
     * Steps are only disabled when the field verfuegbar is false
     * @param stepName
     * @returns {boolean} Sollte etwas schief gehen, true wird zurueckgegeben
     */
    public isWizardStepDisabled(stepName: TSWizardStepName): boolean {
        var step = this.wizardStepManager.getStepByName(stepName);
        if (step) {
            return (step.verfuegbar === false);
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
            if (this.gesuchModelManager.isGesuchSaved()) {
                return this.$translate.instant('MENU_ERSTGESUCH_VOM', {
                    date: this.getDateFromGesuch()
                });
            } else {
                return this.$translate.instant('MENU_ERSTGESUCH');
            }
        } else {
            if (this.gesuchModelManager.isGesuchSaved()) {
                return this.$translate.instant('MENU_MUTATION_VOM', {
                    date: this.getDateFromGesuch()
                });
            } else {
                return this.$translate.instant('MENU_MUTATION');
            }
        }
    }

}
