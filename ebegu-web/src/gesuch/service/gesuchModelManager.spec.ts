import {EbeguWebCore} from '../../core/core.module';
import GesuchModelManager from './gesuchModelManager';
import {IHttpBackendService, IScope, IQService} from 'angular';
import BetreuungRS from '../../core/service/betreuungRS';
import {TSBetreuungsstatus} from '../../models/enums/TSBetreuungsstatus';
import FallRS from './fallRS.rest';
import GesuchRS from './gesuchRS.rest';
import TestDataUtil from '../../utils/TestDataUtil';
import DateUtil from '../../utils/DateUtil';

describe('gesuchModelManager', function () {

    let gesuchModelManager: GesuchModelManager;
    let betreuungRS: BetreuungRS;
    let fallRS: FallRS;
    let gesuchRS: GesuchRS;
    let scope: IScope;
    let $httpBackend: IHttpBackendService;
    let $q: IQService;

    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        gesuchModelManager = $injector.get('GesuchModelManager');
        $httpBackend = $injector.get('$httpBackend');
        betreuungRS = $injector.get('BetreuungRS');
        fallRS = $injector.get('FallRS');
        gesuchRS = $injector.get('GesuchRS');
        scope = $injector.get('$rootScope').$new();
        $q = $injector.get('$q');
    }));

    describe('Public API', function () {
        it('should include a createBetreuung() function', function () {
            expect(gesuchModelManager.createBetreuung).toBeDefined();
        });
    });

    describe('API Usage', function () {
        describe('createBetreuung', () => {
            it('should create a new empty Betreuung for the current KindContainer', () => {
                gesuchModelManager.initGesuch();
                createKindContainer();
                expect(gesuchModelManager.getKindToWorkWith().betreuungen).toBeDefined();
                expect(gesuchModelManager.getKindToWorkWith().betreuungen.length).toBe(0);
                gesuchModelManager.createBetreuung();
                expect(gesuchModelManager.getKindToWorkWith().betreuungen).toBeDefined();
                expect(gesuchModelManager.getKindToWorkWith().betreuungen.length).toBe(1);
                expect(gesuchModelManager.getBetreuungToWorkWith().bemerkungen).toBeUndefined();
                expect(gesuchModelManager.getBetreuungToWorkWith().betreuungspensumContainers).toEqual([]);
                expect(gesuchModelManager.getBetreuungToWorkWith().betreuungsstatus).toEqual(TSBetreuungsstatus.AUSSTEHEND);
                expect(gesuchModelManager.getBetreuungToWorkWith().institutionStammdaten).toBeUndefined();
            });
        });
        describe('removeBetreuungFromKind', () => {
            it('should remove the current Betreuung from the list of the current Kind', () => {
                gesuchModelManager.initGesuch();
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
                gesuchModelManager.initGesuch();
                createKindContainer();
                gesuchModelManager.createBetreuung();
                gesuchModelManager.getBetreuungToWorkWith().bemerkungen = 'Neue_Bemerkung';
                gesuchModelManager.getKindToWorkWith().id = '2afc9d9a-957e-4550-9a22-97624a000feb';
                let called: boolean = false;
                spyOn(betreuungRS, 'createBetreuung').and.callFake(function() {
                    called = true;
                    return $q.when({});
                });
                gesuchModelManager.updateBetreuung();
                expect(betreuungRS.createBetreuung).toHaveBeenCalledWith(gesuchModelManager.getBetreuungToWorkWith(), '2afc9d9a-957e-4550-9a22-97624a000feb');
                expect(called).toBe(true);
                expect(gesuchModelManager.getBetreuungToWorkWith().bemerkungen).toEqual('Neue_Bemerkung');
            });
        });
        describe('saveGesuchAndFall', () => {
            it('creates a Fall with a linked Gesuch', () => {
                spyOn(fallRS, 'createFall').and.returnValue($q.when({}));
                spyOn(gesuchRS, 'createGesuch').and.returnValue($q.when({}));
                TestDataUtil.mockDefaultGesuchModelManagerHttpCalls($httpBackend);

                gesuchModelManager.initGesuch();
                gesuchModelManager.saveGesuchAndFall();

                scope.$apply();
                expect(fallRS.createFall).toHaveBeenCalled();
                expect(gesuchRS.createGesuch).toHaveBeenCalled();
            });
            it('only updates the Gesuch because it already exists', () => {
                spyOn(gesuchRS, 'updateGesuch').and.returnValue($q.when({}));
                TestDataUtil.mockDefaultGesuchModelManagerHttpCalls($httpBackend);

                gesuchModelManager.initGesuch();
                gesuchModelManager.gesuch.timestampErstellt = DateUtil.today();
                gesuchModelManager.saveGesuchAndFall();

                scope.$apply();
                expect(gesuchRS.updateGesuch).toHaveBeenCalled();
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
