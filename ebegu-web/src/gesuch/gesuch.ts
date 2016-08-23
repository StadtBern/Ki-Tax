import AbstractGesuchViewController from './component/abstractGesuchView';
import {IStateService} from 'angular-ui-router';
import GesuchModelManager from './service/gesuchModelManager';
import BerechnungsManager from './service/berechnungsManager';
import DateUtil from '../utils/DateUtil';

export class GesuchRouteController extends AbstractGesuchViewController {


    static $inject: string[] = ['$state', 'GesuchModelManager', 'BerechnungsManager', '$scope'];
    /* @ngInject */
    constructor(state: IStateService, gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager, $scope: any) {
        super(state, gesuchModelManager, berechnungsManager);
    }

    showFinanzsituationStart(): boolean {
        return !!this.gesuchModelManager.isGesuchsteller2Required();
    }


    public getDateErstgesuch(): string {
        if (this.gesuchModelManager && this.gesuchModelManager.getGesuch()) {
            return DateUtil.momentToLocalDateFormat(this.gesuchModelManager.getGesuch().eingangsdatum, 'DD.MM.YYYY');
        }
        return undefined;
    }

}
