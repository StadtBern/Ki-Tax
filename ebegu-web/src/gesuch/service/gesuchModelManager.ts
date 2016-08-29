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
import EinkommensverschlechterungContainerRS from './einkommensverschlechterungContainerRS.rest';
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
import TSUser from '../../models/TSUser';
import VerfuegungRS from '../../core/service/verfuegungRS.rest';
import TSVerfuegung from '../../models/TSVerfuegung';
import WizardStepManager from './wizardStepManager';

export default class GesuchModelManager {
    private gesuch: TSGesuch;
    gesuchSnapshot: TSGesuch;
    gesuchstellerNumber: number = 1;
    basisJahrPlusNumber: number = 1;
    private kindNumber: number;
    private betreuungNumber: number;
    private fachstellenList: Array<TSFachstelle>;
    private institutionenList: Array<TSInstitutionStammdaten>;
    private activeGesuchsperiodenList: Array<TSGesuchsperiode>;

    //diese Variable enthaelt alle Kinder die die Methode verfuegungRS.calculateVerfuegung zurueckgibt. Normalerweise sollten die Kinder im
    // gesuch aktualisiert werden. Das Problem ist, dass die Verfuegungen nicht gespeichert werden duerfen, bis der Benutzer auf den Knopf
    // Verfuegen klickt
    // todo dies koennte verbessert werden. Das Problem hier ist, dass wir beim Berechnen der Verfuegung nciht das ganze Gesuch hin und her schicken wollen
    private kinderWithBetreuungList: Array<TSKindContainer> = [];


    static $inject = ['FamiliensituationRS', 'FallRS', 'GesuchRS', 'GesuchstellerRS', 'FinanzielleSituationRS', 'KindRS', 'FachstelleRS',
        'ErwerbspensumRS', 'InstitutionStammdatenRS', 'BetreuungRS', 'GesuchsperiodeRS', 'EbeguRestUtil', '$log', 'AuthServiceRS',
        'EinkommensverschlechterungContainerRS', 'VerfuegungRS', 'WizardStepManager'];
    /* @ngInject */
    constructor(private familiensituationRS: FamiliensituationRS, private fallRS: FallRS, private gesuchRS: GesuchRS, private gesuchstellerRS: GesuchstellerRS,
                private finanzielleSituationRS: FinanzielleSituationRS, private kindRS: KindRS, private fachstelleRS: FachstelleRS, private erwerbspensumRS: ErwerbspensumRS,
                private instStamRS: InstitutionStammdatenRS, private betreuungRS: BetreuungRS, private gesuchsperiodeRS: GesuchsperiodeRS,
                private ebeguRestUtil: EbeguRestUtil, private log: ILogService, private authServiceRS: AuthServiceRS,
                private einkommensverschlechterungContainerRS: EinkommensverschlechterungContainerRS, private verfuegungRS: VerfuegungRS,
                private wizardStepManager: WizardStepManager) {

        this.fachstellenList = [];
        this.institutionenList = [];
        this.activeGesuchsperiodenList = [];
        this.updateFachstellenList();
        this.updateInstitutionenList();
        this.updateActiveGesuchsperiodenList();
    }

    /**
     * In dieser Methode wird das Gesuch ersetzt. Das Gesuch ist jetzt private und darf nur ueber diese Methode geaendert werden.
     *
     * @param gesuch das Gesuch. Null und undefined werden erlaubt.
     */
    public setGesuch(gesuch: TSGesuch): void {
        this.gesuch = gesuch;
        this.wizardStepManager.findStepsFromGesuch(this.gesuch.id);
    }

    public getGesuch(): TSGesuch {
        return this.gesuch;
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

    public isBasisJahr2Required(): boolean {
        return this.getEkvFuerBasisJahrPlus(2);
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
            return this.fallRS.createFall(this.gesuch.fall).then((fallResponse: TSFall) => {
                this.gesuch.fall = angular.copy(fallResponse);
                return this.gesuchRS.createGesuch(this.gesuch).then((gesuchResponse: any) => {
                    this.gesuch = this.ebeguRestUtil.parseGesuch(this.gesuch, gesuchResponse.data);
                    return this.wizardStepManager.findStepsFromGesuch(this.gesuch.id).then(() => {
                        return this.gesuch;
                    });
                });
            });
        }
    }

