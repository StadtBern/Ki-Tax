import {EbeguWebCore} from '../core.module';
import {DVsTPersistService} from './dVsTPersistService';

describe('betreuungRS', function () {

    let dVsTPersistService: DVsTPersistService;
    let object1: any;
    let object2: any;
    let namespace_one: string;
    let namespace_two: string;

    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        dVsTPersistService = $injector.get('DVsTPersistService');
        object1 = {name: 'Angelina', nachname: 'Jolie'};
        object2 = {name: 'Brad', nachname: 'Pitt'};
        namespace_one = 'ns1';
        namespace_two = 'ns2';
    }));


    describe('save and load Data', function () {
        it('saves configurations in 2 different namespaces', function () {
            dVsTPersistService.saveData(namespace_one, object1);
            dVsTPersistService.saveData(namespace_two, object2);

            expect(dVsTPersistService.loadData(namespace_one)).toEqual(object1);
            expect(dVsTPersistService.loadData(namespace_two)).toEqual(object2);
        });
        it('saves 2 configurations in the same namespace, so the second overwrite the first', function () {
            dVsTPersistService.saveData(namespace_one, object1);
            dVsTPersistService.saveData(namespace_one, object2);

            expect(dVsTPersistService.loadData(namespace_one)).toEqual(object2);
            expect(dVsTPersistService.loadData(namespace_two)).toBeUndefined();
        });
    });
    fdescribe('delete Data', function () {
        it('delete non-existing namespace', function () {
            dVsTPersistService.saveData(namespace_one, object1);

            expect(dVsTPersistService.deleteData(namespace_two)).toBe(false);
            expect(dVsTPersistService.loadData(namespace_one)).toEqual(object1);
        });
        it('delete non-existing namespace', function () {
            dVsTPersistService.saveData(namespace_one, object1);

            expect(dVsTPersistService.deleteData(namespace_one)).toBe(true);
            expect(dVsTPersistService.loadData(namespace_one)).toBeUndefined();
        });
    });

});
