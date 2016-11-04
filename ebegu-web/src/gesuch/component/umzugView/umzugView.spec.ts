import '../../../bootstrap.ts';
import 'angular-mocks';
import {UmzugViewController} from './umzugView';
import {TSBetroffene} from '../../../models/enums/TSBetroffene';
import GesuchModelManager from '../../service/gesuchModelManager';
import TSGesuch from '../../../models/TSGesuch';
import {EbeguWebCore} from '../../../core/core.module';
import WizardStepManager from '../../service/wizardStepManager';
import TestDataUtil from '../../../utils/TestDataUtil';
import TSAdresse from '../../../models/TSAdresse';
import ErrorService from '../../../core/errors/service/ErrorService';
import BerechnungsManager from '../../service/berechnungsManager';
import {TSAdressetyp} from '../../../models/enums/TSAdressetyp';
import IInjectorService = angular.auto.IInjectorService;
import IHttpBackendService = angular.IHttpBackendService;
import ITranslateService = angular.translate.ITranslateService;

describe('umzugView', function () {

    let umzugController: UmzugViewController;
    let gesuchModelManager: GesuchModelManager;
    let wizardStepManager: WizardStepManager;
    let berechnungsManager: BerechnungsManager;
    let errorService: ErrorService;
    let $translate: ITranslateService;

    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        gesuchModelManager = $injector.get('GesuchModelManager');
        wizardStepManager = $injector.get('WizardStepManager');
        spyOn(wizardStepManager, 'updateWizardStepStatus').and.returnValue({});
        berechnungsManager = $injector.get('BerechnungsManager');
        errorService = $injector.get('ErrorService');
        $translate = $injector.get('$translate');
    }));

    describe('getNameFromBetroffene', function () {
        beforeEach(function () {
            umzugController = new UmzugViewController(gesuchModelManager, berechnungsManager,
                wizardStepManager, errorService, $translate);
        });
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
        beforeEach(function () {
            umzugController = new UmzugViewController(gesuchModelManager, berechnungsManager,
                wizardStepManager, errorService, $translate);
        });
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

    describe('getAdressenListFromGS', function () {
        it('should have an empty AdressenList for gesuch=null', function () {
            spyOn(gesuchModelManager, 'getGesuch').and.returnValue(undefined);

            umzugController = new UmzugViewController(gesuchModelManager, berechnungsManager,
                wizardStepManager, errorService, $translate);

            expect(umzugController.getUmzugAdressenList().length).toBe(0);
        });
        it('should have all adresse for GS1 and GS2', function () {
            let gesuch: TSGesuch = new TSGesuch();
            gesuch.gesuchsteller1 = TestDataUtil.createGesuchsteller('Rodolfo', 'Langostino');
            gesuch.gesuchsteller1.addAdresse(TestDataUtil.createAdresse('strasse1', '10'));
            let umzugAdresseGS1: TSAdresse = TestDataUtil.createAdresse('umzugstrasse1', '10');
            gesuch.gesuchsteller1.addAdresse(umzugAdresseGS1);

            gesuch.gesuchsteller2 = TestDataUtil.createGesuchsteller('Conchita', 'Prieto');
            gesuch.gesuchsteller2.addAdresse(TestDataUtil.createAdresse('strasse2', '20'));
            let umzugAdresseGS2: TSAdresse = TestDataUtil.createAdresse('umzugstrasse2', '20');
            gesuch.gesuchsteller2.addAdresse(umzugAdresseGS2);

            spyOn(gesuchModelManager, 'getGesuch').and.returnValue(gesuch);

            umzugController = new UmzugViewController(gesuchModelManager, berechnungsManager,
                wizardStepManager, errorService, $translate);

            expect(umzugController.getUmzugAdressenList().length).toBe(2);
            expect(umzugController.getUmzugAdressenList()[0].betroffene).toBe(TSBetroffene.GESUCHSTELLER_1);
            expect(umzugController.getUmzugAdressenList()[0].adresse).toEqual(umzugAdresseGS1);
            expect(umzugController.getUmzugAdressenList()[1].betroffene).toBe(TSBetroffene.GESUCHSTELLER_2);
            expect(umzugController.getUmzugAdressenList()[1].adresse).toEqual(umzugAdresseGS2);
        });
        // it('should merge the adresse of GS1 and GS2 in a single one with BEIDE_GESUCHSTELLER', function () {
        //     let gesuch: TSGesuch = new TSGesuch();
        //     gesuch.gesuchsteller1 = TestDataUtil.createGesuchsteller('Rodolfo', 'Langostino');
        //     let adresse2: TSAdresse = TestDataUtil.createAdresse('strasse1', '10');
        //     gesuch.gesuchsteller1.addAdresse(adresse2);
        //
        //     gesuch.gesuchsteller2 = TestDataUtil.createGesuchsteller('Conchita', 'Prieto');
        //     gesuch.gesuchsteller2.addAdresse(adresse2);
        //     spyOn(gesuchModelManager, 'getGesuch').and.returnValue(gesuch);
        //
        //     umzugController = new UmzugViewController(gesuchModelManager, berechnungsManager,
        //         wizardStepManager, errorService, $translate);
        //
        //     expect(umzugController.getUmzugAdressenList().length).toBe(1);
        //     expect(umzugController.getUmzugAdressenList()[0].betroffene).toBe(TSBetroffene.BEIDE_GESUCHSTELLER);
        //     expect(umzugController.getUmzugAdressenList()[0].adresse).toBe(adresse2);
        // });
    });

    describe('createAndRemoveUmzugAdresse', function () {
        it('should have all adresse for GS1 and GS2', function () {
            let gesuch: TSGesuch = new TSGesuch();
            gesuch.gesuchsteller1 = TestDataUtil.createGesuchsteller('Rodolfo', 'Langostino');
            gesuch.gesuchsteller1.addAdresse(TestDataUtil.createAdresse('strasse1', '10'));
            let umzugAdresseGS1: TSAdresse = TestDataUtil.createAdresse('umzugstrasse1', '10');
            gesuch.gesuchsteller1.addAdresse(umzugAdresseGS1);
            gesuch.gesuchsteller2 = TestDataUtil.createGesuchsteller('Conchita', 'Prieto');
            spyOn(gesuchModelManager, 'getGesuch').and.returnValue(gesuch);

            umzugController = new UmzugViewController(gesuchModelManager, berechnungsManager,
                wizardStepManager, errorService, $translate);

            expect(umzugController.getUmzugAdressenList().length).toBe(1);
            expect(umzugController.getUmzugAdressenList()[0].betroffene).toBe(TSBetroffene.GESUCHSTELLER_1);
            expect(umzugController.getUmzugAdressenList()[0].adresse).toEqual(umzugAdresseGS1);

            umzugController.createUmzugAdresse();

            expect(umzugController.getUmzugAdressenList().length).toBe(2);
            expect(umzugController.getUmzugAdressenList()[1].betroffene).toBeUndefined();
            expect(umzugController.getUmzugAdressenList()[1].adresse.adresseTyp).toBe(TSAdressetyp.WOHNADRESSE);
            expect(umzugController.getUmzugAdressenList()[1].adresse.showDatumVon).toBe(true);

            umzugController.removeUmzugAdresse(umzugController.getUmzugAdressenList()[0]);

            expect(umzugController.getUmzugAdressenList().length).toBe(1);
            expect(umzugController.getUmzugAdressenList()[0].betroffene).toBeUndefined();
            expect(umzugController.getUmzugAdressenList()[0].adresse.adresseTyp).toBe(TSAdressetyp.WOHNADRESSE);
            expect(umzugController.getUmzugAdressenList()[0].adresse.showDatumVon).toBe(true);
        });
    });
});
