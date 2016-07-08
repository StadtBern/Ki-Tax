import {EbeguWebCore} from '../../core/core.module';
import GesuchModelManager from './gesuchModelManager';
import {IHttpBackendService, IScope, IQService} from 'angular';
import BetreuungRS from '../../core/service/betreuungRS';
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

    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        gesuchModelManager = $injector.get('GesuchModelManager');
        $httpBackend = $injector.get('$httpBackend');
        betreuungRS = $injector.get('BetreuungRS');
        fallRS = $injector.get('FallRS');
        gesuchRS = $injector.get('GesuchRS');
        kindRS = $injector.get('KindRS');
        scope = $injector.get('$rootScope').$new();
        $q = $injector.get('$q');
        authServiceRS = $injector.get('AuthServiceRS');
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
                expect(gesuchModelManager.getBetreuungToWorkWith().bemerkungen).toBeUndefined();
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
                gesuchModelManager.getBetreuungToWorkWith().bemerkungen = 'Neue_Bemerkung';
                gesuchModelManager.getKindToWorkWith().id = '2afc9d9a-957e-4550-9a22-97624a000feb';

                TestDataUtil.mockDefaultGesuchModelManagerHttpCalls($httpBackend);
                let kindToWorkWith: TSKindContainer = gesuchModelManager.getKindToWorkWith();
                kindToWorkWith.nextNumberBetreuung = 5;
                spyOn(kindRS, 'findKind').and.returnValue($q.when(kindToWorkWith));
                spyOn(betreuungRS, 'createBetreuung').and.returnValue($q.when(gesuchModelManager.getBetreuungToWorkWith()));

                gesuchModelManager.updateBetreuung();
                scope.$apply();

                expect(betreuungRS.createBetreuung).toHaveBeenCalledWith(gesuchModelManager.getBetreuungToWorkWith(), '2afc9d9a-957e-4550-9a22-97624a000feb');
                expect(kindRS.findKind).toHaveBeenCalledWith('2afc9d9a-957e-4550-9a22-97624a000feb');
                expect(gesuchModelManager.getBetreuungToWorkWith().bemerkungen).toEqual('Neue_Bemerkung');
                expect(gesuchModelManager.getKindToWorkWith().nextNumberBetreuung).toEqual(5);
            });
        });
        describe('saveGesuchAndFall', () => {
            it('creates a Fall with a linked Gesuch', () => {
                spyOn(fallRS, 'createFall').and.returnValue($q.when({}));
                spyOn(gesuchRS, 'createGesuch').and.returnValue($q.when({}));
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
                gesuchModelManager.gesuch.timestampErstellt = DateUtil.today();
                gesuchModelManager.saveGesuchAndFall();

                scope.$apply();
                expect(gesuchRS.updateGesuch).toHaveBeenCalled();
            });
        });
        describe('initGesuch', () => {
            beforeEach(() => {
                expect(gesuchModelManager.gesuch).toBeUndefined();
            });
            it('links the fall with the undefined user', () => {
                spyOn(authServiceRS, 'getPrincipal').and.returnValue(undefined);

                gesuchModelManager.initGesuch(false);

                expect(gesuchModelManager.gesuch).toBeDefined();
                expect(gesuchModelManager.gesuch.fall).toBeDefined();
                expect(gesuchModelManager.gesuch.fall.verantwortlicher).toBe(undefined);
            });
            it('links the fall with the current user', () => {
                let currentUser: TSUser = new TSUser('Test', 'User', 'username');
                spyOn(authServiceRS, 'getPrincipal').and.returnValue(currentUser);

                gesuchModelManager.initGesuch(false);

                expect(gesuchModelManager.gesuch).toBeDefined();
                expect(gesuchModelManager.gesuch.fall).toBeDefined();
                expect(gesuchModelManager.gesuch.fall.verantwortlicher).toBe(currentUser);
            });
            it('does not force to create a new fall and gesuch', () => {
                gesuchModelManager.initGesuch(false);
                expect(gesuchModelManager.gesuch).toBeDefined();
            });
            it('does force to create a new fall and gesuch', () => {
                gesuchModelManager.initGesuch(true);
                expect(gesuchModelManager.gesuch).toBeDefined();
            });
            it('forces to create a new gesuch and fall even though one already exists', () => {
                gesuchModelManager.initGesuch(false);
                let oldGesuch: TSGesuch = gesuchModelManager.gesuch;
                expect(gesuchModelManager.gesuch).toBeDefined();

                gesuchModelManager.initGesuch(true);
                expect(gesuchModelManager.gesuch).toBeDefined();
                expect(oldGesuch).not.toBe(gesuchModelManager.gesuch);
            });
            it('does not force to create a new gesuch and fall and the old ones will remain', () => {
                gesuchModelManager.initGesuch(false);
                let oldGesuch: TSGesuch = gesuchModelManager.gesuch;
                expect(gesuchModelManager.gesuch).toBeDefined();

                gesuchModelManager.initGesuch(false);
                expect(gesuchModelManager.gesuch).toBeDefined();
                expect(oldGesuch).toBe(gesuchModelManager.gesuch);
            });
        });
        describe('setUserAsFallVerantwortlicher', () => {
            it('puts the given user as the verantwortlicher for the fall', () => {
                gesuchModelManager.initGesuch(false);
                spyOn(authServiceRS, 'getPrincipal').and.returnValue(undefined);
                let user: TSUser = new TSUser('Emiliano', 'Camacho');
                gesuchModelManager.setUserAsFallVerantwortlicher(user);
                expect(gesuchModelManager.gesuch.fall.verantwortlicher).toBe(user);
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
