import {IComponentOptions} from 'angular';
import {IStateService} from 'angular-ui-router';
import TSStatistikParameter from '../../../models/TSStatistikParameter';
import {TSStatistikParameterType} from '../../../models/enums/TSStatistikParameterType';
import TSGesuchsperiode from '../../../models/TSGesuchsperiode';
import GesuchsperiodeRS from '../../../core/service/gesuchsperiodeRS.rest';
import {TSRole} from '../../../models/enums/TSRole';
import {TSRoleUtil} from '../../../utils/TSRoleUtil';
import IFormController = angular.IFormController;
import ILogService = angular.ILogService;

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
    TSRole: any;
    TSRoleUtil: any;

    static $inject: string[] = ['$state', 'GesuchsperiodeRS', '$log'];

    constructor(private $state: IStateService, private gesuchsperiodeRS: GesuchsperiodeRS, private $log: ILogService) {
        this._statistikParameter = new TSStatistikParameter();
        this.initViewModel();
        this.gesuchsperiodeRS.getAllGesuchsperioden().then((response: any) => {
            this._gesuchsperioden = response;
        });
        this.TSRole = TSRole;
        this.TSRoleUtil = TSRoleUtil;
    }

    private initViewModel() {
    }

    public generateStatistik(form: IFormController, type?: TSStatistikParameterType): void {
        if (form.$valid) {
            let tmpType = (<any>TSStatistikParameterType)[type];
            tmpType ? this.$log.debug('Statistik Type: ' + tmpType) : this.$log.debug('default, Type not recognized');
            this.$log.debug('Validated Form: ' + form.$name);
            switch (tmpType) {
                case TSStatistikParameterType.GESUCH_STICHTAG:
                    break;
                case TSStatistikParameterType.GESUCH_ZEITRAUM:
                    break;
                case TSStatistikParameterType.KINDER:
                    break;
                case TSStatistikParameterType.GESUCHSTELLER:
                    break;
                case TSStatistikParameterType.KANTON:
                    break;
                case TSStatistikParameterType.GESUCHSTELLER_KINDER_BETREUUNG:
                    break;
                default:
                    this.$log.debug('default, Type not recognized');
                    break;
            }
        }
    }

    get statistikParameter(): TSStatistikParameter {
        return this._statistikParameter;
    }

    get gesuchsperioden(): Array<TSGesuchsperiode> {
        return this._gesuchsperioden;
    }
}
