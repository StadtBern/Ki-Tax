import EbeguRestUtil from '../../../utils/EbeguRestUtil';
import {EnumEx} from '../../../utils/EnumEx';
import {IComponentOptions} from 'angular';
import AbstractGesuchViewController from '../abstractGesuchView';
import {TSGeschlecht} from '../../../models/enums/TSGeschlecht';
import {IStammdatenStateParams} from '../../gesuch.route';
import './stammdatenView.less';
import GesuchModelManager from '../../service/gesuchModelManager';
import TSGesuchsteller from '../../../models/TSGesuchsteller';
import BerechnungsManager from '../../service/berechnungsManager';
import ErrorService from '../../../core/errors/service/ErrorService';
import {TSRole} from '../../../models/enums/TSRole';
import WizardStepManager from '../../service/wizardStepManager';
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
import {TSWizardStepStatus} from '../../../models/enums/TSWizardStepStatus';
let template = require('./stammdatenView.html');
require('./stammdatenView.less');

export class StammdatenViewComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {};
    template = template;
    controller = StammdatenViewController;
    controllerAs = 'vm';
}


export class StammdatenViewController extends AbstractGesuchViewController {
    geschlechter: Array<string>;
    showUmzug: boolean;
    showKorrespondadr: boolean;
    ebeguRestUtil: EbeguRestUtil;
    allowedRoles: Array<TSRole>;

    /* 'dv-stammdaten-view gesuchsteller="vm.aktuellerGesuchsteller" on-upate="vm.updateGesuchsteller(key)">'
     this.onUpdate({key: data})*/

    static $inject = ['$stateParams', 'EbeguRestUtil', 'GesuchModelManager', 'BerechnungsManager', 'ErrorService', 'WizardStepManager',
        'CONSTANTS'];
    /* @ngInject */
    constructor($stateParams: IStammdatenStateParams, ebeguRestUtil: EbeguRestUtil, gesuchModelManager: GesuchModelManager,
                berechnungsManager: BerechnungsManager, private errorService: ErrorService,
                wizardStepManager: WizardStepManager, private CONSTANTS: any) {
        super(gesuchModelManager, berechnungsManager, wizardStepManager);
        this.ebeguRestUtil = ebeguRestUtil;
        let parsedNum: number = parseInt($stateParams.gesuchstellerNumber, 10);
        this.gesuchModelManager.setGesuchstellerNumber(parsedNum);
        this.initViewmodel();
    }

    private initViewmodel() {
        this.gesuchModelManager.initStammdaten();
        this.wizardStepManager.setCurrentStep(TSWizardStepName.GESUCHSTELLER);
        this.wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.IN_BEARBEITUNG);
        this.geschlechter = EnumEx.getNames(TSGeschlecht);
        this.gesuchModelManager.calculateShowDatumFlags(this.gesuchModelManager.getStammdatenToWorkWith());
        this.showUmzug = (this.gesuchModelManager.getStammdatenToWorkWith().umzugAdresse) ? true : false;
        this.showKorrespondadr = (this.gesuchModelManager.getStammdatenToWorkWith().korrespondenzAdresse) ? true : false;
        this.allowedRoles = this.TSRoleUtil.getAllRolesButTraegerschaftInstitution();
    }

    umzugadreseClicked() {
        this.gesuchModelManager.setUmzugAdresse(this.showUmzug);
    }

    korrespondenzAdrClicked() {
        this.gesuchModelManager.setKorrespondenzAdresse(this.showKorrespondadr);
    }

    private save(form: angular.IFormController) {
        if (form.$valid) {
            if (!this.showUmzug) {
                this.gesuchModelManager.setUmzugAdresse(this.showUmzug);
            }
            if (!this.showKorrespondadr) {
                this.gesuchModelManager.setKorrespondenzAdresse(this.showKorrespondadr);
            }
            this.errorService.clearAll();
            return this.gesuchModelManager.updateGesuchsteller();
        }
        return undefined;
    }

    public getModel(): TSGesuchsteller {
        return this.gesuchModelManager.getStammdatenToWorkWith();
    }

}
