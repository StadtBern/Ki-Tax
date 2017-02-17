import {IComponentOptions, IPromise, ILogService, IScope} from 'angular';
import AbstractGesuchViewController from '../abstractGesuchView';
import GesuchModelManager from '../../service/gesuchModelManager';
import {IStateService} from 'angular-ui-router';
import EbeguUtil from '../../../utils/EbeguUtil';
import {TSBetreuungsstatus} from '../../../models/enums/TSBetreuungsstatus';
import BerechnungsManager from '../../service/berechnungsManager';
import DateUtil from '../../../utils/DateUtil';
import TSVerfuegung from '../../../models/TSVerfuegung';
import TSVerfuegungZeitabschnitt from '../../../models/TSVerfuegungZeitabschnitt';
import WizardStepManager from '../../service/wizardStepManager';
import {TSAntragStatus} from '../../../models/enums/TSAntragStatus';
import {DvDialog} from '../../../core/directive/dv-dialog/dv-dialog';
import {RemoveDialogController} from '../../dialog/RemoveDialogController';
import {DownloadRS} from '../../../core/service/downloadRS.rest';
import TSDownloadFile from '../../../models/TSDownloadFile';
import TSBetreuung from '../../../models/TSBetreuung';
import {IBetreuungStateParams} from '../../gesuch.route';
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
let template = require('./verfuegenView.html');
require('./verfuegenView.less');
let removeDialogTempl = require('../../dialog/removeDialogTemplate.html');


export class VerfuegenViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = VerfuegenViewController;
    controllerAs = 'vm';
}

export class VerfuegenViewController extends AbstractGesuchViewController<any> {

    //this is the model...
    public bemerkungen: string;

    static $inject: string[] = ['$state', 'GesuchModelManager', 'BerechnungsManager', 'EbeguUtil', '$scope', 'WizardStepManager',
        'DvDialog', 'DownloadRS', '$log', '$stateParams', '$window'];

    private verfuegungen: TSVerfuegung[] = [];

    /* @ngInject */
    constructor(private $state: IStateService, gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager,
                private ebeguUtil: EbeguUtil, $scope: IScope, wizardStepManager: WizardStepManager,
                private DvDialog: DvDialog, private downloadRS: DownloadRS, private $log: ILogService, $stateParams: IBetreuungStateParams,
                private $window: ng.IWindowService) {

        super(gesuchModelManager, berechnungsManager, wizardStepManager, $scope, TSWizardStepName.VERFUEGEN);

        let kindIndex : number = this.gesuchModelManager.convertKindNumberToKindIndex(parseInt($stateParams.kindNumber, 10));
        this.gesuchModelManager.setKindIndex(kindIndex);
        let betreuungIndex : number = this.gesuchModelManager.convertBetreuungNumberToBetreuungIndex(parseInt($stateParams.betreuungNumber, 10));
        this.gesuchModelManager.setBetreuungIndex(betreuungIndex);
        this.wizardStepManager.setCurrentStep(TSWizardStepName.VERFUEGEN);

        this.initView();

        // EBEGE-741: Bemerkungen sollen automatisch zum Inhalt der Verfügung hinzugefügt werden
        if ($scope) {
            $scope.$watch(() => {
                return this.gesuchModelManager.getGesuch().bemerkungen;
            }, (newValue, oldValue) => {
                if ((newValue !== oldValue)) {
                    this.setBemerkungen();
                }
            });
        }
    }

