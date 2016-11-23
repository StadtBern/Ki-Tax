import TSFall from '../../models/TSFall';
import TSGesuch from '../../models/TSGesuch';
import TSGesuchsteller from '../../models/TSGesuchsteller';
import TSAdresse from '../../models/TSAdresse';
import {TSAdressetyp} from '../../models/enums/TSAdressetyp';
import TSFamiliensituation from '../../models/TSFamiliensituation';
import FallRS from './fallRS.rest';
import GesuchRS from './gesuchRS.rest';
import GesuchstellerRS from '../../core/service/gesuchstellerRS.rest';
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
import BetreuungRS from '../../core/service/betreuungRS.rest';
import {TSBetreuungsstatus} from '../../models/enums/TSBetreuungsstatus';
import TSGesuchsperiode from '../../models/TSGesuchsperiode';
import GesuchsperiodeRS from '../../core/service/gesuchsperiodeRS.rest';
import AuthServiceRS from '../../authentication/service/AuthServiceRS.rest';
import TSEinkommensverschlechterungInfo from '../../models/TSEinkommensverschlechterungInfo';
import TSUser from '../../models/TSUser';
import VerfuegungRS from '../../core/service/verfuegungRS.rest';
import TSVerfuegung from '../../models/TSVerfuegung';
import WizardStepManager from './wizardStepManager';
import EinkommensverschlechterungInfoRS from './einkommensverschlechterungInfoRS.rest';
import {TSAntragStatus} from '../../models/enums/TSAntragStatus';
import AntragStatusHistoryRS from '../../core/service/antragStatusHistoryRS.rest';
import {TSWizardStepName} from '../../models/enums/TSWizardStepName';
import {TSWizardStepStatus} from '../../models/enums/TSWizardStepStatus';
import {TSAntragTyp} from '../../models/enums/TSAntragTyp';
import EbeguUtil from '../../utils/EbeguUtil';
import ErrorService from '../../core/errors/service/ErrorService';
import TSExceptionReport from '../../models/TSExceptionReport';
import {TSErrorType} from '../../models/enums/TSErrorType';
import {TSErrorLevel} from '../../models/enums/TSErrorLevel';
import AdresseRS from '../../core/service/adresseRS.rest';
import IQService = angular.IQService;

export default class GesuchModelManager {
    private gesuch: TSGesuch;
    gesuchSnapshot: TSGesuch;
    gesuchstellerNumber: number = 1;
    basisJahrPlusNumber: number = 1;
    private kindNumber: number;
    private betreuungNumber: number;
    private fachstellenList: Array<TSFachstelle>;
    private activInstitutionenList : Array<TSInstitutionStammdaten>;
    private activeGesuchsperiodenList: Array<TSGesuchsperiode>;


    static $inject = ['FamiliensituationRS', 'FallRS', 'GesuchRS', 'GesuchstellerRS', 'FinanzielleSituationRS', 'KindRS', 'FachstelleRS',
        'ErwerbspensumRS', 'InstitutionStammdatenRS', 'BetreuungRS', 'GesuchsperiodeRS', 'EbeguRestUtil', '$log', 'AuthServiceRS',
        'EinkommensverschlechterungContainerRS', 'VerfuegungRS', 'WizardStepManager', 'EinkommensverschlechterungInfoRS',
        'AntragStatusHistoryRS', 'EbeguUtil', 'ErrorService', 'AdresseRS', '$q'];
    /* @ngInject */
    constructor(private familiensituationRS: FamiliensituationRS, private fallRS: FallRS, private gesuchRS: GesuchRS, private gesuchstellerRS: GesuchstellerRS,
                private finanzielleSituationRS: FinanzielleSituationRS, private kindRS: KindRS, private fachstelleRS: FachstelleRS, private erwerbspensumRS: ErwerbspensumRS,
                private instStamRS: InstitutionStammdatenRS, private betreuungRS: BetreuungRS, private gesuchsperiodeRS: GesuchsperiodeRS,
                private ebeguRestUtil: EbeguRestUtil, private log: ILogService, private authServiceRS: AuthServiceRS,
                private einkommensverschlechterungContainerRS: EinkommensverschlechterungContainerRS, private verfuegungRS: VerfuegungRS,
                private wizardStepManager: WizardStepManager, private einkommensverschlechterungInfoRS: EinkommensverschlechterungInfoRS,
                private antragStatusHistoryRS: AntragStatusHistoryRS, private ebeguUtil: EbeguUtil, private errorService: ErrorService,
                private adresseRS: AdresseRS, private $q: IQService) {

        this.fachstellenList = [];
        this.activInstitutionenList = [];
        this.activeGesuchsperiodenList = [];
        this.updateFachstellenList();
        this.updateActiveInstitutionenList();
        this.updateActiveGesuchsperiodenList();
    }


