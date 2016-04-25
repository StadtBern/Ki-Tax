import TSFall from '../../models/TSFall';
import TSGesuch from '../../models/TSGesuch';
import TSPerson from '../../models/TSPerson';
import TSAdresse from '../../models/TSAdresse';
import {TSAdressetyp} from '../../models/enums/TSAdressetyp';
import TSFamiliensituation from '../../models/TSFamiliensituation';
import {TSFamilienstatus} from '../../models/enums/TSFamilienstatus';
import {TSGesuchstellerKardinalitaet} from '../../models/enums/TSGesuchstellerKardinalitaet';
import FallRS from './fallRS.rest';
import GesuchRS from './gesuchRS.rest';
import PersonRS from '../../core/service/personRS.rest';
import FamiliensituationRS from './familiensituationRS.rest';
import {IPromise} from 'angular';
import EbeguRestUtil from '../../utils/EbeguRestUtil';


export default class GesuchForm {
    fall: TSFall;
    gesuch: TSGesuch;
    familiensituation: TSFamiliensituation;
    fallRS: FallRS;
    gesuchRS: GesuchRS;
    personRS: PersonRS;
    familiensituationRS: FamiliensituationRS;
    gesuchstellerNumber: number;
    ebeguRestUtil: EbeguRestUtil;

    static $inject = ['FamiliensituationRS', 'FallRS', 'GesuchRS', 'PersonRS', 'EbeguRestUtil'];
    /* @ngInject */
    constructor(familiensituationRS: FamiliensituationRS, fallRS: FallRS, gesuchRS: GesuchRS, personRS: PersonRS, ebeguRestUtil: EbeguRestUtil) {
        this.fallRS = fallRS;
        this.gesuchRS = gesuchRS;
        this.personRS = personRS;
        this.familiensituationRS = familiensituationRS;
        this.fall = new TSFall();
        this.gesuch = new TSGesuch();
        this.familiensituation = new TSFamiliensituation();
        this.ebeguRestUtil = ebeguRestUtil;
    }

    /**
     * Prueft ob der 2. Gesuchtsteller eingetragen werden muss je nach dem was in Familiensituation ausgewaehlt wurde
     * @returns {boolean} False wenn "Alleinerziehend" oder "weniger als 5 Jahre" und dazu "alleine" ausgewaehlt wurde.
     */
    public isGesuchsteller2Required(): boolean {
        if ((this.familiensituation !== null) && (this.familiensituation !== undefined)) {
            return !(((this.familiensituation.familienstatus === TSFamilienstatus.ALLEINERZIEHEND) || (this.familiensituation.familienstatus === TSFamilienstatus.WENIGER_FUENF_JAHRE))
            && (this.familiensituation.gesuchstellerKardinalitaet === TSGesuchstellerKardinalitaet.ALLEINE));
        }
        return false;
    }

    public updateFamiliensituation(): IPromise<TSFamiliensituation> {
        //testen ob aktuelles familiensituation schon gespeichert ist
        if (this.familiensituation.timestampErstellt) {
            return this.familiensituationRS.update(this.familiensituation).then((familienResponse: any) => {
                return this.familiensituation = this.ebeguRestUtil.parseFamiliensituation(this.familiensituation, familienResponse.data);
            });
        } else {
            //todo team. Fall und Gesuch sollten in ihren eigenen Services gespeichert werden
            //todo homa beim review das sollte nicht so verschachtelt sein imho ist aber nur temporaer so gedacht
            return this.fallRS.create(this.fall).then((fallResponse: any) => {
                this.fall = this.ebeguRestUtil.parseFall(this.fall, fallResponse.data);
                this.gesuch.fall = angular.copy(this.fall);
                return this.gesuchRS.create(this.gesuch).then((gesuchResponse: any) => {
                    this.gesuch = this.ebeguRestUtil.parseGesuch(this.gesuch, gesuchResponse.data);
                    this.familiensituation.gesuch = angular.copy(this.gesuch);
                    return this.familiensituationRS.create(this.familiensituation).then((familienResponse: any) => {
                        return this.familiensituation = this.ebeguRestUtil.parseFamiliensituation(this.familiensituation, familienResponse.data);
                    });
                });
            });
        }
    }

