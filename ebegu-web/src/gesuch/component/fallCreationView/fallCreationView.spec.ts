import {EbeguWebCore} from '../../../core/core.module';
import {TSAntragTyp} from '../../../models/enums/TSAntragTyp';
import TSGesuch from '../../../models/TSGesuch';
import TSGesuchsperiode from '../../../models/TSGesuchsperiode';
import TestDataUtil from '../../../utils/TestDataUtil';
import GesuchModelManager from '../../service/gesuchModelManager';
import {FallCreationViewController} from './fallCreationView';

describe('fallCreationView', function () {

    let fallCreationview: FallCreationViewController;
    let gesuchModelManager: GesuchModelManager;
    let $state: angular.ui.IStateService;
    let $q: angular.IQService;
    let $rootScope: angular.IScope;
    let form: any;
    let gesuch: TSGesuch;

    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: angular.auto.IInjectorService) {
        gesuchModelManager = $injector.get('GesuchModelManager');
        TestDataUtil.mockLazyGesuchModelManagerHttpCalls($injector.get('$httpBackend'));
        $state = $injector.get('$state');
        $q = $injector.get('$q');
        $rootScope = $injector.get('$rootScope');
        form = {};
        form.$valid = true;
        form.$dirty = true;
        fallCreationview = new FallCreationViewController(gesuchModelManager, $injector.get('BerechnungsManager'),
            $injector.get('ErrorService'), $injector.get('$stateParams'), $injector.get('WizardStepManager'),
            $injector.get('$translate'), $q, $rootScope, $injector.get('AuthServiceRS'), $injector.get('GesuchsperiodeRS'),
            $injector.get('$timeout'));
        fallCreationview.form = form;
        spyOn(fallCreationview, 'isGesuchValid').and.callFake(function () {
            return form.$valid;
        });
        gesuch = new TSGesuch();
        gesuch.typ = TSAntragTyp.ERSTGESUCH;
    }));

    describe('nextStep', () => {
        it('submitted but rejected -> it does not go to the next step', () => {
            spyOn($state, 'go');
            let reject = $q.reject({}).catch(() => {
                //need to catch rejected promise
            });
            spyOn(gesuchModelManager, 'saveGesuchAndFall').and.returnValue(reject);
            spyOn(gesuchModelManager, 'getGesuch').and.returnValue(new TSGesuch());
            fallCreationview.save();
            $rootScope.$apply();
            expect(gesuchModelManager.saveGesuchAndFall).toHaveBeenCalled();
            expect($state.go).not.toHaveBeenCalled();
        });
        it('should submit the form and go to the next page', () => {
            spyOn($state, 'go');
            spyOn(gesuchModelManager, 'saveGesuchAndFall').and.returnValue($q.when({}));
            spyOn(gesuchModelManager, 'getGesuch').and.returnValue(new TSGesuch());
            fallCreationview.save();
            $rootScope.$apply();
            expect(gesuchModelManager.saveGesuchAndFall).toHaveBeenCalled();
        });
        it('should not submit the form and not go to the next page because form is invalid', () => {
            spyOn($state, 'go');
            spyOn(gesuchModelManager, 'saveGesuchAndFall');
            form.$valid = false;
            fallCreationview.save();
            expect(gesuchModelManager.saveGesuchAndFall).not.toHaveBeenCalled();
        });
    });
    describe('getTitle', () => {
        it('should return Änderung Ihrer Daten', () => {
            spyOn(gesuchModelManager, 'isGesuch').and.returnValue(false);
            expect(fallCreationview.getTitle()).toBe('Änderung Ihrer Daten');
        });
        it('should return Ki-Tax – Erstgesuch der Periode', () => {
            let gesuchsperiode: TSGesuchsperiode = TestDataUtil.createGesuchsperiode20162017();
            spyOn(gesuchModelManager, 'getGesuchsperiode').and.returnValue(gesuchsperiode);
            spyOn(gesuchModelManager, 'isGesuch').and.returnValue(true);
            spyOn(gesuchModelManager, 'isGesuchSaved').and.returnValue(true);
            spyOn(gesuchModelManager, 'getGesuch').and.returnValue(gesuch);
            expect(fallCreationview.getTitle()).toBe('Ki-Tax – Erstgesuch der Periode 2016/17');
        });
        it('should return Ki-Tax – Erstgesuch', () => {
            spyOn(gesuchModelManager, 'isGesuch').and.returnValue(true);
            spyOn(gesuchModelManager, 'isGesuchSaved').and.returnValue(false);
            spyOn(gesuchModelManager, 'getGesuch').and.returnValue(gesuch);
            expect(fallCreationview.getTitle()).toBe('Ki-Tax – Erstgesuch');
        });
    });
});
