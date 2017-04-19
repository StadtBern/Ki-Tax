import AbstractGesuchViewController from '../abstractGesuchView';
import {IComponentOptions, IPromise} from 'angular';
import GesuchModelManager from '../../service/gesuchModelManager';
import BerechnungsManager from '../../service/berechnungsManager';
import WizardStepManager from '../../service/wizardStepManager';
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
import {TSWizardStepStatus} from '../../../models/enums/TSWizardStepStatus';
import {DvDialog} from '../../../core/directive/dv-dialog/dv-dialog';
import {DownloadRS} from '../../../core/service/downloadRS.rest';
import TSDownloadFile from '../../../models/TSDownloadFile';
import {isAtLeastFreigegeben, TSAntragStatus} from '../../../models/enums/TSAntragStatus';
import DateUtil from '../../../utils/DateUtil';
import {TSZustelladresse} from '../../../models/enums/TSZustelladresse';
import {ApplicationPropertyRS} from '../../../admin/service/applicationPropertyRS.rest';
import {FreigabeDialogController} from '../../dialog/FreigabeDialogController';
import ITranslateService = angular.translate.ITranslateService;
import IFormController = angular.IFormController;
import IScope = angular.IScope;
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import {TSRoleUtil} from '../../../utils/TSRoleUtil';
let template = require('./freigabeView.html');
require('./freigabeView.less');
let dialogTemplate = require('../../dialog/removeDialogTemplate.html');


export class FreigabeViewComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {};
    template = template;
    controller = FreigabeViewController;
    controllerAs = 'vm';
}


export class FreigabeViewController extends AbstractGesuchViewController<any> {

    bestaetigungFreigabequittung: boolean = false;
    isFreigebenClicked: boolean = false;
    private showGesuchFreigebenSimulationButton: boolean = false;
    TSRoleUtil: any;

    static $inject = ['GesuchModelManager', 'BerechnungsManager', 'WizardStepManager',
        'DvDialog', 'DownloadRS', '$scope', 'ApplicationPropertyRS', '$window', 'AuthServiceRS'];
    /* @ngInject */
    constructor(gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager,
                wizardStepManager: WizardStepManager, private DvDialog: DvDialog,
                private downloadRS: DownloadRS, $scope: IScope, private applicationPropertyRS: ApplicationPropertyRS,
                private $window: ng.IWindowService,  private authServiceRS: AuthServiceRS) {

        super(gesuchModelManager, berechnungsManager, wizardStepManager, $scope, TSWizardStepName.FREIGABE);
        this.initViewModel();
    }

