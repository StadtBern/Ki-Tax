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
import {IPromise, ILogService} from 'angular';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import TSFinanzielleSituation from '../../models/TSFinanzielleSituation';
import TSFinanzielleSituationContainer from '../../models/TSFinanzielleSituationContainer';
import TSEinkommensverschlechterungContainer from '../../models/TSEinkommensverschlechterungContainer';
import TSEinkommensverschlechterung from '../../models/TSEinkommensverschlechterung';
import FinanzielleSituationRS from './finanzielleSituationRS.rest';
import TSKindContainer from '../../models/TSKindContainer';
import TSKind from '../../models/TSKind';
import KindRS from '../../core/service/kindRS.rest';
import {TSFachstelle} from '../../models/TSFachstelle';
import {FachstelleRS} from '../../core/service/fachstelleRS.rest';
import TSErwerbspensumContainer from '../../models/TSErwerbspensumContainer';
import ErwerbspensumRS from '../../core/service/erwerbspensumRS.rest';
import TSBetreuung from '../../models/TSBetreuung';
import TSInstitutionStammdaten from '../../models/TSInstitutionStammdaten';
import {InstitutionStammdatenRS} from '../../core/service/institutionStammdatenRS.rest';
import DateUtil from '../../utils/DateUtil';
import BetreuungRS from '../../core/service/betreuungRS';
import {TSBetreuungsstatus} from '../../models/enums/TSBetreuungsstatus';
import TSGesuchsperiode from '../../models/TSGesuchsperiode';
import GesuchsperiodeRS from '../../core/service/gesuchsperiodeRS.rest';
import AuthServiceRS from '../../authentication/service/AuthServiceRS.rest';
import TSEinkommensverschlechterungInfo from '../../models/TSEinkommensverschlechterungInfo';


export default class GesuchModelManager {
    gesuch: TSGesuch;
    gesuchstellerNumber: number = 1;
    private kindNumber: number;
    private betreuungNumber: number;
    private fachstellenList: Array<TSFachstelle>;
    private institutionenList: Array<TSInstitutionStammdaten>;
    private activeGesuchsperiodenList: Array<TSGesuchsperiode>;


    static $inject = ['FamiliensituationRS', 'FallRS', 'GesuchRS', 'GesuchstellerRS', 'FinanzielleSituationRS', 'KindRS', 'FachstelleRS',
        'ErwerbspensumRS', 'InstitutionStammdatenRS', 'BetreuungRS', 'GesuchsperiodeRS', 'EbeguRestUtil', '$log', 'AuthServiceRS'];
    /* @ngInject */
    constructor(private familiensituationRS: FamiliensituationRS, private fallRS: FallRS, private gesuchRS: GesuchRS, private gesuchstellerRS: GesuchstellerRS,
                private finanzielleSituationRS: FinanzielleSituationRS, private kindRS: KindRS, private fachstelleRS: FachstelleRS, private erwerbspensumRS: ErwerbspensumRS,
                private instStamRS: InstitutionStammdatenRS, private betreuungRS: BetreuungRS, private gesuchsperiodeRS: GesuchsperiodeRS,
                private ebeguRestUtil: EbeguRestUtil, private log: ILogService, private authServiceRS: AuthServiceRS) {

        this.fachstellenList = [];
        this.institutionenList = [];
        this.activeGesuchsperiodenList = [];
        this.updateFachstellenList();
        this.updateInstitutionenList();
        this.updateActiveGesuchsperiodenList();
    }

    /**
     * Prueft ob der 2. Gesuchtsteller eingetragen werden muss je nach dem was in Familiensituation ausgewaehlt wurde
     * @returns {boolean} False wenn "Alleinerziehend" oder "weniger als 5 Jahre" und dazu "alleine" ausgewaehlt wurde.
     */
    public isGesuchsteller2Required(): boolean {
        if (this.gesuch && this.getFamiliensituation() && this.getFamiliensituation().familienstatus) {
            return !(((this.getFamiliensituation().familienstatus === TSFamilienstatus.ALLEINERZIEHEND)
            || (this.getFamiliensituation().familienstatus === TSFamilienstatus.WENIGER_FUENF_JAHRE))
            && (this.getFamiliensituation().gesuchstellerKardinalitaet === TSGesuchstellerKardinalitaet.ALLEINE));
        } else {
            return false;
        }
    }

