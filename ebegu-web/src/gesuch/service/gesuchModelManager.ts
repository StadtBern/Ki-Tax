import TSFall from '../../models/TSFall';
import TSGesuch from '../../models/TSGesuch';
import TSGesuchsteller from '../../models/TSGesuchsteller';
import TSAdresse from '../../models/TSAdresse';
import {TSAdressetyp} from '../../models/enums/TSAdressetyp';
import TSFamiliensituation from '../../models/TSFamiliensituation';
import {TSFamilienstatus} from '../../models/enums/TSFamilienstatus';
import {TSGesuchstellerKardinalitaet} from '../../models/enums/TSGesuchstellerKardinalitaet';
import FallRS from './fallRS.rest';
import GesuchRS from './gesuchRS.rest';
import GesuchstellerRS from '../../core/service/gesuchstellerRS.rest.ts';
import FamiliensituationRS from './familiensituationRS.rest';
import {IPromise} from 'angular';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import TSFinanzielleSituation from '../../models/TSFinanzielleSituation';
import TSFinanzielleSituationContainer from '../../models/TSFinanzielleSituationContainer';
import FinanzielleSituationRS from './finanzielleSituationRS.rest';
import TSKindContainer from '../../models/TSKindContainer';
import TSKind from '../../models/TSKind';
import KindRS from '../../core/service/kindRS.rest';
import {TSFachstelle} from '../../models/TSFachstelle';
import {FachstelleRS} from '../../core/service/fachstelleRS.rest';


export default class GesuchModelManager {
    fall: TSFall;
    gesuch: TSGesuch;
    familiensituation: TSFamiliensituation;
    gesuchstellerNumber: number;
    kindNumber: number;
    fachstellenList: Array<TSFachstelle>;

