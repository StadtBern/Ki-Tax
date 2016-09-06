import AbstractGesuchViewController from './component/abstractGesuchView';
import GesuchModelManager from './service/gesuchModelManager';
import BerechnungsManager from './service/berechnungsManager';
import DateUtil from '../utils/DateUtil';
import WizardStepManager from './service/wizardStepManager';
import {TSWizardStepName} from '../models/enums/TSWizardStepName';
import {TSWizardStepStatus} from '../models/enums/TSWizardStepStatus';

export class GesuchRouteController extends AbstractGesuchViewController {


    static $inject: string[] = ['GesuchModelManager', 'BerechnungsManager', '$scope', 'WizardStepManager'];
    /* @ngInject */
    constructor(gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager, $scope: any,
                wizardStepManager: WizardStepManager) {
        super(gesuchModelManager, berechnungsManager, wizardStepManager);
    }

    showFinanzsituationStart(): boolean {
        return !!this.gesuchModelManager.isGesuchsteller2Required();
    }


    public getDateErstgesuch(): string {
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

}
