import GesuchModelManager from '../service/gesuchModelManager';
import BerechnungsManager from '../service/berechnungsManager';
import {TSRole} from '../../models/enums/TSRole';
import {TSRoleUtil} from '../../utils/TSRoleUtil';
import WizardStepManager from '../service/wizardStepManager';
import {IGesuchStateParams} from '../gesuch.route';

export default class AbstractGesuchViewController {

    gesuchModelManager: GesuchModelManager;
    berechnungsManager: BerechnungsManager;
    wizardStepManager: WizardStepManager;
    TSRole: any;
    TSRoleUtil: any;
    stateParams: IGesuchStateParams;

    constructor($gesuchModelManager: GesuchModelManager, $berechnungsManager: BerechnungsManager,
                wizardStepManager: WizardStepManager, $stateParams?: IGesuchStateParams) {
        this.gesuchModelManager = $gesuchModelManager;
        this.berechnungsManager = $berechnungsManager;
        this.wizardStepManager = wizardStepManager;
        this.TSRole = TSRole;
        this.TSRoleUtil = TSRoleUtil;
        this.stateParams = $stateParams;
        this.loadGesuchFromStateParams();

    }

    public isGesuchStatusVerfuegenVerfuegt(): boolean {
        return this.gesuchModelManager.isGesuchStatusVerfuegenVerfuegt();
    }

    private setGesuchId(gesuchId: string) {
        if (gesuchId) {
            if (this.gesuchModelManager.getGesuch() && this.gesuchModelManager.getGesuch().id !== gesuchId) {
                // Wenn die antrags id im GescuchModelManager nicht mit der GesuchId überreinstimmt wird das gesuch neu geladen
                this.berechnungsManager.clear();
                this.gesuchModelManager.openGesuch(gesuchId);
            }
        }
    }

    private loadGesuchFromStateParams() {
        if (this.stateParams) {
            let gesuchIdParams = this.stateParams.gesuchId;
            this.setGesuchId(gesuchIdParams);
            console.log('Navigiert auf view mit AntragId: ' + gesuchIdParams);
        }
    }


}
