import GesuchModelManager from '../service/gesuchModelManager';
import BerechnungsManager from '../service/berechnungsManager';
import {TSRole} from '../../models/enums/TSRole';
import {TSRoleUtil} from '../../utils/TSRoleUtil';
import WizardStepManager from '../service/wizardStepManager';
import {TSAntragStatus} from '../../models/enums/TSAntragStatus';
import {TSBetreuungsstatus} from '../../models/enums/TSBetreuungsstatus';
import IPromise = angular.IPromise;

export default class AbstractGesuchViewController {

    gesuchModelManager: GesuchModelManager;
    berechnungsManager: BerechnungsManager;
    wizardStepManager: WizardStepManager;
    TSRole: any;
    TSRoleUtil: any;

    constructor($gesuchModelManager: GesuchModelManager, $berechnungsManager: BerechnungsManager,
                wizardStepManager: WizardStepManager) {
        this.gesuchModelManager = $gesuchModelManager;
        this.berechnungsManager = $berechnungsManager;
        this.wizardStepManager = wizardStepManager;
        this.TSRole = TSRole;
        this.TSRoleUtil = TSRoleUtil;
    }

    public isGesuchStatusVerfuegenVerfuegt(): boolean {
        return this.gesuchModelManager.isGesuchStatusVerfuegenVerfuegt();
    }

    public getGesuchId(): string {
        if (this.gesuchModelManager && this.gesuchModelManager.getGesuch()) {
            return this.gesuchModelManager.getGesuch().id;
        } else {
            return '';
        }
    }

    public setGesuchStatus(status: TSAntragStatus): IPromise<TSAntragStatus> {
        if (this.gesuchModelManager) {
            return this.gesuchModelManager.saveGesuchStatus(status);
        }
        return undefined;
    }

    public isGesuchInStatus(status : TSAntragStatus): boolean {
        return status === this.gesuchModelManager.getGesuch().status;
    }

    public isBetreuungInStatus(status: TSBetreuungsstatus): boolean {
        if (this.gesuchModelManager.getBetreuungToWorkWith()) {
            return status === this.gesuchModelManager.getBetreuungToWorkWith().betreuungsstatus;
        }
        return false;
    }

    public isMutation(): boolean {
        if (this.gesuchModelManager.getGesuch()) {
            return this.gesuchModelManager.getGesuch().isMutation();
        }
        return false;
    }
}
