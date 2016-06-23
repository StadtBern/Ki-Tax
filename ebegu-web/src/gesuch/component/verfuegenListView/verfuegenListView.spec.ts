import {EbeguWebCore} from '../../../core/core.module';
import {IStateService} from 'angular-ui-router';
import {EbeguWebGesuch} from '../../gesuch.module';
import GesuchModelManager from '../../service/gesuchModelManager';
import {VerfuegenListViewController} from './verfuegenListView';
import TSBetreuung from '../../../models/TSBetreuung';
import TSKindContainer from '../../../models/TSKindContainer';

describe('verfuegenListViewTest', function () {

    let verfuegenListView: VerfuegenListViewController;
    let gesuchModelManager: GesuchModelManager;
    let $state: IStateService;
    let tsKindContainer: TSKindContainer;

    beforeEach(angular.mock.module(EbeguWebCore.name));
    beforeEach(angular.mock.module(EbeguWebGesuch.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        gesuchModelManager = $injector.get('GesuchModelManager');
        $state = $injector.get('$state');
        tsKindContainer = new TSKindContainer();
        spyOn(gesuchModelManager, 'getKinderWithBetreuungList').and.returnValue([tsKindContainer]);
        verfuegenListView = new VerfuegenListViewController($state, gesuchModelManager, undefined);
    }));

    describe('Public API', function () {
        it('should include a getKinderWithBetreuungList() function', function () {
            expect(verfuegenListView.getKinderWithBetreuungList).toBeDefined();
        });
        it('should include a openVerfuegung() function', function () {
            expect(verfuegenListView.openVerfuegung).toBeDefined();
        });
    });

    describe('Usage API', function () {
        it('should call gesuchModelManager.getBetreuungenList() and return it back', function () {
            let kinderWithetreuungList = verfuegenListView.getKinderWithBetreuungList();
            expect(kinderWithetreuungList).toBeDefined();
            expect(kinderWithetreuungList.length).toBe(1);
            expect(kinderWithetreuungList[0]).toBe(tsKindContainer);
        });
    });

});
