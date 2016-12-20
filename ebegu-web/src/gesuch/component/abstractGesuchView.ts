import GesuchModelManager from '../service/gesuchModelManager';
import BerechnungsManager from '../service/berechnungsManager';
import {TSRole} from '../../models/enums/TSRole';
import {TSRoleUtil} from '../../utils/TSRoleUtil';
import WizardStepManager from '../service/wizardStepManager';
import {TSAntragStatus} from '../../models/enums/TSAntragStatus';
import {TSBetreuungsstatus} from '../../models/enums/TSBetreuungsstatus';
import IPromise = angular.IPromise;
import IRootScopeService = angular.IRootScopeService;
import TSExceptionReport from '../../models/TSExceptionReport';
import IFormController = angular.IFormController;
import IScope = angular.IScope;
import {TSMessageEvent} from '../../models/enums/TSErrorEvent';

export default class AbstractGesuchViewController<T> {

    $scope: IScope;
    gesuchModelManager: GesuchModelManager;
    berechnungsManager: BerechnungsManager;
    wizardStepManager: WizardStepManager;
    TSRole: any;
    TSRoleUtil: any;
    private _model: T;
    form: IFormController;

    constructor($gesuchModelManager: GesuchModelManager, $berechnungsManager: BerechnungsManager,
                wizardStepManager: WizardStepManager, $scope: IScope) {
        this.gesuchModelManager = $gesuchModelManager;
        this.berechnungsManager = $berechnungsManager;
        this.wizardStepManager = wizardStepManager;
        this.TSRole = TSRole;
        this.TSRoleUtil = TSRoleUtil;
        this.$scope = $scope;
    }

    $onInit() {
        /**
         * Grund fuer diesen Code ist:
         * Wenn der Server einen Validation-Fehler zurueckgibt, wird der DirtyPlugin nicht informiert und setzt das Form
         * auf !dirty. Dann kann der Benutzer nochmal auf Speichern klicken und die Daten werden gespeichert.
         * Damit dies nicht passiert, hoeren wir in allen Views auf diesen Event und setzen das Form auf dirty
         */
        this.$scope.$on(TSMessageEvent[TSMessageEvent.ERROR_UPDATE], (event: any, errors: Array<TSExceptionReport>) => {
            this.form.$dirty = true;
            this.form.$pristine = false;
        });
    }

    public isGesuchReadonly(): boolean {
        return this.gesuchModelManager.isGesuchReadonly();
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

    public isGesuchInStatus(status: TSAntragStatus): boolean {
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

    public isKorrekturModusJugendamt(): boolean {
        return this.gesuchModelManager.isKorrekturModusJugendamt();
    }

    get model(): T {
        return this._model;
    }

    set model(value: T) {
        this._model = value;
    }

    public extractFullNameGS2(): string {
        if (this.gesuchModelManager.getGesuch() && this.gesuchModelManager.getGesuch().gesuchsteller2) {
            return this.gesuchModelManager.getGesuch().gesuchsteller2.extractFullName();
        }
        return 'Gesuchsteller 2';
    }

    public extractFullNameGS1(): string {
        if (this.gesuchModelManager.getGesuch() && this.gesuchModelManager.getGesuch().gesuchsteller1) {
            return this.gesuchModelManager.getGesuch().gesuchsteller1.extractFullName();
        }
        return 'Gesuchsteller 1';
    }
}
