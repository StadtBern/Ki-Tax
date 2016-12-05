import {EbeguWebCore} from '../../../core/core.module';
import {IStateService} from 'angular-ui-router';
import {EbeguWebGesuch} from '../../gesuch.module';
import GesuchModelManager from '../../service/gesuchModelManager';
import {VerfuegenListViewController} from './verfuegenListView';
import TSBetreuung from '../../../models/TSBetreuung';
import TSKindContainer from '../../../models/TSKindContainer';
import BerechnungsManager from '../../service/berechnungsManager';
import {IQService, IScope, IHttpBackendService} from 'angular';
import TestDataUtil from '../../../utils/TestDataUtil';
import {TSBetreuungsstatus} from '../../../models/enums/TSBetreuungsstatus';

describe('verfuegenListViewTest', function () {

    let verfuegenListView: VerfuegenListViewController;
    let gesuchModelManager: GesuchModelManager;
    let $state: IStateService;
    let tsKindContainer: TSKindContainer;
    let berechnungsManager: BerechnungsManager;
    let $q: IQService;
    let $rootScope: IScope;
    let $httpBackend: IHttpBackendService;

    beforeEach(angular.mock.module(EbeguWebCore.name));
    beforeEach(angular.mock.module(EbeguWebGesuch.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        gesuchModelManager = $injector.get('GesuchModelManager');
        $state = $injector.get('$state');
        $q = $injector.get('$q');
        $rootScope = $injector.get('$rootScope');
        $httpBackend = $injector.get('$httpBackend');
        tsKindContainer = new TSKindContainer();
        let wizardStepManager = $injector.get('WizardStepManager');
        spyOn(wizardStepManager, 'updateWizardStepStatus').and.returnValue({});
        spyOn(gesuchModelManager, 'getKinderWithBetreuungList').and.returnValue([tsKindContainer]);
        spyOn(gesuchModelManager, 'calculateVerfuegungen').and.returnValue($q.when({}));

        berechnungsManager = $injector.get('BerechnungsManager');
        spyOn(berechnungsManager, 'calculateFinanzielleSituation').and.returnValue({});
        spyOn(berechnungsManager, 'calculateEinkommensverschlechterung').and.returnValue({});

        TestDataUtil.mockDefaultGesuchModelManagerHttpCalls($httpBackend);
        verfuegenListView = new VerfuegenListViewController($state, gesuchModelManager, berechnungsManager, undefined,
            wizardStepManager, null, $injector.get('DownloadRS'), $injector.get('MahnungRS'), $injector.get('$log'));
        $rootScope.$apply();
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
        describe('openVerfuegen', function () {
            it('does not open the betreuung because it is not BESTAETIGT', function () {
                spyOn(gesuchModelManager, 'findKind').and.returnValue(1);
                let betreuung: TSBetreuung = new TSBetreuung();
                betreuung.betreuungsstatus = TSBetreuungsstatus.ABGEWIESEN;

                verfuegenListView.openVerfuegung(tsKindContainer, betreuung);

                expect(gesuchModelManager.findKind).not.toHaveBeenCalled();
            });
            it('does not find the Kind, so it stops loading and does not move to the next page', function () {
                spyOn(gesuchModelManager, 'findKind').and.returnValue(-1);
                spyOn(gesuchModelManager, 'setKindNumber');
                spyOn($state, 'go');
                let betreuung: TSBetreuung = new TSBetreuung();
                betreuung.betreuungsstatus = TSBetreuungsstatus.BESTAETIGT;

                verfuegenListView.openVerfuegung(tsKindContainer, betreuung);

                expect(gesuchModelManager.findKind).toHaveBeenCalledWith(tsKindContainer);
                expect(gesuchModelManager.setKindNumber).not.toHaveBeenCalled();
                expect($state.go).not.toHaveBeenCalledWith('gesuch.verfuegenView', { gesuchId: ''});
            });
            it('does find the Kind but does not find the Betreuung, so it stops loading and does not move to the next page', function () {
                spyOn(gesuchModelManager, 'findKind').and.returnValue(1);
                spyOn(gesuchModelManager, 'findBetreuung').and.returnValue(-1);
                spyOn(gesuchModelManager, 'setKindNumber');
                spyOn(gesuchModelManager, 'setBetreuungNumber');
                spyOn($state, 'go');
                let betreuung: TSBetreuung = new TSBetreuung();
                betreuung.betreuungsstatus = TSBetreuungsstatus.BESTAETIGT;

                verfuegenListView.openVerfuegung(tsKindContainer, betreuung);

                expect(gesuchModelManager.findKind).toHaveBeenCalledWith(tsKindContainer);
                expect(gesuchModelManager.setKindNumber).toHaveBeenCalledWith(1);
                expect(gesuchModelManager.findBetreuung).toHaveBeenCalledWith(betreuung);
                expect(gesuchModelManager.setBetreuungNumber).not.toHaveBeenCalled();
                expect($state.go).not.toHaveBeenCalledWith('gesuch.verfuegenView', { gesuchId: ''});
            });
            it('does find the Kind but does not find the Betreuung, so it stops loading and does not move to the next page', function () {
                spyOn(gesuchModelManager, 'findKind').and.returnValue(1);
                spyOn(gesuchModelManager, 'findBetreuung').and.returnValue(2);
                spyOn(gesuchModelManager, 'setKindNumber');
                spyOn(gesuchModelManager, 'setBetreuungNumber');
                spyOn($state, 'go');
                let betreuung: TSBetreuung = new TSBetreuung();
                betreuung.betreuungsstatus = TSBetreuungsstatus.BESTAETIGT;

                verfuegenListView.openVerfuegung(tsKindContainer, betreuung);

                expect(gesuchModelManager.findKind).toHaveBeenCalledWith(tsKindContainer);
                expect(gesuchModelManager.setKindNumber).toHaveBeenCalledWith(1);
                expect(gesuchModelManager.findBetreuung).toHaveBeenCalledWith(betreuung);
                expect(gesuchModelManager.setBetreuungNumber).toHaveBeenCalledWith(2);
                expect($state.go).toHaveBeenCalledWith('gesuch.verfuegenView', { gesuchId: ''});
            });
        });
    });

});
