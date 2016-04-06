import TSPerson from '../../../models/TSPerson';
import TSAdresse from '../../../models/TSAdresse';
import {PersonRS} from '../../../core/service/personRS.rest';
import {TSAdressetyp} from '../../../models/enums/TSAdressetyp';
import EbeguRestUtil from '../../../utils/EbeguRestUtil';
import {EnumEx} from '../../../utils/EnumEx';
import {IComponentOptions, IFormController} from 'angular';
import {IStateService} from 'angular-ui-router';
import AbstractGesuchViewController from '../abstractGesuchView';
import {TSGeschlecht} from '../../../models/enums/TSGeschlecht';

class StammdatenViewComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {};
    templateUrl = 'src/gesuch/component/stammdatenView/stammdatenView.html';
    controller = StammdatenViewController;
    controllerAs = 'vm';
}

class StammdatenViewController extends AbstractGesuchViewController {
    static $inject = ['personRS', '$state', 'ebeguRestUtil'];

    stammdaten: TSPerson;
    geschlechter: string[];
    showUmzug: boolean;
    showKorrespondadr: boolean;
    personRS: PersonRS;
    ebeguRestUtil: EbeguRestUtil;

    /* @ngInject */
    constructor(personRS: PersonRS, $state: IStateService, ebeguRestUtil: EbeguRestUtil) {
        super($state);
        this.initViewmodel();
        this.personRS = personRS;
        this.ebeguRestUtil = ebeguRestUtil;
    }

    public submit(form: IFormController) {
        if (form.$valid) {
            //do all things
            //this.state.go("next.step"); //go to the next step
            if (!this.showUmzug) {
                this.stammdaten.umzugAdresse = undefined;
            }
            if (!this.showKorrespondadr) {
                this.stammdaten.korrespondenzAdresse = undefined;
            }
            if (!this.stammdaten.timestampErstellt) {
                //es handel sich um eine neue Person
                this.personRS.create(this.stammdaten).then((response) => {
                        this.stammdaten = this.ebeguRestUtil.parsePerson(new TSPerson(), response.data);
                    }
                );

            } else {
                //update
                this.personRS.update(this.stammdaten).then((response) => {
                        this.stammdaten = this.ebeguRestUtil.parsePerson(new TSPerson(), response.data);
                    }
                );
            }
        }
    }

    public umzugadreseClicked() {
        if (this.showUmzug) {
            this.stammdaten.umzugAdresse = this.initUmzugadresse();
        } else {
            this.stammdaten.umzugAdresse = undefined;
        }
    }

    public korrespondenzAdrClicked() {
        if (this.showKorrespondadr) {
            var korrAdr = this.initKorrespondenzAdresse();
            this.stammdaten.korrespondenzAdresse = korrAdr;
        } else {
            this.stammdaten.korrespondenzAdresse = undefined;
        }
    }

    public resetForm() {
        this.stammdaten = undefined;
        this.initViewmodel();
    }

    public previousStep() {
        this.state.go('gesuch.familiensituation');
    }

    private initViewmodel() {
        this.stammdaten = new TSPerson();
        let wohnAdr = new TSAdresse();
        wohnAdr.adresseTyp = TSAdressetyp.WOHNADRESSE;
        this.stammdaten.adresse = wohnAdr;
        this.stammdaten.umzugAdresse = undefined;
        this.stammdaten.korrespondenzAdresse = undefined;
        this.geschlechter = EnumEx.getNames(TSGeschlecht);
        this.showUmzug = false;
        this.showKorrespondadr = false;
    }

    private initUmzugadresse() {
        let umzugAdr = new TSAdresse();
        umzugAdr.showDatumVon = true;
        umzugAdr.adresseTyp = TSAdressetyp.WOHNADRESSE;
        return umzugAdr;
    }

    private  initKorrespondenzAdresse(): TSAdresse {
        let korrAdr = new TSAdresse();
        korrAdr.showDatumVon = false;
        korrAdr.adresseTyp = TSAdressetyp.KORRESPONDENZADRESSE;
        return korrAdr;
    }
}

angular.module('ebeguWeb.gesuch').component('stammdatenView', new StammdatenViewComponentConfig());
