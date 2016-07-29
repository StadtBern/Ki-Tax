import {EbeguWebCore} from '../../../core/core.module';
import {IStateService} from 'angular-ui-router';
import {EbeguWebGesuch} from '../../gesuch.module';
import GesuchModelManager from '../../service/gesuchModelManager';
import {VerfuegenListViewController} from './verfuegenListView';
import TSBetreuung from '../../../models/TSBetreuung';
import TSKindContainer from '../../../models/TSKindContainer';
import BerechnungsManager from '../../service/berechnungsManager';

describe('verfuegenListViewTest', function () {

    let verfuegenListView: VerfuegenListViewController;
    let gesuchModelManager: GesuchModelManager;
    let $state: IStateService;
    let tsKindContainer: TSKindContainer;
    let berechnungsManager: BerechnungsManager;

    beforeEach(angular.mock.module(EbeguWebCore.name));
    beforeEach(angular.mock.module(EbeguWebGesuch.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        gesuchModelManager = $injector.get('GesuchModelManager');
        $state = $injector.get('$state');
        tsKindContainer = new TSKindContainer();
        spyOn(gesuchModelManager, 'getKinderWithBetreuungList').and.returnValue([tsKindContainer]);
        spyOn(gesuchModelManager, 'calculateVerfuegungen').and.returnValue({});

        berechnungsManager = $injector.get('BerechnungsManager');
        spyOn(berechnungsManager, 'calculateFinanzielleSituation').and.returnValue({});
        spyOn(berechnungsManager, 'calculateEinkommensverschlechterung').and.returnValue({});

        verfuegenListView = new VerfuegenListViewController($state, gesuchModelManager, berechnungsManager, undefined);
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
            it('does not find the Kind, so it stops loading and does not move to the next page', function() {
                spyOn(gesuchModelManager, 'findKind').and.returnValue(-1);
                spyOn(gesuchModelManager, 'setKindNumber');
                spyOn($state, 'go');
                let betreuung: TSBetreuung = new TSBetreuung();

                verfuegenListView.openVerfuegung(tsKindContainer, betreuung);

                expect(gesuchModelManager.findKind).toHaveBeenCalledWith(tsKindContainer);
                expect(gesuchModelManager.setKindNumber).not.toHaveBeenCalled();
                expect($state.go).not.toHaveBeenCalledWith('gesuch.verfuegenView');
            });
            it('does find the Kind but does not find the Betreuung, so it stops loading and does not move to the next page', function() {
                spyOn(gesuchModelManager, 'findKind').and.returnValue(1);
                spyOn(gesuchModelManager, 'findBetreuung').and.returnValue(-1);
                spyOn(gesuchModelManager, 'setKindNumber');
                spyOn(gesuchModelManager, 'setBetreuungNumber');
                spyOn($state, 'go');
                let betreuung: TSBetreuung = new TSBetreuung();

                verfuegenListView.openVerfuegung(tsKindContainer, betreuung);

                expect(gesuchModelManager.findKind).toHaveBeenCalledWith(tsKindContainer);
                expect(gesuchModelManager.setKindNumber).toHaveBeenCalledWith(1);
                expect(gesuchModelManager.findBetreuung).toHaveBeenCalledWith(betreuung);
                expect(gesuchModelManager.setBetreuungNumber).not.toHaveBeenCalled();
                expect($state.go).not.toHaveBeenCalledWith('gesuch.verfuegenView');
            });
            it('does find the Kind but does not find the Betreuung, so it stops loading and does not move to the next page', function() {
                spyOn(gesuchModelManager, 'findKind').and.returnValue(1);
                spyOn(gesuchModelManager, 'findBetreuung').and.returnValue(2);
                spyOn(gesuchModelManager, 'setKindNumber');
                spyOn(gesuchModelManager, 'setBetreuungNumber');
                spyOn($state, 'go');
                let betreuung: TSBetreuung = new TSBetreuung();

                verfuegenListView.openVerfuegung(tsKindContainer, betreuung);

                expect(gesuchModelManager.findKind).toHaveBeenCalledWith(tsKindContainer);
                expect(gesuchModelManager.setKindNumber).toHaveBeenCalledWith(1);
                expect(gesuchModelManager.findBetreuung).toHaveBeenCalledWith(betreuung);
                expect(gesuchModelManager.setBetreuungNumber).toHaveBeenCalledWith(2);
                expect($state.go).toHaveBeenCalledWith('gesuch.verfuegenView');
            });
        });
    });

});
