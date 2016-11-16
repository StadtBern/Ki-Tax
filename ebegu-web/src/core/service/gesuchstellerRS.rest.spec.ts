import '../../bootstrap.ts';
import 'angular-mocks';
import GesuchstellerRS from './gesuchstellerRS.rest.ts';
import {EbeguWebCore} from '../core.module';
import TSGesuchsteller from '../../models/TSGesuchsteller';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import IInjectorService = angular.auto.IInjectorService;
import {IHttpBackendService, IQService} from 'angular';
import WizardStepManager from '../../gesuch/service/wizardStepManager';


describe('GesuchstellerRS', function () {

    let gesuchstellerRS: GesuchstellerRS;
    let $httpBackend: IHttpBackendService;
    let ebeguRestUtil: EbeguRestUtil;
    let mockGesuchsteller: TSGesuchsteller;
    let mockGesuchstellerRest: any;
    let dummyGesuchID: string = '123';
    let $q: IQService;
    let wizardStepManager: WizardStepManager;


    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        gesuchstellerRS = $injector.get('GesuchstellerRS');
        $httpBackend = $injector.get('$httpBackend');
        ebeguRestUtil = $injector.get('EbeguRestUtil');
        wizardStepManager = $injector.get('WizardStepManager');
        $q = $injector.get('$q');
        spyOn(wizardStepManager, 'findStepsFromGesuch').and.returnValue($q.when({}));
    }));

    beforeEach(() => {
        mockGesuchsteller = new TSGesuchsteller('Tim', 'Tester');
        mockGesuchsteller.id = '2afc9d9a-957e-4550-9a22-97624a1d8feb';
        mockGesuchstellerRest = ebeguRestUtil.gesuchstellerToRestObject({}, mockGesuchsteller);

        $httpBackend.whenGET(gesuchstellerRS.serviceURL + '/' + encodeURIComponent(mockGesuchsteller.id)).respond(mockGesuchstellerRest);
    });

    describe('Public API', function () {
        it('check Service name', function () {
            expect(gesuchstellerRS.getServiceName()).toBe('GesuchstellerRS');
        });
        it('should include a findGesuchsteller() function', function () {
            expect(gesuchstellerRS.findGesuchsteller).toBeDefined();
        });
        it('should include a updateGesuchsteller() function', function () {
            expect(gesuchstellerRS.saveGesuchsteller).toBeDefined();
        });
    });

    describe('API Usage', function () {
        describe('updateGesuchsteller', () => {
            it('should updateGesuchsteller a gesuchsteller and her adresses', () => {
                    mockGesuchsteller.nachname = 'changedname';
                    let updatedGesuchsteller: TSGesuchsteller;
                    $httpBackend.expectPUT(gesuchstellerRS.serviceURL + '/' + dummyGesuchID + '/gsNumber/1/false', ebeguRestUtil.gesuchstellerToRestObject({}, mockGesuchsteller))
                        .respond(ebeguRestUtil.gesuchstellerToRestObject({}, mockGesuchsteller));


                    gesuchstellerRS.saveGesuchsteller(mockGesuchsteller, dummyGesuchID, 1, false).then((result) => {
                        updatedGesuchsteller = result;
                    });
                    $httpBackend.flush();
                    expect(wizardStepManager.findStepsFromGesuch).toHaveBeenCalledWith(dummyGesuchID);
                    expect(updatedGesuchsteller).toBeDefined();
                    expect(updatedGesuchsteller.nachname).toEqual(mockGesuchsteller.nachname);
                    expect(updatedGesuchsteller.id).toEqual(mockGesuchsteller.id);
                }
            );
        });

        describe('findGesuchsteller', () => {
            it('should return the gesuchsteller by id', () => {
                    let foundGesuchsteller: TSGesuchsteller;
                    $httpBackend.expectGET(gesuchstellerRS.serviceURL + '/' + mockGesuchsteller.id);


                    gesuchstellerRS.findGesuchsteller(mockGesuchsteller.id).then((result) => {
                        foundGesuchsteller = result;
                    });
                    $httpBackend.flush();
                    expect(foundGesuchsteller).toBeDefined();
                    expect(foundGesuchsteller.nachname).toEqual(mockGesuchsteller.nachname);
                }
            );
        });
    });

});
