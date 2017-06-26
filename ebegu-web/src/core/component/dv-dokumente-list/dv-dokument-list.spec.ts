import {EbeguWebCore} from '../../core.module';
import {DVDokumenteListController} from './dv-dokumente-list';
import GesuchModelManager from '../../../gesuch/service/gesuchModelManager';
import TSDokumentGrund from '../../../models/TSDokumentGrund';
import {TSDokumentGrundPersonType} from '../../../models/enums/TSDokumentGrundPersonType';
import TSGesuch from '../../../models/TSGesuch';
import TSGesuchstellerContainer from '../../../models/TSGesuchstellerContainer';
import TSKindContainer from '../../../models/TSKindContainer';
import TSKind from '../../../models/TSKind';

describe('dvDokumenteList', function () {

    let controller: DVDokumenteListController;
    let gesuchModelManager: GesuchModelManager;

    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        gesuchModelManager = $injector.get('GesuchModelManager');

        controller = new DVDokumenteListController(undefined, gesuchModelManager, undefined, undefined,
            undefined, undefined, undefined, undefined, undefined, undefined);

    }));

    describe('extractFullName', function () {
        it('should return the fiedl fullName for FREETEXT', function () {
            let dokumentGrund: TSDokumentGrund = new TSDokumentGrund();
            dokumentGrund.personType = TSDokumentGrundPersonType.FREETEXT;
            dokumentGrund.fullName = 'Leonardo Dantes';
            expect(controller.extractFullName(dokumentGrund)).toBe(dokumentGrund.fullName);
        });
        it('should return the fullName of GS1 for GESUCHSTELLER and personNumber=1', function () {
            let dokumentGrund: TSDokumentGrund = new TSDokumentGrund();
            dokumentGrund.personType = TSDokumentGrundPersonType.GESUCHSTELLER;
            dokumentGrund.personNumber = 1;
            dokumentGrund.fullName = 'Leonardo Dantes'; // even though this is set it shouldn't take it
            mockGesuch();
            expect(controller.extractFullName(dokumentGrund)).toBe('Leonardo Primero');
        });
        it('should return empty string for GESUCHSTELLER and personNumber=null', function () {
            let dokumentGrund: TSDokumentGrund = new TSDokumentGrund();
            dokumentGrund.personType = TSDokumentGrundPersonType.GESUCHSTELLER;
            dokumentGrund.personNumber = undefined;
            dokumentGrund.fullName = 'Leonardo Dantes'; // even though this is set it shouldn't take it
            mockGesuch();
            expect(controller.extractFullName(dokumentGrund)).toBe('');
        });
        it('should return the fullName of GS1 for GESUCHSTELLER and personNumber=2', function () {
            let dokumentGrund: TSDokumentGrund = new TSDokumentGrund();
            dokumentGrund.personType = TSDokumentGrundPersonType.GESUCHSTELLER;
            dokumentGrund.personNumber = 2;
            dokumentGrund.fullName = 'Leonardo Dantes'; // even though this is set it shouldn't take it
            mockGesuch();
            expect(controller.extractFullName(dokumentGrund)).toBe('Leonardo Segundo');
        });
        it('should return the fullName of KIND3 for KIND and personNumber=3', function () {
            let dokumentGrund: TSDokumentGrund = new TSDokumentGrund();
            dokumentGrund.personType = TSDokumentGrundPersonType.KIND;
            dokumentGrund.personNumber = 3;
            dokumentGrund.fullName = 'Leonardo Dantes'; // even though this is set it shouldn't take it
            mockGesuch();
            expect(controller.extractFullName(dokumentGrund)).toBe('Leonardo Hijo');
        });
        it('should return emptz string for a not existing KIND', function () {
            let dokumentGrund: TSDokumentGrund = new TSDokumentGrund();
            dokumentGrund.personType = TSDokumentGrundPersonType.KIND;
            dokumentGrund.personNumber = 6;
            dokumentGrund.fullName = 'Leonardo Dantes'; // even though this is set it shouldn't take it
            mockGesuch();
            expect(controller.extractFullName(dokumentGrund)).toBe('');
        });
    });

    function mockGesuch() {
        let gesuch: TSGesuch = new TSGesuch();
        gesuch.gesuchsteller1 = new TSGesuchstellerContainer();
        spyOn(gesuch.gesuchsteller1, 'extractFullName').and.returnValue('Leonardo Primero');
        gesuch.gesuchsteller2 = new TSGesuchstellerContainer();
        spyOn(gesuch.gesuchsteller2, 'extractFullName').and.returnValue('Leonardo Segundo');

        let kind: TSKindContainer = new TSKindContainer();
        kind.kindJA = new TSKind();
        spyOn(kind.kindJA, 'getFullName').and.returnValue('Leonardo Hijo');
        kind.kindNummer = 3;
        gesuch.kindContainers = [kind];

        spyOn(gesuchModelManager, 'getGesuch').and.returnValue(gesuch);
    }
});