    private initView() {
        if (!this.gesuchModelManager.getVerfuegenToWorkWith()) {
            this.gesuchModelManager.calculateVerfuegungen().then(() => {
                this.setBemerkungen();
            });
        } else {
            this.setBemerkungen();
        }

        //if finanzielleSituationResultate is undefined/empty (this may happen if user presses reloads this page) then we recalculate it
        if (angular.equals(this.berechnungsManager.finanzielleSituationResultate, {})) {
            this.berechnungsManager.calculateFinanzielleSituation(this.gesuchModelManager.getGesuch());
        }
        if (this.gesuchModelManager.getGesuch() && this.gesuchModelManager.getGesuch().extractEinkommensverschlechterungInfo()
            && this.gesuchModelManager.getGesuch().extractEinkommensverschlechterungInfo().ekvFuerBasisJahrPlus1
            && angular.equals(this.berechnungsManager.einkommensverschlechterungResultateBjP1, {})) {

            this.berechnungsManager.calculateEinkommensverschlechterung(this.gesuchModelManager.getGesuch(), 1); //.then(() => {});
        }
        if (this.gesuchModelManager.getGesuch() && this.gesuchModelManager.getGesuch().extractEinkommensverschlechterungInfo()
            && this.gesuchModelManager.getGesuch().extractEinkommensverschlechterungInfo().ekvFuerBasisJahrPlus2
            && angular.equals(this.berechnungsManager.einkommensverschlechterungResultateBjP2, {})) {

            this.berechnungsManager.calculateEinkommensverschlechterung(this.gesuchModelManager.getGesuch(), 2); //.then(() => {});
        }
    }

    cancel(): void {
        this.form.$setPristine();
    }


    save(): void {
        if (this.isGesuchValid()) {
            this.saveVerfuegung().then(() => {
                this.downloadRS.getAccessTokenVerfuegungGeneratedDokument(this.gesuchModelManager.getGesuch().id,
                    this.gesuchModelManager.getBetreuungToWorkWith().id, true, null).then(() => {
                    this.$state.go('gesuch.verfuegen', {
                        gesuchId: this.getGesuchId()
                    });
                });
            });
        }
    }

    schliessenOhneVerfuegen() {
        if (this.isGesuchValid()) {
            this.verfuegungSchliessenOhenVerfuegen().then(() => {
                this.$state.go('gesuch.verfuegen', {
                    gesuchId: this.getGesuchId()
                });
            });
        }
    }

    nichtEintreten() {
        if (this.isGesuchValid()) {
            this.verfuegungNichtEintreten().then(() => {
                this.downloadRS.getAccessTokenNichteintretenGeneratedDokument(
                    this.gesuchModelManager.getBetreuungToWorkWith().id, true).then(() => {
                    this.$state.go('gesuch.verfuegen', {
                        gesuchId: this.getGesuchId()
                    });
                });
            });
        }
    }

    public getVerfuegenToWorkWith(): TSVerfuegung {
        if (this.gesuchModelManager) {
            return this.gesuchModelManager.getVerfuegenToWorkWith();
        }
        return undefined;
    }

    public getVerfuegungZeitabschnitte(): Array<TSVerfuegungZeitabschnitt> {
        if (this.getVerfuegenToWorkWith()) {
            return this.getVerfuegenToWorkWith().zeitabschnitte;
        }
        return undefined;
    }

    public getFall() {
        if (this.gesuchModelManager && this.gesuchModelManager.getGesuch()) {
            return this.gesuchModelManager.getGesuch().fall;
        }
        return undefined;
    }

    public getGesuchsperiode() {
        if (this.gesuchModelManager) {
            return this.gesuchModelManager.getGesuchsperiode();
        }
        return undefined;
    }

    public getBetreuung(): TSBetreuung {
        return this.gesuchModelManager.getBetreuungToWorkWith();
    }

    public getKindName(): string {
        if (this.gesuchModelManager && this.gesuchModelManager.getKindToWorkWith() && this.gesuchModelManager.getKindToWorkWith().kindJA) {
            return this.gesuchModelManager.getKindToWorkWith().kindJA.getFullName();
        }
        return undefined;
    }

    public getInstitutionName(): string {
        if (this.gesuchModelManager && this.getBetreuung() && this.getBetreuung().institutionStammdaten) {
            return this.getBetreuung().institutionStammdaten.institution.name;
        }
        return undefined;
    }

