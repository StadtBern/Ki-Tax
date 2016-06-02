import {EbeguWebCore} from '../../../core/core.module';
import {FallCreationViewController} from './fallCreationView';
import GesuchModelManager from '../../service/gesuchModelManager';
import TSGesuchsperiode from '../../../models/TSGesuchsperiode';
import {TSDateRange} from '../../../models/types/TSDateRange';
import DateUtil from '../../../utils/DateUtil';
import {IQService, IScope} from 'angular';
import {IStateService} from 'angular-ui-router';
import TestDataUtil from '../../../utils/TestDataUtil';

describe('fallCreationView', function () {

    let fallCreationview: FallCreationViewController;
    let gesuchModelManager: GesuchModelManager;
    let $state: IStateService;
    let $q: IQService;
    let $rootScope: IScope;
    let form: any;

    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        gesuchModelManager = $injector.get('GesuchModelManager');
        TestDataUtil.mockDefaultGesuchModelManagerHttpCalls($injector.get('$httpBackend'));
        $state = $injector.get('$state');
        $q = $injector.get('$q');
        $rootScope = $injector.get('$rootScope');
        form = {};
        form.$valid = true;
        fallCreationview = new FallCreationViewController($injector.get('$state'), gesuchModelManager, $injector.get('BerechnungsManager'));
    }));

    describe('API Usage', function () {
        it('should return the current Gesuchsperiode formatted', function () {
            var momentAb = DateUtil.today().year(2016);
            var momentBis = DateUtil.today().year(2017);
            let gesuchsperiode: TSGesuchsperiode = new TSGesuchsperiode(true, new TSDateRange(momentAb, momentBis));
            spyOn(gesuchModelManager, 'getGesuchsperiode').and.returnValue(gesuchsperiode);
            let result: string = fallCreationview.getCurrentGesuchsperiodeAsString();
            expect(result).toEqual('2016/2017');
        });
    });
    describe('Submit', () => {
        it('submitted but rejected -> it does not go to the next step', () => {
            spyOn($state, 'go');
            spyOn(gesuchModelManager, 'createFallWithGesuch').and.returnValue($q.reject({}));
            spyOn(gesuchModelManager, 'isGesuchSaved').and.returnValue(false);
            fallCreationview.submit(form);
            $rootScope.$apply();
            expect(gesuchModelManager.createFallWithGesuch).toHaveBeenCalled();
            expect($state.go).not.toHaveBeenCalledWith();
        });
        it('should submit the form and go to the next page because gesuch is not saved', () => {
            spyOn($state, 'go');
            spyOn(gesuchModelManager, 'createFallWithGesuch').and.returnValue($q.when({}));
            spyOn(gesuchModelManager, 'isGesuchSaved').and.returnValue(false);
            fallCreationview.submit(form);
            $rootScope.$apply();
            expect(gesuchModelManager.createFallWithGesuch).toHaveBeenCalled();
            expect($state.go).toHaveBeenCalledWith('gesuch.familiensituation');
        });
        it('should not submit the form and must go directly to the next page because gesuch is already saved', () => {
            spyOn($state, 'go');
            spyOn(gesuchModelManager, 'createFallWithGesuch').and.returnValue($q.when({}));
            spyOn(gesuchModelManager, 'isGesuchSaved').and.returnValue(true);
            fallCreationview.submit(form);
            $rootScope.$apply();
            expect(gesuchModelManager.createFallWithGesuch).not.toHaveBeenCalled();
            expect($state.go).toHaveBeenCalledWith('gesuch.familiensituation');
        });
        it('should not submit the form and not go to the next page because form is invalid', () => {
            spyOn($state, 'go');
            spyOn(gesuchModelManager, 'createFallWithGesuch');
            spyOn(gesuchModelManager, 'isGesuchSaved').and.returnValue(false);
            form.$valid = false;
            fallCreationview.submit(form);
            expect(gesuchModelManager.createFallWithGesuch).not.toHaveBeenCalled();
            expect($state.go).not.toHaveBeenCalled();
        });
    });
});
