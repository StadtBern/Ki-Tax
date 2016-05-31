import {EbeguWebCore} from '../../../core/core.module';
import {FallCreationViewController} from './fallCreationView';

describe('fallCreationView', function () {

    let fallCreationview: FallCreationViewController;

    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        fallCreationview = new FallCreationViewController($injector.get('$state'), $injector.get('GesuchModelManager'), $injector.get('BerechnungsManager'));
    }));

    describe('API Usage', function () {
        it('', function () {
            
        });
    });
});
