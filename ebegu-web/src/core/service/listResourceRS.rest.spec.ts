import '../../bootstrap.ts';
import 'angular-mocks';
import ListResourceRS from './listResourceRS.rest';

describe('ListResourceRS', function () {

    let listResourceRS: ListResourceRS;

    beforeEach(angular.mock.module('ebeguWeb.core'));


    beforeEach(angular.mock.inject(function ($injector: any) {
        listResourceRS = $injector.get('ListResourceRS');

    }));

    describe('Public API', function () {
        it('should include a getLaenderList() function', function () {
            expect(listResourceRS.getLaenderList).toBeDefined();
        });


    });

    describe('API Usage', function () {

    });
});