    ///**
    // * Da die Verkuepfung zwischen Gesuchsteller und Gesuch 'cascade' ist, werden die Gesuchsteller
    // * automatisch gespeichert wenn Gesuch gespeichert wird.
    // */
    //public updateGesuch(): IPromise<TSGesuch> {
    //    return this.gesuchRS.update(this.gesuch).then((gesuchResponse: any) => {
    //        return this.gesuch = this.ebeguRestUtil.parseGesuch(this.gesuch, gesuchResponse.data);
    //    });
    //}

    /**
     * Speichert den StammdatenToWorkWith.
     */
    public updateGesuchsteller(): IPromise<TSPerson> {
        if (this.getStammdatenToWorkWith().timestampErstellt) {
            return this.personRS.update(this.getStammdatenToWorkWith()).then((personResponse: any) => {
                this.setStammdatenToWorkWith(this.ebeguRestUtil.parsePerson(this.getStammdatenToWorkWith(), personResponse.data));
                return this.gesuchRS.update(this.gesuch).then(() => {
                    return this.getStammdatenToWorkWith();
                });
            });
        } else {
            return this.personRS.create(this.getStammdatenToWorkWith()).then((personResponse: any) => {
                this.setStammdatenToWorkWith(this.ebeguRestUtil.parsePerson(this.getStammdatenToWorkWith(), personResponse.data));
                return this.gesuchRS.update(this.gesuch).then(() => {
                    return this.getStammdatenToWorkWith();
                });
            });
        }
    }

    public setGesuchstellerNumber(gsNumber: number) {
        if (gsNumber === 1 || gsNumber === 2) {
            this.gesuchstellerNumber = gsNumber;
        } else {
            this.gesuchstellerNumber = 1;
        }
    }

    public getStammdatenToWorkWith(): TSPerson {
        if (this.gesuchstellerNumber === 1) {
            return this.gesuch.gesuchsteller1;
        } else {
            return this.gesuch.gesuchsteller2;
        }
    }

    public setStammdatenToWorkWith(person: TSPerson): TSPerson {
        // Die Adresse kommt vom Server ohne das Feld 'showDatumVon', weil dieses ein Client-Feld ist
        this.calculateShowDatumFlags(person);
        if (this.gesuchstellerNumber === 1) {
            return this.gesuch.gesuchsteller1 = person;
        } else {
            return this.gesuch.gesuchsteller2 = person;
        }
    }

    public initStammdaten(): void {
        if (!this.getStammdatenToWorkWith()) {
            //todo imanol try to load data from database and only if nothing is there create a new model
            this.setStammdatenToWorkWith(new TSPerson());
            this.getStammdatenToWorkWith().adresse = this.initAdresse();
        }
    }

    public setKorrespondenzAdresse(showKorrespondadr: boolean): void {
        if (showKorrespondadr) {
            this.getStammdatenToWorkWith().korrespondenzAdresse = this.initKorrespondenzAdresse();
        } else {
            this.getStammdatenToWorkWith().korrespondenzAdresse = undefined;
        }
    }

    public setUmzugAdresse(showUmzug: boolean): void {
        if (showUmzug) {
            this.getStammdatenToWorkWith().umzugAdresse = this.initUmzugadresse();
        } else {
            this.getStammdatenToWorkWith().umzugAdresse = undefined;
        }
    }


    private initAdresse(): TSAdresse {
        let wohnAdr = new TSAdresse();
        wohnAdr.showDatumVon = false;
        wohnAdr.adresseTyp = TSAdressetyp.WOHNADRESSE;
        return wohnAdr;
    }

    private initKorrespondenzAdresse(): TSAdresse {
        let korrAdr = new TSAdresse();
        korrAdr.showDatumVon = false;
        korrAdr.adresseTyp = TSAdressetyp.KORRESPONDENZADRESSE;
        return korrAdr;
    }

    private initUmzugadresse(): TSAdresse {
        let umzugAdr = new TSAdresse();
        umzugAdr.showDatumVon = true;
        umzugAdr.adresseTyp = TSAdressetyp.WOHNADRESSE;
        return umzugAdr;
    }

    private calculateShowDatumFlags(person: TSPerson): void {
        if (person.adresse) {
            person.adresse.showDatumVon = false;
        }
        if (person.korrespondenzAdresse) {
            person.korrespondenzAdresse.showDatumVon = false;
        }
        if (person.umzugAdresse) {
            person.umzugAdresse.showDatumVon = true;
        }
    }
}
