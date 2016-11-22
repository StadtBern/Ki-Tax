import {EbeguWebCore} from '../../core/core.module';
import GesuchModelManager from './gesuchModelManager';
import {IHttpBackendService, IScope, IQService} from 'angular';
import BetreuungRS from '../../core/service/betreuungRS.rest';
import {TSBetreuungsstatus} from '../../models/enums/TSBetreuungsstatus';
import FallRS from './fallRS.rest';
import GesuchRS from './gesuchRS.rest';
import DateUtil from '../../utils/DateUtil';
import KindRS from '../../core/service/kindRS.rest';
import TestDataUtil from '../../utils/TestDataUtil';
import TSKindContainer from '../../models/TSKindContainer';
import TSGesuch from '../../models/TSGesuch';
import TSUser from '../../models/TSUser';
import AuthServiceRS from '../../authentication/service/AuthServiceRS.rest';
import WizardStepManager from './wizardStepManager';
import TSBetreuung from '../../models/TSBetreuung';
import TSKind from '../../models/TSKind';
import {TSAntragStatus} from '../../models/enums/TSAntragStatus';
import TSVerfuegung from '../../models/TSVerfuegung';
import VerfuegungRS from '../../core/service/verfuegungRS.rest';
import AntragStatusHistoryRS from '../../core/service/antragStatusHistoryRS.rest';
import {TSWizardStepName} from '../../models/enums/TSWizardStepName';
import {TSWizardStepStatus} from '../../models/enums/TSWizardStepStatus';
import {TSAntragTyp} from '../../models/enums/TSAntragTyp';
import IPromise = angular.IPromise;

