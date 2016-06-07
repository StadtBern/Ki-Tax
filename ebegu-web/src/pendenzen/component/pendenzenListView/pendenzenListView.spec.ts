import {EbeguWebPendenzen} from '../../pendenzen.module';
import PendenzRS from '../../service/PendenzRS.rest';
import {PendenzenListViewController} from './pendenzenListView';
import {IScope, IQService} from 'angular';
import TSPendenzJA from '../../../models/TSPendenzJA';
import {TSAntragTyp} from '../../../models/enums/TSAntragTyp';
import {TSBetreuungsangebotTyp} from '../../../models/enums/TSBetreuungsangebotTyp';
describe('pendenzenListView', function () {

    let pendenzRS: PendenzRS;
    let pendenzListViewController: PendenzenListViewController;
    let $q: IQService;
    let $scope: IScope;


    beforeEach(angular.mock.module(EbeguWebPendenzen.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        pendenzRS = $injector.get('PendenzRS');
        $q = $injector.get('$q');
        $scope = $injector.get('$rootScope');
    }));


    describe('API Usage', function () {
        describe('getPendenzenList', function () {
            it('should return the list with all pendenzen', function () {
                let mockPendenz: TSPendenzJA = new TSPendenzJA(123, 'name', TSAntragTyp.GESUCH, undefined,
                    undefined, [TSBetreuungsangebotTyp.KITA], ['Inst1, Inst2']);
                let result: Array<TSPendenzJA> = [mockPendenz];
                spyOn(pendenzRS, 'getPendenzenList').and.returnValue($q.when(result));

                pendenzListViewController = new PendenzenListViewController(pendenzRS);
                $scope.$apply();
                expect(pendenzRS.getPendenzenList).toHaveBeenCalled();

                let list: Array<TSPendenzJA> = pendenzListViewController.getPendenzenList();
                expect(list).toBeDefined();
                expect(list.length).toBe(1);
                expect(list[0]).toEqual(mockPendenz);
            });
        });
    });
});
