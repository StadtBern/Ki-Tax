import {IComponentOptions} from 'angular';
import {IKindStateParams} from '../../gesuch.route';
import GesuchModelManager from '../../service/gesuchModelManager';
import TSKind from '../../../models/TSKind';
import {EnumEx} from '../../../utils/EnumEx';
import {TSGeschlecht} from '../../../models/enums/TSGeschlecht';
import AbstractGesuchViewController from '../abstractGesuchView';
import {TSPensumFachstelle} from '../../../models/TSPensumFachstelle';
import BerechnungsManager from '../../service/berechnungsManager';
import TSKindContainer from '../../../models/TSKindContainer';
import {TSKinderabzug, getTSKinderabzugValues} from '../../../models/enums/TSKinderabzug';
import ErrorService from '../../../core/errors/service/ErrorService';
import WizardStepManager from '../../service/wizardStepManager';
import {TSRole} from '../../../models/enums/TSRole';
import DateUtil from '../../../utils/DateUtil';
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
import IPromise = angular.IPromise;
import IQService = angular.IQService;
import ITranslateService = angular.translate.ITranslateService;
import IScope = angular.IScope;


let template = require('./kindView.html');
require('./kindView.less');

export class KindViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = KindViewController;
    controllerAs = 'vm';
}

export class KindViewController extends AbstractGesuchViewController<TSKindContainer> {
    geschlechter: Array<string>;
    kinderabzugValues: Array<TSKinderabzug>;
    showFachstelle: boolean;
    showFachstelleGS: boolean;
    fachstelleId: string; //der ausgewaehlte fachstelleId wird hier gespeichert und dann in die entsprechende Fachstelle umgewandert
    allowedRoles: Array<TSRole>;
    kindIndex : number;

    static $inject: string[] = ['$stateParams', 'GesuchModelManager', 'BerechnungsManager', 'CONSTANTS', '$scope',
        'ErrorService', 'WizardStepManager', '$q', '$translate'];
    /* @ngInject */
    constructor($stateParams: IKindStateParams, gesuchModelManager: GesuchModelManager,
                berechnungsManager: BerechnungsManager, private CONSTANTS: any, $scope: IScope, private errorService: ErrorService,
                wizardStepManager: WizardStepManager, private $q: IQService, private $translate: ITranslateService) {
        super(gesuchModelManager, berechnungsManager, wizardStepManager, $scope, TSWizardStepName.KINDER);
        if ($stateParams.kindNumber) {
            this.kindIndex = this.gesuchModelManager.convertKindNumberToKindIndex(parseInt($stateParams.kindNumber));
            if (this.kindIndex >= 0) {
                this.model = angular.copy(this.gesuchModelManager.getGesuch().kindContainers[this.kindIndex]);
                this.gesuchModelManager.setKindIndex(this.kindIndex);
            }
        } else {
            //wenn kind nummer nicht definiert ist heisst dass, das wir ein neues erstellen sollten
            this.model = this.initEmptyKind(undefined);
            this.kindIndex  = this.gesuchModelManager.getGesuch().kindContainers ? this.gesuchModelManager.getGesuch().kindContainers.length : 0;
            this.gesuchModelManager.setKindIndex(this.kindIndex);
        }
        this.initViewModel();
        this.allowedRoles = this.TSRoleUtil.getAllRolesButTraegerschaftInstitution();
    }

    private initViewModel(): void {
        this.geschlechter = EnumEx.getNames(TSGeschlecht);
        this.kinderabzugValues = getTSKinderabzugValues();
        this.showFachstelle = (this.model.kindJA.pensumFachstelle) ? true : false;
        this.showFachstelleGS = (this.model.kindGS && this.model.kindGS.pensumFachstelle) ? true : false;
        if (this.getPensumFachstelle() && this.getPensumFachstelle().fachstelle) {
            this.fachstelleId = this.getPensumFachstelle().fachstelle.id;
        }
        if (!this.gesuchModelManager.getFachstellenList() || this.gesuchModelManager.getFachstellenList().length <= 0) {
            this.gesuchModelManager.updateFachstellenList();
        }
    }

