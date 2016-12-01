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
import {TSAntragStatus} from '../../../models/enums/TSAntragStatus';
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
        'DvDialog', 'DownloadRS', 'MahnungRS', '$log'];

    /* @ngInject */
    constructor(private $state: IStateService, gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager,
                private ebeguUtil: EbeguUtil, wizardStepManager: WizardStepManager, private DvDialog: DvDialog,
                private downloadRS: DownloadRS, private mahnungRS: MahnungRS, private $log: ILogService) {
        super(gesuchModelManager, berechnungsManager, wizardStepManager);
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
        this.wizardStepManager.setCurrentStep(TSWizardStepName.VERFUEGEN);
        this.wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.WARTEN);

        //Berechnung aller finanziellen Daten
        if (!this.berechnungsManager.finanzielleSituationResultate) {
            this.berechnungsManager.calculateFinanzielleSituation(this.gesuchModelManager.getGesuch()); //.then(() => {});
        }
        if (this.gesuchModelManager.getGesuch() && this.gesuchModelManager.getGesuch().einkommensverschlechterungInfo
            && this.gesuchModelManager.getGesuch().einkommensverschlechterungInfo.ekvFuerBasisJahrPlus1
            && !this.berechnungsManager.einkommensverschlechterungResultateBjP1) {

            this.berechnungsManager.calculateEinkommensverschlechterung(this.gesuchModelManager.getGesuch(), 1); //.then(() => {});
        }
        if (this.gesuchModelManager.getGesuch() && this.gesuchModelManager.getGesuch().einkommensverschlechterungInfo
            && this.gesuchModelManager.getGesuch().einkommensverschlechterungInfo.ekvFuerBasisJahrPlus2
            && !this.berechnungsManager.einkommensverschlechterungResultateBjP2) {

            this.berechnungsManager.calculateEinkommensverschlechterung(this.gesuchModelManager.getGesuch(), 2); //.then(() => {});
        }
        //todo wenn man aus der verfuegung zurueck kommt muss man hier nicht neu berechnen
        this.gesuchModelManager.calculateVerfuegungen().then(() => {
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
                    this.gesuchModelManager.setBetreuungNumber(betreuungNumber);
                    this.$state.go('gesuch.verfuegenView', {
                        gesuchId: this.getGesuchId()
                    });
                }
            }
        }
    }

    public kannVerfuegungOeffnen(betreuung: TSBetreuung): boolean {
        return TSBetreuungsstatus.BESTAETIGT === betreuung.betreuungsstatus ||
            TSBetreuungsstatus.VERFUEGT === betreuung.betreuungsstatus ||
            TSBetreuungsstatus.NICHT_EINGETRETEN === betreuung.betreuungsstatus;
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

    public setGesuchStatusVerfuegen(): IPromise<TSAntragStatus> {
        //by default wird alles auf VERFUEGEN gesetzt, da es der normale Fall ist
        let newStatus: TSAntragStatus = TSAntragStatus.VERFUEGEN;
        let deleteTextValue: string = 'BESCHREIBUNG_GESUCH_STATUS_WECHSELN';

        if (this.gesuchModelManager.areThereOnlySchulamtAngebote()) {
            newStatus = TSAntragStatus.NUR_SCHULAMT;
            deleteTextValue = 'BESCHREIBUNG_GESUCH_STATUS_WECHSELN_SCHULAMT';
            this.wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.OK);
        }
        return this.DvDialog.showDialog(removeDialogTempl, RemoveDialogController, {
            title: 'CONFIRM_GESUCH_STATUS_VERFUEGEN',
            deleteText: deleteTextValue
        }).then(() => {
            return this.createNeededPDFs().then(() => {
                return this.setGesuchStatus(newStatus);
            });
        });
    }

    private hasOffeneMahnungen(): boolean {
        for (let mahn of this.mahnungList) {
            if (mahn.active) {
                return true;
            }
        }
        return false;
    }

    public isFristAbgelaufen(mahnung : TSMahnung): boolean {
        return mahnung.datumFristablauf.isBefore(DateUtil.today());
    }

    public showErsteMahnungErstellen(): boolean {
        // Nur wenn keine offenen Mahnungen vorhanden!
        return this.gesuchModelManager.isGesuchStatus(TSAntragStatus.IN_BEARBEITUNG_JA) && this.mahnung === undefined && !this.hasOffeneMahnungen();
    }

    public showErsteMahnungAusloesen(): boolean {
        return this.mahnung !== undefined && this.mahnung.mahnungTyp === TSMahnungTyp.ERSTE_MAHNUNG;
    }

    public showZweiteMahnungErstellen(): boolean {
        return this.gesuchModelManager.isGesuchStatus(TSAntragStatus.ERSTE_MAHNUNG_ABGELAUFEN)
            && this.mahnung === undefined;
    }

    public showZweiteMahnungAusloesen(): boolean {
        return this.mahnung !== undefined && this.mahnung.mahnungTyp === TSMahnungTyp.ZWEITE_MAHNUNG;
    }

    public showMahnlaufBeenden(): boolean {
        return this.gesuchModelManager.isGesuchStatus(TSAntragStatus.ERSTE_MAHNUNG) ||
            this.gesuchModelManager.isGesuchStatus(TSAntragStatus.ERSTE_MAHNUNG_DOKUMENTE_HOCHGELADEN) ||
            this.gesuchModelManager.isGesuchStatus(TSAntragStatus.ERSTE_MAHNUNG_ABGELAUFEN) ||
            this.gesuchModelManager.isGesuchStatus(TSAntragStatus.ZWEITE_MAHNUNG) ||
            this.gesuchModelManager.isGesuchStatus(TSAntragStatus.ZWEITE_MAHNUNG_DOKUMENTE_HOCHGELADEN) ||
            this.gesuchModelManager.isGesuchStatus(TSAntragStatus.ZWEITE_MAHNUNG_ABGELAUFEN);
    }

    public showDokumenteNichtKomplett(): boolean {
        return this.gesuchModelManager.isGesuchStatus(TSAntragStatus.ERSTE_MAHNUNG_DOKUMENTE_HOCHGELADEN) ||
            this.gesuchModelManager.isGesuchStatus(TSAntragStatus.ZWEITE_MAHNUNG_DOKUMENTE_HOCHGELADEN);
    }

    public showZweiteMahnungNichtEingetreten(): boolean {
        return this.gesuchModelManager.isGesuchStatus(TSAntragStatus.ZWEITE_MAHNUNG_ABGELAUFEN);
    }

    public ersteMahnungErstellen(): void {
        this.tempAntragStatus = TSAntragStatus.ERSTE_MAHNUNG;
        this.createMahnung(TSMahnungTyp.ERSTE_MAHNUNG);
    }

    public zweiteMahnungErstellen(): void {
        this.tempAntragStatus = TSAntragStatus.ZWEITE_MAHNUNG;
        this.createMahnung(TSMahnungTyp.ZWEITE_MAHNUNG);
    }

    public saveMahnung(form: angular.IFormController): void {
        if (form.$valid) {
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
            this.mahnung.active = true;
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
        return this.gesuchModelManager.isGesuchStatus(TSAntragStatus.IN_BEARBEITUNG_JA)
            && this.wizardStepManager.areAllStepsOK() && this.mahnung === undefined;
    }

    /**
     * Der Button Verfuegung starten wird angezeigt, wenn alle Betreuungen bestaetigt und das Gesuch geprueft wurden
     * @returns {boolean}
     */
    public showVerfuegenStarten(): boolean {
        return this.gesuchModelManager.isGesuchStatus(TSAntragStatus.GEPRUEFT)
            && this.wizardStepManager.isStepStatusOk(TSWizardStepName.BETREUUNG)
            && this.gesuchModelManager.getGesuch().status !== TSAntragStatus.VERFUEGEN;
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
        if (mahnung == null)
            mahnung = this.mahnung;

        this.downloadRS.getAccessTokenMahnungGeneratedDokument(mahnung, false)
            .then((downloadFile: TSDownloadFile) => {
                this.$log.debug('accessToken: ' + downloadFile.accessToken);
                this.downloadRS.startDownload(downloadFile.accessToken, downloadFile.filename, false);
            });
    }

    private createNeededPDFs(): IPromise<TSDownloadFile> {
        return this.downloadRS.getAccessTokenGeneratedDokument(this.gesuchModelManager.getGesuch().id, TSGeneratedDokumentTyp.FINANZIELLE_SITUATION, true)
            .then((downloadFile: TSDownloadFile) => {
                return this.downloadRS.getAccessTokenGeneratedDokument(this.gesuchModelManager.getGesuch().id, TSGeneratedDokumentTyp.BEGLEITSCHREIBEN, true);
            });
    }

}
