import {EbeguWebCore} from '../../../core/core.module';
import {IStateService} from 'angular-ui-router';
import {EbeguWebGesuch} from '../../gesuch.module';
import {BetreuungListViewController} from './betreuungListView';
import GesuchModelManager from '../../service/gesuchModelManager';
import TSKindContainer from '../../../models/TSKindContainer';
import TSBetreuung from '../../../models/TSBetreuung';
import TSKind from '../../../models/TSKind';

describe('betreuungListViewTest', function () {

    let betreuungListView: BetreuungListViewController;
    let gesuchModelManager: GesuchModelManager;
    let $state: IStateService;

    beforeEach(angular.mock.module(EbeguWebCore.name));
    beforeEach(angular.mock.module(EbeguWebGesuch.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        gesuchModelManager = $injector.get('GesuchModelManager');
        let wizardStepManager = $injector.get('WizardStepManager');
        spyOn(wizardStepManager, 'updateWizardStepStatus').and.returnValue({});
        $state = $injector.get('$state');
        let mddialog = $injector.get('$mdDialog');
        let dialog = $injector.get('DvDialog');
        let ebeguRestUtil = $injector.get('EbeguRestUtil');
        let errorService = $injector.get('ErrorService');
        betreuungListView = new BetreuungListViewController($state, gesuchModelManager, mddialog, dialog, ebeguRestUtil, undefined,
            errorService, wizardStepManager);
    }));

    describe('Public API', function () {
        it('should include a createBetreuung() function', function () {
            expect(betreuungListView.createBetreuung).toBeDefined();
        });
        it('should include a nextStep() function', function () {
            expect(betreuungListView.nextStep).toBeDefined();
        });
        it('should include a previousStep() function', function () {
            expect(betreuungListView.previousStep).toBeDefined();
        });
    });

    describe('API Usage', function () {
        describe('createBetreuung', () => {
            it('should create a Betreuung', () => {
                var tsKindContainer = new TSKindContainer();
                tsKindContainer.betreuungen = [];
                spyOn(gesuchModelManager, 'createBetreuung');
                spyOn($state, 'go');
                spyOn(gesuchModelManager, 'findKind').and.returnValue(1);
                betreuungListView.createBetreuung(tsKindContainer);
                expect(gesuchModelManager.findKind).toHaveBeenCalledWith(tsKindContainer);
                expect(gesuchModelManager.getKindNumber()).toBe(1);
                expect(gesuchModelManager.createBetreuung).toHaveBeenCalled();
                expect($state.go).toHaveBeenCalledWith('gesuch.betreuung');
            });
        });
        describe('nextStep', () => {
            it('should go to finanzielleSituation', () => {
                spyOn($state, 'go');
                betreuungListView.nextStep();
                expect($state.go).toHaveBeenCalledWith('gesuch.erwerbsPensen');
            });
        });
        describe('previousStep', () => {
            it('should go to kinder', () => {
                spyOn($state, 'go');
                betreuungListView.previousStep();
                expect($state.go).toHaveBeenCalledWith('gesuch.kinder');
            });
        });
        describe('exist at least one Betreuung among all kinder', function () {
            it('should return false for empty list', function() {
                spyOn(gesuchModelManager, 'getKinderWithBetreuungList').and.returnValue([]);
                expect(betreuungListView.isThereAnyBetreuung()).toBe(false);
            });
            it('should return false for a list with Kinder but no Betreuung', function() {
                let kind: TSKindContainer = new TSKindContainer();
                kind.kindJA = new TSKind();
                kind.kindJA.familienErgaenzendeBetreuung = false;
                spyOn(gesuchModelManager, 'getKinderWithBetreuungList').and.returnValue([kind]);
                expect(betreuungListView.isThereAnyBetreuung()).toBe(false);
            });
            it('should return true for a list with Kinder needing Betreuung', function() {
                let kind: TSKindContainer = new TSKindContainer();
                kind.kindJA = new TSKind();
                kind.kindJA.familienErgaenzendeBetreuung = true;
                let betreuung: TSBetreuung = new TSBetreuung();
                kind.betreuungen = [betreuung];
                spyOn(gesuchModelManager, 'getKinderWithBetreuungList').and.returnValue([kind]);
                expect(betreuungListView.isThereAnyBetreuung()).toBe(true);
            });
        });
    });


});