    public updateFamiliensituation(): IPromise<TSFamiliensituation> {
        //testen ob aktuelles familiensituation schon gespeichert ist
        if (this.getFamiliensituation().timestampErstellt) {
            return this.familiensituationRS.update(this.getFamiliensituation(), this.gesuch.id).then((familienResponse: any) => {
                this.gesuch.familiensituation = this.ebeguRestUtil.parseFamiliensituation(this.getFamiliensituation(), familienResponse.data);
                return this.wizardStepManager.findStepsFromGesuch(this.gesuch.id).then(() => {
                    return this.gesuch.familiensituation;
                });
            });
        } else {
            return this.familiensituationRS.create(this.getFamiliensituation(), this.gesuch.id).then((familienResponse: any) => {
                this.gesuch.familiensituation = this.ebeguRestUtil.parseFamiliensituation(this.getFamiliensituation(), familienResponse.data);
                return this.wizardStepManager.findStepsFromGesuch(this.gesuch.id).then(() => {
                    return this.gesuch.familiensituation;
                });
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
     * Update den Fall
     * @returns {IPromise<TSFall>}
     */
    public updateFall(): IPromise<TSFall> {
        return this.fallRS.updateFall(this.gesuch.fall).then((fallResponse: any) => {
            let parsedFall = this.ebeguRestUtil.parseFall(this.gesuch.fall, fallResponse);
            return this.gesuch.fall = angular.copy(parsedFall);
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
                    return this.wizardStepManager.findStepsFromGesuch(this.gesuch.id).then(() => {
                        return this.getStammdatenToWorkWith();
                    });
                });
            });
        } else {
            return this.gesuchstellerRS.createGesuchsteller(this.getStammdatenToWorkWith(), this.gesuch.id, this.gesuchstellerNumber)
                .then((gesuchstellerResponse: any) => {
                    this.setStammdatenToWorkWith(gesuchstellerResponse);
                    return this.gesuchRS.updateGesuch(this.gesuch).then(() => {
                        return this.wizardStepManager.findStepsFromGesuch(this.gesuch.id).then(() => {
                            return this.getStammdatenToWorkWith();
                        });
                    });
            });
        }
    }

    public saveFinanzielleSituation(): IPromise<TSFinanzielleSituationContainer> {
        return this.finanzielleSituationRS.saveFinanzielleSituation(
            this.getStammdatenToWorkWith().finanzielleSituationContainer, this.getStammdatenToWorkWith().id, this.gesuch.id)
            .then((finSitContRespo: TSFinanzielleSituationContainer) => {
                this.getStammdatenToWorkWith().finanzielleSituationContainer = finSitContRespo;
                return this.wizardStepManager.findStepsFromGesuch(this.gesuch.id).then(() => {
                    return finSitContRespo;
                });
            });
    }

    public saveEinkommensverschlechterungContainer(): IPromise<TSEinkommensverschlechterungContainer> {
        return this.einkommensverschlechterungContainerRS.saveEinkommensverschlechterungContainer(
            this.getStammdatenToWorkWith().einkommensverschlechterungContainer, this.getStammdatenToWorkWith().id, this.gesuch.id)
            .then((ekvContRespo: TSEinkommensverschlechterungContainer) => {
                this.getStammdatenToWorkWith().einkommensverschlechterungContainer = ekvContRespo;
                return this.wizardStepManager.findStepsFromGesuch(this.gesuch.id).then(() => {
                    return ekvContRespo;
                });
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
     * BasisJahrPlus nummer darf nur 1 oder 2 sein. Wenn die uebergebene Nummer nicht 1 oder 2 ist, wird dann 1 gesetzt
     * @param bjpNumber
     */
    public setBasisJahrPlusNumber(bjpNumber: number) {
        if (bjpNumber === 1 || bjpNumber === 2) {
            this.basisJahrPlusNumber = bjpNumber;
        } else {
            this.basisJahrPlusNumber = 1;
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

    public getEinkommensverschlechterungToWorkWith(): TSEinkommensverschlechterung {
        let gesuchsteller: TSGesuchsteller;
        if (this.gesuchstellerNumber === 2) {
            return this.getEkvFromGesuchstellerOfBsj_JA(this.gesuch.gesuchsteller2);
        } else {
            return this.getEkvFromGesuchstellerOfBsj_JA(this.gesuch.gesuchsteller1);
        }
    }

    public getEinkommensverschlechterungToWorkWith_GS(): TSEinkommensverschlechterung {
        let gesuchsteller: TSGesuchsteller;
        if (this.gesuchstellerNumber === 2) {
            return this.getEkvFromGesuchstellerOfBsj_GS(this.gesuch.gesuchsteller2);
        } else {
            return this.getEkvFromGesuchstellerOfBsj_GS(this.gesuch.gesuchsteller1);
        }
    }

    public getEkvFromGesuchstellerOfBsj_JA(gesuchsteller: TSGesuchsteller): TSEinkommensverschlechterung {
        if (this.basisJahrPlusNumber === 2) {
            return gesuchsteller.einkommensverschlechterungContainer.ekvJABasisJahrPlus2;
        } else {
            return gesuchsteller.einkommensverschlechterungContainer.ekvJABasisJahrPlus1;
        }
    }

    private getEkvFromGesuchstellerOfBsj_GS(gesuchsteller: TSGesuchsteller): TSEinkommensverschlechterung {
        if (this.basisJahrPlusNumber === 2) {
            return gesuchsteller.einkommensverschlechterungContainer.ekvGSBasisJahrPlus2;
        } else {
            return gesuchsteller.einkommensverschlechterungContainer.ekvGSBasisJahrPlus1;
        }
    }

    public getEkvFuerBasisJahrPlusToWorkWith(): boolean {
        return this.getEkvFuerBasisJahrPlus(this.basisJahrPlusNumber);
    }

    public getEkvFuerBasisJahrPlus(basisJahrPlus: number): boolean {
        if (!this.gesuch.einkommensverschlechterungInfo) {
            this.initEinkommensverschlechterungInfo();
        }

        if (basisJahrPlus === 2) {
            return this.gesuch.einkommensverschlechterungInfo.ekvFuerBasisJahrPlus2;
        } else {
            return this.gesuch.einkommensverschlechterungInfo.ekvFuerBasisJahrPlus1;
        }
    }

    public getGemeinsameSteuererklaerungToWorkWith(): boolean {
        return this.getGemeinsameSteuererklaerungToWorkWith_2(this.basisJahrPlusNumber);
    }


    private getGemeinsameSteuererklaerungToWorkWith_2(basisJahrPlus: number): boolean {
        if (!this.gesuch.einkommensverschlechterungInfo) {
            this.initEinkommensverschlechterungInfo();
        }

        if (basisJahrPlus === 2) {
            return this.gesuch.einkommensverschlechterungInfo.gemeinsameSteuererklaerung_BjP2;
        } else {
            return this.gesuch.einkommensverschlechterungInfo.gemeinsameSteuererklaerung_BjP1;
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
            this.gesuch.gesuchsteller1.finanzielleSituationContainer.finanzielleSituationJA = new TSFinanzielleSituation();
        }
        if (this.gesuch && this.isGesuchsteller2Required() && this.gesuch.gesuchsteller2 && !this.gesuch.gesuchsteller2.finanzielleSituationContainer) {
            this.gesuch.gesuchsteller2.finanzielleSituationContainer = new TSFinanzielleSituationContainer();
            this.gesuch.gesuchsteller2.finanzielleSituationContainer.jahr = this.getBasisjahr();
            this.gesuch.gesuchsteller2.finanzielleSituationContainer.finanzielleSituationJA = new TSFinanzielleSituation();
        }
    }

    public initEinkommensverschlechterungInfo(): void {
        if (this.gesuch && !this.gesuch.einkommensverschlechterungInfo) {
            this.gesuch.einkommensverschlechterungInfo = new TSEinkommensverschlechterungInfo();
            this.gesuch.einkommensverschlechterungInfo.ekvFuerBasisJahrPlus1 = false;
            this.gesuch.einkommensverschlechterungInfo.ekvFuerBasisJahrPlus2 = false;

        }
    }

    public initEinkommensverschlechterungContainer(basisjahrPlus: number, gesuchstellerNumber: number): void {
        if (!this.gesuch) {
            this.initGesuch(false);
        }

        this.initStammdaten();

        if (gesuchstellerNumber === 1 && this.gesuch.gesuchsteller1) {
            if (!this.gesuch.gesuchsteller1.einkommensverschlechterungContainer) {
                this.gesuch.gesuchsteller1.einkommensverschlechterungContainer = new TSEinkommensverschlechterungContainer();
            }

            if (basisjahrPlus === 1) {
                if (!this.gesuch.gesuchsteller1.einkommensverschlechterungContainer.ekvJABasisJahrPlus1) {
                    this.gesuch.gesuchsteller1.einkommensverschlechterungContainer.ekvJABasisJahrPlus1 = new TSEinkommensverschlechterung();
                }
            }

            if (basisjahrPlus === 2) {
                if (!this.gesuch.gesuchsteller1.einkommensverschlechterungContainer.ekvJABasisJahrPlus2) {
                    this.gesuch.gesuchsteller1.einkommensverschlechterungContainer.ekvJABasisJahrPlus2 = new TSEinkommensverschlechterung();
                }
            }
        }

        if (gesuchstellerNumber === 2 && this.gesuch.gesuchsteller2) {
            if (!this.gesuch.gesuchsteller2.einkommensverschlechterungContainer) {
                this.gesuch.gesuchsteller2.einkommensverschlechterungContainer = new TSEinkommensverschlechterungContainer();
            }

            if (basisjahrPlus === 1) {
                if (!this.gesuch.gesuchsteller2.einkommensverschlechterungContainer.ekvJABasisJahrPlus1) {
                    this.gesuch.gesuchsteller2.einkommensverschlechterungContainer.ekvJABasisJahrPlus1 = new TSEinkommensverschlechterung();
                }
            }

            if (basisjahrPlus === 2) {
                if (!this.gesuch.gesuchsteller2.einkommensverschlechterungContainer.ekvJABasisJahrPlus2) {
                    this.gesuch.gesuchsteller2.einkommensverschlechterungContainer.ekvJABasisJahrPlus2 = new TSEinkommensverschlechterung();
                }
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
            this.wizardStepManager.initWizardSteps();
            this.setCurrentUserAsFallVerantwortlicher();
        }
        this.backupCurrentGesuch();
    }

    /**
     * erstellt eine kopie der aktuellen gesuchsdaten die spaeter bei bedarf wieder hergestellt werden kann
     */
    private backupCurrentGesuch() {
        this.gesuchSnapshot =  angular.copy(this.gesuch);
    }

    public restoreBackupOfPreviousGesuch() {
        this.gesuch = this.gesuchSnapshot;
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


    public getBasisjahrToWorkWith(): number {
        return this.getBasisjahrPlus(this.basisJahrPlusNumber);
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

    public calculateShowDatumFlags(gesuchsteller: TSGesuchsteller): void {
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
                this.getKindFromServer();
                this.backupCurrentGesuch();
                return this.wizardStepManager.findStepsFromGesuch(this.gesuch.id).then(() => {
                    return this.setBetreuungToWorkWith(betreuungResponse);
                });
            });
            //neu -> create
        } else {
            return this.betreuungRS.createBetreuung(this.getBetreuungToWorkWith(), this.getKindToWorkWith().id).then((betreuungResponse: any) => {
                this.getKindFromServer();
                this.backupCurrentGesuch();
                return this.wizardStepManager.findStepsFromGesuch(this.gesuch.id).then(() => {
                    return this.setBetreuungToWorkWith(betreuungResponse);
                });
            });
        }
    }

    public updateKind(): IPromise<TSKindContainer> {
        if (this.getKindToWorkWith().timestampErstellt) {
            return this.kindRS.updateKind(this.getKindToWorkWith(), this.gesuch.id).then((kindResponse: any) => {
                this.setKindToWorkWith(kindResponse);
                this.getFallFromServer();
                this.backupCurrentGesuch();
                return this.wizardStepManager.findStepsFromGesuch(this.gesuch.id).then(() => {
                    return this.getKindToWorkWith();
                });
            });
        } else {
            return this.kindRS.createKind(this.getKindToWorkWith(), this.gesuch.id).then((kindResponse: any) => {
                this.setKindToWorkWith(kindResponse);
                this.getFallFromServer();
                this.backupCurrentGesuch();
                return this.wizardStepManager.findStepsFromGesuch(this.gesuch.id).then(() => {
                    return this.getKindToWorkWith();
                });
            });
        }
    }

    /**
     * Sucht das KindToWorkWith im Server und aktualisiert es mit dem bekommenen Daten
     * @returns {IPromise<TSKindContainer>}
     */
    private getKindFromServer(): IPromise<TSKindContainer> {
        return this.kindRS.findKind(this.getKindToWorkWith().id).then((kindResponse) => {
            return this.setKindToWorkWith(kindResponse);
        });
    }

    /**
     * Sucht das Gesuch im Server und aktualisiert es mit dem bekommenen Daten
     * @returns {IPromise<TResult>}
     */
    private getFallFromServer(): IPromise<TSFall> {
        return this.fallRS.findFall(this.gesuch.fall.id).then((fallResponse) => {
            return this.gesuch.fall = fallResponse;
        });
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
    public setBetreuungToWorkWith(betreuung: TSBetreuung): TSBetreuung {
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

    public getBasisJahrPlusNumber(): number {
        return this.basisJahrPlusNumber;
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

    /**
     * Sucht das Kind mit der eingegebenen KindID in allen KindContainers des Gesuchs. kindNumber wird gesetzt und zurueckgegeben
     * @param kindID
     * @returns {number}
     */
    public findKindById(kindID: string): number {
        if (this.gesuch.kindContainers) {
            for (let i = 0; i < this.gesuch.kindContainers.length; i++) {
                if (this.gesuch.kindContainers[i].id === kindID) {
                    return this.kindNumber = i + 1;
                }
            }
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

    /**
     * Sucht die Betreuung mit der eingegebenen betreuungID in allen Betreuungen des aktuellen Kind. betreuungNumber wird gesetzt und zurueckgegeben
     * @param betreuungID
     * @returns {number}
     */
    public findBetreuungById(betreuungID: string): number {
        if (this.getKindToWorkWith()) {
            for (let i = 0; i < this.getKindToWorkWith().betreuungen.length; i++) {
                if (this.getKindToWorkWith().betreuungen[i].id === betreuungID) {
                    return this.betreuungNumber = i + 1;
                }
            }
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
            return this.erwerbspensumRS.updateErwerbspensum(erwerbspensum, gesuchsteller.id, this.gesuch.id)
                .then((response: TSErwerbspensumContainer) => {
                    let i = gesuchsteller.erwerbspensenContainer.indexOf(erwerbspensum);
                    if (i >= 0) {
                        gesuchsteller.erwerbspensenContainer[i] = erwerbspensum;
                    }
                    this.backupCurrentGesuch();
                    return this.wizardStepManager.findStepsFromGesuch(this.gesuch.id).then(() => {
                        return response;
                    });
                });
        } else {
            return this.erwerbspensumRS.createErwerbspensum(erwerbspensum, gesuchsteller.id, this.gesuch.id)
                .then((storedErwerbspensum: TSErwerbspensumContainer) => {
                    gesuchsteller.erwerbspensenContainer.push(storedErwerbspensum);
                    this.backupCurrentGesuch();
                    return this.wizardStepManager.findStepsFromGesuch(this.gesuch.id).then(() => {
                        return storedErwerbspensum;
                    });
                });
        }

    }

    /**
     * Takes current user and sets it as the verantwortlicher of Fall
     */
    private setCurrentUserAsFallVerantwortlicher() {
        if (this.authServiceRS) {
            this.setUserAsFallVerantwortlicher(this.authServiceRS.getPrincipal());
        }
    }

    public setUserAsFallVerantwortlicher(user: TSUser) {
        if (this.gesuch && this.gesuch.fall) {
            this.gesuch.fall.verantwortlicher = user;
        }
    }

    public getFallVerantwortlicher(): TSUser {
        if (this.gesuch && this.gesuch.fall) {
            return this.gesuch.fall.verantwortlicher;
        }
        return undefined;
    }

    public calculateVerfuegungen(): void {
        this.verfuegungRS.calculateVerfuegung(this.gesuch.id)
            .then((response: TSKindContainer[]) => {
                this.kinderWithBetreuungList = response;
            });
    }

    public getVerfuegenToWorkWith(): TSVerfuegung {
        if (this.getKindToWorkWith() && this.getBetreuungToWorkWith()) {
            for (let i = 0; i < this.kinderWithBetreuungList.length; i++) {
                if (this.kinderWithBetreuungList[i].id === this.getKindToWorkWith().id) {
                    for (let j = 0; j < this.kinderWithBetreuungList[i].betreuungen.length; j++) {
                        if (this.kinderWithBetreuungList[i].betreuungen[j].id === this.getBetreuungToWorkWith().id) {
                            return this.kinderWithBetreuungList[i].betreuungen[j].verfuegung;
                        }
                    }
                }
            }
        }
        return undefined;
    }
}
