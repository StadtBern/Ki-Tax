import {EbeguWebCore} from '../../../core/core.module';
import {FallCreationViewController} from './fallCreationView';
import GesuchModelManager from '../../service/gesuchModelManager';
import {TSGeschlecht} from '../../../models/enums/TSGeschlecht';
import TSGesuch from '../../../models/TSGesuch';
import TSGesuchsperiode from '../../../models/TSGesuchsperiode';
import {TSDateRange} from '../../../models/types/TSDateRange';
import DateUtil from '../../../utils/DateUtil';

describe('fallCreationView', function () {

    let fallCreationview: FallCreationViewController;
    let gesuchModelManager: GesuchModelManager;

    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        gesuchModelManager = $injector.get('GesuchModelManager');
        fallCreationview = new FallCreationViewController($injector.get('$state'), gesuchModelManager, $injector.get('BerechnungsManager'));
    }));

    describe('API Usage', function () {
        it('should return the current Gesuchsperiode formatted', function () {
            var momentAb = DateUtil.today().year(2016);
            var momentBis = DateUtil.today().year(2017);
            let gesuchsperiode: TSGesuchsperiode = new TSGesuchsperiode(true, new TSDateRange(momentAb, momentBis));
            spyOn(gesuchModelManager, 'getGesuchsperiode').and.returnValue(gesuchsperiode);
            let result: string = fallCreationview.getCurrentGesuchsperiode();
            expect(result).toEqual('2016/2017');
        });
    });
});
