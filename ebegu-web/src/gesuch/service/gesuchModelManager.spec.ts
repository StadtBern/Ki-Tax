import {EbeguWebCore} from '../../core/core.module';
import GesuchModelManager from './gesuchModelManager';
import IPromise = angular.IPromise;
import BetreuungRS from '../../core/service/betreuungRS';
import IQService = angular.IQService;
import {TSBetreuungsstatus} from '../../models/enums/TSBetreuungsstatus';

describe('gesuchModelManager', function () {

    let gesuchModelManager: GesuchModelManager;
    let betreuungRS: BetreuungRS;
    let $q: IQService;

    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        gesuchModelManager = $injector.get('GesuchModelManager');
        betreuungRS = $injector.get('BetreuungRS');
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
    });


    // HELP METHODS

    function createKindContainer() {
        gesuchModelManager.initKinder();
        gesuchModelManager.createKind();
        gesuchModelManager.initBetreuung();
    }

});
