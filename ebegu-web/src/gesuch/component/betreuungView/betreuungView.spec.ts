import {EbeguWebCore} from '../../../core/core.module';
import {IStateService} from 'angular-ui-router';
import {BetreuungViewController} from './betreuungView';
import GesuchModelManager from '../../service/gesuchModelManager';
import TSBetreuung from '../../../models/TSBetreuung';
import DateUtil from '../../../utils/DateUtil';

describe('betreuungView', function () {

    let betreuungView: BetreuungViewController;
    let gesuchModelManager: GesuchModelManager;
    let $state: IStateService;


    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        gesuchModelManager = $injector.get('GesuchModelManager');
        $state = $injector.get('$state');
        betreuungView = new BetreuungViewController($state, gesuchModelManager);
    }));

    describe('Public API', function () {
        it('should include a cancel() function', function () {
            expect(betreuungView.cancel).toBeDefined();
        });
    });

    describe('API Usage', function () {
        describe('cancel existing object', () => {
            it('should not remove the kind and then go to betreuungen', () => {
                spyOn($state, 'go');
                let betreuung: TSBetreuung = new TSBetreuung();
                betreuung.timestampErstellt = DateUtil.today();
                spyOn(gesuchModelManager, 'getBetreuungToWorkWith').and.returnValue(betreuung);
                spyOn(gesuchModelManager, 'removeBetreuungFromKind');

                betreuungView.cancel();
                expect(gesuchModelManager.removeBetreuungFromKind).not.toHaveBeenCalled();
                expect($state.go).toHaveBeenCalledWith('gesuch.betreuungen');
            });
        });
        describe('cancel non-existing object', () => {
            it('should remove the kind and then go to betreuungen', () => {
                spyOn($state, 'go');
                let betreuung: TSBetreuung = new TSBetreuung();
                spyOn(gesuchModelManager, 'getBetreuungToWorkWith').and.returnValue(betreuung);
                spyOn(gesuchModelManager, 'removeBetreuungFromKind');

                betreuungView.cancel();
                expect(gesuchModelManager.removeBetreuungFromKind).toHaveBeenCalled();
                expect($state.go).toHaveBeenCalledWith('gesuch.betreuungen');
            });
        });
    });


});
