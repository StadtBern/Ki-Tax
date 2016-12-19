import AbstractGesuchViewController from '../abstractGesuchView';
import {IComponentOptions, IPromise} from 'angular';
import GesuchModelManager from '../../service/gesuchModelManager';
import BerechnungsManager from '../../service/berechnungsManager';
import WizardStepManager from '../../service/wizardStepManager';
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
import {TSWizardStepStatus} from '../../../models/enums/TSWizardStepStatus';
import {DvDialog} from '../../../core/directive/dv-dialog/dv-dialog';
import {RemoveDialogController} from '../../dialog/RemoveDialogController';
import {DownloadRS} from '../../../core/service/downloadRS.rest';
import TSDownloadFile from '../../../models/TSDownloadFile';
import {isAtLeastFreigegeben, TSAntragStatus} from '../../../models/enums/TSAntragStatus';
import DateUtil from '../../../utils/DateUtil';
import {TSZustelladresse} from '../../../models/enums/TSZustelladresse';
import {ApplicationPropertyRS} from '../../../admin/service/applicationPropertyRS.rest';
import ITranslateService = angular.translate.ITranslateService;
import IFormController = angular.IFormController;
import IScope = angular.IScope;
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

    static $inject = ['GesuchModelManager', 'BerechnungsManager', 'WizardStepManager',
        'DvDialog', 'DownloadRS', '$scope', 'ApplicationPropertyRS'];
    /* @ngInject */
    constructor(gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager,
                wizardStepManager: WizardStepManager, private DvDialog: DvDialog,
                private downloadRS: DownloadRS, $scope: IScope,  private applicationPropertyRS: ApplicationPropertyRS) {

        super(gesuchModelManager, berechnungsManager, wizardStepManager, $scope);
        this.initViewModel();
    }

    private initViewModel(): void {
        this.wizardStepManager.setCurrentStep(TSWizardStepName.FREIGABE);
        this.wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.IN_BEARBEITUNG);
        this.initDevModeParameter();
    }

    public gesuchEinreichen(): IPromise<void> {
        this.isFreigebenClicked = true;
        if (this.form.$valid && this.bestaetigungFreigabequittung === true) {
            return this.DvDialog.showDialog(dialogTemplate, RemoveDialogController, {
                title: 'CONFIRM_GESUCH_FREIGEBEN',
                deleteText: 'CONFIRM_GESUCH_FREIGEBEN_DESCRIPTION'
            }).then(() => {
                return this.openFreigabequittungPDF();
            });
        }
        return undefined;
    }

    public gesuchFreigeben(): void {
        let gesuchID = this.gesuchModelManager.getGesuch().id;
        this.gesuchModelManager.antragFreigeben(gesuchID);
    }

    private initDevModeParameter() {
        this.applicationPropertyRS.isDevMode().then((response: boolean) => {
            // Die Simulation ist nur im Dev-Mode moeglich und nur, wenn das Gesuch im Status FREIGABEQUITTUNG ist
            this.showGesuchFreigebenSimulationButton = (response && this.isGesuchInStatus(TSAntragStatus.FREIGABEQUITTUNG));
        });
    }

    public isGesuchFreigegeben(): boolean {
        if (this.gesuchModelManager.getGesuch() && this.gesuchModelManager.getGesuch().status) {
            return isAtLeastFreigegeben(this.gesuchModelManager.getGesuch().status) || (this.gesuchModelManager.getGesuch().status === TSAntragStatus.FREIGABEQUITTUNG);
        }
        return false;
    }

    public isFreigabequittungAusstehend(): boolean {
        if (this.gesuchModelManager.getGesuch() && this.gesuchModelManager.getGesuch().status) {
            return this.gesuchModelManager.getGesuch().status === TSAntragStatus.FREIGABEQUITTUNG;
        }
        return false;
    }


    public openFreigabequittungPDF(): IPromise<void> {
        return this.downloadRS.getFreigabequittungAccessTokenGeneratedDokument(this.gesuchModelManager.getGesuch().id, false, this.getZustelladresse())
            .then((downloadFile: TSDownloadFile) => {
                // wir laden das Gesuch neu, da die Erstellung des Dokumentes auch Aenderungen im Gesuch verursacht
                this.gesuchModelManager.openGesuch(this.gesuchModelManager.getGesuch().id).then(() => {
                    this.downloadRS.startDownload(downloadFile.accessToken, downloadFile.filename, false);
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

    /**
     * Die Methodes wizardStepManager.areAllStepsOK() erlaubt dass die Betreuungen in Status PLATZBESTAETIGUNG sind
     * aber in diesem Fall duerfen diese nur OK sein, deswegen die Frage extra.
     */
    public canBeFreigegeben(): boolean {
        return this.wizardStepManager.areAllStepsOK() &&
            this.wizardStepManager.isStepStatusOk(TSWizardStepName.BETREUUNG);
    }

    private getZustelladresse(): TSZustelladresse {
        if (this.gesuchModelManager.isErstgesuch()) {
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
        return this.gesuchModelManager.isErstgesuch() || this.gesuchModelManager.areAllJAAngeboteNew();
    }
}