    public getFamiliensituation(): TSFamiliensituation {
        if (this.gesuch) {
            return this.gesuch.familiensituation;
        }
        return undefined;
    }

    public updateFachstellenList(): void {
        this.fachstelleRS.getAllFachstellen().then((response: any) => {
            this.fachstellenList = angular.copy(response);
        });
    }

    /**
     * Retrieves the list of InstitutionStammdaten for the date of today.
     */
    public updateInstitutionenList(): void {
        this.instStamRS.getAllInstitutionStammdatenByDate(DateUtil.today()).then((response: any) => {
            this.institutionenList = angular.copy(response);
        });
    }

    public updateActiveGesuchsperiodenList(): void {
        this.gesuchsperiodeRS.getAllActiveGesuchsperioden().then((response: any) => {
            this.activeGesuchsperiodenList = angular.copy(response);
        });
    }

    /**
     * Wenn das Gesuch schon gespeichert ist (timestampErstellt != null), wird dieses nur aktualisiert. Wenn es um ein neues Gesuch handelt
     * dann wird zuerst der Fall erstellt, dieser ins Gesuch kopiert und dann das Gesuch erstellt
     * @returns {IPromise<TSGesuch>}
     */
    public saveGesuchAndFall(): IPromise<TSGesuch> {
        if (this.gesuch && this.gesuch.timestampErstellt) { //update
            return this.updateGesuch();
        } else { //create
            return this.fallRS.createFall(this.gesuch.fall).then((fallResponse: any) => {
                let parsedFall = this.ebeguRestUtil.parseFall(this.gesuch.fall, fallResponse.data);
                this.gesuch.fall = angular.copy(parsedFall);
                return this.gesuchRS.createGesuch(this.gesuch).then((gesuchResponse: any) => {
                    return this.gesuch = this.ebeguRestUtil.parseGesuch(this.gesuch, gesuchResponse.data);
                });
            });
        }
    }

    public updateFamiliensituation(): IPromise<TSFamiliensituation> {
        //testen ob aktuelles familiensituation schon gespeichert ist
        if (this.getFamiliensituation().timestampErstellt) {
            return this.familiensituationRS.update(this.getFamiliensituation()).then((familienResponse: any) => {
                return this.gesuch.familiensituation = this.ebeguRestUtil.parseFamiliensituation(this.getFamiliensituation(), familienResponse.data);
            });
        } else {
            return this.familiensituationRS.create(this.getFamiliensituation()).then((familienResponse: any) => {
                return this.gesuch.familiensituation = this.ebeguRestUtil.parseFamiliensituation(this.getFamiliensituation(), familienResponse.data);
            });
        }
    }

    /**
     * Update das Gesuch
     * @returns {IPromise<TSGesuch>}
     */
    public updateGesuch(): IPromise<TSGesuch> {
        return this.gesuchRS.updateGesuch(this.gesuch).then((gesuchResponse: any) => {
            return this.gesuch = this.ebeguRestUtil.parseGesuch(this.gesuch, gesuchResponse.data);
        });
    }

