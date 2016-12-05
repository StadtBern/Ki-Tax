import {EbeguWebGesuch} from '../../gesuch.module';
import {FreigabeViewController} from './freigabeView';
import TSGesuch from '../../../models/TSGesuch';
import IScope = angular.IScope;
import IFormController = angular.IFormController;
import IPromise = angular.IPromise;
import WizardStepManager from '../../service/wizardStepManager';

describe('freigabeView', function () {

    let controller: FreigabeViewController;
    let $scope: IScope;
    let wizardStepManager: WizardStepManager;


    beforeEach(angular.mock.module(EbeguWebGesuch.name));


    beforeEach(angular.mock.inject(function ($injector: any) {
        $scope = $injector.get('$rootScope');
        wizardStepManager = $injector.get('WizardStepManager');

        spyOn(wizardStepManager, 'updateCurrentWizardStepStatus'). and.returnValue({});

        controller = new FreigabeViewController($injector.get('GesuchModelManager'), $injector.get('BerechnungsManager'),
            $injector.get('ErrorService'), wizardStepManager, $injector.get('DvDialog'),
            $injector.get('$translate'), $injector.get('$q'), $scope);
    }));

    describe('save', function () {
        it('should return undefined wenn das Form nicht valid ist', function () {
            let form: any = {};
            form.$valid = false;

            let returned: IPromise<TSGesuch> = controller.save(form);

            expect(returned).toBeUndefined();
        });
        it('should return  wenn das Form nicht valid', function () {
            let form: any = {};
            form.$valid = true;

            let returned: IPromise<TSGesuch> = controller.save(form);

            expect(returned).toBeDefined();
        });
    });
});
