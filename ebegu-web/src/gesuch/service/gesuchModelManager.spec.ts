import {EbeguWebCore} from '../../core/core.module';
import GesuchModelManager from './gesuchModelManager';

describe('gesuchModelManager', function () {

    let gesuchModelManager: GesuchModelManager;

    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        gesuchModelManager = $injector.get('GesuchModelManager');
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
                expect(gesuchModelManager.getBetreuungToWorkWith().betreuungspensumContainers).toBeUndefined();
                expect(gesuchModelManager.getBetreuungToWorkWith().betreuungsstatus).toBeUndefined();
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
    });


    // HELP METHODS

    function createKindContainer() {
        gesuchModelManager.initKinder();
        gesuchModelManager.createKind();
        gesuchModelManager.initBetreuung();
    }

});
