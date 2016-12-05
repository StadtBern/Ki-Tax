import AbstractGesuchViewController from '../abstractGesuchView';
import {IComponentOptions, IPromise, IQService, IScope} from 'angular';
import GesuchModelManager from '../../service/gesuchModelManager';
import BerechnungsManager from '../../service/berechnungsManager';
import ErrorService from '../../../core/errors/service/ErrorService';
import WizardStepManager from '../../service/wizardStepManager';
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
import {TSWizardStepStatus} from '../../../models/enums/TSWizardStepStatus';
import {DvDialog} from '../../../core/directive/dv-dialog/dv-dialog';
import {RemoveDialogController} from '../../dialog/RemoveDialogController';
import {DownloadRS} from '../../../core/service/downloadRS.rest';
import {TSGeneratedDokumentTyp} from '../../../models/enums/TSGeneratedDokumentTyp';
import TSDownloadFile from '../../../models/TSDownloadFile';
import ITranslateService = angular.translate.ITranslateService;
import IFormController = angular.IFormController;
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


export class FreigabeViewController extends AbstractGesuchViewController {

    bestaetigungFreigabequittung: boolean = false;

    static $inject = ['GesuchModelManager', 'BerechnungsManager', 'ErrorService', 'WizardStepManager',
        'DvDialog', '$translate', '$q', '$scope', 'DownloadRS'];
    /* @ngInject */
    constructor(gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager,
                private errorService: ErrorService, wizardStepManager: WizardStepManager, private DvDialog: DvDialog,
                private $translate: ITranslateService, private $q: IQService, private $scope: IScope, private downloadRS: DownloadRS) {

        super(gesuchModelManager, berechnungsManager, wizardStepManager);
        this.initViewModel();
    }

    private initViewModel(): void {
        this.wizardStepManager.setCurrentStep(TSWizardStepName.FREIGABE);
        this.wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.IN_BEARBEITUNG);
    }

    public gesuchFreigeben(form: IFormController): IPromise<void> {
        if (form.$valid && this.bestaetigungFreigabequittung === true) {
            return this.DvDialog.showDialog(dialogTemplate, RemoveDialogController, {
                title: 'CONFIRM_GESUCH_FREIGEBEN',
                deleteText: 'CONFIRM_GESUCH_FREIGEBEN_DESCRIPTION'
            }).then(() => {
                return this.openFreigabequittungPDF();
            });
        }
        return undefined;
    }

    public isGesuchFreigegeben(): boolean {
        // if (this.gesuchModelManager.getGesuch()) {
        //     return isFreigegeben(this.gesuchModelManager.getGesuch().status);
        // }
        return false;
    }

    public openFreigabequittungPDF(): IPromise<void> {
        return this.downloadRS.getAccessTokenGeneratedDokument(this.gesuchModelManager.getGesuch().id, TSGeneratedDokumentTyp.FREIGABEQUITTUNG, false)
            .then((downloadFile: TSDownloadFile) => {
                this.downloadRS.startDownload(downloadFile.accessToken, downloadFile.filename, false);
            });
    }

    public isThereAnySchulamtAngebot(): boolean {
        return this.gesuchModelManager.isThereAnySchulamtAngebot();
    }

    public getFreigabeDatum(): string {
        return '';
    }

}