    public getBetreuungNumber(): string {
        if (this.ebeguUtil && this.gesuchModelManager && this.gesuchModelManager.getKindToWorkWith() && this.gesuchModelManager.getBetreuungToWorkWith()) {
            return this.ebeguUtil.calculateBetreuungsId(this.getGesuchsperiode(), this.getFall(), this.gesuchModelManager.getKindToWorkWith().kindNummer,
                this.getBetreuung().betreuungNummer);
        }
        return undefined;
    }

    public getBetreuungsstatus(): TSBetreuungsstatus {
        if (this.gesuchModelManager && this.gesuchModelManager.getBetreuungToWorkWith()) {
            return this.getBetreuung().betreuungsstatus;
        }
        return undefined;
    }

    public getAnfangsPeriode(): string {
        if (this.ebeguUtil) {
            return this.ebeguUtil.getFirstDayGesuchsperiodeAsString(this.gesuchModelManager.getGesuchsperiode());
        }
        return undefined;
    }

    public getAnfangsVerschlechterung1(): string {
        if (this.gesuchModelManager && this.gesuchModelManager.getGesuch() && this.gesuchModelManager.getGesuch().extractEinkommensverschlechterungInfo()) {
            return DateUtil.momentToLocalDateFormat(this.gesuchModelManager.getGesuch().extractEinkommensverschlechterungInfo().stichtagFuerBasisJahrPlus1, 'DD.MM.YYYY');
        }
        return undefined;
    }

    public getAnfangsVerschlechterung2(): string {
        if (this.gesuchModelManager && this.gesuchModelManager.getGesuch() && this.gesuchModelManager.getGesuch().extractEinkommensverschlechterungInfo()) {
            return DateUtil.momentToLocalDateFormat(this.gesuchModelManager.getGesuch().extractEinkommensverschlechterungInfo().stichtagFuerBasisJahrPlus2, 'DD.MM.YYYY');
        }
        return undefined;
    }

    /**
     * Nur wenn das Gesuch im Status VERFUEGEN und die Betreuung im Status BESTAETIGT oder GEKUENDIGT_VOR_EINTRITT
     * sind, kann der Benutzer das Angebot verfuegen. Sonst ist dieses nicht erlaubt.
     * GEKUENDIGT_VOR_EINTRITT ist erlaubt weil die Kita verantwortlicher dafuer ist, die Betreuung in diesem Status zu setzen,
     * d.h. die Betreuung hat bereits diesen Status wenn man auf den Step Verfuegung kommt
     * @returns {boolean}
     */
    public showVerfuegen(): boolean {
        return this.gesuchModelManager.isGesuchStatus(TSAntragStatus.VERFUEGEN)
            && (TSBetreuungsstatus.BESTAETIGT === this.getBetreuungsstatus() || TSBetreuungsstatus.GEKUENDIGT_VOR_EINTRITT === this.getBetreuungsstatus());
    }

    public saveVerfuegung(): IPromise<TSVerfuegung> {
        return this.DvDialog.showDialog(removeDialogTempl, RemoveDialogController, {
            title: 'CONFIRM_SAVE_VERFUEGUNG',
            deleteText: 'BESCHREIBUNG_SAVE_VERFUEGUNG'
        })
            .then(() => {
                this.getVerfuegenToWorkWith().manuelleBemerkungen = this.bemerkungen;
                return this.gesuchModelManager.saveVerfuegung();
            });
    }

    public verfuegungSchliessenOhenVerfuegen(): IPromise<void> {
        return this.DvDialog.showDialog(removeDialogTempl, RemoveDialogController, {
            title: 'CONFIRM_CLOSE_VERFUEGUNG_OHNE_VERFUEGEN',
            deleteText: 'BESCHREIBUNG_CLOSE_VERFUEGUNG_OHNE_VERFUEGEN'
        })
            .then(() => {
                this.getVerfuegenToWorkWith().manuelleBemerkungen = this.bemerkungen;
                this.gesuchModelManager.verfuegungSchliessenOhenVerfuegen();
            });
    }

