import EbeguRestUtil from '../../../utils/EbeguRestUtil';
import {EnumEx} from '../../../utils/EnumEx';
import {IComponentOptions, IFormController} from 'angular';
import {IStateService} from 'angular-ui-router';
import AbstractGesuchViewController from '../abstractGesuchView';
import {TSGeschlecht} from '../../../models/enums/TSGeschlecht';
import {IStammdatenStateParams} from '../../gesuch.route';
import './stammdatenView.less';
import GesuchModelManager from '../../service/gesuchModelManager';
let template = require('./stammdatenView.html');

export class StammdatenViewComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {};
    template = template;
    controller = StammdatenViewController;
    controllerAs = 'vm';
}


export class StammdatenViewController extends AbstractGesuchViewController {
    gesuchModelManager: GesuchModelManager;
    geschlechter: Array<string>;
    showUmzug: boolean;
    showKorrespondadr: boolean;
    ebeguRestUtil: EbeguRestUtil;
    phonePattern: string;

    /* 'dv-stammdaten-view gesuchsteller="vm.aktuellerGesuchsteller" on-upate="vm.updateGesuchsteller(key)">'
     this.onUpdate({key: data})*/

    static $inject = ['$stateParams', '$state', 'EbeguRestUtil', 'GesuchModelManager'];
    /* @ngInject */
    constructor($stateParams: IStammdatenStateParams, $state: IStateService, ebeguRestUtil: EbeguRestUtil,
                gesuchModelManager: GesuchModelManager) {
        super($state);
        this.gesuchModelManager = gesuchModelManager;
        this.ebeguRestUtil = ebeguRestUtil;
        let parsedNum: number = parseInt($stateParams.gesuchstellerNumber, 10);
        this.gesuchModelManager.setGesuchstellerNumber(parsedNum);
        this.initViewmodel();
        this.phonePattern = '(0|\\+41|0041)\\s?([\\d]{2})\\s?([\\d]{3})\\s?([\\d]{2})\\s?([\\d]{2})';
    }

    private initViewmodel() {
        this.gesuchModelManager.initStammdaten();
        this.geschlechter = EnumEx.getNames(TSGeschlecht);
        this.showUmzug = (this.gesuchModelManager.getStammdatenToWorkWith().umzugAdresse) ? true : false;
        this.showKorrespondadr = (this.gesuchModelManager.getStammdatenToWorkWith().korrespondenzAdresse) ? true : false;
    }

    submit(form: IFormController) {
        if (form.$valid) {
            //this.state.go("next.step"); //go to the next step
            if (!this.showUmzug) {
                this.gesuchModelManager.setUmzugAdresse(this.showUmzug);
            }
            if (!this.showKorrespondadr) {
                this.gesuchModelManager.setKorrespondenzAdresse(this.showKorrespondadr);
            }

            this.gesuchModelManager.updateGesuchsteller().then((personResponse: any) => {
                this.nextStep();
            });
        }
    }

    umzugadreseClicked() {
        this.gesuchModelManager.setUmzugAdresse(this.showUmzug);
    }

    korrespondenzAdrClicked() {
        this.gesuchModelManager.setKorrespondenzAdresse(this.showKorrespondadr);
    }

    resetForm() {
        this.gesuchModelManager.initStammdaten();
        this.initViewmodel();
    }

    previousStep() {
        if ((this.gesuchModelManager.gesuchstellerNumber === 2)) {
            this.state.go('gesuch.stammdaten', {gesuchstellerNumber: '1'});
        } else {
            this.state.go('gesuch.familiensituation');
        }

    }

    nextStep() {
        if ((this.gesuchModelManager.gesuchstellerNumber === 1) && this.gesuchModelManager.isGesuchsteller2Required()) {
            this.state.go('gesuch.stammdaten', {gesuchstellerNumber: '2'});
        } else {
            this.state.go('gesuch.kinder');
        }
    }

    public getModel(): TSPerson {
        return this.gesuchModelManager.getStammdatenToWorkWith();
    }

}
