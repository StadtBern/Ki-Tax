import {IComponentOptions, IFormController} from 'angular';
import {IKindStateParams} from '../../gesuch.route';
import {IStateService} from 'angular-ui-router';
import GesuchModelManager from '../../service/gesuchModelManager';
import TSKind from '../../../models/TSKind';
import {EnumEx} from '../../../utils/EnumEx';
import {TSGeschlecht} from '../../../models/enums/TSGeschlecht';
import AbstractGesuchViewController from '../abstractGesuchView';
let template = require('./kindView.html');

export class KindViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = KindViewController;
    controllerAs = 'vm';
}

export class KindViewController extends AbstractGesuchViewController {
    geschlechter: Array<string>;
    showFachstelle: boolean;
    fachstelleId: string; //der ausgewaehlte fachstelleId wird hier gespeichert und dann in die entsprechende Fachstelle umgewandert

    static $inject: string[] = ['$stateParams', '$state', 'GesuchModelManager', 'CONSTANTS'];
    /* @ngInject */
    /* @ngInject */
    constructor($stateParams: IKindStateParams, state: IStateService, gesuchModelManager: GesuchModelManager, private CONSTANTS: any) {
        super(state, gesuchModelManager);
        this.gesuchModelManager.setKindNumber(parseInt($stateParams.kindNumber, 10));
        this.initViewModel();
    }

    private initViewModel(): void {
        this.geschlechter = EnumEx.getNames(TSGeschlecht);
        this.showFachstelle = (this.gesuchModelManager.getKindToWorkWith().kindGS.fachstelle) ? true : false;
        if (this.getModel().fachstelle) {
            this.fachstelleId = this.getModel().fachstelle.id;
        }
    }

    submit(form: IFormController) {
        if (form.$valid) {
            this.gesuchModelManager.updateKind().then((kindResponse: any) => {
                this.state.go('gesuch.kinder');
            });
        }
    }

    cancel() {
        if (!this.gesuchModelManager.getKindToWorkWith().timestampErstellt) {
            //wenn das Kind noch nicht erstellt wurde, löschen wir das Kind vom Array
            this.gesuchModelManager.removeKindFromList();
        }
        this.state.go('gesuch.kinder');
    }

    public setSelectedFachsstelle() {
        let fachstellenList = this.getFachstellenList();
        for (let i: number = 0; i < fachstellenList.length; i++) {
            if (fachstellenList[i].id === this.fachstelleId) {
                this.getModel().fachstelle = fachstellenList[i];
            }
        }
    }

    public showFachstelleClicked() {
        if (!this.showFachstelle) {
            this.resetFachstelleFields();
        }
    }

    public familienErgaenzendeBetreuungClicked() {
        if (!this.getModel().familienErgaenzendeBetreuung) {
            this.showFachstelle = false;
            this.resetFachstelleFields();
        }
    }

    private resetFachstelleFields() {
        this.fachstelleId = undefined;
        this.getModel().fachstelle = undefined;
        this.getModel().betreuungspensumFachstelle = undefined;
    }

    public getFachstellenList() {
        return this.gesuchModelManager.fachstellenList;
    }

    public getModel(): TSKind {
        //todo beim richtiges Kind zurueckliefern
        if (this.gesuchModelManager.getKindToWorkWith()) {
            return this.gesuchModelManager.getKindToWorkWith().kindGS;
        }
        return undefined;
    }
}