describe('gesuchModelManager', function () {

    let gesuchModelManager: GesuchModelManager;
    let betreuungRS: BetreuungRS;
    let fallRS: FallRS;
    let gesuchRS: GesuchRS;
    let kindRS: KindRS;
    let scope: IScope;
    let $httpBackend: IHttpBackendService;
    let $q: IQService;
    let authServiceRS: AuthServiceRS;
    let wizardStepManager: WizardStepManager;
    let verfuegungRS: VerfuegungRS;
    let antragStatusHistoryRS: AntragStatusHistoryRS;

    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        gesuchModelManager = $injector.get('GesuchModelManager');
        $httpBackend = $injector.get('$httpBackend');
        betreuungRS = $injector.get('BetreuungRS');
        fallRS = $injector.get('FallRS');
        gesuchRS = $injector.get('GesuchRS');
        kindRS = $injector.get('KindRS');
        scope = $injector.get('$rootScope');
        $q = $injector.get('$q');
        authServiceRS = $injector.get('AuthServiceRS');
        wizardStepManager = $injector.get('WizardStepManager');
        verfuegungRS = $injector.get('VerfuegungRS');
        antragStatusHistoryRS = $injector.get('AntragStatusHistoryRS');
    }));

    describe('Public API', function () {
        it('should include a createBetreuung() function', function () {
            expect(gesuchModelManager.createBetreuung).toBeDefined();
        });
    });

    describe('API Usage', function () {
        describe('createBetreuung', () => {
            it('should create a new empty Betreuung for the current KindContainer', () => {
                gesuchModelManager.initGesuch(false);
                createKindContainer();
                expect(gesuchModelManager.getKindToWorkWith().betreuungen).toBeDefined();
                expect(gesuchModelManager.getKindToWorkWith().betreuungen.length).toBe(0);
                gesuchModelManager.createBetreuung();
                expect(gesuchModelManager.getKindToWorkWith().betreuungen).toBeDefined();
                expect(gesuchModelManager.getKindToWorkWith().betreuungen.length).toBe(1);
                expect(gesuchModelManager.getBetreuungToWorkWith().betreuungspensumContainers).toEqual([]);
                expect(gesuchModelManager.getBetreuungToWorkWith().betreuungsstatus).toEqual(TSBetreuungsstatus.AUSSTEHEND);
                expect(gesuchModelManager.getBetreuungToWorkWith().institutionStammdaten).toBeUndefined();
            });
        });
        describe('removeBetreuungFromKind', () => {
            it('should remove the current Betreuung from the list of the current Kind', () => {
                gesuchModelManager.initGesuch(false);
                createKindContainer();
                gesuchModelManager.createBetreuung();
                expect(gesuchModelManager.getKindToWorkWith().betreuungen).toBeDefined();
                expect(gesuchModelManager.getKindToWorkWith().betreuungen.length).toBe(1);
                gesuchModelManager.removeBetreuungFromKind();
                expect(gesuchModelManager.getKindToWorkWith().betreuungen).toBeDefined();
                expect(gesuchModelManager.getKindToWorkWith().betreuungen.length).toBe(0);
            });
        });
        describe('updateBetreuung', () => {
            it('creates a new betreuung', () => {
                gesuchModelManager.initGesuch(false);
                createKindContainer();
                gesuchModelManager.createBetreuung();
                gesuchModelManager.getKindToWorkWith().id = '2afc9d9a-957e-4550-9a22-97624a000feb';

                TestDataUtil.mockDefaultGesuchModelManagerHttpCalls($httpBackend);
                let kindToWorkWith: TSKindContainer = gesuchModelManager.getKindToWorkWith();
                kindToWorkWith.nextNumberBetreuung = 5;
                spyOn(kindRS, 'findKind').and.returnValue($q.when(kindToWorkWith));
                spyOn(betreuungRS, 'saveBetreuung').and.returnValue($q.when(gesuchModelManager.getBetreuungToWorkWith()));
                spyOn(wizardStepManager, 'findStepsFromGesuch').and.returnValue($q.when({}));

                gesuchModelManager.updateBetreuung(false);
                scope.$apply();

                expect(betreuungRS.saveBetreuung).toHaveBeenCalledWith(gesuchModelManager.getBetreuungToWorkWith(), '2afc9d9a-957e-4550-9a22-97624a000feb', undefined, false);
                expect(kindRS.findKind).toHaveBeenCalledWith('2afc9d9a-957e-4550-9a22-97624a000feb');
                expect(gesuchModelManager.getKindToWorkWith().nextNumberBetreuung).toEqual(5);
            });
        });
        describe('saveGesuchAndFall', () => {
            it('creates a Fall with a linked Gesuch', () => {
                spyOn(fallRS, 'createFall').and.returnValue($q.when({}));
                let gesuch: TSGesuch = new TSGesuch();
                gesuch.id = '123123';
                spyOn(gesuchRS, 'createGesuch').and.returnValue($q.when({data: gesuch}));
                spyOn(wizardStepManager, 'findStepsFromGesuch').and.returnValue($q.when({}));
                TestDataUtil.mockDefaultGesuchModelManagerHttpCalls($httpBackend);

                gesuchModelManager.initGesuch(false);
                gesuchModelManager.saveGesuchAndFall();

                scope.$apply();
                expect(fallRS.createFall).toHaveBeenCalled();
                expect(gesuchRS.createGesuch).toHaveBeenCalled();
            });
            it('only updates the Gesuch because it already exists', () => {
                spyOn(gesuchRS, 'updateGesuch').and.returnValue($q.when({}));
                TestDataUtil.mockDefaultGesuchModelManagerHttpCalls($httpBackend);

                gesuchModelManager.initGesuch(false);
                gesuchModelManager.getGesuch().timestampErstellt = DateUtil.today();
                gesuchModelManager.saveGesuchAndFall();

                scope.$apply();
                expect(gesuchRS.updateGesuch).toHaveBeenCalled();
            });
        });
        describe('initGesuch', () => {
            beforeEach(() => {
                expect(gesuchModelManager.getGesuch()).toBeUndefined();
            });
            it('links the fall with the undefined user', () => {
                spyOn(authServiceRS, 'getPrincipal').and.returnValue(undefined);

                gesuchModelManager.initGesuch(false);

                expect(gesuchModelManager.getGesuch()).toBeDefined();
                expect(gesuchModelManager.getGesuch().fall).toBeDefined();
                expect(gesuchModelManager.getGesuch().fall.verantwortlicher).toBe(undefined);
            });
            it('links the fall with the current user', () => {
                let currentUser: TSUser = new TSUser('Test', 'User', 'username');
                spyOn(authServiceRS, 'getPrincipal').and.returnValue(currentUser);

                gesuchModelManager.initGesuch(false);

                expect(gesuchModelManager.getGesuch()).toBeDefined();
                expect(gesuchModelManager.getGesuch().fall).toBeDefined();
                expect(gesuchModelManager.getGesuch().fall.verantwortlicher).toBe(currentUser);
            });
            it('does not force to create a new fall and gesuch', () => {
                gesuchModelManager.initGesuch(false);
                expect(gesuchModelManager.getGesuch()).toBeDefined();
            });
            it('does force to create a new fall and gesuch', () => {
                gesuchModelManager.initGesuch(true);
                expect(gesuchModelManager.getGesuch()).toBeDefined();
            });
            it('forces to create a new gesuch and fall even though one already exists', () => {
                gesuchModelManager.initGesuch(false);
                let oldGesuch: TSGesuch = gesuchModelManager.getGesuch();
                expect(gesuchModelManager.getGesuch()).toBeDefined();

                gesuchModelManager.initGesuch(true);
                expect(gesuchModelManager.getGesuch()).toBeDefined();
                expect(oldGesuch).not.toBe(gesuchModelManager.getGesuch());
            });
            it('does not force to create a new gesuch and fall and the old ones will remain', () => {
                gesuchModelManager.initGesuch(false);
                let oldGesuch: TSGesuch = gesuchModelManager.getGesuch();
                expect(gesuchModelManager.getGesuch()).toBeDefined();

                gesuchModelManager.initGesuch(false);
                expect(gesuchModelManager.getGesuch()).toBeDefined();
                expect(oldGesuch).toBe(gesuchModelManager.getGesuch());
            });
        });
        describe('setUserAsFallVerantwortlicher', () => {
            it('puts the given user as the verantwortlicher for the fall', () => {
                gesuchModelManager.initGesuch(false);
                spyOn(authServiceRS, 'getPrincipal').and.returnValue(undefined);
                let user: TSUser = new TSUser('Emiliano', 'Camacho');
                gesuchModelManager.setUserAsFallVerantwortlicher(user);
                expect(gesuchModelManager.getGesuch().fall.verantwortlicher).toBe(user);
            });
        });
        describe('exist at least one Betreuung among all kinder', function () {
            it('should return false for empty list', function() {
                spyOn(gesuchModelManager, 'getKinderWithBetreuungList').and.returnValue([]);
                expect(gesuchModelManager.isThereAnyBetreuung()).toBe(false);
            });
            it('should return false for a list with Kinder but no Betreuung', function() {
                let kind: TSKindContainer = new TSKindContainer();
                kind.kindJA = new TSKind();
                kind.kindJA.familienErgaenzendeBetreuung = false;
                spyOn(gesuchModelManager, 'getKinderWithBetreuungList').and.returnValue([kind]);
                expect(gesuchModelManager.isThereAnyBetreuung()).toBe(false);
            });
            it('should return true for a list with Kinder needing Betreuung', function() {
                let kind: TSKindContainer = new TSKindContainer();
                kind.kindJA = new TSKind();
                kind.kindJA.familienErgaenzendeBetreuung = true;
                let betreuung: TSBetreuung = new TSBetreuung();
                kind.betreuungen = [betreuung];
                spyOn(gesuchModelManager, 'getKinderWithBetreuungList').and.returnValue([kind]);
                expect(gesuchModelManager.isThereAnyBetreuung()).toBe(true);
            });
        });

        describe('exist kinder with betreuung needed', function () {
            it('should return false for empty list', function() {
                spyOn(gesuchModelManager, 'getKinderList').and.returnValue([]);
                expect(gesuchModelManager.isThereAnyKindWithBetreuungsbedarf()).toBe(false);
            });
            it('should return false for a list with no Kind needing Betreuung', function() {
                let kind: TSKindContainer = new TSKindContainer();
                kind.kindJA = new TSKind();
                kind.kindJA.familienErgaenzendeBetreuung = false;
                spyOn(gesuchModelManager, 'getKinderList').and.returnValue([kind]);
                expect(gesuchModelManager.isThereAnyKindWithBetreuungsbedarf()).toBe(false);
            });
            it('should return true for a list with Kinder needing Betreuung', function() {
                let kind: TSKindContainer = new TSKindContainer();
                kind.kindJA = new TSKind();
                kind.kindJA.timestampErstellt = DateUtil.today();
                kind.kindJA.familienErgaenzendeBetreuung = true;
                spyOn(gesuchModelManager, 'getKinderList').and.returnValue([kind]);
                expect(gesuchModelManager.isThereAnyKindWithBetreuungsbedarf()).toBe(true);
            });
        });
        describe('saveGesuchStatus', function () {
            it('should update the status of the Gesuch im Server und Client', function() {
                TestDataUtil.mockDefaultGesuchModelManagerHttpCalls($httpBackend);
                gesuchModelManager.initGesuch(false);
                spyOn(gesuchRS, 'updateGesuchStatus').and.returnValue($q.when({}));
                spyOn(antragStatusHistoryRS, 'loadLastStatusChange').and.returnValue($q.when({}));

                gesuchModelManager.saveGesuchStatus(TSAntragStatus.ERSTE_MAHNUNG);

                scope.$apply();
                expect(gesuchModelManager.getGesuch().status).toEqual(TSAntragStatus.ERSTE_MAHNUNG);
            });
        });
        describe('saveVerfuegung', function () {
            it('should save the current Verfuegung und set the status of the Betreuung to VERFUEGT', function() {
                TestDataUtil.mockDefaultGesuchModelManagerHttpCalls($httpBackend);
                gesuchModelManager.initGesuch(false);
                createKindContainer();
                gesuchModelManager.createBetreuung();
                gesuchModelManager.getBetreuungToWorkWith().id = '2afc9d9a-957e-4550-9a22-97624a000feb';
                let verfuegung: TSVerfuegung = new TSVerfuegung();
                spyOn(verfuegungRS, 'saveVerfuegung').and.returnValue($q.when(verfuegung));

                gesuchModelManager.saveVerfuegung();
                scope.$apply();

                expect(gesuchModelManager.getVerfuegenToWorkWith()).toBe(verfuegung);
                expect(gesuchModelManager.getBetreuungToWorkWith().betreuungsstatus).toEqual(TSBetreuungsstatus.VERFUEGT);
            });
        });
        describe('calculateNewStatus', function () {
            it('should be GEPRUEFT if there is no betreuung', function() {
                spyOn(wizardStepManager, 'hasStepGivenStatus').and.callFake(function(stepName: TSWizardStepName, status: TSWizardStepStatus) {
                    return stepName === TSWizardStepName.BETREUUNG && status === TSWizardStepStatus.NOK;
                });
                spyOn(gesuchModelManager, 'isThereAnyBetreuung').and.returnValue(false);
                expect(gesuchModelManager.calculateNewStatus(TSAntragStatus.GEPRUEFT)).toEqual(TSAntragStatus.GEPRUEFT);
                expect(gesuchModelManager.calculateNewStatus(TSAntragStatus.PLATZBESTAETIGUNG_WARTEN)).toEqual(TSAntragStatus.GEPRUEFT);
                expect(gesuchModelManager.calculateNewStatus(TSAntragStatus.PLATZBESTAETIGUNG_ABGEWIESEN)).toEqual(TSAntragStatus.GEPRUEFT);
            });
            it('should be PLATZBESTAETIGUNG_ABGEWIESEN if there are betreuungen and status of Betreuung is NOK', function() {
                spyOn(wizardStepManager, 'hasStepGivenStatus').and.callFake(function(stepName: TSWizardStepName, status: TSWizardStepStatus) {
                    return stepName === TSWizardStepName.BETREUUNG && status === TSWizardStepStatus.NOK;
                });
                spyOn(gesuchModelManager, 'isThereAnyBetreuung').and.returnValue(true);
                expect(gesuchModelManager.calculateNewStatus(TSAntragStatus.GEPRUEFT)).toEqual(TSAntragStatus.PLATZBESTAETIGUNG_ABGEWIESEN);
                expect(gesuchModelManager.calculateNewStatus(TSAntragStatus.PLATZBESTAETIGUNG_WARTEN)).toEqual(TSAntragStatus.PLATZBESTAETIGUNG_ABGEWIESEN);
                expect(gesuchModelManager.calculateNewStatus(TSAntragStatus.PLATZBESTAETIGUNG_ABGEWIESEN)).toEqual(TSAntragStatus.PLATZBESTAETIGUNG_ABGEWIESEN);
            });
            it('should be PLATZBESTAETIGUNG_WARTEN if the status of Betreuung is PLATZBESTAETIGUNG', function() {
                spyOn(wizardStepManager, 'hasStepGivenStatus').and.callFake(function(stepName: TSWizardStepName, status: TSWizardStepStatus) {
                    return stepName === TSWizardStepName.BETREUUNG && status === TSWizardStepStatus.PLATZBESTAETIGUNG;
                });
                expect(gesuchModelManager.calculateNewStatus(TSAntragStatus.GEPRUEFT)).toEqual(TSAntragStatus.PLATZBESTAETIGUNG_WARTEN);
                expect(gesuchModelManager.calculateNewStatus(TSAntragStatus.PLATZBESTAETIGUNG_WARTEN)).toEqual(TSAntragStatus.PLATZBESTAETIGUNG_WARTEN);
                expect(gesuchModelManager.calculateNewStatus(TSAntragStatus.PLATZBESTAETIGUNG_ABGEWIESEN)).toEqual(TSAntragStatus.PLATZBESTAETIGUNG_WARTEN);
            });
            it('should be GEPRUEFT if the status of Betreuung is PLATZBESTAETIGUNG', function() {
                spyOn(wizardStepManager, 'hasStepGivenStatus').and.callFake(function(stepName: TSWizardStepName, status: TSWizardStepStatus) {
                    return stepName === TSWizardStepName.BETREUUNG && status === TSWizardStepStatus.OK;
                });
                expect(gesuchModelManager.calculateNewStatus(TSAntragStatus.GEPRUEFT)).toEqual(TSAntragStatus.GEPRUEFT);
                expect(gesuchModelManager.calculateNewStatus(TSAntragStatus.PLATZBESTAETIGUNG_WARTEN)).toEqual(TSAntragStatus.GEPRUEFT);
                expect(gesuchModelManager.calculateNewStatus(TSAntragStatus.PLATZBESTAETIGUNG_ABGEWIESEN)).toEqual(TSAntragStatus.GEPRUEFT);
            });
            it('returns the same TSAntragStatus for all others', function() {
                expect(gesuchModelManager.calculateNewStatus(TSAntragStatus.ERSTE_MAHNUNG)).toEqual(TSAntragStatus.ERSTE_MAHNUNG);
                expect(gesuchModelManager.calculateNewStatus(TSAntragStatus.ERSTE_MAHNUNG_ABGELAUFEN)).toEqual(TSAntragStatus.ERSTE_MAHNUNG_ABGELAUFEN);
                expect(gesuchModelManager.calculateNewStatus(TSAntragStatus.FREIGEGEBEN)).toEqual(TSAntragStatus.FREIGEGEBEN);
                expect(gesuchModelManager.calculateNewStatus(TSAntragStatus.IN_BEARBEITUNG_GS)).toEqual(TSAntragStatus.IN_BEARBEITUNG_GS);
                expect(gesuchModelManager.calculateNewStatus(TSAntragStatus.IN_BEARBEITUNG_JA)).toEqual(TSAntragStatus.IN_BEARBEITUNG_JA);
                expect(gesuchModelManager.calculateNewStatus(TSAntragStatus.FREIGABEQUITTUNG)).toEqual(TSAntragStatus.FREIGABEQUITTUNG);
                expect(gesuchModelManager.calculateNewStatus(TSAntragStatus.NUR_SCHULAMT)).toEqual(TSAntragStatus.NUR_SCHULAMT);
                expect(gesuchModelManager.calculateNewStatus(TSAntragStatus.VERFUEGEN)).toEqual(TSAntragStatus.VERFUEGEN);
                expect(gesuchModelManager.calculateNewStatus(TSAntragStatus.VERFUEGT)).toEqual(TSAntragStatus.VERFUEGT);
                expect(gesuchModelManager.calculateNewStatus(TSAntragStatus.ZURUECKGEWIESEN)).toEqual(TSAntragStatus.ZURUECKGEWIESEN);
                expect(gesuchModelManager.calculateNewStatus(TSAntragStatus.ZWEITE_MAHNUNG)).toEqual(TSAntragStatus.ZWEITE_MAHNUNG);
                expect(gesuchModelManager.calculateNewStatus(TSAntragStatus.ZWEITE_MAHNUNG_ABGELAUFEN)).toEqual(TSAntragStatus.ZWEITE_MAHNUNG_ABGELAUFEN);
            });
        });
        describe('hideSteps', function () {
            it('should hide the steps ABWESENHEIT and UMZUG for Erstgesuch without umzug', function() {
                TestDataUtil.mockDefaultGesuchModelManagerHttpCalls($httpBackend);
                spyOn(wizardStepManager, 'hideStep').and.returnValue(undefined);
                spyOn(wizardStepManager, 'unhideStep').and.returnValue(undefined);
                gesuchModelManager.initGesuch(true);

                expect(wizardStepManager.hideStep).toHaveBeenCalledWith(TSWizardStepName.UMZUG);
                expect(wizardStepManager.hideStep).toHaveBeenCalledWith(TSWizardStepName.ABWESENHEIT);
                expect(wizardStepManager.unhideStep).not.toHaveBeenCalled();
            });
            it('should unhide the steps ABWESENHEIT and UMZUG for Mutation', function() {
                TestDataUtil.mockDefaultGesuchModelManagerHttpCalls($httpBackend);
                spyOn(wizardStepManager, 'hideStep').and.returnValue(undefined);
                spyOn(wizardStepManager, 'unhideStep').and.returnValue(undefined);
                let gesuch: TSGesuch = new TSGesuch();
                gesuch.typ = TSAntragTyp.MUTATION;
                gesuchModelManager.setGesuch(gesuch);

                expect(wizardStepManager.hideStep).not.toHaveBeenCalled();
                expect(wizardStepManager.unhideStep).toHaveBeenCalledWith(TSWizardStepName.UMZUG);
                expect(wizardStepManager.unhideStep).toHaveBeenCalledWith(TSWizardStepName.ABWESENHEIT);
            });
            it('should unhide the step UMZUG for Erstgesuch with umzug and hide ABWESENHEIT', function() {
                TestDataUtil.mockDefaultGesuchModelManagerHttpCalls($httpBackend);
                spyOn(wizardStepManager, 'hideStep').and.returnValue(undefined);
                spyOn(wizardStepManager, 'unhideStep').and.returnValue(undefined);
                let gesuch: TSGesuch = new TSGesuch();
                gesuch.typ = TSAntragTyp.GESUCH;
                gesuch.gesuchsteller1 = TestDataUtil.createGesuchsteller('Julio', 'Iglesias');
                gesuch.gesuchsteller1.addAdresse(TestDataUtil.createAdresse('wohnstrasse', '1'));
                gesuch.gesuchsteller1.addAdresse(TestDataUtil.createAdresse('umzug', '2'));
                gesuchModelManager.setGesuch(gesuch);

                expect(wizardStepManager.hideStep).not.toHaveBeenCalledWith(TSWizardStepName.UMZUG);
                expect(wizardStepManager.hideStep).toHaveBeenCalledWith(TSWizardStepName.ABWESENHEIT);
                expect(wizardStepManager.unhideStep).toHaveBeenCalledWith(TSWizardStepName.UMZUG);
                expect(wizardStepManager.unhideStep).not.toHaveBeenCalledWith(TSWizardStepName.ABWESENHEIT);
            });
        });
        describe('updateBetreuungen', function () {
            it('should return empty Promise for undefined betreuung list', function() {
                TestDataUtil.mockDefaultGesuchModelManagerHttpCalls($httpBackend);
                let promise: IPromise<Array<TSBetreuung>> = gesuchModelManager.updateBetreuungen(undefined, true);
                expect(promise).toBeDefined();
                let promiseExecuted: boolean = false;
                promise.then(() => {
                    promiseExecuted = true;
                });
                $httpBackend.flush();
                expect(promiseExecuted).toBe(true);
            });
            it('should return empty Promise for empty betreuung list', function() {
                TestDataUtil.mockDefaultGesuchModelManagerHttpCalls($httpBackend);
                let promise: IPromise<Array<TSBetreuung>> = gesuchModelManager.updateBetreuungen([], true);
                expect(promise).toBeDefined();
                let promiseExecuted: boolean = false;
                promise.then(() => {
                    promiseExecuted = true;
                });
                $httpBackend.flush();
                expect(promiseExecuted).toBe(true);
            });
            it('should return a Promise with the Betreuung that was updated', function() {
                let myGesuch = new TSGesuch();
                myGesuch.id = 'gesuchID';
                TestDataUtil.setAbstractFieldsUndefined(myGesuch);
                let betreuung: TSBetreuung = new TSBetreuung();
                betreuung.id = 'betreuungId';
                let betreuungen: Array<TSBetreuung> = [betreuung];
                let kindContainer: TSKindContainer = new TSKindContainer(undefined, undefined, betreuungen);
                kindContainer.id = 'kindID';
                myGesuch.kindContainers = [kindContainer];

                spyOn(betreuungRS, 'saveBetreuungen').and.returnValue($q.when([betreuung]));
                spyOn(wizardStepManager, 'findStepsFromGesuch').and.returnValue($q.when(undefined));
                spyOn(gesuchModelManager, 'setHiddenSteps').and.returnValue(undefined);
                gesuchModelManager.setGesuch(myGesuch);

                TestDataUtil.mockDefaultGesuchModelManagerHttpCalls($httpBackend);

                let promise: IPromise<Array<TSBetreuung>> = gesuchModelManager.updateBetreuungen(betreuungen, true);

                expect(promise).toBeDefined();
                let promiseExecuted: Array<TSBetreuung> = undefined;
                promise.then((response) => {
                    promiseExecuted = response;
                });

                $httpBackend.flush();

                expect(betreuungRS.saveBetreuungen).toHaveBeenCalledWith(betreuungen, myGesuch.id, true);
                expect(promiseExecuted.length).toBe(1);
                expect(promiseExecuted[0]).toEqual(betreuung);
            });
        });
    });


    // HELP METHODS

    function createKindContainer() {
        gesuchModelManager.initKinder();
        gesuchModelManager.createKind();
        gesuchModelManager.initBetreuung();
    }

});
