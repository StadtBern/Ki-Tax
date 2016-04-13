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
import FamiliensituationRS from './familiensituationRS.rest';
import {IPromise} from 'angular';


export default class GesuchForm {
    fall:TSFall;
    gesuch:TSGesuch;
    familiensituation: TSFamiliensituation;
    fallRS: FallRS;
    gesuchRS: GesuchRS;
    familiensituationRS: FamiliensituationRS;
    gesuchstellerNumber: number;

    static $inject = ['FamiliensituationRS', 'FallRS', 'GesuchRS'];
    /* @ngInject */
    constructor(familiensituationRS: FamiliensituationRS, fallRS: FallRS, gesuchRS: GesuchRS) {
        this.fallRS = fallRS;
        this.gesuchRS = gesuchRS;
        this.familiensituationRS = familiensituationRS;
        this.fall = new TSFall();
        this.gesuch = new TSGesuch();
        this.familiensituation = new TSFamiliensituation();
    }

    /**
     * Prueft ob der 2. Gesuchtsteller eingetragen werden muss je nach dem was in Familiensituation ausgewaehlt wurde
     * @returns {boolean} False wenn "Alleinerziehend" oder "weniger als 5 Jahre" und dazu "alleine" ausgewaehlt wurde.
     */
    public isGesuchsteller2Required():boolean {
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
                return this.familiensituation = familienResponse.data;
            });
        }
        else {
            //todo team. Fall und Gesuch sollten in ihren eigenen Services gespeichert werden

            //todo team Parse alle response.XXX
            return this.fallRS.create(this.fall).then((fallResponse: any) => {
                this.fall = fallResponse.data;
                this.gesuch.fall = fallResponse.data;
                return this.gesuchRS.create(this.gesuch).then((gesuchResponse: any) => {
                    this.gesuch = gesuchResponse.data;
                    this.familiensituation.gesuch = gesuchResponse.data;
                    return this.familiensituationRS.create(this.familiensituation).then((familienResponse: any) => {
                        return this.familiensituation = familienResponse.data;

                    });
                });
            });
        }
    }

    /**
     * Da die Verkuepfung zwischen Gesuchsteller und Gesuch 'cascade' ist, werden die Gesuchsteller
     * automatisch gespeichert wenn Gesuch gespeichert wird.
     */
    public updateGesuch(): IPromise<TSGesuch> {
        return this.gesuchRS.update(this.gesuch).then((gesuchResponse: any) => {
            return this.gesuch = gesuchResponse.data;
        });
    }

    public setGesuchstellerNumber(gsNumber: number) {
        //todo team ueberlegen ob es by default 1 sein muss oder ob man irgendeinen Fehler zeigen soll
        if (gsNumber == 1 || gsNumber == 2) {
            this.gesuchstellerNumber = gsNumber;
        }
        else {
            this.gesuchstellerNumber = 1;
        }
    }

    public getStammdatenToWorkWith(): TSPerson {
        if(this.gesuchstellerNumber == 1) {
            return this.gesuch.gesuchsteller1;
        }
        else {
            return this.gesuch.gesuchsteller2;
        }
    }

    public initStammdaten():void {
        if(!this.getStammdatenToWorkWith()){
            //todo imanol improve this e.g. try to load data from database and only if nothing is there create a new model
            if(this.gesuchstellerNumber == 1) {
                this.gesuch.gesuchsteller1 = new TSPerson();
            }
            else {
                this.gesuch.gesuchsteller2 = new TSPerson();
            }

            if(!this.getStammdatenToWorkWith().adresse) {
                this.setAdresse();
                this.setKorrespondenzAdresse(false);
                this.setUmzugAdresse(false);
            }
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

    public setAdresse(): void {
        this.getStammdatenToWorkWith().adresse = this.initAdresse();
    }

    private initAdresse():TSAdresse {
        let wohnAdr = new TSAdresse();
        wohnAdr.showDatumVon = false;
        wohnAdr.adresseTyp = TSAdressetyp.WOHNADRESSE;
        return wohnAdr;
    }

    private initKorrespondenzAdresse():TSAdresse {
        let korrAdr = new TSAdresse();
        korrAdr.showDatumVon = false;
        korrAdr.adresseTyp = TSAdressetyp.KORRESPONDENZADRESSE;
        return korrAdr;
    }

    private initUmzugadresse():TSAdresse {
        let umzugAdr = new TSAdresse();
        umzugAdr.showDatumVon = true;
        umzugAdr.adresseTyp = TSAdressetyp.WOHNADRESSE;
        return umzugAdr;
    }

}
