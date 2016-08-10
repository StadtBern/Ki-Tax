import {EbeguWebCore} from '../../../core/core.module';
import {IStateService} from 'angular-ui-router';
import {BetreuungViewController} from './betreuungView';
import GesuchModelManager from '../../service/gesuchModelManager';
import TSBetreuung from '../../../models/TSBetreuung';
import DateUtil from '../../../utils/DateUtil';
import TSInstitutionStammdaten from '../../../models/TSInstitutionStammdaten';
import {TSBetreuungsangebotTyp} from '../../../models/enums/TSBetreuungsangebotTyp';
import {IHttpBackendService, IQService, IScope} from 'angular';
import {TSBetreuungsstatus} from '../../../models/enums/TSBetreuungsstatus';
import TestDataUtil from '../../../utils/TestDataUtil';
import EbeguUtil from '../../../utils/EbeguUtil';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';

describe('betreuungView', function () {

    let betreuungView: BetreuungViewController;
    let gesuchModelManager: GesuchModelManager;
    let $state: IStateService;
    let ebeguUtil: EbeguUtil;
    let $q: IQService;
    let betreuung: TSBetreuung;
    let $rootScope: IScope;
    let $httpBackend: IHttpBackendService;
    let authServiceRS: AuthServiceRS;


    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        gesuchModelManager = $injector.get('GesuchModelManager');
        $state = $injector.get('$state');
        ebeguUtil = $injector.get('EbeguUtil');
        $httpBackend = $injector.get('$httpBackend');
        $q = $injector.get('$q');
        betreuung = new TSBetreuung();
        betreuung.timestampErstellt = DateUtil.today();
        spyOn(gesuchModelManager, 'getBetreuungToWorkWith').and.returnValue(betreuung);
        $rootScope = $injector.get('$rootScope');
        authServiceRS = $injector.get('AuthServiceRS');
        spyOn(authServiceRS, 'isRole').and.returnValue(true);
        betreuungView = new BetreuungViewController($state, gesuchModelManager, ebeguUtil, $injector.get('CONSTANTS'),
            $rootScope.$new(), $injector.get('BerechnungsManager'), $injector.get('ErrorService'), authServiceRS);
    }));

    describe('Public API', function () {
        it('should include a cancel() function', function () {
            expect(betreuungView.cancel).toBeDefined();
        });
    });

    describe('API Usage', function () {
        describe('Object creation', () => {
            it('create an empty list of Betreuungspensen for a role different than Institution', () => {
                let myBetreuungView: BetreuungViewController = new BetreuungViewController($state, gesuchModelManager, ebeguUtil, null,
                    $rootScope.$new(), null, null, authServiceRS);
                expect(myBetreuungView.getBetreuungspensen()).toBeDefined();
                expect(myBetreuungView.getBetreuungspensen().length).toEqual(1);
            });
        });
        describe('cancel existing object', () => {
            it('should not remove the kind and then go to betreuungen', () => {
                spyOn($state, 'go');
                spyOn(gesuchModelManager, 'removeBetreuungFromKind');

                betreuungView.cancel();
                expect(gesuchModelManager.removeBetreuungFromKind).not.toHaveBeenCalled();
                expect($state.go).toHaveBeenCalledWith('gesuch.betreuungen');
            });
        });
        describe('cancel non-existing object', () => {
            it('should remove the kind and then go to betreuungen', () => {
                spyOn($state, 'go');
                betreuung.timestampErstellt = undefined;
                spyOn(gesuchModelManager, 'removeBetreuungFromKind');

                betreuungView.cancel();
                expect(gesuchModelManager.removeBetreuungFromKind).toHaveBeenCalled();
                expect($state.go).toHaveBeenCalledWith('gesuch.betreuungen');
            });
        });
        describe('getInstitutionenSDList', () => {
            beforeEach(function () {
                gesuchModelManager.getInstitutionenList().push(createInstitutionStammdaten('1', TSBetreuungsangebotTyp.KITA));
                gesuchModelManager.getInstitutionenList().push(createInstitutionStammdaten('2', TSBetreuungsangebotTyp.KITA));
                gesuchModelManager.getInstitutionenList().push(createInstitutionStammdaten('3', TSBetreuungsangebotTyp.TAGESELTERN_KLEINKIND));
                gesuchModelManager.getInstitutionenList().push(createInstitutionStammdaten('4', TSBetreuungsangebotTyp.TAGESSCHULE));
            });
            it('should return an empty list if betreuungsangebot is not yet defined', () => {
                let list: Array<TSInstitutionStammdaten> = betreuungView.getInstitutionenSDList();
                expect(list).toBeDefined();
                expect(list.length).toBe(0);
            });
            it('should return a list with 2 Institutions of type TSBetreuungsangebotTyp.KITA', () => {
                betreuungView.betreuungsangebot = {key: 'KITA', value: 'kita'};
                let list: Array<TSInstitutionStammdaten> = betreuungView.getInstitutionenSDList();
                expect(list).toBeDefined();
                expect(list.length).toBe(2);
                expect(list[0].iban).toBe('1');
                expect(list[1].iban).toBe('2');
            });
        });
        describe('createBetreuungspensum', () => {
            it('creates the first betreuungspensum in empty list and then a second one (for role=Institution)', () => {
                // Just creating an object must add a new BetreuungspensumContainer
                expect(gesuchModelManager.getBetreuungToWorkWith().betreuungspensumContainers).toBeDefined();
                expect(gesuchModelManager.getBetreuungToWorkWith().betreuungspensumContainers.length).toBe(1);
                betreuungView.createBetreuungspensum();
                expect(gesuchModelManager.getBetreuungToWorkWith().betreuungspensumContainers).toBeDefined();
                expect(gesuchModelManager.getBetreuungToWorkWith().betreuungspensumContainers.length).toBe(2);
                expect(gesuchModelManager.getBetreuungToWorkWith().betreuungspensumContainers[0].betreuungspensumGS).toBeUndefined();
                expect(gesuchModelManager.getBetreuungToWorkWith().betreuungspensumContainers[0].betreuungspensumJA).toBeDefined();
                expect(gesuchModelManager.getBetreuungToWorkWith().betreuungspensumContainers[0].betreuungspensumJA.pensum).toBeUndefined();
                expect(gesuchModelManager.getBetreuungToWorkWith().betreuungspensumContainers[0].betreuungspensumJA.gueltigkeit.gueltigAb).toBeUndefined();
                expect(gesuchModelManager.getBetreuungToWorkWith().betreuungspensumContainers[0].betreuungspensumJA.gueltigkeit.gueltigBis).toBeUndefined();
            });
        });
        describe('submit', () => {
            it('Does not submit because form is invalid', () => {
                spyOn(gesuchModelManager, 'updateBetreuung').and.returnValue($q.when({}));
                let form: any = {};
                form.$valid = false;
                betreuungView.submit(form);
                expect(gesuchModelManager.updateBetreuung).not.toHaveBeenCalled();
            });
            it('submits all data of current Betreuung', () => {
                testSubmit($q.when({}), true);
            });
            it('submits but data are invalid and does not move forward', () => {
                testSubmit($q.reject(), false);
            });
        });
        describe('platzAnfordern()', () => {
            it('must change the status of the Betreuung to WARTEN', () => {
                spyOn(gesuchModelManager, 'updateBetreuung').and.returnValue($q.when({}));
                let form: any = {};
                form.$valid = true;
                // betreuung.timestampErstellt = undefined;
                betreuung.betreuungsstatus = TSBetreuungsstatus.AUSSTEHEND;
                expect(gesuchModelManager.getBetreuungToWorkWith().betreuungsstatus).toEqual(TSBetreuungsstatus.AUSSTEHEND);
                betreuungView.platzAnfordern(form);
                expect(gesuchModelManager.getBetreuungToWorkWith().betreuungsstatus).toEqual(TSBetreuungsstatus.WARTEN);
                expect(gesuchModelManager.updateBetreuung).toHaveBeenCalled();
            });
        });
        describe('removeBetreuungspensum', () => {
            it('should remove the betreuungspensum from the list', () => {
                betreuungView.getBetreuungModel().betreuungspensumContainers = [];
                betreuungView.createBetreuungspensum();
                expect(betreuungView.getBetreuungspensen().length).toEqual(1);
                betreuungView.removeBetreuungspensum(betreuungView.getBetreuungspensen()[0]);
                expect(betreuungView.getBetreuungspensen().length).toEqual(0);
                betreuungView.createBetreuungspensum();
                expect(betreuungView.getBetreuungspensen().length).toEqual(1);
                betreuungView.removeBetreuungspensum(betreuungView.getBetreuungspensen()[0]);
                expect(betreuungView.getBetreuungspensen().length).toEqual(0);
            });
        });
    });

    function createInstitutionStammdaten(iban: string, betAngTyp: TSBetreuungsangebotTyp) {
        let instStam1: TSInstitutionStammdaten = new TSInstitutionStammdaten();
        instStam1.iban = iban;
        instStam1.betreuungsangebotTyp = betAngTyp;
        return instStam1;
    }

    /**
     * Das Parameter promiseResponse ist das Object das die Methode gesuchModelManager.updateBetreuung() zurueckgeben muss. Wenn dieses
     * eine Exception (reject) ist, muss der $state nicht geaendert werden und daher wird die Methode $state.go()  nicht aufgerufen.
     * Ansonsten wird sie mit  dem naechsten state 'gesuch.betreuungen' aufgerufen
     * @param promiseResponse
     */
    function testSubmit(promiseResponse: any, moveToNextStep: boolean) {
        spyOn($state, 'go');
        spyOn(gesuchModelManager, 'updateBetreuung').and.returnValue(promiseResponse);
        TestDataUtil.mockDefaultGesuchModelManagerHttpCalls($httpBackend);
        let form: any = {};
        form.$valid = true;
        betreuungView.submit(form);
        $rootScope.$apply();
        expect(gesuchModelManager.updateBetreuung).toHaveBeenCalled();
        if (moveToNextStep) {
            expect($state.go).toHaveBeenCalledWith('gesuch.betreuungen');
        } else {
            expect($state.go).not.toHaveBeenCalled();
        }
    }

});
