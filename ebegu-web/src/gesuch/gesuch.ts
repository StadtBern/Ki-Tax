import AbstractGesuchViewController from './component/abstractGesuchView';
import {IStateService} from 'angular-ui-router';
import GesuchModelManager from './service/gesuchModelManager';
import BerechnungsManager from './service/berechnungsManager';
import DateUtil from '../utils/DateUtil';
import WizardStepManager from './service/wizardStepManager';
import {TSWizardStepName} from '../models/enums/TSWizardStepName';
import {TSWizardStepStatus} from '../models/enums/TSWizardStepStatus';

export class GesuchRouteController extends AbstractGesuchViewController {


    static $inject: string[] = ['$state', 'GesuchModelManager', 'BerechnungsManager', '$scope', 'WizardStepManager'];
    /* @ngInject */
    constructor(state: IStateService, gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager, $scope: any,
                public wizardStepManager: WizardStepManager) {
        super(state, gesuchModelManager, berechnungsManager);
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
                return 'fa fa-pencil';
            } else if (status === TSWizardStepStatus.PLATZBESTAETIGUNG) {
                return 'fa fa-hourglass orange';
            } else if (status === TSWizardStepStatus.UNBESUCHT) {
                return '';
            }
        }
        return '';
    }

    /**
     * Steps are only disabled when the status is UNBESUCHT
     * @param stepName
     * @returns {boolean} Sollte etwas schief gehen, true wird zurueckgegeben
     */
    public isWizardStepDisabled(stepName: TSWizardStepName): boolean {
        var step = this.wizardStepManager.getStepByName(stepName);
        if (step) {
            return step.wizardStepStatus === TSWizardStepStatus.UNBESUCHT;
        }
        return true;
    }

}
