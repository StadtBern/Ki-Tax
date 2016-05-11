import {IComponentOptions, IFormController} from 'angular';
import {IKindStateParams} from '../../gesuch.route';
import {IStateService} from 'angular-ui-router';
import GesuchModelManager from '../../service/gesuchModelManager';
import TSKind from '../../../models/TSKind';
import {EnumEx} from '../../../utils/EnumEx';
import {TSGeschlecht} from '../../../models/enums/TSGeschlecht';
import AbstractGesuchViewController from '../abstractGesuchView';
import {TSPensumFachstelle} from '../../../models/TSPensumFachstelle';
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

    static $inject: string[] = ['$stateParams', '$state', 'GesuchModelManager', 'CONSTANTS', '$scope'];
    /* @ngInject */
    /* @ngInject */
    constructor($stateParams: IKindStateParams, state: IStateService, gesuchModelManager: GesuchModelManager, private CONSTANTS: any, private $scope: any) {
        super(state, gesuchModelManager);
        this.gesuchModelManager.setKindNumber(parseInt($stateParams.kindNumber, 10));
        this.initViewModel();

        //Wenn die Maske KindView verlassen wird, werden automatisch die Kinder entfernt, die noch nicht in der DB gespeichert wurden
        $scope.$on('$stateChangeStart', () => {
            console.log('stateChangeStart');
            this.removeKindFromList();
        });
    }

    private initViewModel(): void {
        this.geschlechter = EnumEx.getNames(TSGeschlecht);
        this.showFachstelle = (this.gesuchModelManager.getKindToWorkWith().kindGS.pensumFachstelle) ? true : false;
        if (this.getPensumFachstelle() && this.getPensumFachstelle().fachstelle) {
            this.fachstelleId = this.getPensumFachstelle().fachstelle.id;
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
        this.removeKindFromList();
        this.state.go('gesuch.kinder');
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
            this.resetFachstelleFields();
        }
    }

    private resetFachstelleFields() {
        this.fachstelleId = undefined;
        this.getModel().pensumFachstelle = undefined;
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

