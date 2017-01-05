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
import TSFinanzielleSituationContainer from '../../models/TSFinanzielleSituationContainer';
import TSEinkommensverschlechterungContainer from '../../models/TSEinkommensverschlechterungContainer';
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
import TSUser from '../../models/TSUser';
import VerfuegungRS from '../../core/service/verfuegungRS.rest';
import TSVerfuegung from '../../models/TSVerfuegung';
import WizardStepManager from './wizardStepManager';
import EinkommensverschlechterungInfoRS from './einkommensverschlechterungInfoRS.rest';
import {
    TSAntragStatus,
    isAtLeastFreigegebenOrFreigabequittung,
    isStatusVerfuegenVerfuegt,
    isAtLeastFreigegeben
} from '../../models/enums/TSAntragStatus';
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
import {TSRole} from '../../models/enums/TSRole';
import {TSRoleUtil} from '../../utils/TSRoleUtil';
import {isJugendamt, isSchulamt} from '../../models/enums/TSBetreuungsangebotTyp';
import {TSEingangsart} from '../../models/enums/TSEingangsart';
import TSEinkommensverschlechterungInfoContainer from '../../models/TSEinkommensverschlechterungInfoContainer';
import TSFamiliensituationContainer from '../../models/TSFamiliensituationContainer';
import TSGesuchstellerContainer from '../../models/TSGesuchstellerContainer';
import TSAdresseContainer from '../../models/TSAdresseContainer';
import IQService = angular.IQService;

export default class GesuchModelManager {
    private gesuch: TSGesuch;
    gesuchstellerNumber: number = 1;
    basisJahrPlusNumber: number = 1;
    private kindNumber: number;
    private betreuungNumber: number;
    private fachstellenList: Array<TSFachstelle>;
    private activInstitutionenList: Array<TSInstitutionStammdaten>;
    private activeGesuchsperiodenList: Array<TSGesuchsperiode>;


    static $inject = ['FamiliensituationRS', 'FallRS', 'GesuchRS', 'GesuchstellerRS', 'FinanzielleSituationRS', 'KindRS', 'FachstelleRS',
        'ErwerbspensumRS', 'InstitutionStammdatenRS', 'BetreuungRS', 'GesuchsperiodeRS', 'EbeguRestUtil', '$log', 'AuthServiceRS',
        'EinkommensverschlechterungContainerRS', 'VerfuegungRS', 'WizardStepManager', 'EinkommensverschlechterungInfoRS',
        'AntragStatusHistoryRS', 'EbeguUtil', 'ErrorService', 'AdresseRS', '$q', 'CONSTANTS'];
    /* @ngInject */
    constructor(private familiensituationRS: FamiliensituationRS, private fallRS: FallRS, private gesuchRS: GesuchRS, private gesuchstellerRS: GesuchstellerRS,
                private finanzielleSituationRS: FinanzielleSituationRS, private kindRS: KindRS, private fachstelleRS: FachstelleRS, private erwerbspensumRS: ErwerbspensumRS,
                private instStamRS: InstitutionStammdatenRS, private betreuungRS: BetreuungRS, private gesuchsperiodeRS: GesuchsperiodeRS,
                private ebeguRestUtil: EbeguRestUtil, private log: ILogService, private authServiceRS: AuthServiceRS,
                private einkommensverschlechterungContainerRS: EinkommensverschlechterungContainerRS, private verfuegungRS: VerfuegungRS,
                private wizardStepManager: WizardStepManager, private einkommensverschlechterungInfoRS: EinkommensverschlechterungInfoRS,
                private antragStatusHistoryRS: AntragStatusHistoryRS, private ebeguUtil: EbeguUtil, private errorService: ErrorService,
                private adresseRS: AdresseRS, private $q: IQService, private CONSTANTS: any) {

        this.fachstellenList = [];
        this.activInstitutionenList = [];
        this.activeGesuchsperiodenList = [];
        this.updateFachstellenList();
        this.updateActiveInstitutionenList();
        this.updateActiveGesuchsperiodenList();
    }


