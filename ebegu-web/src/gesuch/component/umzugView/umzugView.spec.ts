import '../../../bootstrap.ts';
import 'angular-mocks';
import {UmzugViewController} from './umzugView';
import {TSBetroffene} from '../../../models/enums/TSBetroffene';
import GesuchModelManager from '../../service/gesuchModelManager';
import TSGesuch from '../../../models/TSGesuch';
import TestDataUtil from '../../../utils/TestDataUtil';
import {EbeguWebCore} from '../../../core/core.module';
import WizardStepManager from '../../service/wizardStepManager';
import IInjectorService = angular.auto.IInjectorService;
import IHttpBackendService = angular.IHttpBackendService;

describe('umzugView', function () {

    let umzugController: UmzugViewController;
    let gesuchModelManager: GesuchModelManager;
    let wizardStepManager: WizardStepManager;

    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        gesuchModelManager = $injector.get('GesuchModelManager');
        wizardStepManager = $injector.get('WizardStepManager');
        spyOn(wizardStepManager, 'updateWizardStepStatus').and.returnValue({});
        umzugController = new UmzugViewController(gesuchModelManager, $injector.get('BerechnungsManager'),
            wizardStepManager, $injector.get('ErrorService'), $injector.get('$translate'));
    }));

    describe('getNameFromBetroffene', function () {
        it('should return the names of the GS or beide Gesuchsteller', function () {
            let gesuch: TSGesuch = new TSGesuch();
            gesuch.gesuchsteller1 = TestDataUtil.createGesuchsteller('Rodolfo', 'Langostino');
            gesuch.gesuchsteller2 = TestDataUtil.createGesuchsteller('Ana', 'Karenina');
            spyOn(gesuchModelManager, 'getGesuch').and.returnValue(gesuch);

            expect(umzugController.getNameFromBetroffene(TSBetroffene.GESUCHSTELLER_1)).toEqual(gesuch.gesuchsteller1.getFullName());
            expect(umzugController.getNameFromBetroffene(TSBetroffene.GESUCHSTELLER_2)).toEqual(gesuch.gesuchsteller2.getFullName());
            expect(umzugController.getNameFromBetroffene(TSBetroffene.BEIDE_GESUCHSTELLER)).toEqual('beide Gesuchsteller');
        });
        it('should return empty string for empty data', function () {
            let gesuch: TSGesuch = new TSGesuch();
            spyOn(gesuchModelManager, 'getGesuch').and.returnValue(gesuch);

            expect(umzugController.getNameFromBetroffene(TSBetroffene.GESUCHSTELLER_1)).toEqual('');
            expect(umzugController.getNameFromBetroffene(TSBetroffene.GESUCHSTELLER_2)).toEqual('');
            expect(umzugController.getNameFromBetroffene(TSBetroffene.BEIDE_GESUCHSTELLER)).toEqual('beide Gesuchsteller');
        });
    });

    describe('getBetroffenenList', function () {
        it('should return a list with only GS1', function () {
            let gesuch: TSGesuch = new TSGesuch();
            gesuch.gesuchsteller1 = TestDataUtil.createGesuchsteller('Rodolfo', 'Langostino');
            spyOn(gesuchModelManager, 'getGesuch').and.returnValue(gesuch);

            let betroffenenList: Array<TSBetroffene> = umzugController.getBetroffenenList();
            expect(betroffenenList.length).toBe(1);
            expect(betroffenenList[0]).toBe(TSBetroffene.GESUCHSTELLER_1);
        });
        it('should return a list with GS1, GS2 und BEIDE', function () {
            let gesuch: TSGesuch = new TSGesuch();
            gesuch.gesuchsteller1 = TestDataUtil.createGesuchsteller('Rodolfo', 'Langostino');
            gesuch.gesuchsteller2 = TestDataUtil.createGesuchsteller('Ana', 'Karenina');
            spyOn(gesuchModelManager, 'getGesuch').and.returnValue(gesuch);

            let betroffenenList: Array<TSBetroffene> = umzugController.getBetroffenenList();
            expect(betroffenenList.length).toBe(3);
            expect(betroffenenList[0]).toBe(TSBetroffene.GESUCHSTELLER_1);
            expect(betroffenenList[1]).toBe(TSBetroffene.GESUCHSTELLER_2);
            expect(betroffenenList[2]).toBe(TSBetroffene.BEIDE_GESUCHSTELLER);
        });
    });
});
