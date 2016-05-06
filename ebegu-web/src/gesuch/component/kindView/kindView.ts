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

    static $inject: string[] = ['$stateParams', '$state', 'GesuchModelManager'];
    /* @ngInject */
    /* @ngInject */
    constructor($stateParams: IKindStateParams, state: IStateService, private gesuchModelManager: GesuchModelManager) {
        super(state);
        this.gesuchModelManager.setKindNumber(parseInt($stateParams.kindNumber, 10));
        this.initViewModel();
    }

    private initViewModel(): void {
        this.geschlechter = EnumEx.getNames(TSGeschlecht);
        this.showFachstelle = (this.gesuchModelManager.getKindToWorkWith().kindGS.fachstelle) ? true : false;
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

    public showFachstelleClicked() {
    }

    public getModel(): TSKind {
        //todo beim richtiges Kind zurueckliefern
        if (this.gesuchModelManager.getKindToWorkWith()) {
            return this.gesuchModelManager.getKindToWorkWith().kindGS;
        }
        return undefined;
    }
}

