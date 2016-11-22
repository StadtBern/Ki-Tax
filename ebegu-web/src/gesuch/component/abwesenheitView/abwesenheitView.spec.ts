import {AbwesenheitViewController, KindBetreuungUI} from './abwesenheitView';
import {EbeguWebCore} from '../../../core/core.module';
import GesuchModelManager from '../../service/gesuchModelManager';
import WizardStepManager from '../../service/wizardStepManager';
import BerechnungsManager from '../../service/berechnungsManager';
import ErrorService from '../../../core/errors/service/ErrorService';
import {DvDialog} from '../../../core/directive/dv-dialog/dv-dialog';
import TSBetreuung from '../../../models/TSBetreuung';
import TSInstitutionStammdaten from '../../../models/TSInstitutionStammdaten';
import TSInstitution from '../../../models/TSInstitution';
import TSKind from '../../../models/TSKind';
import TSKindContainer from '../../../models/TSKindContainer';
import ITranslateService = angular.translate.ITranslateService;
import IQService = angular.IQService;

describe('abwesenheitView', function () {

    let abwesenheitController: AbwesenheitViewController;
    let gesuchModelManager: GesuchModelManager;
    let wizardStepManager: WizardStepManager;
    let berechnungsManager: BerechnungsManager;
    let errorService: ErrorService;
    let $translate: ITranslateService;
    let dialog: DvDialog;
    let $q: IQService;

    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        gesuchModelManager = $injector.get('GesuchModelManager');
        wizardStepManager = $injector.get('WizardStepManager');
        spyOn(wizardStepManager, 'updateWizardStepStatus').and.returnValue({});
        berechnungsManager = $injector.get('BerechnungsManager');
        errorService = $injector.get('ErrorService');
        $translate = $injector.get('$translate');
        dialog = $injector.get('DvDialog');
        $q = $injector.get('$q');
    }));

    describe('getNameFromBetroffene', function () {
        beforeEach(function () {
            abwesenheitController = new AbwesenheitViewController(gesuchModelManager, berechnungsManager,
                wizardStepManager, dialog, $translate, $q, errorService);
        });
        it('should return empty string for undefined kindBetreuung', function () {
            let kindBetreuung: KindBetreuungUI = new KindBetreuungUI();
            expect(abwesenheitController.getTextForBetreuungDDL(kindBetreuung)).toBe('');
        });
        it('should return empty string for empty data', function () {
            let kindBetreuung: KindBetreuungUI = new KindBetreuungUI();
            expect(abwesenheitController.getTextForBetreuungDDL(kindBetreuung)).toBe('');
        });
        it('should return Name of KindBetreuung', function () {
            let kindBetreuung: KindBetreuungUI = new KindBetreuungUI();
            let betreuung = new TSBetreuung();
            let institutionStammdaten = new TSInstitutionStammdaten();
            let ins = new TSInstitution();
            ins.name = 'InstitutionTest';
            institutionStammdaten.institution = ins;
            betreuung.institutionStammdaten = institutionStammdaten;
            kindBetreuung.betreuung = betreuung;

            let kind = new TSKindContainer();
            let kindJA = new TSKind();
            kindJA.vorname = 'Pedrito';
            kindJA.nachname = 'Contreras';
            kind.kindJA = kindJA;
            kindBetreuung.kind = kind;

            expect(abwesenheitController.getTextForBetreuungDDL(kindBetreuung)).toBe('Pedrito Contreras - InstitutionTest');
        });
    });
    describe('createAbwesenheit', function () {
        it('should return empty string for empty data', function () {
            expect(abwesenheitController.getAbwesenheiten().length).toBe(0);
            abwesenheitController.createAbwesenheit();
            expect(abwesenheitController.getAbwesenheiten().length).toBe(1);
            expect(abwesenheitController.getAbwesenheiten()[0]).toBeDefined();
            expect(abwesenheitController.getAbwesenheiten()[0].kindBetreuung).toBeUndefined();
            expect(abwesenheitController.getAbwesenheiten()[0].abwesenheit).toBeDefined();
        });
    });
});
