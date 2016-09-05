import {EbeguWebCore} from '../../../core/core.module';
import {FallCreationViewController} from './fallCreationView';
import GesuchModelManager from '../../service/gesuchModelManager';
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
        fallCreationview = new FallCreationViewController($injector.get('$state'), gesuchModelManager, $injector.get('BerechnungsManager'),
            $injector.get('EbeguUtil'), $injector.get('ErrorService'), $injector.get('$stateParams'), $injector.get('WizardStepManager'));
    }));

    describe('nextStep', () => {
        it('submitted but rejected -> it does not go to the next step', () => {
            spyOn($state, 'go');
            spyOn(gesuchModelManager, 'saveGesuchAndFall').and.returnValue($q.reject({}));
            fallCreationview.save(form);
            $rootScope.$apply();
            expect(gesuchModelManager.saveGesuchAndFall).toHaveBeenCalled();
        });
        it('should submit the form and go to the next page', () => {
            spyOn($state, 'go');
            spyOn(gesuchModelManager, 'saveGesuchAndFall').and.returnValue($q.when({}));
            fallCreationview.save(form);
            $rootScope.$apply();
            expect(gesuchModelManager.saveGesuchAndFall).toHaveBeenCalled();
        });
        it('should not submit the form and not go to the next page because form is invalid', () => {
            spyOn($state, 'go');
            spyOn(gesuchModelManager, 'saveGesuchAndFall');
            form.$valid = false;
            fallCreationview.save(form);
            expect(gesuchModelManager.saveGesuchAndFall).not.toHaveBeenCalled();
        });
    });
});
