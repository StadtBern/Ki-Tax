import AdresseRS from '../../service/adresseRS.rest';
import TSLand from '../../../models/types/TSLand';
import ListResourceRS from '../../service/listResourceRS.rest';
import {IComponentOptions, IFormController} from 'angular';
import {TSRoleUtil} from '../../../utils/TSRoleUtil';
import GesuchModelManager from '../../../gesuch/service/gesuchModelManager';
import TSAdresseContainer from '../../../models/TSAdresseContainer';
import ITranslateService = angular.translate.ITranslateService;
require('./dv-adresse.less');

export class AdresseComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {
        adresse: '<',
        prefix: '@',
        organisation: '<',
        showNichtInGemeinde: '<',
        showIfBisherNone: '<'
    };
    template = require('./dv-adresse.html');
    controller = DvAdresseController;
    controllerAs = 'vm';
    require: any = {parentForm: '?^form'};
}


export class DvAdresseController {
    static $inject = ['AdresseRS', 'ListResourceRS', 'GesuchModelManager', '$translate'];

    adresse: TSAdresseContainer;
    prefix: string;
    adresseRS: AdresseRS;
    $translate: ITranslateService;
    parentForm: IFormController;
    laenderList: TSLand[];
    organisation: boolean;
    TSRoleUtil = TSRoleUtil;
    showNichtInGemeinde: boolean;
    gesuchModelManager: GesuchModelManager;
    bisherLand: string;

    /* @ngInject */
    constructor(adresseRS: AdresseRS, listResourceRS: ListResourceRS, gesuchModelManager: GesuchModelManager, $translate: ITranslateService) {
        this.TSRoleUtil = TSRoleUtil;
        this.adresseRS = adresseRS;
        this.gesuchModelManager = gesuchModelManager;
        this.$translate = $translate;
        this.bisherLand = this.getBisherLand();
        listResourceRS.getLaenderList().then((laenderList: TSLand[]) => {
            this.laenderList = laenderList;
        });
    }

    submit() {
        this.adresseRS.create(this.adresse)
            .then((response: any) => {
                if (response.status === 201) {
                    this.resetForm();
                }
            });
    }

    resetForm() {
        this.adresse = undefined;
    }


    public isGesuchReadonly(): boolean {
        return this.gesuchModelManager.isGesuchReadonly();
    }

    /**
     *
     * @returns {boolean}
     */
    public disableWohnadresseFor2GS(): boolean {
        return (this.prefix !== 'umzug')
            && (this.gesuchModelManager.getGesuch().isMutation() && (this.gesuchModelManager.getGesuchstellerNumber() === 1
            || (this.gesuchModelManager.getStammdatenToWorkWith().vorgaengerId !== null
            && this.gesuchModelManager.getStammdatenToWorkWith().vorgaengerId !== undefined)));
    }

    public showDatumVon(): boolean {
        return this.adresse.showDatumVon;
    }

    public getModel(): TSAdresseContainer {
        return this.adresse;
    }

    private getBisherLand(): string {
        if (this.getModel() &&  this.getModel().adresseGS && this.getModel().adresseGS.land) {
            return this.$translate.instant('Land_' + this.getModel().adresseGS.land);
        }
        return '';
    }

}

