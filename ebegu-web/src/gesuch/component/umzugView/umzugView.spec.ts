import '../../../bootstrap.ts';
import 'angular-mocks';
import {EbeguWebGesuch} from '../../gesuch.module';
import IInjectorService = angular.auto.IInjectorService;
import IHttpBackendService = angular.IHttpBackendService;
import {UmzugViewController} from './umzugView';

describe('umzugView', function () {

    let umzugController: UmzugViewController;

    beforeEach(angular.mock.module(EbeguWebGesuch.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        umzugController = new  UmzugViewController($injector.get('GesuchModelManager'), $injector.get('BerechnungsManager'),
            $injector.get('WizardStepManager'), $injector.get('ErrorService'));
    }));

    describe('', function () {
        it('', function () {
        });
    });
});
