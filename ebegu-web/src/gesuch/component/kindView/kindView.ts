import {IComponentOptions, IFormController} from 'angular';
import {IKindStateParams} from '../../gesuch.route';
import {IStateService} from 'angular-ui-router';
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
let template = require('./kindView.html');
require('./kindView.less');

export class KindViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = KindViewController;
    controllerAs = 'vm';
}

export class KindViewController extends AbstractGesuchViewController {
    geschlechter: Array<string>;
    kinderabzugValues: Array<TSKinderabzug>;
    showFachstelle: boolean;
    fachstelleId: string; //der ausgewaehlte fachstelleId wird hier gespeichert und dann in die entsprechende Fachstelle umgewandert

    static $inject: string[] = ['$stateParams', '$state', 'GesuchModelManager', 'BerechnungsManager', 'CONSTANTS', '$scope',
                                'ErrorService', 'WizardStepManager'];
    /* @ngInject */
    constructor($stateParams: IKindStateParams, state: IStateService, gesuchModelManager: GesuchModelManager,
                berechnungsManager: BerechnungsManager, private CONSTANTS: any, private $scope: any, private errorService: ErrorService,
                wizardStepManager: WizardStepManager) {
        super(state, gesuchModelManager, berechnungsManager, wizardStepManager);
        this.gesuchModelManager.setKindNumber(parseInt($stateParams.kindNumber, 10));
        this.initViewModel();

        //Wir verlassen uns hier darauf, dass zuerst das Popup vom unsavedChanges Plugin kommt welches den User fragt ob er die ungesp. changes verwerfen will
        $scope.$on('$stateChangeStart', (navEvent: any, toState: any, toParams: any, fromState: any, fromParams: any) => {
            // wenn der user die changes verwerfen will koennen wir die view resetten, ansonsten machen wir nichts da wir hier bleiben
            if (navEvent.defaultPrevented !== undefined && navEvent.defaultPrevented === false) {
                //Wenn die Maske KindView verlassen wird, werden automatisch die Kinder entfernt, die noch nicht in der DB gespeichert wurden
                this.reset();
            }
        });

    }

    private initViewModel(): void {
        this.gesuchModelManager.initGesuch(false);  //wird aufgerufen um einen restorepunkt des aktullen gesuchs zu machen
        this.geschlechter = EnumEx.getNames(TSGeschlecht);
        this.kinderabzugValues = getTSKinderabzugValues();
        this.showFachstelle = (this.gesuchModelManager.getKindToWorkWith().kindJA.pensumFachstelle) ? true : false;
        if (this.getPensumFachstelle() && this.getPensumFachstelle().fachstelle) {
            this.fachstelleId = this.getPensumFachstelle().fachstelle.id;
        }
        if (this.gesuchModelManager.getFachstellenList() || this.gesuchModelManager.getFachstellenList().length <= 0) {
            this.gesuchModelManager.updateFachstellenList();
        }
    }

    save(form: IFormController) {
        if (form.$valid) {
            this.errorService.clearAll();
            this.gesuchModelManager.updateKind().then((kindResponse: any) => {
                this.state.go('gesuch.kinder');
            });
        }
    }

    cancel(form: IFormController) {
        this.reset();
        form.$setPristine();
        this.state.go('gesuch.kinder');
    }

    reset() {
        this.gesuchModelManager.restoreBackupOfPreviousGesuch();
        this.removeKindFromList();
    }

    private removeKindFromList() {
        if (!this.gesuchModelManager.getKindToWorkWith().timestampErstellt) {
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
        if (this.gesuchModelManager.getKindToWorkWith()) {
            return this.gesuchModelManager.getKindToWorkWith().kindJA;
        }
        return undefined;
    }

    public getContainer(): TSKindContainer {
        if (this.gesuchModelManager.getKindToWorkWith()) {
            return this.gesuchModelManager.getKindToWorkWith();
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
}

