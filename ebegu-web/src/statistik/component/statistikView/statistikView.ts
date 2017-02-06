import {IComponentOptions} from 'angular';
import {IStateService} from 'angular-ui-router';
import TSStatistikParameter from '../../../models/TSStatistikParameter';
import {TSStatistikParameterType} from '../../../models/enums/TSStatistikParameterType';
import TSGesuchsperiode from '../../../models/TSGesuchsperiode';
import GesuchsperiodeRS from '../../../core/service/gesuchsperiodeRS.rest';

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


    static $inject: string[] = ['$state', 'GesuchsperiodeRS'];

    constructor(private $state: IStateService, private gesuchsperiodeRS: GesuchsperiodeRS) {
        this._statistikParameter = new TSStatistikParameter();
        this.initViewModel();
        this.gesuchsperiodeRS.getAllGesuchsperioden().then((response: any) => {
            this._gesuchsperioden = response;
        });
    }

    private initViewModel() {
    }

    public generateStatistik(type?: TSStatistikParameterType): void {
     let tmpType = (<any>TSStatistikParameterType)[type];
        console.log(this._statistikParameter.von);
        console.log(this._statistikParameter.bis);
        switch (tmpType) {
            case TSStatistikParameterType.GESUCH_STICHTAG:
                console.log(tmpType);
                break;
            case TSStatistikParameterType.GESUCH_ZEITRAUM:
                console.log(tmpType);
                break;
            case TSStatistikParameterType.KINDER:
                console.log(tmpType);
                break;
            case TSStatistikParameterType.GESUCHSTELLER:
                console.log(tmpType);
                break;
            case TSStatistikParameterType.KANTON:
                console.log(tmpType);
                break;
            case TSStatistikParameterType.GESUCHSTELLER_KINDER_BETREUUNG:
                console.log(tmpType);
                break;
            default:
                console.log('default, Type not recognized');
                break;

        }
    }

    get statistikParameter(): TSStatistikParameter {
        return this._statistikParameter;
    }

    get gesuchsperioden(): Array<TSGesuchsperiode> {
        return this._gesuchsperioden;
    }
}
