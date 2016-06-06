import TSEbeguParameter from '../../../models/TSEbeguParameter';
import {EbeguParameterRS} from '../../service/ebeguParameterRS.rest';
import EbeguRestUtil from '../../../utils/EbeguRestUtil';
import {IHttpPromiseCallbackArg, IComponentOptions} from 'angular';
import './parameterView.less';
import IPromise = angular.IPromise;
import TSGesuchsperiode from '../../../models/TSGesuchsperiode';
import GesuchsperiodeRS from '../../../core/service/gesuchsperiodeRS.rest';
let template = require('./parameterView.html');

export class ParameterViewComponentConfig implements IComponentOptions {
    transclude: boolean = false;
    template: string = template;
    controller: any = ParameterViewController;
    controllerAs: string = 'vm';
}

export class ParameterViewController {
    static $inject = ['EbeguParameterRS', 'GesuchsperiodeRS', 'EbeguRestUtil'];

    ebeguParameterRS: EbeguParameterRS;
    ebeguRestUtil: EbeguRestUtil;

    gesuchsperiodenList: Array<TSGesuchsperiode>;
    gesuchsperiode: TSGesuchsperiode;

    jahr: number;

    ebeguParameterListGesuchsperiode: TSEbeguParameter[];
    ebeguParameterListJahr: TSEbeguParameter[];


    /* @ngInject */
    constructor(ebeguParameterRS: EbeguParameterRS, private gesuchsperiodeRS: GesuchsperiodeRS, ebeguRestUtil: EbeguRestUtil) {
        this.ebeguParameterRS = ebeguParameterRS;
        this.ebeguRestUtil = ebeguRestUtil;
        this.readGesuchsperioden();
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
        this.gesuchsperiode = new TSGesuchsperiode();
        this.gesuchsperiode.active = false;
    }

    saveGesuchsperiode(): void {
        this.gesuchsperiodeRS.updateGesuchsperiode(this.gesuchsperiode).then((response: any) => {
            this.gesuchsperiode = response;
        });
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
    }

    saveParameterByJahr(): void {
        for (var i = 0; i < this.ebeguParameterListJahr.length; i++) {
            var param = this.ebeguParameterListJahr[i];
            this.ebeguParameterRS.saveEbeguParameter(param);
        }
    }
}
