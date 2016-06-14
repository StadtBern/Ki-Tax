import TSEbeguParameter from '../../../models/TSEbeguParameter';
import {EbeguParameterRS} from '../../service/ebeguParameterRS.rest';
import EbeguRestUtil from '../../../utils/EbeguRestUtil';
import {IComponentOptions} from 'angular';
import './parameterView.less';
import TSGesuchsperiode from '../../../models/TSGesuchsperiode';
import GesuchsperiodeRS from '../../../core/service/gesuchsperiodeRS.rest';
import DateUtil from '../../../utils/DateUtil';
import {TSDateRange} from '../../../models/types/TSDateRange';
import IPromise = angular.IPromise;
import ITranslateService = angular.translate.ITranslateService;
import Moment = moment.Moment;
let template = require('./parameterView.html');

export class ParameterViewComponentConfig implements IComponentOptions {
    transclude: boolean = false;
    template: string = template;
    controller: any = ParameterViewController;
    controllerAs: string = 'vm';
}

export class ParameterViewController {
    static $inject = ['EbeguParameterRS', 'GesuchsperiodeRS', 'EbeguRestUtil', '$translate'];

    ebeguParameterRS: EbeguParameterRS;
    ebeguRestUtil: EbeguRestUtil;

    gesuchsperiodenList: Array<TSGesuchsperiode>;
    gesuchsperiode: TSGesuchsperiode;

    jahr: number;

    ebeguParameterListGesuchsperiode: TSEbeguParameter[];
    ebeguParameterListJahr: TSEbeguParameter[];


    /* @ngInject */
    constructor(ebeguParameterRS: EbeguParameterRS, private gesuchsperiodeRS: GesuchsperiodeRS, ebeguRestUtil: EbeguRestUtil, private $translate: ITranslateService) {
        this.ebeguParameterRS = ebeguParameterRS;
        this.ebeguRestUtil = ebeguRestUtil;
        this.readGesuchsperioden();
        this.jahr = DateUtil.currentYear();
        this.jahrChanged();
    }

    private readGesuchsperioden(): void {
        this.gesuchsperiodeRS.getAllNichtAbgeschlosseneGesuchsperioden().then((response: any) => {
            this.gesuchsperiodenList = angular.copy(response);
        });
    }

    private readEbeguParameterByGesuchsperiode(): void {
        this.ebeguParameterRS.getEbeguParameterByGesuchsperiode(this.gesuchsperiode.id).then((response: TSEbeguParameter[]) => {
            this.ebeguParameterListGesuchsperiode = response;
        });
    }

    private readEbeguParameterByJahr(): void {
        this.ebeguParameterRS.getEbeguParameterByJahr(this.jahr).then((response: TSEbeguParameter[]) => {
            this.ebeguParameterListJahr = response;
        });
    }

    gesuchsperiodeClicked(gesuchsperiode: any) {
        if (gesuchsperiode.isSelected) {
            this.gesuchsperiode = gesuchsperiode;
            this.readEbeguParameterByGesuchsperiode();
        } else {
            this.cancelGesuchsperiode();
        }
    }

    createGesuchsperiode(): void {
        this.gesuchsperiode = new TSGesuchsperiode(false, new TSDateRange());
        if (this.gesuchsperiodenList) {
            let prevGesPer: TSGesuchsperiode = this.gesuchsperiodenList[this.gesuchsperiodenList.length - 1];
            this.gesuchsperiode.gueltigkeit.gueltigAb =  prevGesPer.gueltigkeit.gueltigAb.clone().add('years', 1);
            this.gesuchsperiode.gueltigkeit.gueltigBis =  prevGesPer.gueltigkeit.gueltigBis.clone().add('years', 1);
        }
    }

    saveGesuchsperiode(): void {
        this.gesuchsperiodeRS.updateGesuchsperiode(this.gesuchsperiode).then((response: TSGesuchsperiode) => {
            this.gesuchsperiode = response;

            let index: number = this.getIndexOfElementwithID(response);
            if (index !== -1) {
                this.gesuchsperiodenList[index] = response;
            } else {
                this.gesuchsperiodenList.push(response);
            }
            this.readEbeguParameterByGesuchsperiode();
        });
    }

    private getIndexOfElementwithID(gesuchsperiodeToSearch: TSGesuchsperiode): number {
        var idToSearch = gesuchsperiodeToSearch.id;
        for (var i = 0; i < this.gesuchsperiodenList.length; i++) {
            if (this.gesuchsperiodenList[i].id === idToSearch) {
                return i;
            }
        }
        return -1;

    }

    cancelGesuchsperiode(): void {
        this.gesuchsperiode = undefined;
        this.ebeguParameterListGesuchsperiode = undefined;
    }

    jahrChanged(): void {
        this.readEbeguParameterByJahr();
    }

    saveParameterByGesuchsperiode(): void {
        for (var i = 0; i < this.ebeguParameterListGesuchsperiode.length; i++) {
            var param = this.ebeguParameterListGesuchsperiode[i];
            this.ebeguParameterRS.saveEbeguParameter(param);
        }
        this.gesuchsperiode = undefined;
    }

    saveParameterByJahr(): void {
        for (var i = 0; i < this.ebeguParameterListJahr.length; i++) {
            var param = this.ebeguParameterListJahr[i];
            this.ebeguParameterRS.saveEbeguParameter(param);
        }
    }

}
