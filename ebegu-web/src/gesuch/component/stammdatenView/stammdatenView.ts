import EbeguRestUtil from '../../../utils/EbeguRestUtil';
import {EnumEx} from '../../../utils/EnumEx';
import {IComponentOptions, IFormController} from 'angular';
import {IStateService} from 'angular-ui-router';
import AbstractGesuchViewController from '../abstractGesuchView';
import {TSGeschlecht} from '../../../models/enums/TSGeschlecht';
import {IStammdatenStateParams} from '../../gesuch.route';
import './stammdatenView.less';
import GesuchForm from '../../service/gesuchForm';
let template = require('./stammdatenView.html');

export class StammdatenViewComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {};
    template = template;
    controller = StammdatenViewController;
    controllerAs = 'vm';
}


export class StammdatenViewController extends AbstractGesuchViewController {
    gesuchForm: GesuchForm;
    geschlechter: Array<string>;
    showUmzug: boolean;
    showKorrespondadr: boolean;
    ebeguRestUtil: EbeguRestUtil;
    phonePattern: string;

   /* 'dv-stammdaten-view gesuchsteller="vm.aktuellerGesuchsteller" on-upate="vm.updateGesuchsteller(key)">'
    this.onUpdate({key: data})*/

    static $inject = ['$stateParams', '$state', 'EbeguRestUtil', 'GesuchForm'];
    /* @ngInject */
    constructor($stateParams: IStammdatenStateParams, $state: IStateService, ebeguRestUtil: EbeguRestUtil,
                gesuchForm: GesuchForm) {
        super($state);
        this.gesuchForm = gesuchForm;
        this.ebeguRestUtil = ebeguRestUtil;
        let parsedNum: number = parseInt($stateParams.gesuchstellerNumber, 10);
        this.gesuchForm.setGesuchstellerNumber(parsedNum);
        this.initViewmodel();
        this.phonePattern = '(0|\\+41|0041)\\s?([\\d]{2})\\s?([\\d]{3})\\s?([\\d]{2})\\s?([\\d]{2})';
    }

    private initViewmodel() {
        this.gesuchForm.initStammdaten();
        this.geschlechter = EnumEx.getNames(TSGeschlecht);
        this.showUmzug = (this.gesuchForm.getStammdatenToWorkWith().umzugAdresse) ? true : false;
        this.showKorrespondadr = (this.gesuchForm.getStammdatenToWorkWith().korrespondenzAdresse) ? true : false;
    }

    submit(form: IFormController) {
        if (form.$valid) {
            //do all things
            //this.state.go("next.step"); //go to the next step
            if (!this.showUmzug) {
                this.gesuchForm.setUmzugAdresse(this.showUmzug);
            }
            if (!this.showKorrespondadr) {
                this.gesuchForm.setKorrespondenzAdresse(this.showKorrespondadr);
            }

            this.gesuchForm.updateGesuchsteller().then((personResponse: any) => {
                this.nextStep();
            });
        }
    }

    umzugadreseClicked() {
        this.gesuchForm.setUmzugAdresse(this.showUmzug);
    }

    korrespondenzAdrClicked() {
        this.gesuchForm.setKorrespondenzAdresse(this.showKorrespondadr);
    }

    resetForm() {
        this.gesuchForm.initStammdaten();
        this.initViewmodel();
    }

    previousStep() {
        if ((this.gesuchForm.gesuchstellerNumber === 2)) {
            this.state.go('gesuch.stammdaten', {gesuchstellerNumber: '1'});
        } else {
            this.state.go('gesuch.familiensituation');
        }

    }

    nextStep() {
        if ((this.gesuchForm.gesuchstellerNumber === 1) && this.gesuchForm.isGesuchsteller2Required()) {
            this.state.go('gesuch.stammdaten', {gesuchstellerNumber: '2'});
        } else {
            this.state.go('gesuch.kinder');
        }
    }

}
