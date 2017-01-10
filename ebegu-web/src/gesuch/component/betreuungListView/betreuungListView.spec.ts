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
        let wizardStepManager = $injector.get('WizardStepManager');
        spyOn(wizardStepManager, 'updateWizardStepStatus').and.returnValue({});
        $state = $injector.get('$state');
        let mddialog = $injector.get('$mdDialog');
        let dialog = $injector.get('DvDialog');
        let ebeguRestUtil = $injector.get('EbeguRestUtil');
        let errorService = $injector.get('ErrorService');
        betreuungListView = new BetreuungListViewController($state, gesuchModelManager, mddialog, dialog, ebeguRestUtil, undefined,
            errorService, wizardStepManager, $injector.get('$rootScope'));
    }));

    describe('Public API', function () {
        it('should include a createBetreuung() function', function () {
            expect(betreuungListView.createBetreuung).toBeDefined();
        });
    });

    describe('API Usage', function () {
        describe('createBetreuung', () => {
            it('should create a Betreuung', () => {
                let tsKindContainer = new TSKindContainer();
                tsKindContainer.betreuungen = [];
                spyOn($state, 'go');
                spyOn(gesuchModelManager, 'findKind').and.returnValue(1);

                betreuungListView.createBetreuung(tsKindContainer);

                expect(gesuchModelManager.findKind).toHaveBeenCalledWith(tsKindContainer);
                expect(gesuchModelManager.getKindNumber()).toBe(1);
                expect(gesuchModelManager.createBetreuung).toHaveBeenCalled();
                expect($state.go).toHaveBeenCalledWith('gesuch.betreuung', { betreuungNumber: undefined, kindNumber: 1, gesuchId: ''});
            });
        });
    });


});