    public verfuegungNichtEintreten(): IPromise<TSVerfuegung> {
        return this.DvDialog.showDialog(removeDialogTempl, RemoveDialogController, {
            title: 'CONFIRM_CLOSE_VERFUEGUNG_NICHT_EINTRETEN',
            deleteText: 'BESCHREIBUNG_CLOSE_VERFUEGUNG_NICHT_EINTRETEN'
        }).then(() => {
            this.getVerfuegenToWorkWith().manuelleBemerkungen = this.bemerkungen;
            return this.gesuchModelManager.verfuegungSchliessenNichtEintreten();
        });
    }

    /**
     * Die Bemerkungen sind immer die generierten, es sei denn das Angebot ist schon verfuegt
     */
    private setBemerkungen(): void {
        if (this.getBetreuung().betreuungsstatus === TSBetreuungsstatus.VERFUEGT ||
            this.getBetreuung().betreuungsstatus === TSBetreuungsstatus.GESCHLOSSEN_OHNE_VERFUEGUNG) {
            this.bemerkungen = this.getVerfuegenToWorkWith().manuelleBemerkungen;
        } else {
            this.bemerkungen = '';
            if (this.getVerfuegenToWorkWith().generatedBemerkungen && this.getVerfuegenToWorkWith().generatedBemerkungen.length > 0) {
                this.bemerkungen = this.getVerfuegenToWorkWith().generatedBemerkungen + '\n';
            }
            if (this.gesuchModelManager.getGesuch().bemerkungen) {
                this.bemerkungen = this.bemerkungen + this.gesuchModelManager.getGesuch().bemerkungen;
            }
        }
    }

    public isBemerkungenDisabled(): boolean {
        return this.gesuchModelManager.getGesuch().status !== TSAntragStatus.VERFUEGEN
            || this.getBetreuung().betreuungsstatus === TSBetreuungsstatus.VERFUEGT
            || this.getBetreuung().betreuungsstatus === TSBetreuungsstatus.GESCHLOSSEN_OHNE_VERFUEGUNG;
    }

    public openVerfuegungPDF(): void {
        let win: Window = this.downloadRS.prepareDownloadWindow();
        this.downloadRS.getAccessTokenVerfuegungGeneratedDokument(this.gesuchModelManager.getGesuch().id,
            this.getBetreuung().id, false, this.bemerkungen)
            .then((downloadFile: TSDownloadFile) => {
                this.$log.debug('accessToken: ' + downloadFile.accessToken);
                this.downloadRS.startDownload(downloadFile.accessToken, downloadFile.filename, false, win);
            });
    }

    public openNichteintretenPDF(): void {
        let win: Window = this.downloadRS.prepareDownloadWindow();
        this.downloadRS.getAccessTokenNichteintretenGeneratedDokument(this.getBetreuung().id, false)
            .then((downloadFile: TSDownloadFile) => {
                this.$log.debug('accessToken: ' + downloadFile.accessToken);
                this.downloadRS.startDownload(downloadFile.accessToken, downloadFile.filename, false, win);
            });
    }

    public isSameVerfuegungdaten(): boolean {
        if (this.getVerfuegenToWorkWith()) {
            return this.getVerfuegenToWorkWith().sameVerfuegungsdaten;
        }
        return undefined;
    }

    public showVerfuegungsDetails(): boolean {
        return !this.isBetreuungInStatus(TSBetreuungsstatus.NICHT_EINGETRETEN);
    }

    public showVerfuegungPdfLink(): boolean {
        return !this.isBetreuungInStatus(TSBetreuungsstatus.NICHT_EINGETRETEN);
    }

    public showNichtEintretenPdfLink(): boolean {
        let nichtVerfuegt = !this.isBetreuungInStatus(TSBetreuungsstatus.VERFUEGT);
        let mutation = !this.gesuchModelManager.isErstgesuch();
        let nichtNichteingetreten = !this.isBetreuungInStatus(TSBetreuungsstatus.NICHT_EINGETRETEN);
        return nichtVerfuegt && !(mutation && nichtNichteingetreten);
    }
}
