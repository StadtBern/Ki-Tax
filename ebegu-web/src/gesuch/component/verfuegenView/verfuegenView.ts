import {IFormController, IComponentOptions, IPromise, ILogService} from 'angular';
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
import {TSGesuchEvent} from '../../../models/enums/TSGesuchEvent';
import IRootScopeService = angular.IRootScopeService;
let template = require('./verfuegenView.html');
require('./verfuegenView.less');
let removeDialogTempl = require('../../dialog/removeDialogTemplate.html');


export class VerfuegenViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = VerfuegenViewController;
    controllerAs = 'vm';
}

export class VerfuegenViewController extends AbstractGesuchViewController {

    public bemerkungen: string;

    static $inject: string[] = ['$state', 'GesuchModelManager', 'BerechnungsManager', 'EbeguUtil', '$scope', 'WizardStepManager',
        'DvDialog', 'DownloadRS', '$log', '$rootScope'];

    private verfuegungen: TSVerfuegung[] = [];

    /* @ngInject */
    constructor(private $state: IStateService, gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager,
                private ebeguUtil: EbeguUtil, private $scope: any, wizardStepManager: WizardStepManager,
                private DvDialog: DvDialog, private downloadRS: DownloadRS, private $log: ILogService, private $rootScope: IRootScopeService) {
        super(gesuchModelManager, berechnungsManager, wizardStepManager);
        this.setBemerkungen();

        $scope.$on('$stateChangeStart', (navEvent: any, toState: any, toParams: any, fromState: any, fromParams: any) => {
            console.log('resetting state due to navigation change, ');
            if (navEvent.defaultPrevented !== undefined && navEvent.defaultPrevented === false) {
                //Wenn die Maske verlassen wird, werden automatisch die Eintraege entfernt, die noch nicht in der DB gespeichert wurden
                this.reset();
            }
        });
    }

    cancel(form: IFormController): void {
        this.reset();
        form.$setPristine();
    }

    reset(): void {
        this.gesuchModelManager.restoreBackupOfPreviousGesuch();
    }

    save(form: IFormController): void {
        if (form.$valid) {
            this.saveVerfuegung().then(() => {
                this.downloadRS.getAccessTokenVerfuegungGeneratedDokument(this.gesuchModelManager.getGesuch().id,
                    this.gesuchModelManager.getBetreuungToWorkWith().id, true, null).then(() => {
                    this.$state.go('gesuch.verfuegen');
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

    public getKindName(): string {
        if (this.gesuchModelManager && this.gesuchModelManager.getKindToWorkWith() && this.gesuchModelManager.getKindToWorkWith().kindJA) {
            return this.gesuchModelManager.getKindToWorkWith().kindJA.getFullName();
        }
        return undefined;
    }

    public getInstitutionName(): string {
        if (this.gesuchModelManager && this.gesuchModelManager.getBetreuungToWorkWith() && this.gesuchModelManager.getBetreuungToWorkWith().institutionStammdaten) {
            return this.gesuchModelManager.getBetreuungToWorkWith().institutionStammdaten.institution.name;
        }
        return undefined;
    }

    public getBetreuungNumber(): string {
        if (this.ebeguUtil && this.gesuchModelManager && this.gesuchModelManager.getKindToWorkWith() && this.gesuchModelManager.getBetreuungToWorkWith()) {
            return this.ebeguUtil.calculateBetreuungsId(this.getGesuchsperiode(), this.getFall(), this.gesuchModelManager.getKindToWorkWith().kindNummer,
                this.gesuchModelManager.getBetreuungToWorkWith().betreuungNummer);
        }
        return undefined;
    }

    public getBetreuungsstatus(): TSBetreuungsstatus {
        if (this.gesuchModelManager && this.gesuchModelManager.getBetreuungToWorkWith()) {
            return this.gesuchModelManager.getBetreuungToWorkWith().betreuungsstatus;
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
        if (this.gesuchModelManager && this.gesuchModelManager.getGesuch() && this.gesuchModelManager.getGesuch().einkommensverschlechterungInfo) {
            return DateUtil.momentToLocalDateFormat(this.gesuchModelManager.getGesuch().einkommensverschlechterungInfo.stichtagFuerBasisJahrPlus1, 'DD.MM.YYYY');
        }
        return undefined;
    }

    public getAnfangsVerschlechterung2(): string {
        if (this.gesuchModelManager && this.gesuchModelManager.getGesuch() && this.gesuchModelManager.getGesuch().einkommensverschlechterungInfo) {
            return DateUtil.momentToLocalDateFormat(this.gesuchModelManager.getGesuch().einkommensverschlechterungInfo.stichtagFuerBasisJahrPlus2, 'DD.MM.YYYY');
        }
        return undefined;
    }

    /**
     * Nur wenn das Gesuch im Status VERFUEGEN und die Betreuung im Status BESTAETIGT sind, kann der Benutzer
     * das Angebot verfuegen. Sonst ist dieses nicht erlaubt.
     * @returns {boolean}
     */
    public showVerfuegen(): boolean {
        return this.gesuchModelManager.isGesuchStatus(TSAntragStatus.VERFUEGEN)
            && (TSBetreuungsstatus.BESTAETIGT === this.getBetreuungsstatus());
    }

    public saveVerfuegung(): IPromise<TSVerfuegung> {
        return this.DvDialog.showDialog(removeDialogTempl, RemoveDialogController, {
            title: 'CONFIRM_SAVE_VERFUEGUNG',
            deleteText: 'BESCHREIBUNG_SAVE_VERFUEGUNG'
        })
            .then(() => {
                this.getVerfuegenToWorkWith().manuelleBemerkungen = this.bemerkungen;
                return this.gesuchModelManager.saveVerfuegung().then((response) => {
                    if (this.gesuchModelManager.getGesuch().status === TSAntragStatus.VERFUEGT) {
                        this.$rootScope.$broadcast(TSGesuchEvent[TSGesuchEvent.STATUS_VERFUEGT]);
                    }
                    return response;
                });
            });
    }

    /**
     * Die Bemerkungen sind immer die generierten, es sei denn das Angebot ist schon verfuegt
     */
    private setBemerkungen(): void {
        if (this.gesuchModelManager.getBetreuungToWorkWith().betreuungsstatus === TSBetreuungsstatus.VERFUEGT) {
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
            || this.gesuchModelManager.getBetreuungToWorkWith().betreuungsstatus === TSBetreuungsstatus.VERFUEGT;
    }

    public openVerfuegungPDF(): void {
        this.downloadRS.getAccessTokenVerfuegungGeneratedDokument(this.gesuchModelManager.getGesuch().id,
            this.gesuchModelManager.getBetreuungToWorkWith().id, false, this.bemerkungen)
            .then((downloadFile: TSDownloadFile) => {
                this.$log.debug('accessToken: ' + downloadFile.accessToken);
                this.downloadRS.startDownload(downloadFile.accessToken, downloadFile.filename, false);
            });
    }
}