    static $inject = ['FamiliensituationRS', 'FallRS', 'GesuchRS', 'GesuchstellerRS', 'FinanzielleSituationRS', 'KindRS', 'FachstelleRS', 'EbeguRestUtil'];
    /* @ngInject */
    constructor(private familiensituationRS: FamiliensituationRS, private fallRS: FallRS, private gesuchRS: GesuchRS, private gesuchstellerRS: GesuchstellerRS,
                private finanzielleSituationRS: FinanzielleSituationRS, private kindRS: KindRS, private fachstelleRS: FachstelleRS,
                private ebeguRestUtil: EbeguRestUtil) {

        this.fall = new TSFall();
        this.gesuch = new TSGesuch();
        this.familiensituation = new TSFamiliensituation();
        this.fachstellenList = [];
        this.updateFachstellenList();
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

    public updateFachstellenList(): void {
        this.fachstelleRS.getAllFachstellen().then((response: any) => {
            this.fachstellenList = angular.copy(response);
        });
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
    public updateGesuchsteller(): IPromise<TSGesuchsteller> {
        if (this.getStammdatenToWorkWith().timestampErstellt) {
            return this.gesuchstellerRS.updateGesuchsteller(this.getStammdatenToWorkWith()).then((gesuchstellerResponse: any) => {
                this.setStammdatenToWorkWith(gesuchstellerResponse);
                return this.gesuchRS.update(this.gesuch).then(() => {
                    return this.getStammdatenToWorkWith();
                });
            });
        } else {
            return this.gesuchstellerRS.create(this.getStammdatenToWorkWith()).then((gesuchstellerResponse: any) => {
                this.setStammdatenToWorkWith(gesuchstellerResponse);
                return this.gesuchRS.update(this.gesuch).then(() => {
                    return this.getStammdatenToWorkWith();
                });
            });
        }
    }

    public saveFinanzielleSituation(): IPromise<TSFinanzielleSituationContainer> {
        return this.finanzielleSituationRS.saveFinanzielleSituation(
            this.getStammdatenToWorkWith().finanzielleSituationContainer, this.getStammdatenToWorkWith())
            .then((finSitContRespo: TSFinanzielleSituationContainer) => {
            this.getStammdatenToWorkWith().finanzielleSituationContainer = finSitContRespo;
                return finSitContRespo;
        });
    }

    /**
     * Gesuchsteller nummer darf nur 1 oder 2 sein. Wenn die uebergebene Nummer nicht 1 oder 2 ist, wird dann 1 gesetzt
     * @param gsNumber
     */
    public setGesuchstellerNumber(gsNumber: number) {
        if (gsNumber === 1 || gsNumber === 2) {
            this.gesuchstellerNumber = gsNumber;
        } else {
            this.gesuchstellerNumber = 1;
        }
    }

    /**
     * Kind nummer geht von 1 bis unendlich. Fuer 0 oder negative Nummern wird kindNumber als 1 gesetzt.
     * @param kindNumber
     */
    public setKindNumber(kindNumber: number) {
        if (kindNumber > 0) {
            this.kindNumber = kindNumber;
        } else {
            this.kindNumber = 1;
        }
    }

    public getStammdatenToWorkWith(): TSGesuchsteller {
        if (this.gesuchstellerNumber === 1) {
            return this.gesuch.gesuchsteller1;
        } else {
            return this.gesuch.gesuchsteller2;
        }
    }

    public setStammdatenToWorkWith(gesuchsteller: TSGesuchsteller): TSGesuchsteller {
        // Die Adresse kommt vom Server ohne das Feld 'showDatumVon', weil dieses ein Client-Feld ist
        this.calculateShowDatumFlags(gesuchsteller);
        if (this.gesuchstellerNumber === 1) {
            return this.gesuch.gesuchsteller1 = gesuchsteller;
        } else {
            return this.gesuch.gesuchsteller2 = gesuchsteller;
        }
    }

    public initStammdaten(): void {
        if (!this.getStammdatenToWorkWith()) {
            //todo imanol try to load data from database and only if nothing is there create a new model
            this.setStammdatenToWorkWith(new TSGesuchsteller());
            this.getStammdatenToWorkWith().adresse = this.initAdresse();
        }
    }

    public initFinanzielleSituation(): void {
        this.initStammdaten();
        if (!this.getStammdatenToWorkWith().finanzielleSituationContainer) {
            //TODO (hefr) Dummy Daten!
            this.getStammdatenToWorkWith().finanzielleSituationContainer = new TSFinanzielleSituationContainer();
            this.getStammdatenToWorkWith().finanzielleSituationContainer.jahr = 2015;
            this.getStammdatenToWorkWith().finanzielleSituationContainer.finanzielleSituationSV = new TSFinanzielleSituation();
            this.getStammdatenToWorkWith().finanzielleSituationContainer.finanzielleSituationSV.nettolohn = 12345;
        }
    }

    public initKinder(): void {
        if (!this.gesuch.kindContainer) {
            this.gesuch.kindContainer = [];
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

    public getBasisjahr(): string {
        //TODO (team) muss aufgrund Gesuchsperiode ermittelt werden!
        return '2015';
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

    private calculateShowDatumFlags(gesuchsteller: TSGesuchsteller): void {
        if (gesuchsteller.adresse) {
            gesuchsteller.adresse.showDatumVon = false;
        }
        if (gesuchsteller.korrespondenzAdresse) {
            gesuchsteller.korrespondenzAdresse.showDatumVon = false;
        }
        if (gesuchsteller.umzugAdresse) {
            gesuchsteller.umzugAdresse.showDatumVon = true;
        }
    }

    public getKinderList(): Array<TSKindContainer> {
        if (this.gesuch) {
            return this.gesuch.kindContainer;
        }
        return [];
    }

    public createKind(): void {
        //todo team KindJA setzen
        this.gesuch.kindContainer.push(new TSKindContainer(new TSKind(), undefined));
        this.kindNumber = this.gesuch.kindContainer.length;
    }

    public updateKind(): IPromise<TSKindContainer> {
        if (this.getKindToWorkWith().timestampErstellt) {
            return this.kindRS.updateKind(this.getKindToWorkWith(), this.gesuch.id).then((kindResponse: any) => {
                this.setKindToWorkWith(kindResponse);
                return this.gesuchRS.update(this.gesuch).then(() => {
                    return this.getKindToWorkWith();
                });
            });
        } else {
            return this.kindRS.createKind(this.getKindToWorkWith(), this.gesuch.id).then((kindResponse: any) => {
                this.setKindToWorkWith(kindResponse);
                return this.gesuchRS.update(this.gesuch).then(() => {
                    return this.getKindToWorkWith();
                });
            });
        }
    }

    public getKindToWorkWith(): TSKindContainer {
        if (this.gesuch) {
            return this.gesuch.kindContainer[this.kindNumber - 1]; //kindNumber faengt in 1 an
        }
        return undefined;
    }

    public setKindToWorkWith(kind: TSKindContainer): TSKindContainer {
        return this.gesuch.kindContainer[this.kindNumber - 1] = kind;
    }

    /**
     * Entfernt das aktuelle Kind von der Liste.
     */
    public removeKindFromList() {
        this.gesuch.kindContainer.splice(this.kindNumber - 1, 1);
        this.setKindNumber(undefined); //by default auf undefined setzen
        //todo beim Auch KindRS.removeKind aufrufen???????
    }

    public getKindNumber(): number {
        return this.kindNumber;
    }

}
