import {EbeguWebPendenzen} from '../../pendenzen.module';
import PendenzRS from '../../service/PendenzRS.rest';
import {PendenzenListViewController} from './pendenzenListView';
import {IScope, IQService, IFilterService} from 'angular';
import TSPendenzJA from '../../../models/TSPendenzJA';
import {TSAntragTyp} from '../../../models/enums/TSAntragTyp';
import {TSBetreuungsangebotTyp} from '../../../models/enums/TSBetreuungsangebotTyp';
describe('pendenzenListView', function () {

    let pendenzRS: PendenzRS;
    let pendenzListViewController: PendenzenListViewController;
    let $q: IQService;
    let $scope: IScope;
    let $filter: IFilterService;


    beforeEach(angular.mock.module(EbeguWebPendenzen.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        pendenzRS = $injector.get('PendenzRS');
        $q = $injector.get('$q');
        $scope = $injector.get('$rootScope');
        $filter = $injector.get('$filter');
    }));


    describe('API Usage', function () {
        describe('getPendenzenList', function () {
            it('should return the list with all pendenzen', function () {
                let mockPendenz: TSPendenzJA = new TSPendenzJA(123, 'name', TSAntragTyp.GESUCH, undefined,
                    undefined, [TSBetreuungsangebotTyp.KITA], ['Inst1, Inst2']);
                let result: Array<TSPendenzJA> = [mockPendenz];
                spyOn(pendenzRS, 'getPendenzenList').and.returnValue($q.when(result));

                pendenzListViewController = new PendenzenListViewController(pendenzRS, undefined, $filter);
                $scope.$apply();
                expect(pendenzRS.getPendenzenList).toHaveBeenCalled();

                let list: Array<TSPendenzJA> = pendenzListViewController.getPendenzenList();
                expect(list).toBeDefined();
                expect(list.length).toBe(1);
                expect(list[0]).toEqual(mockPendenz);
            });
        });
        describe('addZerosToFallnummer', () => {
            it('returns a string with 6 chars starting with 0s and ending with the given number', () => {
                expect(pendenzListViewController.addZerosToFallnummer(0)).toEqual('000000');
                expect(pendenzListViewController.addZerosToFallnummer(1)).toEqual('000001');
                expect(pendenzListViewController.addZerosToFallnummer(12)).toEqual('000012');
                expect(pendenzListViewController.addZerosToFallnummer(123)).toEqual('000123');
                expect(pendenzListViewController.addZerosToFallnummer(1234)).toEqual('001234');
                expect(pendenzListViewController.addZerosToFallnummer(12345)).toEqual('012345');
                expect(pendenzListViewController.addZerosToFallnummer(123456)).toEqual('123456');
            });
            it('returns undefined if the number is undefined', () => {
                expect(pendenzListViewController.addZerosToFallnummer(undefined)).toBeUndefined();
                expect(pendenzListViewController.addZerosToFallnummer(null)).toBeUndefined();
            });
            it('returns the given number as string if its length is greather than 6', () => {
                expect(pendenzListViewController.addZerosToFallnummer(1234567)).toEqual('1234567');
            });
        });
        describe('translateBetreuungsangebotTypList', () => {
            it('returns a comma separated string with all BetreuungsangebotTypen', () => {
                let list: Array<TSBetreuungsangebotTyp> = [TSBetreuungsangebotTyp.KITA, TSBetreuungsangebotTyp.TAGESELTERN];
                expect(pendenzListViewController.translateBetreuungsangebotTypList(list))
                    .toEqual('Tagesstätte für Kleinkinder, Tageseltern');
            });
            it('returns an empty string for invalid values or empty lists', () => {
                expect(pendenzListViewController.translateBetreuungsangebotTypList([])).toEqual('');
                expect(pendenzListViewController.translateBetreuungsangebotTypList(undefined)).toEqual('');
                expect(pendenzListViewController.translateBetreuungsangebotTypList(null)).toEqual('');
            });
        });
    });
});