    /**
     * Speichert den StammdatenToWorkWith.
     */
    public updateGesuchsteller(): IPromise<TSGesuchsteller> {
        if (this.getStammdatenToWorkWith().timestampErstellt) {
            return this.gesuchstellerRS.updateGesuchsteller(this.getStammdatenToWorkWith()).then((gesuchstellerResponse: any) => {
                this.setStammdatenToWorkWith(gesuchstellerResponse);
                return this.gesuchRS.updateGesuch(this.gesuch).then(() => {
                    return this.getStammdatenToWorkWith();
                });
            });
        } else {
            return this.gesuchstellerRS.create(this.getStammdatenToWorkWith()).then((gesuchstellerResponse: any) => {
                this.setStammdatenToWorkWith(gesuchstellerResponse);
                return this.gesuchRS.updateGesuch(this.gesuch).then(() => {
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
     * Kind nummer geht von 1 bis unendlich. Fuer 0 oder negative Nummer wird kindNumber als 1 gesetzt.
     * @param kindNumber
     */
    public setKindNumber(kindNumber: number) {
        if (kindNumber > 0) {
            this.kindNumber = kindNumber;
        } else {
            this.kindNumber = 1;
        }
    }

    /**
     * Betreuung nummer geht von 1 bis unendlich. Fuer 0 oder negative Nummer wird betreuungNumber als 1 gesetzt.
     * @param betreuungNumber
     */
    public setBetreuungNumber(betreuungNumber: number) {
        if (betreuungNumber > 0) {
            this.betreuungNumber = betreuungNumber;
        } else {
            this.betreuungNumber = 1;
        }
    }

    public getFachstellenList(): Array<TSFachstelle> {
        return this.fachstellenList;
    }

    public getInstitutionenList(): Array<TSInstitutionStammdaten> {
        return this.institutionenList;
    }

    public getAllActiveGesuchsperioden(): Array<TSGesuchsperiode> {
        return this.activeGesuchsperiodenList;
    }

    public getStammdatenToWorkWith(): TSGesuchsteller {
        if (this.gesuchstellerNumber === 2) {
            return this.gesuch.gesuchsteller2;
        } else {
            return this.gesuch.gesuchsteller1;
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
        if (this.gesuch && !this.gesuch.gesuchsteller1.finanzielleSituationContainer) {
            this.gesuch.gesuchsteller1.finanzielleSituationContainer = new TSFinanzielleSituationContainer();
            this.gesuch.gesuchsteller1.finanzielleSituationContainer.jahr = this.getBasisjahr();
            this.gesuch.gesuchsteller1.finanzielleSituationContainer.finanzielleSituationSV = new TSFinanzielleSituation();
        }
        if (this.gesuch && this.isGesuchsteller2Required() && !this.gesuch.gesuchsteller2.finanzielleSituationContainer) {
            this.gesuch.gesuchsteller2.finanzielleSituationContainer = new TSFinanzielleSituationContainer();
            this.gesuch.gesuchsteller2.finanzielleSituationContainer.jahr = this.getBasisjahr();
            this.gesuch.gesuchsteller2.finanzielleSituationContainer.finanzielleSituationSV = new TSFinanzielleSituation();
        }
    }

    public initEinkommensverschlechterungInfo(): void {
        if (this.gesuch && !this.gesuch.einkommensverschlechterungInfo) {
            this.gesuch.einkommensverschlechterungInfo = new TSEinkommensverschlechterungInfo();
            this.gesuch.einkommensverschlechterungInfo.ekvFuerBasisJahrPlus1 = false;
            this.gesuch.einkommensverschlechterungInfo.ekvFuerBasisJahrPlus2 = false;

        }
    }

    public initEinkommensverschlechterungContainer(beideHalbjahre: boolean): void {
        this.initStammdaten();
        if (this.gesuch && !this.gesuch.gesuchsteller1.einkommensverschlechterungContainer) {
            this.gesuch.gesuchsteller1.einkommensverschlechterungContainer = new TSEinkommensverschlechterungContainer();
            this.gesuch.gesuchsteller1.einkommensverschlechterungContainer.ekvJABasisJahrPlus1 = new TSEinkommensverschlechterung();
            if (beideHalbjahre) {
                this.gesuch.gesuchsteller1.einkommensverschlechterungContainer.ekvJABasisJahrPlus2 = new TSEinkommensverschlechterung();
            }
        }
        if (this.gesuch && this.isGesuchsteller2Required() && !this.gesuch.gesuchsteller2.einkommensverschlechterungContainer) {
            this.gesuch.gesuchsteller2.einkommensverschlechterungContainer = new TSEinkommensverschlechterungContainer();
            this.gesuch.gesuchsteller2.einkommensverschlechterungContainer.ekvJABasisJahrPlus1 = new TSEinkommensverschlechterung();
            if (beideHalbjahre) {
                this.gesuch.gesuchsteller2.einkommensverschlechterungContainer.ekvJABasisJahrPlus2 = new TSEinkommensverschlechterung();
            }
        }
    }


    /**
     * Erstellt ein neues Gesuch und einen neuen Fall. Wenn !forced sie werden nur erstellt wenn das Gesuch noch nicht erstellt wurde i.e. es null/undefined ist
     * Wenn force werden Gesuch und Fall immer erstellt.
     * @param forced
     */
    public initGesuch(forced: boolean) {
        if (forced || (!forced && !this.gesuch)) {
            this.gesuch = new TSGesuch();
            this.gesuch.fall = new TSFall();
            this.setCurrentUserAsFallVerantwortlicher();
        }
    }

    public initFamiliensituation() {
        if (!this.getFamiliensituation()) {
            this.gesuch.familiensituation = new TSFamiliensituation();
        }
    }

    public initKinder(): void {
        if (!this.gesuch.kindContainers) {
            this.gesuch.kindContainers = [];
        }
    }

    public initBetreuung(): void {
        if (!this.getKindToWorkWith().betreuungen) {
            this.getKindToWorkWith().betreuungen = [];
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

    /**
     * Gibt das Jahr des Anfangs der Gesuchsperiode minus 1 zurueck. undefined wenn die Gesuchsperiode nicht richtig gesetzt wurde
     * @returns {number}
     */
    public getBasisjahr(): number {
        if (this.getGesuchsperiodeBegin()) {
            return this.getGesuchsperiodeBegin().year() - 1;
        }
        return undefined;
    }

    /**
     * Gibt das Jahr des Anfangs der Gesuchsperiode minus 1 zurueck. undefined wenn die Gesuchsperiode nicht richtig gesetzt wurde
     * @returns {number}
     */
    public getBasisjahrPlus(plus: number): number {
        if (this.getGesuchsperiodeBegin()) {
            return this.getGesuchsperiodeBegin().year() - 1 + plus;
        }
        return undefined;
    }

    /**
     * Gibt das gesamte Objekt Gesuchsperiode zurueck, das zum Gesuch gehoert.
     * @returns {any}
     */
    public getGesuchsperiode(): TSGesuchsperiode {
        if (this.gesuch) {
            return this.gesuch.gesuchsperiode;
        }
        return undefined;
    }

    /**
     * Gibt den Anfang der Gesuchsperiode als Moment zurueck
     * @returns {any}
     */
    public getGesuchsperiodeBegin(): moment.Moment {
        if (this.getGesuchsperiode() && this.getGesuchsperiode().gueltigkeit) {
            return this.gesuch.gesuchsperiode.gueltigkeit.gueltigAb;
        }
        return undefined;
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
            return this.gesuch.kindContainers;
        }
        return [];
    }

    /**
     *
     * @returns {any} Alle KindContainer in denen das Kind Betreuung benoetigt
     */
    public getKinderWithBetreuungList(): Array<TSKindContainer> {
        let listResult: Array<TSKindContainer> = [];
        if (this.gesuch && this.gesuch.kindContainers) {
            this.gesuch.kindContainers.forEach((kind) => {
                if (kind.kindJA.familienErgaenzendeBetreuung) {
                    listResult.push(kind);
                }
            });
        }
        return listResult;
    }

    public createKind(): void {
        this.gesuch.kindContainers.push(new TSKindContainer(undefined, new TSKind()));
        this.kindNumber = this.gesuch.kindContainers.length;
    }

    /**
     * Creates a Betreuung for the kind given by the kindNumber attribute of the class.
     * Thus the kindnumber must be set before this method is called.
     */
    public createBetreuung(): void {
        if (this.getKindToWorkWith()) {
            this.initBetreuung();
            let tsBetreuung: TSBetreuung = new TSBetreuung();
            tsBetreuung.betreuungsstatus = TSBetreuungsstatus.AUSSTEHEND;
            this.getKindToWorkWith().betreuungen.push(tsBetreuung);
            this.betreuungNumber = this.getKindToWorkWith().betreuungen.length;
        }
    }

    public updateBetreuung(): IPromise<TSBetreuung> {
        //besteht schon -> update
        if (this.getBetreuungToWorkWith().timestampErstellt) {
            return this.betreuungRS.updateBetreuung(this.getBetreuungToWorkWith(), this.getKindToWorkWith().id).then((betreuungResponse: any) => {
                this.setBetreuungToWorkWith(betreuungResponse);
                return this.getBetreuungToWorkWith();
            });
            //neu -> create
        } else {
            return this.betreuungRS.createBetreuung(this.getBetreuungToWorkWith(), this.getKindToWorkWith().id).then((betreuungResponse: any) => {
                this.setBetreuungToWorkWith(betreuungResponse);
                return this.getBetreuungToWorkWith();
            });
        }
    }

    public updateKind(): IPromise<TSKindContainer> {
        if (this.getKindToWorkWith().timestampErstellt) {
            return this.kindRS.updateKind(this.getKindToWorkWith(), this.gesuch.id).then((kindResponse: any) => {
                this.setKindToWorkWith(kindResponse);
                return this.gesuchRS.updateGesuch(this.gesuch).then(() => {
                    return this.getKindToWorkWith();
                });
            });
        } else {
            return this.kindRS.createKind(this.getKindToWorkWith(), this.gesuch.id).then((kindResponse: any) => {
                this.setKindToWorkWith(kindResponse);
                return this.gesuchRS.updateGesuch(this.gesuch).then(() => {
                    return this.getKindToWorkWith();
                });
            });
        }
    }

    public getKindToWorkWith(): TSKindContainer {
        if (this.gesuch && this.gesuch.kindContainers && this.gesuch.kindContainers.length >= this.kindNumber) {
            return this.gesuch.kindContainers[this.kindNumber - 1]; //kindNumber faengt mit 1 an
        }
        return undefined;
    }

    /**
     * Sucht im ausgewaehlten Kind (kindNumber) nach der aktuellen Betreuung. Deshalb muessen sowohl
     * kindNumber als auch betreuungNumber bereits gesetzt sein.
     * @returns {any}
     */
    public getBetreuungToWorkWith(): TSBetreuung {
        if (this.getKindToWorkWith() && this.getKindToWorkWith().betreuungen.length >= this.betreuungNumber) {
            return this.getKindToWorkWith().betreuungen[this.betreuungNumber - 1];
        }
        return undefined;
    }

    /**
     * Ersetzt das Kind in der aktuelle Position "kindNumber" durch das gegebene Kind. Aus diesem Grund muss diese Methode
     * nur aufgerufen werden, wenn die Position "kindNumber" schon richtig gesetzt wurde.
     * @param kind
     * @returns {TSKindContainer}
     */
    private setKindToWorkWith(kind: TSKindContainer): TSKindContainer {
        return this.gesuch.kindContainers[this.kindNumber - 1] = kind;
    }

    /**
     * Ersetzt die Betreuung in der aktuelle Position "betreuungNumber" durch die gegebene Betreuung. Aus diesem Grund muss diese Methode
     * nur aufgerufen werden, wenn die Position "betreuungNumber" schon richtig gesetzt wurde.
     * @param betreuung
     * @returns {TSBetreuung}
     */
    private setBetreuungToWorkWith(betreuung: TSBetreuung): TSBetreuung {
        return this.getKindToWorkWith().betreuungen[this.betreuungNumber - 1] = betreuung;
    }

    /**
     * Entfernt das aktuelle Kind von der Liste aber nicht von der DB.
     */
    public removeKindFromList() {
        this.gesuch.kindContainers.splice(this.kindNumber - 1, 1);
        this.setKindNumber(undefined); //by default auf undefined setzen
        //todo beim Auch KindRS.removeKind aufrufen???????
    }

    /**
     * Entfernt die aktuelle Betreuung des aktuellen Kindes von der Liste aber nicht von der DB.
     */
    public removeBetreuungFromKind() {
        this.getKindToWorkWith().betreuungen.splice(this.betreuungNumber - 1, 1);
        this.setBetreuungNumber(undefined); //by default auf undefined setzen
    }

    public getKindNumber(): number {
        return this.kindNumber;
    }

    public getBetreuungNumber(): number {
        return this.betreuungNumber;
    }

    public getGesuchstellerNumber(): number {
        return this.gesuchstellerNumber;
    }

    /**
     * Check whether the Gesuch is already saved in the database.
     * Case yes the fields shouldn't be editable anymore
     */
    public isGesuchSaved(): boolean {
        return this.gesuch && (this.gesuch.timestampErstellt !== undefined)
            && (this.gesuch.timestampErstellt !== null);
    }

    /**
     * Sucht das gegebene KindContainer in der List von KindContainer, erstellt es als KindToWorkWith
     * und gibt die Position in der Array zurueck. Gibt -1 zurueck wenn das Kind nicht gefunden wurde.
     * @param kind
     */
    public findKind(kind: TSKindContainer): number {
        if (this.gesuch.kindContainers.indexOf(kind) >= 0) {
            return this.kindNumber = this.gesuch.kindContainers.indexOf(kind) + 1;
        }
        return -1;
    }

    public removeKind(): IPromise<TSKindContainer> {
        return this.kindRS.removeKind(this.getKindToWorkWith().id).then((responseKind: any) => {
            this.removeKindFromList();
            return this.gesuchRS.updateGesuch(this.gesuch);
        });
    }

    public findBetreuung(betreuung: TSBetreuung): number {
        if (this.getKindToWorkWith() && this.getKindToWorkWith().betreuungen) {
            return this.betreuungNumber = this.getKindToWorkWith().betreuungen.indexOf(betreuung) + 1;
        }
        return -1;
    }

    public removeBetreuung(): IPromise<TSKindContainer> {
        return this.betreuungRS.removeBetreuung(this.getBetreuungToWorkWith().id).then((responseBetreuung: any) => {
            this.removeBetreuungFromKind();
            return this.kindRS.updateKind(this.getKindToWorkWith(), this.gesuch.id);
        });
    }


    public removeErwerbspensum(pensum: TSErwerbspensumContainer) {
        let erwerbspensenOfCurrentGS: Array<TSErwerbspensumContainer>;
        erwerbspensenOfCurrentGS = this.getStammdatenToWorkWith().erwerbspensenContainer;
        let index: number = erwerbspensenOfCurrentGS.indexOf(pensum);
        if (index >= 0) {
            let pensumToRemove: TSErwerbspensumContainer = this.getStammdatenToWorkWith().erwerbspensenContainer[index];
            if (pensumToRemove.id) { //wenn id vorhanden dann aus der DB loeschen
                this.erwerbspensumRS.removeErwerbspensum(pensumToRemove.id)
                    .then((ewpContainer: TSErwerbspensumContainer) => {
                        erwerbspensenOfCurrentGS.splice(index, 1);
                    });
            } else {
                //sonst nur vom gui wegnehmen
                erwerbspensenOfCurrentGS.splice(index, 1);
            }
        } else {
            console.log('can not remove Erwerbspensum since it  could not be found in list');
        }
    }

    findIndexOfErwerbspensum(gesuchstellerNumber: number, pensum: any): number {
        let gesuchsteller: TSGesuchsteller;
        gesuchsteller = gesuchstellerNumber === 2 ? this.gesuch.gesuchsteller2 : this.gesuch.gesuchsteller1;
        return gesuchsteller.erwerbspensenContainer.indexOf(pensum);
    }

    saveErwerbspensum(gesuchsteller: TSGesuchsteller, erwerbspensum: TSErwerbspensumContainer): IPromise<TSErwerbspensumContainer> {
        if (erwerbspensum.id) {
            return this.erwerbspensumRS.updateErwerbspensum(erwerbspensum, gesuchsteller.id)
                .then((response: TSErwerbspensumContainer) => {
                    let i = gesuchsteller.erwerbspensenContainer.indexOf(erwerbspensum);
                    if (i >= 0) {
                        gesuchsteller.erwerbspensenContainer[i] = erwerbspensum;
                    }
                    return response;
                });
        } else {
            return this.erwerbspensumRS.createErwerbspensum(erwerbspensum, gesuchsteller.id)
                .then((storedErwerbspensum: TSErwerbspensumContainer) => {
                    gesuchsteller.erwerbspensenContainer.push(storedErwerbspensum);
                    return storedErwerbspensum;
                });
        }

    }

    /**
     * Takes current user and sets it as the verantwortlicher of Fall
     */
    private setCurrentUserAsFallVerantwortlicher() {
        if (this.authServiceRS) {
            this.gesuch.fall.verantwortlicher = this.authServiceRS.getPrincipal();
        }
    }
}