    save(): IPromise<TSKindContainer> {
        if (this.isGesuchValid()) {
            if (!this.form.$dirty) {
                // If there are no changes in form we don't need anything to update on Server and we could return the
                // promise immediately
                return this.$q.when(this.model);
            }

            this.errorService.clearAll();
            return this.gesuchModelManager.saveKind(this.model);
        }
        return undefined;
    }

    cancel() {
        this.reset();
        this.form.$setPristine();
    }

    reset() {
        this.removeKindFromList();
    }

    private removeKindFromList() {
        if (!this.model.timestampErstellt) {
            //wenn das Kind noch nicht erstellt wurde, löschen wir das Kind vom Array
            this.gesuchModelManager.removeKindFromList();
        }
    }

    public setSelectedFachsstelle() {
        let fachstellenList = this.getFachstellenList();
        for (let i: number = 0; i < fachstellenList.length; i++) {
            if (fachstellenList[i].id === this.fachstelleId) {
                this.getModel().pensumFachstelle.fachstelle = fachstellenList[i];
            }
        }
    }

    public showFachstelleClicked() {
        if (!this.showFachstelle) {
            this.resetFachstelleFields();
        } else {
            this.getModel().pensumFachstelle = new TSPensumFachstelle();
        }
    }

    public familienErgaenzendeBetreuungClicked() {
        if (!this.getModel().familienErgaenzendeBetreuung) {
            this.showFachstelle = false;
            this.getModel().wohnhaftImGleichenHaushalt = undefined;
            this.resetFachstelleFields();
        }
    }

    private resetFachstelleFields() {
        this.fachstelleId = undefined;
        this.getModel().pensumFachstelle = undefined;
    }

    public getFachstellenList() {
        return this.gesuchModelManager.getFachstellenList();
    }

    public getModel(): TSKind {
        if (this.model) {
            return this.model.kindJA;
        }
        return undefined;
    }

    public getContainer(): TSKindContainer {
        if (this.model) {
            return this.model;
        }
        return undefined;
    }

    public getPensumFachstelle(): TSPensumFachstelle {
        if (this.getModel()) {
            return this.getModel().pensumFachstelle;
        }
        return undefined;
    }

    public isFachstelleRequired(): boolean {
        return this.getModel() && this.getModel().familienErgaenzendeBetreuung && this.showFachstelle;
    }

    public getDatumEinschulung(): moment.Moment {
        return this.gesuchModelManager.getGesuchsperiodeBegin();
    }

    public getTextFachstelleKorrekturJA() : string {
        if (this.getContainer().kindGS && this.getContainer().kindGS.pensumFachstelle) {
            let fachstelle : TSPensumFachstelle = this.getContainer().kindGS.pensumFachstelle;
            let vonText = DateUtil.momentToLocalDateFormat(fachstelle.gueltigkeit.gueltigAb, 'DD.MM.YYYY');
            let bisText = fachstelle.gueltigkeit.gueltigBis ? DateUtil.momentToLocalDateFormat(fachstelle.gueltigkeit.gueltigBis, 'DD.MM.YYYY') : '31.12.9999';
            return this.$translate.instant('JA_KORREKTUR_FACHSTELLE', {
                name: fachstelle.fachstelle.name,
                pensum: fachstelle.pensum,
                von: vonText,
                bis: bisText});
        } else {
            return this.$translate.instant('LABEL_KEINE_ANGABE');
        }
    }

    private initEmptyKind(kindNumber: number) : TSKindContainer {
        let tsKindContainer = new TSKindContainer(undefined, new TSKind());
        tsKindContainer.kindNummer = kindNumber;
        return tsKindContainer;
    }
}