    public openGesuch(gesuchId: string): IPromise<TSGesuch> {
        return this.gesuchRS.findGesuch(gesuchId)
            .then((response) => {
                if (response) {
                    this.setGesuch(response);
                    this.setHiddenSteps();
                }
                return response;
            });
    }

    /**
     * Mit den Daten vom Gesuch, werden die entsprechenden Steps der Liste hiddenSteps hinzugefuegt.
     * Oder ggf. aus der Liste entfernt
     */
    private setHiddenSteps(): void {
        //Abwesenheit
        if (!this.gesuch.isMutation()) {
            this.wizardStepManager.hideStep(TSWizardStepName.ABWESENHEIT);
        } else {
            this.wizardStepManager.unhideStep(TSWizardStepName.ABWESENHEIT);
        }

        //Umzug
        if (!this.gesuch.isMutation() && !this.getGesuch().isThereAnyUmzug()) {
            this.wizardStepManager.hideStep(TSWizardStepName.UMZUG);
        } else {
            this.wizardStepManager.unhideStep(TSWizardStepName.UMZUG);
        }
    }

    /**
     * In dieser Methode wird das Gesuch ersetzt. Das Gesuch ist jetzt private und darf nur ueber diese Methode geaendert werden.
     *
     * @param gesuch das Gesuch. Null und undefined werden erlaubt.
     */
    public setGesuch(gesuch: TSGesuch): void {
        this.gesuch = gesuch;
        this.wizardStepManager.findStepsFromGesuch(this.gesuch.id);
        this.setHiddenSteps();
    }

    public getGesuch(): TSGesuch {
        return this.gesuch;
    }

