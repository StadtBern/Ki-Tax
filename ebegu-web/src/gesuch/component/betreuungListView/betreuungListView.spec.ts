import {EbeguWebCore} from '../../../core/core.module';
import {DvDialog} from '../../../core/directive/dv-dialog/dv-dialog';
import ErrorService from '../../../core/errors/service/ErrorService';
import TSKindContainer from '../../../models/TSKindContainer';
import EbeguUtil from '../../../utils/EbeguUtil';
import {EbeguWebGesuch} from '../../gesuch.module';
import GesuchModelManager from '../../service/gesuchModelManager';
import WizardStepManager from '../../service/wizardStepManager';
import {BetreuungListViewController} from './betreuungListView';

describe('betreuungListViewTest', function () {

    let betreuungListView: BetreuungListViewController;
    let gesuchModelManager: GesuchModelManager;
    let $state: angular.ui.IStateService;

    beforeEach(angular.mock.module(EbeguWebCore.name));
    beforeEach(angular.mock.module(EbeguWebGesuch.name));

    beforeEach(angular.mock.inject(function ($injector: angular.auto.IInjectorService) {
        gesuchModelManager = $injector.get('GesuchModelManager');
        let wizardStepManager: WizardStepManager = $injector.get('WizardStepManager');
        spyOn(wizardStepManager, 'updateWizardStepStatus').and.returnValue({});
        spyOn(gesuchModelManager, 'convertKindNumberToKindIndex').and.returnValue(0);
        $state = $injector.get('$state');
        let $translate: angular.translate.ITranslateService = $injector.get('$translate');
        let dialog: DvDialog = $injector.get('DvDialog');
        let ebeguUtil: EbeguUtil = $injector.get('EbeguUtil');
        let errorService: ErrorService = $injector.get('ErrorService');
        let $timeout = $injector.get('$timeout');

        betreuungListView = new BetreuungListViewController($state, gesuchModelManager, $translate, dialog, ebeguUtil, undefined,
            errorService, wizardStepManager, undefined, $injector.get('$rootScope'), undefined, $timeout);
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
                tsKindContainer.kindNummer = 1;
                spyOn($state, 'go');
                spyOn(gesuchModelManager, 'findKind').and.returnValue(0);

                betreuungListView.createBetreuung(tsKindContainer);

                expect(gesuchModelManager.getKindIndex()).toBe(0);

                expect($state.go).toHaveBeenCalledWith('gesuch.betreuung', {
                    betreuungNumber: undefined,
                    kindNumber: 1,
                    gesuchId: ''
                });
            });
        });
    });

});
