import AbstractGesuchViewController from '../abstractGesuchView';
import {IComponentOptions, IPromise} from 'angular';
import GesuchModelManager from '../../service/gesuchModelManager';
import BerechnungsManager from '../../service/berechnungsManager';
import ErrorService from '../../../core/errors/service/ErrorService';
import WizardStepManager from '../../service/wizardStepManager';
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
import {TSWizardStepStatus} from '../../../models/enums/TSWizardStepStatus';
import ITranslateService = angular.translate.ITranslateService;
import TSGesuch from '../../../models/TSGesuch';
import {TSBetroffene} from '../../../models/enums/TSBetroffene';
let template = require('./umzugView.html');
require('./umzugView.less');
let removeDialogTemplate = require('../../dialog/removeDialogTemplate.html');


export class UmzugViewComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {};
    template = template;
    controller = UmzugViewController;
    controllerAs = 'vm';
}


export class UmzugViewController extends AbstractGesuchViewController {

    public betroffene: TSBetroffene;


    static $inject = ['GesuchModelManager', 'BerechnungsManager', 'WizardStepManager', 'ErrorService', '$translate'];
    /* @ngInject */
    constructor(gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager,
                wizardStepManager: WizardStepManager, private errorService: ErrorService, private $translate: ITranslateService) {

        super(gesuchModelManager, berechnungsManager, wizardStepManager);
        this.initViewModel();
    }

    private initViewModel(): void {
        // this.showUmzug = (this.gesuchModelManager.getStammdatenToWorkWith().umzugAdresse) ? true : false;
        this.wizardStepManager.setCurrentStep(TSWizardStepName.UMZUG);
        this.wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.IN_BEARBEITUNG);
    }

    public save(form: angular.IFormController): IPromise<TSGesuch> {
        if (form.$valid) {
            this.errorService.clearAll();
            // if (!this.showUmzug) {
            //     this.gesuchModelManager.setUmzugAdresse(this.showUmzug);
            // }
            return this.gesuchModelManager.updateUmzug().then((response) => {
                return response;
            });
        }
        return undefined;
    }

    /**
     * Hier schauen wir wie viele GS es gibt und dementsprechen fuellen wir die Liste aus.
     * Bei Mutationen wird es nur geschaut ob der GS existiert (!=null), da die Familiensituation nicht relevant ist.
     * Es koennte einen GS2 geben obwohl die neue Familiensituation "ledig" sagt
     */
    public getBetroffenenList(): Array<TSBetroffene> {
        let betroffenenList: Array<TSBetroffene> = [];
        if (this.gesuchModelManager.getGesuch()) {
            if (this.gesuchModelManager.getGesuch().gesuchsteller1) {
                betroffenenList.push(TSBetroffene.GESUCHSTELLER_1);
            }
            if (this.gesuchModelManager.getGesuch().gesuchsteller2) {
                betroffenenList.push(TSBetroffene.GESUCHSTELLER_2);
            }
            if (this.gesuchModelManager.getGesuch().gesuchsteller2 && this.gesuchModelManager.getGesuch().gesuchsteller1) {
                // Dies koennte auch direkt beim Push des GS2 gemacht werden, da es keinen GS2 geben darf wenn es keinen GS1 gibt.
                // Allerdings sind wir mit diesem IF sicher dass GS1 und GS2 wirklich existieren.
                betroffenenList.push(TSBetroffene.BEIDE_GESUCHSTELLER);
            }
        }
        return betroffenenList; // empty list wenn die Daten nicht richtig sind
    }

    public getNameFromBetroffene(betroffene: TSBetroffene): string {
        if (TSBetroffene.GESUCHSTELLER_1 === betroffene && this.gesuchModelManager.getGesuch().gesuchsteller1) {
            return this.gesuchModelManager.getGesuch().gesuchsteller1.getFullName();

        } else if (TSBetroffene.GESUCHSTELLER_2 === betroffene && this.gesuchModelManager.getGesuch().gesuchsteller2) {
            return this.gesuchModelManager.getGesuch().gesuchsteller2.getFullName();

        } else if (TSBetroffene.BEIDE_GESUCHSTELLER === betroffene) {
            return this.$translate.instant(TSBetroffene[betroffene]);
        }

        return '';
    }

}
