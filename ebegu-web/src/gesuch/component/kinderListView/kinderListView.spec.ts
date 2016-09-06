import '../../../bootstrap.ts';
import 'angular-mocks';
import {EbeguWebGesuch} from '../../gesuch.module';
import GesuchModelManager from '../../service/gesuchModelManager';
import {KinderListViewController} from './kinderListView';
import TSKindContainer from '../../../models/TSKindContainer';
import TSKind from '../../../models/TSKind';
import DateUtil from '../../../utils/DateUtil';
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
        kinderListViewController = new KinderListViewController(null, gesuchModelManager, null, null, null, null, wizardStepManager);
        let $rootScope = $injector.get('$rootScope');
        scope = $rootScope.$new();
    }));

    beforeEach(function () {
        gesuchModelManager.initGesuch(false);
    });

    describe('exist kinder with betreuung needed', function () {
        it('should return false for empty list', function() {
            spyOn(gesuchModelManager, 'getKinderList').and.returnValue([]);
            expect(kinderListViewController.isThereAnyKindWithBetreuungsbedarf()).toBe(false);
        });
        it('should return false for a list with no Kind needing Betreuung', function() {
            let kind: TSKindContainer = new TSKindContainer();
            kind.kindJA = new TSKind();
            kind.kindJA.familienErgaenzendeBetreuung = false;
            spyOn(gesuchModelManager, 'getKinderList').and.returnValue([kind]);
            expect(kinderListViewController.isThereAnyKindWithBetreuungsbedarf()).toBe(false);
        });
        it('should return true for a list with Kinder needing Betreuung', function() {
            let kind: TSKindContainer = new TSKindContainer();
            kind.kindJA = new TSKind();
            kind.kindJA.timestampErstellt = DateUtil.today();
            kind.kindJA.familienErgaenzendeBetreuung = true;
            spyOn(gesuchModelManager, 'getKinderList').and.returnValue([kind]);
            expect(kinderListViewController.isThereAnyKindWithBetreuungsbedarf()).toBe(true);
        });
    });
});
