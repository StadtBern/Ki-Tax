import {IComponentOptions, IPromise, ILogService} from 'angular';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import {IStateService} from 'angular-ui-router';
import TSBetreuung from '../../../models/TSBetreuung';
import TSKindContainer from '../../../models/TSKindContainer';
import EbeguUtil from '../../../utils/EbeguUtil';
import BerechnungsManager from '../../service/berechnungsManager';
import WizardStepManager from '../../service/wizardStepManager';
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
import {TSWizardStepStatus} from '../../../models/enums/TSWizardStepStatus';
import {TSAntragStatus, isAtLeastFreigegeben, isAnyStatusOfVerfuegt} from '../../../models/enums/TSAntragStatus';
import {DvDialog} from '../../../core/directive/dv-dialog/dv-dialog';
import {RemoveDialogController} from '../../dialog/RemoveDialogController';
import {TSBetreuungsstatus} from '../../../models/enums/TSBetreuungsstatus';
import {DownloadRS} from '../../../core/service/downloadRS.rest';
import {TSGeneratedDokumentTyp} from '../../../models/enums/TSGeneratedDokumentTyp';
import TSDownloadFile from '../../../models/TSDownloadFile';
import TSMahnung from '../../../models/TSMahnung';
import {TSMahnungTyp} from '../../../models/enums/TSMahnungTyp';
import MahnungRS from '../../service/mahnungRS.rest';
import TSGesuch from '../../../models/TSGesuch';
import TSGesuchsperiode from '../../../models/TSGesuchsperiode';
import DateUtil from '../../../utils/DateUtil';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import {TSRole} from '../../../models/enums/TSRole';
import GesuchRS from '../../service/gesuchRS.rest';
import IScope = angular.IScope;
let template = require('./verfuegenListView.html');
require('./verfuegenListView.less');
let removeDialogTempl = require('../../dialog/removeDialogTemplate.html');


export class VerfuegenListViewComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {
        // Bereits vorhandene Mahnungen
        mahnungList: '<'
    };
    template = template;
    controller = VerfuegenListViewController;
    controllerAs = 'vm';
}

export class VerfuegenListViewController extends AbstractGesuchViewController<any> {

    private kinderWithBetreuungList: Array<TSKindContainer>;
    mahnungList: TSMahnung[];
    private mahnung: TSMahnung;
    private tempAntragStatus: TSAntragStatus;


    static $inject: string[] = ['$state', 'GesuchModelManager', 'BerechnungsManager', 'EbeguUtil', 'WizardStepManager',
        'DvDialog', 'DownloadRS', 'MahnungRS', '$log', 'AuthServiceRS', '$scope', 'GesuchRS'];
    /* @ngInject */

    constructor(private $state: IStateService, gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager,
                private ebeguUtil: EbeguUtil, wizardStepManager: WizardStepManager, private DvDialog: DvDialog,
                private downloadRS: DownloadRS, private mahnungRS: MahnungRS, private $log: ILogService,
                private authServiceRs: AuthServiceRS, $scope: IScope, private gesuchRS: GesuchRS) {
        super(gesuchModelManager, berechnungsManager, wizardStepManager, $scope, TSWizardStepName.VERFUEGEN);
        this.initViewModel();
    }

    /**
     * Die finanzielle Situation und die Einkommensverschlechterungen muessen mithilfe des Berechnungsmanagers berechnet werden, um manche Daten zur Verfügung
     * zu haben. Das ist notwendig weil die finanzielle Situation nicht gespeichert wird. D.H. das erste Mal in einer Sitzung wenn ein Gesuch geoeffnet wird,
     * ist gar nichts berechnet. Wenn man dann die Verfügen direkt aufmacht, ist alles leer und wird nichts angezeigt, deswegen muss alles auch hier berechnet werden.
     * Um Probleme mit der Performance zu vermeiden, wird zuerst geprueft, ob die Berechnung schon vorher gemacht wurde, wenn ja dann wird sie einfach verwendet
     * ohne sie neu berechnen zu muessen. Dieses geht aber davon aus, dass die Berechnungen immer richtig kalkuliert wurden.
     *
     * Die Verfuegungen werden IMMER geladen, wenn diese View geladen wird. Dieses ist etwas ineffizient. Allerdings muss es eigentlich so funktionieren, weil
     * die Daten sich haben aendern koennen. Es ist ein aehnlicher Fall wie mit der finanziellen Situation. Sollte es Probleme mit der Performance geben, muessen
     * wir ueberlegen, ob wir es irgendwie anders berechnen koennen um den Server zu entlasten.
     */
    private initViewModel(): void {
        this.wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.WARTEN);

