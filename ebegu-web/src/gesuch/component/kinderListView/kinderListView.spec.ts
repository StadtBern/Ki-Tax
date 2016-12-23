import '../../../bootstrap.ts';
import 'angular-mocks';
import {EbeguWebGesuch} from '../../gesuch.module';
import GesuchModelManager from '../../service/gesuchModelManager';
import {KinderListViewController} from './kinderListView';
import IInjectorService = angular.auto.IInjectorService;
import IHttpBackendService = angular.IHttpBackendService;
import {TSEingangsart} from '../../../models/enums/TSEingangsart';

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
        let $rootScope = $injector.get('$rootScope');
        scope = $rootScope.$new();
        kinderListViewController = new KinderListViewController(null, gesuchModelManager,
            null, null, null, wizardStepManager, scope );
    }));

    beforeEach(function () {
        gesuchModelManager.initGesuch(false, TSEingangsart.PAPIER);
    });
});
