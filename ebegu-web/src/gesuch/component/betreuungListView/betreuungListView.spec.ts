import {EbeguWebCore} from '../../../core/core.module';
import {IStateService} from 'angular-ui-router';
import {EbeguWebGesuch} from '../../gesuch.module';
import {BetreuungListViewController} from './betreuungListView';
import GesuchModelManager from '../../service/gesuchModelManager';
import TSKindContainer from '../../../models/TSKindContainer';

describe('betreuungListViewTest', function () {

    let betreuungListView: BetreuungListViewController;
    let gesuchModelManager: GesuchModelManager;
    let $state: IStateService;

    beforeEach(angular.mock.module(EbeguWebCore.name));
    beforeEach(angular.mock.module(EbeguWebGesuch.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        gesuchModelManager = $injector.get('GesuchModelManager');
        spyOn(gesuchModelManager, 'initBetreuungStatus').and.returnValue({});
        $state = $injector.get('$state');
        let mddialog = $injector.get('$mdDialog');
        let dialog = $injector.get('DvDialog');
        let ebeguRestUtil = $injector.get('EbeguRestUtil');
        let errorService = $injector.get('ErrorService');
        betreuungListView = new BetreuungListViewController($state, gesuchModelManager, mddialog, dialog, ebeguRestUtil, undefined, errorService);
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
    });


});