        //Berechnung aller finanziellen Daten
        if (!this.berechnungsManager.finanzielleSituationResultate) {
            this.berechnungsManager.calculateFinanzielleSituation(this.gesuchModelManager.getGesuch()); //.then(() => {});
        }
        if (this.gesuchModelManager.getGesuch() && this.gesuchModelManager.getGesuch().extractEinkommensverschlechterungInfo()
            && this.gesuchModelManager.getGesuch().extractEinkommensverschlechterungInfo().ekvFuerBasisJahrPlus1
            && !this.berechnungsManager.einkommensverschlechterungResultateBjP1) {

            this.berechnungsManager.calculateEinkommensverschlechterung(this.gesuchModelManager.getGesuch(), 1); //.then(() => {});
        }
        if (this.gesuchModelManager.getGesuch() && this.gesuchModelManager.getGesuch().extractEinkommensverschlechterungInfo()
            && this.gesuchModelManager.getGesuch().extractEinkommensverschlechterungInfo().ekvFuerBasisJahrPlus2
            && !this.berechnungsManager.einkommensverschlechterungResultateBjP2) {

            this.berechnungsManager.calculateEinkommensverschlechterung(this.gesuchModelManager.getGesuch(), 2); //.then(() => {});
        }
        //todo wenn man aus der verfuegung zurueck kommt muss man hier nicht neu berechnen
        this.refreshKinderListe();
    }

    private refreshKinderListe(): IPromise<any> {
        return this.gesuchModelManager.calculateVerfuegungen().then(() => {
            this.kinderWithBetreuungList = this.gesuchModelManager.getKinderWithBetreuungList();
        });
    }

    public getKinderWithBetreuungList(): Array<TSKindContainer> {
        return this.kinderWithBetreuungList;
    }

    public getMahnungList(): Array<TSMahnung> {
        return this.mahnungList;
    }

    /**
     * Nur bestaetigte Betreuungen koennen geoeffnet werden
     * @param kind
     * @param betreuung
     */
    public openVerfuegung(kind: TSKindContainer, betreuung: TSBetreuung): void {
        if (this.kannVerfuegungOeffnen(betreuung)) {
            let kindNumber: number = this.gesuchModelManager.findKind(kind);
            if (kindNumber > 0) {
                this.gesuchModelManager.setKindNumber(kindNumber);
                let betreuungNumber: number = this.gesuchModelManager.findBetreuung(betreuung);
                if (betreuungNumber > 0) {
                    this.$state.go('gesuch.verfuegenView', {
                        betreuungNumber: betreuungNumber,
                        kindNumber: kindNumber,
                        gesuchId: this.getGesuchId()
                    });
                }
            }
        }
    }

    public kannVerfuegungOeffnen(betreuung: TSBetreuung): boolean {
        return this.isDetailAvailableForGesuchstatus() && this.isDetailAvailableForBetreuungstatus(betreuung.betreuungsstatus);
    }

    private isDetailAvailableForGesuchstatus(): boolean {
        let isGesuchsteller: boolean = this.authServiceRs.isRole(TSRole.GESUCHSTELLER);
        //gesuchsteller hat sicher mal nur Zugriff auf verfuegungsdetail wenn das gesuch mindestens freiggeben ist
        if (isGesuchsteller) {
            let status: TSAntragStatus = this.getGesuch() ? this.getGesuch().status : TSAntragStatus.IN_BEARBEITUNG_GS;
            return isAtLeastFreigegeben(status);
        } else {
            return true;
        }
    }

    private isDetailAvailableForBetreuungstatus(betreuungsstatus: TSBetreuungsstatus): boolean {
        let isGesuchsteller: boolean = this.authServiceRs.isRole(TSRole.GESUCHSTELLER);
        let allowedBetstatus: Array<TSBetreuungsstatus> = [TSBetreuungsstatus.VERFUEGT, TSBetreuungsstatus.NICHT_EINGETRETEN];
        //Annahme: alle ausser Gesuchsteller duerfen bestaetigte betreuungen sehen wenn sie uberhaupt auf die Seite kommen
        if (!isGesuchsteller) {
            allowedBetstatus.push(TSBetreuungsstatus.BESTAETIGT);
        }
        return allowedBetstatus.indexOf(betreuungsstatus) !== -1;
    }

    /**
     * das FinanzielleSituation PDF ist fuer den Gesuchsteller erst sichtbar sobald der Antrag den Status VERFUEGT erreicht hat
     */
    public isFinanziellesituationPDFVisible(): boolean {
        let isGesuchsteller: boolean = this.authServiceRs.isRole(TSRole.GESUCHSTELLER);
        if (isGesuchsteller) {
            let status: TSAntragStatus = this.getGesuch() ? this.getGesuch().status : TSAntragStatus.IN_BEARBEITUNG_GS;
            return isAnyStatusOfVerfuegt(status) && this.getGesuch().hasFSDokument;
        }
        return this.getGesuch().hasFSDokument;

    }

    public isBegleitschreibenVisible(): boolean {
        let isGesuchsteller: boolean = this.authServiceRs.isRole(TSRole.GESUCHSTELLER);
        if (isGesuchsteller) {
            let status: TSAntragStatus = this.getGesuch() ? this.getGesuch().status : TSAntragStatus.IN_BEARBEITUNG_GS;
            return isAnyStatusOfVerfuegt(status);
        }
        return true;
    }

    public getFall() {
        if (this.gesuchModelManager && this.gesuchModelManager.getGesuch()) {
            return this.gesuchModelManager.getGesuch().fall;
        }
        return undefined;
    }

    public getGesuch(): TSGesuch {
        if (this.gesuchModelManager && this.gesuchModelManager.getGesuch()) {
            return this.gesuchModelManager.getGesuch();
        }
        return undefined;
    }

    public getGesuchsperiode(): TSGesuchsperiode {
        if (this.gesuchModelManager) {
            return this.gesuchModelManager.getGesuchsperiode();
        }
        return undefined;
    }

    public setGesuchStatusGeprueft(): IPromise<TSAntragStatus> {
        return this.DvDialog.showDialog(removeDialogTempl, RemoveDialogController, {
            title: 'CONFIRM_GESUCH_STATUS_GEPRUEFT',
            deleteText: 'BESCHREIBUNG_GESUCH_STATUS_WECHSELN'
        }).then(() => {
            return this.createNeededPDFs().then(() => {
                return this.setGesuchStatus(TSAntragStatus.GEPRUEFT);
            });
        });
    }

    public setGesuchStatusVerfuegen(): IPromise<TSGesuch> {
        //by default wird alles auf VERFUEGEN gesetzt, da es der normale Fall ist
        let newStatus: TSAntragStatus = TSAntragStatus.VERFUEGEN;
        let deleteTextValue: string = 'BESCHREIBUNG_GESUCH_STATUS_WECHSELN';

        return this.DvDialog.showDialog(removeDialogTempl, RemoveDialogController, {
            title: 'CONFIRM_GESUCH_STATUS_VERFUEGEN',
            deleteText: deleteTextValue
        }).then(() => {
            return this.createNeededPDFs().then(() => {
                this.gesuchModelManager.getGesuch().status = newStatus;
                return this.gesuchModelManager.updateGesuch().then(() => {  // muss gespeichert werden um hasfsdokument zu aktualisieren
                    return this.refreshKinderListe().then(() => {
                        this.form.$dirty = false;
                        this.form.$pristine = true; // nach dem es gespeichert wird, muessen wir das Form wieder auf clean setzen
                        return this.gesuchModelManager.getGesuch();
                    });
                });
            });
        });
    }

    private hasOffeneMahnungen(): boolean {
        for (let mahn of this.mahnungList) {
            if (!mahn.timestampAbgeschlossen) {
                return true;
            }
        }
        return false;
    }

    public isFristAbgelaufen(mahnung: TSMahnung): boolean {
        return mahnung.datumFristablauf.isBefore(DateUtil.today());
    }

    public showErsteMahnungErstellen(): boolean {
        // Nur wenn keine offenen Mahnungen vorhanden!
        return (this.gesuchModelManager.isGesuchStatus(TSAntragStatus.IN_BEARBEITUNG_JA) || this.gesuchModelManager.isGesuchStatus(TSAntragStatus.FREIGEGEBEN))
            && this.mahnung === undefined && !this.hasOffeneMahnungen() && !this.isGesuchReadonly();
    }

    public showErsteMahnungAusloesen(): boolean {
        return this.mahnung !== undefined && this.mahnung.mahnungTyp === TSMahnungTyp.ERSTE_MAHNUNG
            && !this.isGesuchReadonly();
    }

    public showZweiteMahnungErstellen(): boolean {
        return this.gesuchModelManager.isGesuchStatus(TSAntragStatus.ERSTE_MAHNUNG_ABGELAUFEN)
            && this.mahnung === undefined && !this.isGesuchReadonly();
    }

    public showZweiteMahnungAusloesen(): boolean {
        return this.mahnung !== undefined && this.mahnung.mahnungTyp === TSMahnungTyp.ZWEITE_MAHNUNG
            && !this.isGesuchReadonly();
    }

    public showMahnlaufBeenden(): boolean {
        return (this.gesuchModelManager.isGesuchStatus(TSAntragStatus.ERSTE_MAHNUNG) ||
            this.gesuchModelManager.isGesuchStatus(TSAntragStatus.ERSTE_MAHNUNG_DOKUMENTE_HOCHGELADEN) ||
            this.gesuchModelManager.isGesuchStatus(TSAntragStatus.ERSTE_MAHNUNG_ABGELAUFEN) ||
            this.gesuchModelManager.isGesuchStatus(TSAntragStatus.ZWEITE_MAHNUNG) ||
            this.gesuchModelManager.isGesuchStatus(TSAntragStatus.ZWEITE_MAHNUNG_DOKUMENTE_HOCHGELADEN) ||
            this.gesuchModelManager.isGesuchStatus(TSAntragStatus.ZWEITE_MAHNUNG_ABGELAUFEN))
            && !this.isGesuchReadonly();
    }

    public showDokumenteNichtKomplett(): boolean {
        return (this.gesuchModelManager.isGesuchStatus(TSAntragStatus.ERSTE_MAHNUNG_DOKUMENTE_HOCHGELADEN) ||
            this.gesuchModelManager.isGesuchStatus(TSAntragStatus.ZWEITE_MAHNUNG_DOKUMENTE_HOCHGELADEN))
            && !this.isGesuchReadonly();
    }

    public showZweiteMahnungNichtEingetreten(): boolean {
        return this.gesuchModelManager.isGesuchStatus(TSAntragStatus.ZWEITE_MAHNUNG_ABGELAUFEN) && !this.isGesuchReadonly();
    }

    public ersteMahnungErstellen(): void {
        this.tempAntragStatus = TSAntragStatus.ERSTE_MAHNUNG;
        this.createMahnung(TSMahnungTyp.ERSTE_MAHNUNG);
    }

    public zweiteMahnungErstellen(): void {
        this.tempAntragStatus = TSAntragStatus.ZWEITE_MAHNUNG;
        this.createMahnung(TSMahnungTyp.ZWEITE_MAHNUNG);
    }

    public saveMahnung(): void {
        if (this.form.$valid) {
            this.mahnungRS.saveMahnung(this.mahnung).then((mahnungResponse: TSMahnung) => {
                this.setGesuchStatus(this.tempAntragStatus).then(any => {
                    this.mahnungList.push(mahnungResponse);
                    this.tempAntragStatus = undefined;
                    this.mahnung = undefined;
                });
            });
        }
    }

    private createMahnung(typ: TSMahnungTyp): void {
        this.mahnungRS.getInitialeBemerkungen(this.getGesuch()).then(generatedBemerkungen => {
            this.mahnung = new TSMahnung();
            this.mahnung.mahnungTyp = typ;
            this.mahnung.gesuch = this.getGesuch();
            this.mahnung.timestampAbgeschlossen = null;
            this.mahnung.bemerkungen = generatedBemerkungen.data;
        });
    }

    public mahnlaufBeenden(): void {
        // Gesuchstatus zuruecksetzen UND die Mahnungen auf erledigt setzen
        this.setGesuchStatus(TSAntragStatus.IN_BEARBEITUNG_JA).then(any => {
            this.mahnungRS.mahnlaufBeenden(this.getGesuch()).then(any => {
                this.mahnungRS.findMahnungen(this.getGesuch().id).then(reloadedMahnungen => {
                    this.mahnungList = reloadedMahnungen;
                });
            });
        });
    }

    public dokumenteNichtKomplett(): void {
        // Nur Gesuchstatus zuruecksetzen, und zwar zurueck auf MAHNUNG (die jeweils relevante)
        if (this.gesuchModelManager.isGesuchStatus(TSAntragStatus.ERSTE_MAHNUNG_DOKUMENTE_HOCHGELADEN)) {
            this.setGesuchStatus(TSAntragStatus.ERSTE_MAHNUNG);
        } else if (this.gesuchModelManager.isGesuchStatus(TSAntragStatus.ZWEITE_MAHNUNG_DOKUMENTE_HOCHGELADEN)) {
            this.setGesuchStatus(TSAntragStatus.ZWEITE_MAHNUNG);
        }
    }

    public zweiteMahnungNichtEingetreten(): void {
        // Auf die zweite Mahnung wurde nicht reagiert. Den Status des Gesuchs wieder auf IN_BEARBEITUNG setzen
        // damit die Betreuungen auf NICHT_EINGETRETEN verfügt werden können. Die Mahnungen bleiben aber offen!
        this.setGesuchStatus(TSAntragStatus.IN_BEARBEITUNG_JA);
    }

    /**
     * Der Button Geprueft wird nur beim Status IN_BEARBEITUNG_JA eingeblendet
     * @returns {boolean}
     */
    public showGeprueft(): boolean {
        return (this.gesuchModelManager.isGesuchStatus(TSAntragStatus.IN_BEARBEITUNG_JA) || this.gesuchModelManager.isGesuchStatus(TSAntragStatus.FREIGEGEBEN))
            && this.wizardStepManager.areAllStepsOK() && this.mahnung === undefined && !this.isGesuchReadonly();
    }

    /**
     * Der Button Verfuegung starten wird angezeigt, wenn alle Betreuungen bestaetigt und das Gesuch geprueft wurden
     * @returns {boolean}
     */
    public showVerfuegenStarten(): boolean {
        return this.gesuchModelManager.isGesuchStatus(TSAntragStatus.GEPRUEFT)
            && this.wizardStepManager.isStepStatusOk(TSWizardStepName.BETREUUNG)
            && !this.isGesuchReadonly();
            // && this.gesuchModelManager.getGesuch().status !== TSAntragStatus.VERFUEGEN;
    }

    public openFinanzielleSituationPDF(): void {
        this.downloadRS.getAccessTokenGeneratedDokument(this.gesuchModelManager.getGesuch().id, TSGeneratedDokumentTyp.FINANZIELLE_SITUATION, false)
            .then((downloadFile: TSDownloadFile) => {
                this.$log.debug('accessToken: ' + downloadFile.accessToken);
                this.downloadRS.startDownload(downloadFile.accessToken, downloadFile.filename, false);
            });
    }

    public openBegleitschreibenPDF(): void {
        this.downloadRS.getAccessTokenGeneratedDokument(this.gesuchModelManager.getGesuch().id, TSGeneratedDokumentTyp.BEGLEITSCHREIBEN, false)
            .then((downloadFile: TSDownloadFile) => {
                this.$log.debug('accessToken: ' + downloadFile.accessToken);
                this.downloadRS.startDownload(downloadFile.accessToken, downloadFile.filename, false);
            });
    }

    public openMahnungPDF(mahnung: TSMahnung): void {
        if (mahnung == null) {
            mahnung = this.mahnung;
        }
        this.downloadRS.getAccessTokenMahnungGeneratedDokument(mahnung, false)
            .then((downloadFile: TSDownloadFile) => {
                this.$log.debug('accessToken: ' + downloadFile.accessToken);
                this.downloadRS.startDownload(downloadFile.accessToken, downloadFile.filename, false);
            });
    }

    private createNeededPDFs(): IPromise<TSDownloadFile> {
       return this.downloadRS.getAccessTokenGeneratedDokument(this.gesuchModelManager.getGesuch().id, TSGeneratedDokumentTyp.BEGLEITSCHREIBEN, true)
            .then(() => {
                if (this.getGesuch().hasFSDokument) {
                    return this.downloadRS.getAccessTokenGeneratedDokument(this.gesuchModelManager.getGesuch().id, TSGeneratedDokumentTyp.FINANZIELLE_SITUATION, true);
                }
                return;
            });
    }

    public showBeschwerdeHaengig(): boolean {
        let status: TSAntragStatus = this.getGesuch() ? this.getGesuch().status : TSAntragStatus.IN_BEARBEITUNG_GS;
        //hier wird extra nur "VERFUEGT" gestestet statt alle verfuegten status weil das Schulamt keine Beschwerden erstellen darf
        return TSAntragStatus.VERFUEGT === status && !this.getGesuch().gesperrtWegenBeschwerde;
    }

    public showBeschwerdeAbschliessen(): boolean {
        let status: TSAntragStatus = this.getGesuch() ? this.getGesuch().status : TSAntragStatus.IN_BEARBEITUNG_GS;
        return TSAntragStatus.BESCHWERDE_HAENGIG === status;
    }

    public setGesuchStatusBeschwerdeHaengig(): IPromise<TSGesuch> {
        return this.DvDialog.showDialog(removeDialogTempl, RemoveDialogController, {
            title: 'BESCHWERDE_HAENGIG',
            deleteText: 'BESCHREIBUNG_GESUCH_BESCHWERDE_HAENGIG'
        }).then(() => {
            return this.gesuchRS.setBeschwerdeHaengig(this.getGesuch().id).then((gesuch: TSGesuch) => {
                this.gesuchModelManager.setGesuch(gesuch);
                return this.gesuchModelManager.getGesuch();
            });
        });
    }

    public setGesuchStatusBeschwerdeAbschliessen(): IPromise<TSGesuch> {
        return this.DvDialog.showDialog(removeDialogTempl, RemoveDialogController, {
            title: 'BESCHWERDE_ABSCHLIESSEN',
            deleteText: 'BESCHREIBUNG_GESUCH_BESCHWERDE_ABSCHLIESSEN'
        }).then(() => {
            return this.gesuchRS.removeBeschwerdeHaengig(this.getGesuch().id).then((gesuch: TSGesuch) => {
                this.gesuchModelManager.setGesuch(gesuch);
                return this.gesuchModelManager.getGesuch();
            });
        });
    }
}