    /**
     * Prueft ob der 2. Gesuchtsteller eingetragen werden muss je nach dem was in Familiensituation ausgewaehlt wurde. Wenn es sich
     * um eine Mutation handelt wird nur geschaut ob der 2GS bereits existiert. Wenn ja, dann wird er benoetigt, da bei Mutationen darf
     * der 2GS nicht geloescht werden
     */
    public isGesuchsteller2Required(): boolean {
        if (this.gesuch && this.getFamiliensituation() && this.getFamiliensituation().familienstatus) {
            return this.getFamiliensituation().hasSecondGesuchsteller()
                || (this.gesuch.isMutation() && this.gesuch.gesuchsteller2 != null && this.gesuch.gesuchsteller2 !== undefined);
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

    public getFamiliensituationErstgesuch(): TSFamiliensituation {
        if (this.gesuch) {
            return this.gesuch.familiensituationErstgesuch;
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
    public updateActiveInstitutionenList(): void {
        this.instStamRS.getAllActiveInstitutionStammdatenByDate(DateUtil.today()).then((response: any) => {
            this.activInstitutionenList = angular.copy(response);
        });
    }

    public updateActiveGesuchsperiodenList(): void {
        this.gesuchsperiodeRS.getAllActiveGesuchsperioden().then((response: any) => {
            this.activeGesuchsperiodenList = angular.copy(response);
        });
    }

    /**
     * Wenn das Gesuch schon gespeichert ist (timestampErstellt != null), wird dieses nur aktualisiert. Wenn es sich um ein neues Gesuch handelt
     * dann wird zuerst der Fall erstellt, dieser ins Gesuch kopiert und dann das Gesuch erstellt
     * @returns {IPromise<TSGesuch>}
     */
    public saveGesuchAndFall(): IPromise<TSGesuch> {
        if (this.gesuch && this.gesuch.timestampErstellt) { //update
            return this.updateGesuch();
        } else { //create
            if (this.gesuch.fall && this.gesuch.fall.timestampErstellt) {
                // Fall ist schon vorhanden
                return this.gesuchRS.createGesuch(this.gesuch).then((gesuchResponse: any) => {
                    this.gesuch = gesuchResponse;
                    this.backupCurrentGesuch();
                    return this.gesuch;
                });
            } else {
                return this.fallRS.createFall(this.gesuch.fall).then((fallResponse: TSFall) => {
                    this.gesuch.fall = angular.copy(fallResponse);
                    return this.gesuchRS.createGesuch(this.gesuch).then((gesuchResponse: any) => {
                        this.gesuch = gesuchResponse;
                        this.backupCurrentGesuch();
                        return this.gesuch;
                    });
                });
            }
        }
    }

    public updateFamiliensituation(): IPromise<TSFamiliensituation> {
        return this.familiensituationRS.saveFamiliensituation(this.getFamiliensituation(), this.gesuch.id).then((familienResponse: any) => {
            return this.gesuchRS.findGesuch(this.gesuch.id).then((gesuchResponse: any) => {
                this.gesuch = gesuchResponse;
                this.gesuch.familiensituation = familienResponse;
                this.backupCurrentGesuch();
                return this.gesuch.familiensituation;
            });
        });
    }

    /**
     * Update das Gesuch
     * @returns {IPromise<TSGesuch>}
     */
    public updateGesuch(): IPromise<TSGesuch> {
        return this.gesuchRS.updateGesuch(this.gesuch).then((gesuchResponse: any) => {
            this.gesuch = gesuchResponse;
            this.backupCurrentGesuch();
            return this.gesuch;
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
    public updateGesuchsteller(umzug: boolean): IPromise<TSGesuchsteller> {
        // Da showUmzug nicht im Server gespeichert wird, muessen wir den alten Wert kopieren und nach der Aktualisierung wiedersetzen
        let tempShowUmzug: boolean = this.getStammdatenToWorkWith().showUmzug;
        return this.gesuchstellerRS.saveGesuchsteller(this.getStammdatenToWorkWith(), this.gesuch.id, this.gesuchstellerNumber, umzug)
            .then((gesuchstellerResponse: any) => {
                this.setStammdatenToWorkWith(gesuchstellerResponse);
                this.backupCurrentGesuch();
                return this.gesuchRS.updateGesuch(this.gesuch).then(() => {
                    this.backupCurrentGesuch();
                    //todo reviewer frage team: muessen wir hier das gesuch wirklich separat speichern? wir brauchen die antwort gar nicht
                    this.getStammdatenToWorkWith().showUmzug = tempShowUmzug;
                    return this.getStammdatenToWorkWith();
                });
            });
    }

    public saveFinanzielleSituation(): IPromise<TSFinanzielleSituationContainer> {
        return this.finanzielleSituationRS.saveFinanzielleSituation(
            this.getStammdatenToWorkWith().finanzielleSituationContainer, this.getStammdatenToWorkWith().id, this.gesuch.id)
            .then((finSitContRespo: TSFinanzielleSituationContainer) => {
                this.getStammdatenToWorkWith().finanzielleSituationContainer = finSitContRespo;
                this.backupCurrentGesuch();
                return this.getStammdatenToWorkWith().finanzielleSituationContainer;
            });
    }

    public saveEinkommensverschlechterungContainer(): IPromise<TSEinkommensverschlechterungContainer> {
        return this.einkommensverschlechterungContainerRS.saveEinkommensverschlechterungContainer(
            this.getStammdatenToWorkWith().einkommensverschlechterungContainer, this.getStammdatenToWorkWith().id, this.gesuch.id)
            .then((ekvContRespo: TSEinkommensverschlechterungContainer) => {
                this.getStammdatenToWorkWith().einkommensverschlechterungContainer = ekvContRespo;
                this.backupCurrentGesuch();
                return this.getStammdatenToWorkWith().einkommensverschlechterungContainer;
            });
    }

    public updateEinkommensverschlechterungsInfo(): IPromise<TSEinkommensverschlechterungInfo> {
        return this.einkommensverschlechterungInfoRS.saveEinkommensverschlechterungInfo(
            this.getGesuch().einkommensverschlechterungInfo, this.gesuch.id)
            .then((ekvInfoRespo: TSEinkommensverschlechterungInfo) => {
                this.getGesuch().einkommensverschlechterungInfo = ekvInfoRespo;
                this.backupCurrentGesuch();
                return this.getGesuch().einkommensverschlechterungInfo;
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

    public getActiveInstitutionenList(): Array<TSInstitutionStammdaten> {
        return this.activInstitutionenList;
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
        if (this.gesuchstellerNumber === 2) {
            return this.getEkvFromGesuchstellerOfBsj_JA(this.gesuch.gesuchsteller2);
        } else {
            return this.getEkvFromGesuchstellerOfBsj_JA(this.gesuch.gesuchsteller1);
        }
    }

    public getEinkommensverschlechterungToWorkWith_GS(): TSEinkommensverschlechterung {
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
        if (this.gesuchstellerNumber === 1) {
            return this.gesuch.gesuchsteller1 = gesuchsteller;
        } else {
            return this.gesuch.gesuchsteller2 = gesuchsteller;
        }
    }

    public initStammdaten(): void {
        if (!this.getStammdatenToWorkWith()) {
            this.setStammdatenToWorkWith(new TSGesuchsteller());
            this.getStammdatenToWorkWith().adressen = this.initWohnAdresse();
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

    public getEinkommensverschlechterungsInfo(): TSEinkommensverschlechterungInfo {
        if (this.getGesuch().einkommensverschlechterungInfo == null) {
            this.initEinkommensverschlechterungInfo();
        }
        return this.getGesuch().einkommensverschlechterungInfo;
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
            this.initAntrag(TSAntragTyp.GESUCH);
        }
        this.antragStatusHistoryRS.loadLastStatusChange(this.getGesuch());
        this.backupCurrentGesuch();
    }

    /**
     * Diese Methode erstellt eine Fake-Mutation als gesuch fuer das GesuchModelManager. Die Mutation ist noch leer und hat
     * das ID des Gesuchs aus dem sie erstellt wurde. Wenn der Benutzer auf speichern klickt, wird der Service "antragMutieren"
     * mit dem ID des alten Gesuchs aufgerufen. Das Objekt das man zurueckbekommt, wird dann diese Fake-Mutation mit den richtigen
     * Daten ueberschreiben
     * @param gesuchID
     */
    public initMutation(gesuchID: string): void {
        let gesuchsperiode: TSGesuchsperiode = this.gesuch.gesuchsperiode;
        this.initAntrag(TSAntragTyp.MUTATION);
        this.gesuch.id = gesuchID; //setzen wir das alte gesuchID, um danach im Server die Mutation erstellen zu koennen
        this.gesuch.gesuchsperiode = gesuchsperiode;
    }

    private initAntrag(antragTyp: TSAntragTyp): void {
        this.gesuch = new TSGesuch();
        this.gesuch.fall = new TSFall();
        this.gesuch.typ = antragTyp; // by default ist es ein Erstgesuch
        this.gesuch.status = TSAntragStatus.IN_BEARBEITUNG_JA; //TODO (team) wenn der GS das Gesuch erstellt, kommt hier IN_BEARBEITUN_GS
        this.setHiddenSteps();
        this.wizardStepManager.initWizardSteps();
        this.setCurrentUserAsFallVerantwortlicher();
    }

    /**
     * erstellt eine kopie der aktuellen gesuchsdaten die spaeter bei bedarf wieder hergestellt werden kann
     */
    private backupCurrentGesuch() {
        this.gesuchSnapshot = angular.copy(this.gesuch);
        this.wizardStepManager.backupCurrentSteps();
    }

    public restoreBackupOfPreviousGesuch() {
        this.gesuch = this.gesuchSnapshot;
        this.wizardStepManager.restorePreviousSteps();
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

    private initWohnAdresse(): Array<TSAdresse> {
        let wohnAdresse = new TSAdresse();
        wohnAdresse.showDatumVon = false;
        wohnAdresse.adresseTyp = TSAdressetyp.WOHNADRESSE;
        return [wohnAdresse];
    }

    private initKorrespondenzAdresse(): TSAdresse {
        let korrAdr = new TSAdresse();
        korrAdr.showDatumVon = false;
        korrAdr.adresseTyp = TSAdressetyp.KORRESPONDENZADRESSE;
        return korrAdr;
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
        var tsKindContainer = new TSKindContainer(undefined, new TSKind());
        this.gesuch.kindContainers.push(tsKindContainer);
        this.kindNumber = this.gesuch.kindContainers.length;
        tsKindContainer.kindNummer = this.kindNumber;
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
            tsBetreuung.betreuungNummer = this.betreuungNumber;
        }
    }

    public updateBetreuung(abwesenheit: boolean): IPromise<TSBetreuung> {
        return this.betreuungRS.saveBetreuung(this.getBetreuungToWorkWith(), this.getKindToWorkWith().id, this.gesuch.id, abwesenheit)
            .then((betreuungResponse: any) => {
                this.getKindFromServer();
                this.backupCurrentGesuch();
                return this.setBetreuungToWorkWith(betreuungResponse);
            });
    }

    public updateKind(): IPromise<TSKindContainer> {
        return this.kindRS.saveKind(this.getKindToWorkWith(), this.gesuch.id).then((kindResponse: any) => {
            this.setKindToWorkWith(kindResponse);
            this.getFallFromServer();
            this.backupCurrentGesuch();
            return this.getKindToWorkWith();
        });
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
     * @returns {IPromise<void>}
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

    public removeKind(): IPromise<void> {
        return this.kindRS.removeKind(this.getKindToWorkWith().id, this.gesuch.id).then((responseKind: any) => {
            this.removeKindFromList();
            this.backupCurrentGesuch();
            this.gesuchRS.updateGesuch(this.gesuch);
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

    public removeBetreuung(): IPromise<void> {
        return this.betreuungRS.removeBetreuung(this.getBetreuungToWorkWith().id, this.gesuch.id).then((responseBetreuung: any) => {
            this.removeBetreuungFromKind();
            this.backupCurrentGesuch();
            this.kindRS.saveKind(this.getKindToWorkWith(), this.gesuch.id);
        });
    }


    public removeErwerbspensum(pensum: TSErwerbspensumContainer): void {
        let erwerbspensenOfCurrentGS: Array<TSErwerbspensumContainer>;
        erwerbspensenOfCurrentGS = this.getStammdatenToWorkWith().erwerbspensenContainer;
        let index: number = erwerbspensenOfCurrentGS.indexOf(pensum);
        if (index >= 0) {
            let pensumToRemove: TSErwerbspensumContainer = this.getStammdatenToWorkWith().erwerbspensenContainer[index];
            if (pensumToRemove.id) { //wenn id vorhanden dann aus der DB loeschen
                this.erwerbspensumRS.removeErwerbspensum(pensumToRemove.id, this.getGesuch().id)
                    .then(() => {
                        erwerbspensenOfCurrentGS.splice(index, 1);
                        this.backupCurrentGesuch();
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
            return this.erwerbspensumRS.saveErwerbspensum(erwerbspensum, gesuchsteller.id, this.gesuch.id)
                .then((response: TSErwerbspensumContainer) => {
                    let i = gesuchsteller.erwerbspensenContainer.indexOf(erwerbspensum);
                    if (i >= 0) {
                        gesuchsteller.erwerbspensenContainer[i] = erwerbspensum;
                    }
                    this.backupCurrentGesuch();
                    return response;
                });
        } else {
            return this.erwerbspensumRS.saveErwerbspensum(erwerbspensum, gesuchsteller.id, this.gesuch.id)
                .then((storedErwerbspensum: TSErwerbspensumContainer) => {
                    gesuchsteller.erwerbspensenContainer.push(storedErwerbspensum);
                    this.backupCurrentGesuch();
                    return storedErwerbspensum;
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

    public calculateVerfuegungen(): IPromise<void> {
        return this.verfuegungRS.calculateVerfuegung(this.gesuch.id)
            .then((response: TSKindContainer[]) => {
                this.updateKinderListWithCalculatedVerfuegungen(response);
                this.backupCurrentGesuch();
                return;
            });
    }

    private updateKinderListWithCalculatedVerfuegungen(kinderWithVerfuegungen: TSKindContainer[]) {
        if (kinderWithVerfuegungen.length !== this.gesuch.kindContainers.length) {
            let msg: string = 'ACHTUNG Ungueltiger Zustand, Anzahl zurueckgelieferter Container'
                + (kinderWithVerfuegungen.length ? kinderWithVerfuegungen.length : 'no_container')
                + 'stimmt nicht mit erwareter ueberein ' + this.gesuch.kindContainers.length;
            this.log.error(msg);
            let error: TSExceptionReport = new TSExceptionReport(TSErrorType.INTERNAL, TSErrorLevel.SEVERE, msg, kinderWithVerfuegungen);
            this.errorService.addDvbError(error);
        }
        //todo beim fragen warum nicht einfach die ganze liste austauschen? Wegen der Reihenfolge?
        let numOfAssigned = 0;
        for (let i = 0; i < this.gesuch.kindContainers.length; i++) {
            for (let j = 0; j < kinderWithVerfuegungen.length; j++) {
                if (this.gesuch.kindContainers[i].id === kinderWithVerfuegungen[j].id) {
                    numOfAssigned++;
                    for (let k = 0; k < this.gesuch.kindContainers[i].betreuungen.length; k++) {
                        if (this.gesuch.kindContainers[i].betreuungen.length !== kinderWithVerfuegungen[j].betreuungen.length) {
                            let msg = 'ACHTUNG unvorhergesehener Zustand. Anzahl Betreuungen eines Kindes stimmt nicht' +
                                ' mit der berechneten Anzahl Betreuungen ueberein; erwartet: ' +
                                this.gesuch.kindContainers[i].betreuungen.length + ' erhalten: ' + kinderWithVerfuegungen[j].betreuungen.length;
                            this.log.error(msg, this.gesuch.kindContainers[i], kinderWithVerfuegungen[j]);
                            this.errorService.addMesageAsError(msg);
                        }
                        this.gesuch.kindContainers[i].betreuungen[k] = kinderWithVerfuegungen[j].betreuungen[k];
                    }
                }
            }
        }
        if (numOfAssigned !== this.gesuch.kindContainers.length) {
            let msg = 'ACHTUNG unvorhergesehener Zustand. Es konnte nicht jeder calculated Kindcontainer vom Server einem Container auf dem Client zugeordnet werden';
            this.log.error(msg, this.gesuch.kindContainers, kinderWithVerfuegungen);

            this.errorService.addMesageAsError(msg);
        }
        this.ebeguUtil.handleSmarttablesUpdateBug(this.gesuch.kindContainers);

    }

    public saveVerfuegung(): IPromise<TSVerfuegung> {
        return this.verfuegungRS.saveVerfuegung(this.getVerfuegenToWorkWith(), this.gesuch.id, this.getBetreuungToWorkWith().id).then((response: TSVerfuegung) => {
            this.setVerfuegenToWorkWith(response);
            this.getBetreuungToWorkWith().betreuungsstatus = TSBetreuungsstatus.VERFUEGT;
            this.calculateGesuchStatus();
            this.backupCurrentGesuch();
            return this.getVerfuegenToWorkWith();
        });
    }

    private calculateGesuchStatus() {
        if (!this.isThereAnyOpenBetreuung()) {
            this.gesuch.status = this.calculateNewStatus(TSAntragStatus.VERFUEGT);
        }
    }

    public verfuegungSchliessenOhenVerfuegen(): IPromise<void> {
        return this.verfuegungRS.verfuegungSchliessenOhneVerfuegen(this.gesuch.id, this.getBetreuungToWorkWith().id).then((response) => {
            this.getBetreuungToWorkWith().betreuungsstatus = TSBetreuungsstatus.GESCHLOSSEN_OHNE_VERFUEGUNG;
            this.calculateGesuchStatus();
            this.backupCurrentGesuch();
            return;
        });
    }

    public verfuegungSchliessenNichtEintreten(): IPromise<void> {
        return this.verfuegungRS.nichtEintreten(this.gesuch.id, this.getBetreuungToWorkWith().id).then((response) => {
            this.getBetreuungToWorkWith().betreuungsstatus = TSBetreuungsstatus.NICHT_EINGETRETEN;
            this.calculateGesuchStatus();
            this.backupCurrentGesuch();
            return;
        });
    }

    public getVerfuegenToWorkWith(): TSVerfuegung {
        if (this.getKindToWorkWith() && this.getBetreuungToWorkWith()) {
            return this.getBetreuungToWorkWith().verfuegung;
        }
        return undefined;
    }

    public setVerfuegenToWorkWith(verfuegung: TSVerfuegung): void {
        if (this.getKindToWorkWith() && this.getBetreuungToWorkWith()) {
            this.getBetreuungToWorkWith().verfuegung = verfuegung;
        }
    }

    public isThereAnyKindWithBetreuungsbedarf(): boolean {
        let kinderList: Array<TSKindContainer> = this.getKinderList();
        for (let kind of kinderList) {
            //das kind muss schon gespeichert sein damit es zahelt
            if (kind.kindJA.familienErgaenzendeBetreuung && !kind.kindJA.isNew()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Schaut dass mindestens eine Betreuung erfasst wurde.
     * @returns {boolean}
     */
    public isThereAnyBetreuung(): boolean {
        let kinderWithBetreuungList: Array<TSKindContainer> = this.getKinderWithBetreuungList();
        for (let kind of kinderWithBetreuungList) {
            if (kind.betreuungen && kind.betreuungen.length > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gibt true zurueck wenn es mindestens eine Betreuung gibt, dessen Status anders als VERFUEGT oder GESCHLOSSEN_OHNE_VERFUEGUNG oder SCHULAMT ist
     * @returns {boolean}
     */
    public isThereAnyOpenBetreuung(): boolean {
        let kinderWithBetreuungList: Array<TSKindContainer> = this.getKinderWithBetreuungList();
        for (let kind of kinderWithBetreuungList) {
            for (let betreuung of kind.betreuungen) {
                if (betreuung.betreuungsstatus !== TSBetreuungsstatus.SCHULAMT
                    && betreuung.betreuungsstatus !== TSBetreuungsstatus.VERFUEGT
                    && betreuung.betreuungsstatus !== TSBetreuungsstatus.NICHT_EINGETRETEN
                    && betreuung.betreuungsstatus !== TSBetreuungsstatus.GESCHLOSSEN_OHNE_VERFUEGUNG) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Setzt den Status des Gesuchs und speichert es in der Datenbank. Anstatt das ganze Gesuch zu schicken, rufen wir den Service auf
     * der den Status aktualisiert und erst wenn das geklappt hat, aktualisieren wir den Status auf dem Client.
     * Wird nur durchgefuehrt, wenn der gegebene Status nicht der aktuelle Status ist
     * @param status
     * @returns {IPromise<TSAntragStatus>}
     */
    public saveGesuchStatus(status: TSAntragStatus): IPromise<TSAntragStatus> {
        if (!this.isGesuchStatus(status)) {
            return this.gesuchRS.updateGesuchStatus(this.gesuch.id, status).then(() => {
                return this.antragStatusHistoryRS.loadLastStatusChange(this.getGesuch()).then(() => {
                    this.gesuch.status = this.calculateNewStatus(status);
                    this.backupCurrentGesuch();
                    return this.gesuch.status;
                });
            });
        }
        return undefined;
    }

    /**
     * Returns true if the Gesuch has the given status
     * @param status
     * @returns {boolean}
     */
    public isGesuchStatus(status: TSAntragStatus): boolean {
        return this.gesuch.status === status;
    }

    /**
     * Returns true when the status of the Gesuch is VERFUEGEN or VERFUEGT
     * @returns {boolean}
     */
    public isGesuchStatusVerfuegenVerfuegt(): boolean {
        return this.isGesuchStatus(TSAntragStatus.VERFUEGEN) || this.isGesuchStatus(TSAntragStatus.VERFUEGT);
    }

    /**
     * Einige Status wie GEPRUEFT haben "substatus" auf dem Client die berechnet werden muessen. Aus diesem Grund rufen wir
     * diese Methode auf, bevor wir den Wert setzen.
     * @param status
     */
    public calculateNewStatus(status: TSAntragStatus): TSAntragStatus {
        if (TSAntragStatus.GEPRUEFT === status || TSAntragStatus.PLATZBESTAETIGUNG_ABGEWIESEN === status || TSAntragStatus.PLATZBESTAETIGUNG_WARTEN === status) {
            if (this.wizardStepManager.hasStepGivenStatus(TSWizardStepName.BETREUUNG, TSWizardStepStatus.NOK)) {
                if (this.isThereAnyBetreuung()) {
                    return TSAntragStatus.PLATZBESTAETIGUNG_ABGEWIESEN;
                } else {
                    return TSAntragStatus.GEPRUEFT;
                }
            } else if (this.wizardStepManager.hasStepGivenStatus(TSWizardStepName.BETREUUNG, TSWizardStepStatus.PLATZBESTAETIGUNG)) {
                return TSAntragStatus.PLATZBESTAETIGUNG_WARTEN;
            } else if (this.wizardStepManager.hasStepGivenStatus(TSWizardStepName.BETREUUNG, TSWizardStepStatus.OK)) {
                return TSAntragStatus.GEPRUEFT;
            }
        }
        return status;
    }

    /**
     * Gibt true zurueck, wenn der Antrag ein Erstgesuchist. False bekommt man wenn der Antrag eine Mutation ist
     * By default (beim Fehler oder leerem Gesuch) wird auch true zurueckgegeben
     */
    public isErstgesuch(): boolean {
        if (this.gesuch) {
            return this.gesuch.typ === TSAntragTyp.GESUCH;
        }
        return true;
    }

    public saveMutation(): IPromise<TSGesuch> {
        return this.gesuchRS.antragMutieren(this.gesuch.id, this.gesuch.eingangsdatum)
            .then((response: TSGesuch) => {
                this.setGesuch(response);
                return this.wizardStepManager.findStepsFromGesuch(response.id).then(() => {
                    this.backupCurrentGesuch();
                    return this.getGesuch();
                });
            });
    }

    /**
     * Aktualisiert alle gegebenen Betreuungen.
     * ACHTUNG. Die Betreuungen muessen existieren damit alles richtig funktioniert
     */
    public updateBetreuungen(betreuungenToUpdate: Array<TSBetreuung>, saveForAbwesenheit: boolean): IPromise<Array<TSBetreuung>> {
        if (betreuungenToUpdate && betreuungenToUpdate.length > 0) {
            return this.betreuungRS.saveBetreuungen(betreuungenToUpdate, this.gesuch.id, saveForAbwesenheit).then((updatedBetreuungen: Array<TSBetreuung>) => {
                //update data of Betreuungen
                this.gesuch.kindContainers.forEach((kindContainer: TSKindContainer) => {
                    for (let i = 0; i < kindContainer.betreuungen.length; i++) {
                        let indexOfUpdatedBetreuung = this.wasBetreuungUpdated(kindContainer.betreuungen[i], updatedBetreuungen);
                        if (indexOfUpdatedBetreuung >= 0) {
                            kindContainer.betreuungen[i] = updatedBetreuungen[indexOfUpdatedBetreuung];
                        }
                    }
                });
                return updatedBetreuungen;
            });
        } else {
            let defer = this.$q.defer();
            defer.resolve();
            return defer.promise;
        }
    }

    private wasBetreuungUpdated(betreuung: TSBetreuung, updatedBetreuungen: Array<TSBetreuung>): number {
        if (betreuung && updatedBetreuungen) {
            for (let i = 0; i < updatedBetreuungen.length; i++) {
                if (updatedBetreuungen[i].id === betreuung.id) {
                    return i;
                }
            }
        }
        return -1;
    }
}
