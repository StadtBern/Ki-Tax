import {IComponentOptions} from 'angular';
import EbeguUtil from '../../../utils/EbeguUtil';
import {IStateService} from 'angular-ui-router';
import TSStatistikParameter from '../../../models/TSStatistikParameter';
import {TSStatistikParameterType} from '../../../models/enums/TSStatistikParameterType';
import TSGesuchsperiode from '../../../models/TSGesuchsperiode';
import GesuchsperiodeRS from '../../../core/service/gesuchsperiodeRS.rest';
import {ReportRS} from '../../../core/service/reportRS.rest';

let template = require('./statistikView.html');
require('./statistikView.less');

export class StatistikViewComponentConfig implements IComponentOptions {
    transclude = false;
    template = template;
    controller = StatistikViewController;
    controllerAs = 'vm';
}

export class StatistikViewController {
    private _statistikParameter: TSStatistikParameter;
    private _gesuchsperioden: Array<TSGesuchsperiode>;


    static $inject: string[] = ['EbeguUtil', '$state', 'GesuchsperiodeRS', 'ReportRS'];

    constructor(private ebeguUtil: EbeguUtil, private $state: IStateService, private gesuchsperiodeRS: GesuchsperiodeRS,
        private reportRS: ReportRS) {
        this._statistikParameter = new TSStatistikParameter();
        this.initViewModel();
        this.gesuchsperiodeRS.getAllGesuchsperioden().then((response: any) => {
            this._gesuchsperioden = response;
        });
    }

    private initViewModel() {
    }

    public generateStatistik(type?: TSStatistikParameterType): TSStatistikParameter {
        this._statistikParameter.type = (<any>TSStatistikParameterType)[type];
        let tmpParameter = angular.copy(this._statistikParameter);
        if (this._statistikParameter.type === TSStatistikParameterType.GESUCHSTELLER ||
            this._statistikParameter.type === TSStatistikParameterType.KANTON
            ) {
            tmpParameter.gesuchsperiode = null;
        }
        if (this._statistikParameter.type === TSStatistikParameterType.GESUCH_STICHTAG ||
            this._statistikParameter.type === TSStatistikParameterType.GESUCHSTELLER
            ) {
            tmpParameter.von = null;
            tmpParameter.bis = null;
        }
        if (this._statistikParameter.type !== TSStatistikParameterType.GESUCH_STICHTAG &&
            this._statistikParameter.type !== TSStatistikParameterType.GESUCHSTELLER
            ) {
            tmpParameter.stichtag = null;
        }
        return tmpParameter;
    }

    get statistikParameter(): TSStatistikParameter {
        return this._statistikParameter;
    }

    get gesuchsperioden(): Array<TSGesuchsperiode> {
        return this._gesuchsperioden;
    }
}