    /**
     * Je nach dem welche Rolle der Benutzer hat, wird das Gesuch aus der DB anders geholt.
     * Fuer Institutionen z.B. wird das Gesuch nur mit den relevanten Daten geholt
     */
    public openGesuch(gesuchId: string): IPromise<TSGesuch> {
        if (this.authServiceRS.isOneOfRoles(TSRoleUtil.getTraegerschaftInstitutionRoles())) {
            return this.gesuchRS.findGesuchForInstitution(gesuchId)
                .then((response: TSGesuch) => {
                    return this.wizardStepManager.findStepsFromGesuch(gesuchId).then(bla => {
                        if (response) {
                            this.setGesuch(response);
                        }
                        return response;
                    });
                });
        } else {
            return this.gesuchRS.findGesuch(gesuchId)
                .then((response: TSGesuch) => {
                    return this.wizardStepManager.findStepsFromGesuch(gesuchId).then(bla => {
                        if (response) {
                            this.setGesuch(response);
                        }
                        return response;
                    });
                });
        }
    }

    /**
     * Mit den Daten vom Gesuch, werden die entsprechenden Steps der Liste hiddenSteps hinzugefuegt.
     * Oder ggf. aus der Liste entfernt
     */
    private setHiddenSteps(): void {
        //Freigabe
        if (this.gesuch.isOnlineGesuch()) {
            this.wizardStepManager.unhideStep(TSWizardStepName.FREIGABE);
        } else {
            this.wizardStepManager.hideStep(TSWizardStepName.FREIGABE);
        }

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

    public isRequiredEKV_GS_BJ(gs: number, bj: number): boolean {
        if(gs === 2){
            return this.getEkvFuerBasisJahrPlus(bj) && this.isGesuchsteller2Required();
        }else{
            return this.getEkvFuerBasisJahrPlus(bj);
        }


    }

    public getFamiliensituation(): TSFamiliensituation {
        if (this.gesuch) {
            return this.gesuch.extractFamiliensituation();
        }
        return undefined;
    }

    public getFamiliensituationErstgesuch(): TSFamiliensituation {
        if (this.gesuch) {
            return this.gesuch.extractFamiliensituationErstgesuch();
        }
        return undefined;
    }

    public updateFachstellenList(): void {
        this.fachstelleRS.getAllFachstellen().then((response: TSFachstelle[]) => {
            this.fachstellenList = response;
        });
    }

    /**
     * Retrieves the list of InstitutionStammdaten for the date of today.
     */
    public updateActiveInstitutionenList(): void {
        this.instStamRS.getAllActiveInstitutionStammdatenByDate(DateUtil.today()).then((response: TSInstitutionStammdaten[]) => {
            this.activInstitutionenList = response;
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
                    return this.gesuch;
                });
            } else {
                return this.fallRS.createFall(this.gesuch.fall).then((fallResponse: TSFall) => {
                    this.gesuch.fall = angular.copy(fallResponse);
                    return this.gesuchRS.createGesuch(this.gesuch).then((gesuchResponse: any) => {
                        this.gesuch = gesuchResponse;
                        return this.gesuch;
                    });
                });
            }
        }
    }

    public reloadGesuch(): IPromise<TSGesuch> {
        return this.gesuchRS.findGesuch(this.gesuch.id).then((gesuchResponse: any) => {
            return this.gesuch = gesuchResponse;
        });
    }

    /**
     * Update das Gesuch
     * @returns {IPromise<TSGesuch>}
     */
    public updateGesuch(): IPromise<TSGesuch> {
        return this.gesuchRS.updateGesuch(this.gesuch).then((gesuchResponse: any) => {
            this.gesuch = gesuchResponse;
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
    public updateGesuchsteller(umzug: boolean): IPromise<TSGesuchstellerContainer> {
        // Da showUmzug nicht im Server gespeichert wird, muessen wir den alten Wert kopieren und nach der Aktualisierung wiedersetzen
        let tempShowUmzug: boolean = this.getStammdatenToWorkWith().showUmzug;
        return this.gesuchstellerRS.saveGesuchsteller(this.getStammdatenToWorkWith(), this.gesuch.id, this.gesuchstellerNumber, umzug)
            .then((gesuchstellerResponse: any) => {
                this.setStammdatenToWorkWith(gesuchstellerResponse);
                return this.gesuchRS.updateGesuch(this.gesuch).then(() => {
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
                return this.getStammdatenToWorkWith().finanzielleSituationContainer;
            });
    }

    public saveEinkommensverschlechterungContainer(): IPromise<TSEinkommensverschlechterungContainer> {
        return this.einkommensverschlechterungContainerRS.saveEinkommensverschlechterungContainer(
            this.getStammdatenToWorkWith().einkommensverschlechterungContainer, this.getStammdatenToWorkWith().id, this.gesuch.id)
            .then((ekvContRespo: TSEinkommensverschlechterungContainer) => {
                this.getStammdatenToWorkWith().einkommensverschlechterungContainer = ekvContRespo;
                return this.getStammdatenToWorkWith().einkommensverschlechterungContainer;
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

    public getStammdatenToWorkWith(): TSGesuchstellerContainer {
        if (this.gesuchstellerNumber === 2) {
            return this.gesuch.gesuchsteller2;
        } else {
            return this.gesuch.gesuchsteller1;
        }
    }

    public getEkvFuerBasisJahrPlus(basisJahrPlus: number): boolean {
        if (!this.gesuch.extractEinkommensverschlechterungInfo()) {
            this.initEinkommensverschlechterungInfo();
        }

        if (basisJahrPlus === 2) {
            return this.gesuch.extractEinkommensverschlechterungInfo().ekvFuerBasisJahrPlus2;
        } else {
            return this.gesuch.extractEinkommensverschlechterungInfo().ekvFuerBasisJahrPlus1;
        }
    }

    public setStammdatenToWorkWith(gesuchsteller: TSGesuchstellerContainer): TSGesuchstellerContainer {
        if (this.gesuchstellerNumber === 1) {
            return this.gesuch.gesuchsteller1 = gesuchsteller;
        } else {
            return this.gesuch.gesuchsteller2 = gesuchsteller;
        }
    }

    public initStammdaten(): void {
        if (!this.getStammdatenToWorkWith()) {
            let gesuchsteller: TSGesuchsteller;
            // die daten die wir aus iam importiert haben werden bei gs1 abgefuellt
            if (this.gesuchstellerNumber === 1 && this.authServiceRS.isOneOfRoles(TSRoleUtil.getGesuchstellerOnlyRoles())) {
                let principal: TSUser = this.authServiceRS.getPrincipal();
                let name: string = principal ? principal.nachname : undefined;
                let vorname: string = principal ? principal.vorname : undefined;
                let email: string = principal ? principal.email : undefined;
                gesuchsteller = new TSGesuchsteller(vorname, name, undefined, undefined, email);
            } else {
                gesuchsteller = new TSGesuchsteller();
            }
            this.setStammdatenToWorkWith(new TSGesuchstellerContainer(gesuchsteller));
            this.getStammdatenToWorkWith().adressen = this.initWohnAdresse();
        }
    }

    private initEinkommensverschlechterungInfo(): void {
        if (this.gesuch && !this.gesuch.extractEinkommensverschlechterungInfo()) {
            this.gesuch.einkommensverschlechterungInfoContainer = new TSEinkommensverschlechterungInfoContainer();
            this.gesuch.einkommensverschlechterungInfoContainer.init();
        }
    }

    /**
     * Erstellt ein neues Gesuch und einen neuen Fall. Wenn !forced sie werden nur erstellt wenn das Gesuch noch nicht erstellt wurde i.e. es null/undefined ist
     * Wenn force werden Gesuch und Fall immer erstellt. Das erstellte Gesuch ist ein PAPIER Gesuch
     */
    public initGesuch(forced: boolean, eingangsart: TSEingangsart) {
        if (forced || (!forced && !this.gesuch)) {
            this.initAntrag(TSAntragTyp.GESUCH, eingangsart);
        }
        this.antragStatusHistoryRS.loadLastStatusChange(this.getGesuch());
    }

    /**
     * Erstellt ein Gesuch mit der angegebenen Eingangsart und Gesuchsperiode
     * @param forced
     * @param eingangsart
     * @param gesuchsperiodeId
     * @param fallId
     */
    public initGesuchWithEingangsart(forced: boolean, eingangsart: TSEingangsart, gesuchsperiodeId: string, fallId: string) {
        this.initGesuch(forced, eingangsart);
        if (gesuchsperiodeId) {
            this.gesuchsperiodeRS.findGesuchsperiode(gesuchsperiodeId).then(periode => {
                this.gesuch.gesuchsperiode = periode;
            });
        }
        if (fallId) {
            this.fallRS.findFall(fallId).then(foundFall => {
                this.gesuch.fall = foundFall;
            });
        }
        if (forced) {
            if (TSEingangsart.ONLINE === eingangsart) {
                this.gesuch.status = TSAntragStatus.IN_BEARBEITUNG_GS;
            } else {
                this.gesuch.status = TSAntragStatus.IN_BEARBEITUNG_JA;
            }
        }
    }

    /**
     * Diese Methode erstellt eine Fake-Mutation als gesuch fuer das GesuchModelManager. Die Mutation ist noch leer und hat
     * das ID des Gesuchs aus dem sie erstellt wurde. Wenn der Benutzer auf speichern klickt, wird der Service "antragMutieren"
     * mit dem ID des alten Gesuchs aufgerufen. Das Objekt das man zurueckbekommt, wird dann diese Fake-Mutation mit den richtigen
     * Daten ueberschreiben
     * @param gesuchID
     * @param eingangsart
     * @param gesuchsperiodeId
     * @param fallId
     */
    public initMutation(gesuchID: string, eingangsart: TSEingangsart, gesuchsperiodeId: string, fallId: string): void {
        this.gesuchsperiodeRS.findGesuchsperiode(gesuchsperiodeId).then(periode => {
            this.gesuch.gesuchsperiode = periode;
        });
        this.initAntrag(TSAntragTyp.MUTATION, eingangsart);
        this.fallRS.findFall(fallId).then(foundFall => {
            this.gesuch.fall = foundFall;
        });
        this.gesuch.id = gesuchID; //setzen wir das alte gesuchID, um danach im Server die Mutation erstellen zu koennen
        if (TSEingangsart.ONLINE === eingangsart) {
            this.gesuch.status = TSAntragStatus.IN_BEARBEITUNG_GS;
        } else {
            this.gesuch.status = TSAntragStatus.IN_BEARBEITUNG_JA;
        }
    }

    private initAntrag(antragTyp: TSAntragTyp, eingangsart: TSEingangsart): void {
        this.gesuch = new TSGesuch();
        this.gesuch.fall = new TSFall();
        this.gesuch.typ = antragTyp; // by default ist es ein Erstgesuch
        this.gesuch.eingangsart = eingangsart;
        this.setHiddenSteps();
        this.wizardStepManager.initWizardSteps();
        this.setCurrentUserAsFallVerantwortlicher();
    }

    public initFamiliensituation() {
        if (!this.getFamiliensituation()) {
            this.gesuch.familiensituationContainer = new TSFamiliensituationContainer();
            this.gesuch.familiensituationContainer.familiensituationJA = new TSFamiliensituation();
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

    private initWohnAdresse(): Array<TSAdresseContainer> {
        let wohnAdresseContanier: TSAdresseContainer = new TSAdresseContainer();
        let wohnAdresse = new TSAdresse();
        wohnAdresse.adresseTyp = TSAdressetyp.WOHNADRESSE;
        wohnAdresseContanier.showDatumVon = false;
        wohnAdresseContanier.adresseJA = wohnAdresse;
        return [wohnAdresseContanier];
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
        if (this.gesuch) {
            listResult = this.gesuch.getKinderWithBetreuungList();
        }
        return listResult;
    }

    public createKind(): void {
        let tsKindContainer = new TSKindContainer(undefined, new TSKind());
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
                this.setBetreuungToWorkWith(betreuungResponse);
                return this.getBetreuungToWorkWith();
            });
    }

    public updateKind(): IPromise<TSKindContainer> {
        return this.kindRS.saveKind(this.getKindToWorkWith(), this.gesuch.id).then((kindResponse: any) => {
            this.setKindToWorkWith(kindResponse);
            this.getFallFromServer();
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
    public setKindToWorkWith(kind: TSKindContainer): TSKindContainer {
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
                    this.kindNumber = i + 1;
                    return this.kindNumber;
                }
            }
        }
        return -1;
    }

    public removeKind(): IPromise<void> {
        return this.kindRS.removeKind(this.getKindToWorkWith().id, this.gesuch.id).then((responseKind: any) => {
            this.removeKindFromList();
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
        let gesuchsteller: TSGesuchstellerContainer;
        gesuchsteller = gesuchstellerNumber === 2 ? this.gesuch.gesuchsteller2 : this.gesuch.gesuchsteller1;
        return gesuchsteller.erwerbspensenContainer.indexOf(pensum);
    }

    saveErwerbspensum(gesuchsteller: TSGesuchstellerContainer, erwerbspensum: TSErwerbspensumContainer): IPromise<TSErwerbspensumContainer> {
        if (erwerbspensum.id) {
            return this.erwerbspensumRS.saveErwerbspensum(erwerbspensum, gesuchsteller.id, this.gesuch.id)
                .then((response: TSErwerbspensumContainer) => {

                    let i: number = EbeguUtil.getIndexOfElementwithID(erwerbspensum, gesuchsteller.erwerbspensenContainer);
                    if (i >= 0) {
                        gesuchsteller.erwerbspensenContainer[i] = erwerbspensum;
                    }
                    return response;
                });
        } else {
            return this.erwerbspensumRS.saveErwerbspensum(erwerbspensum, gesuchsteller.id, this.gesuch.id)
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
        if (this.authServiceRS && this.authServiceRS.isOneOfRoles(TSRoleUtil.getAdministratorJugendamtRole())) {
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
            return;
        });
    }

    public verfuegungSchliessenNichtEintreten(): IPromise<TSVerfuegung> {
        return this.verfuegungRS.nichtEintreten(this.getVerfuegenToWorkWith(), this.gesuch.id, this.getBetreuungToWorkWith().id).then((response: TSVerfuegung) => {
            this.setVerfuegenToWorkWith(response);
            this.getBetreuungToWorkWith().betreuungsstatus = TSBetreuungsstatus.NICHT_EINGETRETEN;
            this.calculateGesuchStatus();
            return this.getVerfuegenToWorkWith();
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
     * Returns true when all Betreuungen are of kind SCHULAMT.
     * Returns false also if there are no Kinder with betreuungsbedarf
     */
    public areThereOnlySchulamtAngebote(): boolean {
        if (!this.gesuch) {
            return false;
        }
        return this.gesuch.areThereOnlySchulamtAngebote();
    }

    /**
     * Returns true when all Betreuungen are of kind SCHULAMT.
     * Returns false also if there are no Kinder with betreuungsbedarf
     */
    public isThereAnySchulamtAngebot(): boolean {
        let kinderWithBetreuungList: Array<TSKindContainer> = this.getKinderWithBetreuungList();
        if (kinderWithBetreuungList.length <= 0) {
            return false; // no Kind with bedarf
        }
        for (let kind of kinderWithBetreuungList) {
            for (let betreuung of kind.betreuungen) {
                if (isSchulamt(betreuung.institutionStammdaten.betreuungsangebotTyp)) {
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
                    return this.gesuch.status;
                });
            });
        }
        return undefined;
    }

    /**
     * Antrag freigeben
     */
    public antragFreigeben(antragId: string, username: string): IPromise<TSGesuch> {
        return this.gesuchRS.antragFreigeben(antragId, username).then((response) => {
            this.setGesuch(response);
            return response;
        });

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
     * Returns true when the Gesuch must be readonly
     * @returns {boolean}
     */
    public isGesuchReadonly(): boolean {
        return isStatusVerfuegenVerfuegt(this.gesuch.status) || this.isGesuchReadonlyForRole();
    }

    /**
     * checks if the gesuch is readonly for a given role based on its state
     */
    private isGesuchReadonlyForRole(): boolean {
        if (this.authServiceRS.isRole(TSRole.SCHULAMT)) {
            return true;  // schulamt hat immer nur readonly zugriff
        } else if (this.authServiceRS.isRole(TSRole.GESUCHSTELLER)) {
            return isAtLeastFreigegebenOrFreigabequittung(this.getGesuch().status); //readonly fuer gs wenn gesuch freigegeben oder weiter
        }
        return false;
    }

    /**
     * Wenn das Gesuch Online durch den GS erstellt wurde, nun aber in Bearbeitung beim JA ist, handelt es sich um
     * den Korrekturmodus des Jugendamtes.
     * @returns {boolean}
     */
    public isKorrekturModusJugendamt(): boolean {
        return isAtLeastFreigegeben(this.gesuch.status) && (TSEingangsart.ONLINE === this.getGesuch().eingangsart);
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

    public clearGesuch(): void {
        this.gesuch = undefined;
    }

    /**
     * Schaut alle Betreuungen durch. Wenn es keine "JAAngebote" gibt, gibt es false zurueck.
     * Nur wenn alle JA-Angebote neu sind, gibt es true zurueck.
     */
    public areAllJAAngeboteNew(): boolean {
        let kinderWithBetreuungList: Array<TSKindContainer> = this.getKinderWithBetreuungList();
        if (kinderWithBetreuungList.length <= 0) {
            return false; // no Kind with bedarf
        }
        let jaAngeboteFound: boolean = false; // Wenn kein JA-Angebot gefunden wurde geben wir false zurueck
        for (let kind of kinderWithBetreuungList) {
            for (let betreuung of kind.betreuungen) {
                if (isJugendamt(betreuung.institutionStammdaten.betreuungsangebotTyp)) {
                    if (betreuung.vorgaengerId) { // eine mutierte JA-Betreuung existiert
                        return false;
                    }
                    jaAngeboteFound = true;
                }
            }
        }
        return jaAngeboteFound;
    }

    //TODO: Muss mit IAM noch angepasst werden. Fall und Name soll vom Login stammen nicht vom Gesuch, da auf DashbordSeite die Fallnummer und Name des GS angezeigt werden soll
    public getGesuchName(): string {
        if (this.getGesuch()) {
            var text = '';
            if (this.getGesuch().fall) {
                text = this.ebeguUtil.addZerosToNumber(this.getGesuch().fall.fallNummer, this.CONSTANTS.FALLNUMMER_LENGTH);
            }
            if (this.getGesuch().gesuchsteller1 && this.getGesuch().gesuchsteller1.extractNachname()) {
                text = text + ' ' + this.getGesuch().gesuchsteller1.extractNachname();
            }
            return text;
        } else {
            return '';
        }
    }
}
