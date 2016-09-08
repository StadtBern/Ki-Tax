import '../../../bootstrap.ts';
import 'angular-mocks';
import {EbeguWebGesuch} from '../../gesuch.module';
import GesuchModelManager from '../../service/gesuchModelManager';
import {KinderListViewController} from './kinderListView';
import IInjectorService = angular.auto.IInjectorService;
import IHttpBackendService = angular.IHttpBackendService;

describe('kinderListView', function () {

    let gesuchModelManager: GesuchModelManager;
    let scope : angular.IScope;
    let kinderListViewController: KinderListViewController;

    beforeEach(angular.mock.module(EbeguWebGesuch.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        let wizardStepManager = $injector.get('WizardStepManager');
        spyOn(wizardStepManager, 'updateWizardStepStatus').and.returnValue({});
        gesuchModelManager = $injector.get('GesuchModelManager');
        spyOn(gesuchModelManager, 'initKinder').and.returnValue({});
        kinderListViewController = new KinderListViewController(null, gesuchModelManager, null, null, null, wizardStepManager);
        let $rootScope = $injector.get('$rootScope');
        scope = $rootScope.$new();
    }));

    beforeEach(function () {
        gesuchModelManager.initGesuch(false);
    });
});