    private initViewModel(): void {
        this.wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.IN_BEARBEITUNG);
        this.initDevModeParameter();
        this.TSRoleUtil = TSRoleUtil;
    }

    public gesuchEinreichen(): IPromise<void> {
        this.isFreigebenClicked = true;
        if (this.isGesuchValid() && this.bestaetigungFreigabequittung === true) {
            this.form.$setPristine();
            return this.DvDialog.showDialog(dialogTemplate, FreigabeDialogController, {
                parentController: this
            });
        }
        return undefined;
    }

    public confirmationCallback(): void {
        if (this.gesuchModelManager.isGesuch() || this.gesuchModelManager.areAllJAAngeboteNew()) {
            this.openFreigabequittungPDF(true);
        } else {
            this.gesuchFreigeben(); //wenn keine freigabequittung noetig direkt freigeben
        }
    }

    public gesuchFreigeben(): void {
        let gesuchID = this.gesuchModelManager.getGesuch().id;
        this.gesuchModelManager.antragFreigeben(gesuchID, null);
    }

    private initDevModeParameter() {
        this.applicationPropertyRS.isDevMode().then((response: boolean) => {
            // Simulation nur fuer SuperAdmin freischalten
            let isSuperadmin: boolean = this.authServiceRS.isOneOfRoles(TSRoleUtil.getAdministratorRoles());
            // Die Simulation ist nur im Dev-Mode moeglich und nur, wenn das Gesuch im Status FREIGABEQUITTUNG ist
            this.showGesuchFreigebenSimulationButton = (response && this.isGesuchInStatus(TSAntragStatus.FREIGABEQUITTUNG) && isSuperadmin);
        });
    }

    public isGesuchFreigegeben(): boolean {
        if (this.gesuchModelManager.getGesuch() && this.gesuchModelManager.getGesuch().status) {
            return isAtLeastFreigegeben(this.gesuchModelManager.getGesuch().status)
                || (this.gesuchModelManager.getGesuch().status === TSAntragStatus.FREIGABEQUITTUNG);
        }
        return false;
    }

    public isFreigabequittungAusstehend(): boolean {
        if (this.gesuchModelManager.getGesuch() && this.gesuchModelManager.getGesuch().status) {
            return this.gesuchModelManager.getGesuch().status === TSAntragStatus.FREIGABEQUITTUNG;
        }
        return false;
    }

    public openFreigabequittungPDF(forceCreation: boolean): IPromise<void> {
        let win: Window = this.downloadRS.prepareDownloadWindow();
        return this.downloadRS.getFreigabequittungAccessTokenGeneratedDokument(this.gesuchModelManager.getGesuch().id, forceCreation, this.getZustelladresse())
            .then((downloadFile: TSDownloadFile) => {
                // wir laden das Gesuch neu, da die Erstellung des Dokumentes auch Aenderungen im Gesuch verursacht
                this.gesuchModelManager.openGesuch(this.gesuchModelManager.getGesuch().id).then(() => {
                    this.downloadRS.startDownload(downloadFile.accessToken, downloadFile.filename, false, win);
                });
            });
    }

    public isThereAnySchulamtAngebot(): boolean {
        return this.gesuchModelManager.isThereAnySchulamtAngebot();
    }

    public getFreigabeDatum(): string {
        if (this.gesuchModelManager.getGesuch() && this.gesuchModelManager.getGesuch().freigabeDatum) {
            return DateUtil.momentToLocalDateFormat(this.gesuchModelManager.getGesuch().freigabeDatum, 'DD.MM.YYYY');
        }
        return '';
    }

    public getTextForFreigebenNotAllowed(): string {
        if (this.isGesuchReadonly()) {
            return 'FREIGABEQUITTUNG_NOT_ALLOWED_BESCHWERDE_TEXT';
        } else {
            return 'FREIGABEQUITTUNG_NOT_ALLOWED_TEXT';
        }
    }

    /**
     * Die Methodes wizardStepManager.areAllStepsOK() erlaubt dass die Betreuungen in Status PLATZBESTAETIGUNG sind
     * aber in diesem Fall duerfen diese nur OK sein, deswegen die Frage extra. Ausserdem darf es nur freigegebn werden
     * wenn es nicht in ReadOnly modus ist
     */
    public canBeFreigegeben(): boolean {
        return this.wizardStepManager.areAllStepsOK() &&
            this.wizardStepManager.isStepStatusOk(TSWizardStepName.BETREUUNG)
            && !this.isGesuchReadonly() && this.isGesuchInStatus(TSAntragStatus.IN_BEARBEITUNG_GS);
    }

    private getZustelladresse(): TSZustelladresse {
        if (this.gesuchModelManager.isGesuch()) {
            if (this.gesuchModelManager.areThereOnlySchulamtAngebote()) {
                return TSZustelladresse.SCHULAMT;
            } else {
                return TSZustelladresse.JUGENDAMT;
            }

        } else {
            if (this.gesuchModelManager.areAllJAAngeboteNew()) {
                return TSZustelladresse.JUGENDAMT;
            }
        }
        return undefined;
    }

    /**
     * Wir koennen auf jeden Fall sicher sein, dass alle Erstgesuche eine Freigabequittung haben.
     * Ausserdem nur die Mutationen bei denen alle JA-Angebote neu sind, werden eine Freigabequittung haben
     */
    public isThereFreigabequittung(): boolean {
        return this.gesuchModelManager.isGesuch() || this.gesuchModelManager.areAllJAAngeboteNew();
    }
}
