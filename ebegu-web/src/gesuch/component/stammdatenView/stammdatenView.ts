import TSPerson from '../../../models/TSPerson';
import TSAdresse from '../../../models/TSAdresse';
import TSGesuch from '../../../models/TSGesuch';
import PersonRS from '../../../core/service/personRS.rest';
import GesuchRS from '../../service/gesuchRS.rest';
import {TSAdressetyp} from '../../../models/enums/TSAdressetyp';
import EbeguRestUtil from '../../../utils/EbeguRestUtil';
import {EnumEx} from '../../../utils/EnumEx';
import {IComponentOptions, IFormController} from 'angular';
import {IStateService} from 'angular-ui-router';
import AbstractGesuchViewController from '../abstractGesuchView';
import {TSGeschlecht} from '../../../models/enums/TSGeschlecht';
import {IStammdatenStateParams} from '../../gesuch.route';
import * as template from './stammdatenView.html';
import './stammdatenView.less';
import GesuchForm from '../../service/gesuchForm';

export class StammdatenViewComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {};
    template = template;
    controller = StammdatenViewController;
    controllerAs = 'vm';
}


export class StammdatenViewController extends AbstractGesuchViewController {
    gesuchRS: GesuchRS;
    gesuchForm: GesuchForm;
    geschlechter:Array<string>;
    showUmzug:boolean;
    showKorrespondadr:boolean;
    personRS: PersonRS;
    ebeguRestUtil: EbeguRestUtil;
    gesuchstellerNumber: number;

    static $inject = ['$stateParams', 'personRS', '$state','ebeguRestUtil', 'gesuchRS', 'gesuchForm'];
    /* @ngInject */
    constructor($stateParams: IStammdatenStateParams, _personRS_: PersonRS, $state:IStateService, ebeguRestUtil: EbeguRestUtil,
                gesuchRS: GesuchRS, gesuchForm: GesuchForm) {
        super($state);
        this.gesuchForm = gesuchForm;
        this.gesuchRS = gesuchRS;
        this.personRS = _personRS_;
        this.ebeguRestUtil = ebeguRestUtil;
        this.setGesuchstellerNumber($stateParams.gesuchstellerNumber);
        this.initViewmodel();
    }

    private initViewmodel() {
        this.setStammdatenToWorkWith(new TSPerson());
        let wohnAdr = new TSAdresse();
        wohnAdr.adresseTyp = TSAdressetyp.WOHNADRESSE;
        this.getStammdatenToWorkWith().adresse = wohnAdr;
        this.getStammdatenToWorkWith().umzugAdresse = undefined;
        this.getStammdatenToWorkWith().korrespondenzAdresse = undefined;
        this.geschlechter = EnumEx.getNames(TSGeschlecht);
        this.showUmzug = false;
        this.showKorrespondadr = false;
    }

    private setGesuchstellerNumber(gsNumber: number) {
        //todo team ueberlegen ob es by default 1 sein muss oder ob man irgendeinen Fehler zeigen soll
        if (gsNumber == 1 || gsNumber == 2) {
            this.gesuchstellerNumber = gsNumber;
        }
        else {
            this.gesuchstellerNumber = 1;
        }
    }

    submit(form:angular.IFormController) {
        if (form.$valid) {
            //do all things
            //this.state.go("next.step"); //go to the next step
            if (!this.showUmzug) {
                this.getStammdatenToWorkWith().umzugAdresse = undefined;
            }
            if (!this.showKorrespondadr) {
                this.getStammdatenToWorkWith().korrespondenzAdresse = undefined;
            }

            this.gesuchRS.update(this.gesuchForm.gesuch).then((gesuchResponse: any) => {
                this.gesuchForm.gesuch = gesuchResponse.data;
                this.nextStep();
            });

        }
    }


    umzugadreseClicked() {
        if (this.showUmzug) {
            this.getStammdatenToWorkWith().umzugAdresse = this.initUmzugadresse();
        } else {
            this.getStammdatenToWorkWith().umzugAdresse = undefined;
        }
    }

    private initUmzugadresse() {
        let umzugAdr = new TSAdresse();
        umzugAdr.showDatumVon = true;
        umzugAdr.adresseTyp = TSAdressetyp.WOHNADRESSE;
        return umzugAdr;
    }

    private  initKorrespondenzAdresse():TSAdresse {
        let korrAdr = new TSAdresse();
        korrAdr.showDatumVon = false;
        korrAdr.adresseTyp = TSAdressetyp.KORRESPONDENZADRESSE;
        return korrAdr;
    }

    korrespondenzAdrClicked() {
        if (this.showKorrespondadr) {
            this.getStammdatenToWorkWith().korrespondenzAdresse = this.initKorrespondenzAdresse();
        } else {
            this.getStammdatenToWorkWith().korrespondenzAdresse = undefined;
        }
    }

    resetForm() {
        this.setStammdatenToWorkWith(undefined);
        this.initViewmodel();
    }

    previousStep() {
        this.state.go("gesuch.familiensituation");
    }

    nextStep() {
        if((this.gesuchstellerNumber == 1) && this.gesuchForm.isGesuchsteller2Required()) {
            this.state.go("gesuch.stammdaten", {gesuchstellerNumber:2});
        }
        else {
            this.state.go("gesuch.kinder");
        }
    }

    private getStammdatenToWorkWith():TSPerson {
        if(this.gesuchstellerNumber == 1) {
            return this.gesuchForm.gesuch.gesuchsteller1;
        }
        else {
            return this.gesuchForm.gesuch.gesuchsteller2;
        }
    }

    private setStammdatenToWorkWith(stammdaten: TSPerson):void {
        if(this.gesuchstellerNumber == 1) {
            this.gesuchForm.gesuch.gesuchsteller1 = stammdaten;
        }
        else {
            this.gesuchForm.gesuch.gesuchsteller2 = stammdaten;
        }
    }

}
