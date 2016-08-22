import WizardStepRS from './WizardStepRS.rest';
import {IHttpBackendService} from 'angular';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import TSWizardStep from '../../models/TSWizardStep';
import {EbeguWebCore} from '../../core/core.module';
import TestDataUtil from '../../utils/TestDataUtil';

describe('WizardStepRS', function () {

    let wizardStepRS: WizardStepRS;
    let $httpBackend: IHttpBackendService;
    let ebeguRestUtil: EbeguRestUtil;
    let REST_API: string;
    let mockWizardStep: TSWizardStep;
    let mockWizardStepRest: any;
    let mockWizardStepListRest: Array<any> = [];
    let gesuchId: string = '123123123123';

    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        wizardStepRS = $injector.get('WizardStepRS');
        $httpBackend = $injector.get('$httpBackend');
        ebeguRestUtil = $injector.get('EbeguRestUtil');
        REST_API = $injector.get('REST_API');
    }));

    beforeEach(() => {
        mockWizardStep = TestDataUtil.createWizardStep(gesuchId);
        TestDataUtil.setAbstractFieldsUndefined(mockWizardStep);
        mockWizardStepRest = ebeguRestUtil.wizardStepToRestObject({}, mockWizardStep);
        mockWizardStepListRest = [mockWizardStepRest];
    });

    describe('Public API', function () {
        it('check URI', function () {
            expect(wizardStepRS.serviceURL).toContain('wizard-steps');
        });
        it('check Service name', function () {
            expect(wizardStepRS.getServiceName()).toBe('WizardStepRS');
        });
        it('should include a createWizardStepList() function', function () {
            expect(wizardStepRS.createWizardStepList).toBeDefined();
        });
        it('should include a updateWizardStep() function', function () {
            expect(wizardStepRS.updateWizardStep).toBeDefined();
        });
        it('should include a findWizardStepsFromGesuch() function', function () {
            expect(wizardStepRS.findWizardStepsFromGesuch).toBeDefined();
        });
    });

    describe('API Usage', function () {
        describe('findWizardStepsFromGesuch', () => {
            it('should return the all wizardSteps of a Gesuch', () => {
                $httpBackend.expectGET(wizardStepRS.serviceURL + '/' + gesuchId).respond(mockWizardStepListRest);

                let foundSteps: Array<TSWizardStep>;
                wizardStepRS.findWizardStepsFromGesuch(gesuchId).then((result) => {
                    foundSteps = result;
                });
                $httpBackend.flush();
                console.log('-------------------', foundSteps);
                expect(foundSteps).toBeDefined();
                expect(foundSteps.length).toEqual(1);
                expect(foundSteps[0]).toEqual(mockWizardStep);
            });
        });
    });
});
