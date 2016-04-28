import {EbeguWebCore} from '../core.module';
import {IHttpBackendService} from 'angular';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import {TSInstitution} from '../../models/TSInstitution';
import {InstitutionRS} from './institutionRS.rest';
import {TSTraegerschaft} from '../../models/TSTraegerschaft';
import {TSMandant} from '../../models/TSMandant';

describe('institutionRS', function () {

    var institutionRS: InstitutionRS;
    let $httpBackend: IHttpBackendService;
    let ebeguRestUtil: EbeguRestUtil;
    let mockInstitution: TSInstitution;
    let mockInstitutionRest: any;
    let mandant: TSMandant;
    let traegerschaft: TSTraegerschaft;

    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        institutionRS = $injector.get('InstitutionRS');
        $httpBackend = $injector.get('$httpBackend');
        ebeguRestUtil = $injector.get('EbeguRestUtil');
    }));

    beforeEach(() => {
        traegerschaft = new TSTraegerschaft('Traegerschaft_Test');
        mandant = new TSMandant('Mandant_Test');
        mockInstitution = new TSInstitution('InstitutionTest', traegerschaft, mandant);
        mockInstitution.id = '2afc9d9a-957e-4550-9a22-97624a1d8f05';
        mockInstitutionRest = ebeguRestUtil.institutionToRestObject({}, mockInstitution);
    });

    describe('Public API', function () {
        it('check URI', function () {
            expect(institutionRS.serviceURL).toContain('institutionen');
        });
        it('check Service name', function () {
            expect(institutionRS.getServiceName()).toBe('InstitutionRS');
        });
        it('should include a findInstitution() function', function () {
            expect(institutionRS.findInstitution).toBeDefined();
        });
        it('should include a createInstitution() function', function () {
            expect(institutionRS.createInstitution).toBeDefined();
        });
        it('should include a updateInstitution() function', function () {
            expect(institutionRS.updateInstitution).toBeDefined();
        });
        it('should include a removeInstitution() function', function () {
            expect(institutionRS.removeInstitution).toBeDefined();
        });
        it('should include a getAllInstitutionen() function', function () {
            expect(institutionRS.getAllInstitutionen).toBeDefined();
        });
    });

    describe('API Usage', function () {
        describe('findInstitution', () => {
            it('should return the Institution by id', () => {
                $httpBackend.expectGET(institutionRS.serviceURL + '/' + mockInstitution.id).respond(mockInstitutionRest);

                let foundInstitution: TSInstitution;
                institutionRS.findInstitution(mockInstitution.id).then((result) => {
                    foundInstitution = result;
                });
                $httpBackend.flush();
                expect(foundInstitution).toBeDefined();
                expect(foundInstitution.name).toEqual(mockInstitution.name);
                expect(foundInstitution.mandant.name).toEqual(mockInstitution.mandant.name);
                expect(foundInstitution.traegerschaft.name).toEqual(mockInstitution.traegerschaft.name);
            });

        });

        describe('createInstitution', () => {
            it('should create an institution', () => {
                saveInstitution();
            });
        });

        describe('updateInstitution', () => {
            it('should update an institution', () => {
                mockInstitution.name = 'changedname';
                mockInstitutionRest = ebeguRestUtil.institutionToRestObject({}, mockInstitution);
                saveInstitution();
            });
        });

        describe('removeInstitution', () => {
            it('should remove an institution', () => {
                $httpBackend.expectDELETE(institutionRS.serviceURL + '/' + encodeURIComponent(mockInstitution.id))
                    .respond(200);

                let deleteResult: any;
                institutionRS.removeInstitution(mockInstitution.id)
                    .then((result) => {
                        deleteResult = result;
                    });
                $httpBackend.flush();
                expect(deleteResult).toBeDefined();
                expect(deleteResult.status).toEqual(200);
            });
        });

        describe('getAllInstitutionen', () => {
            it('should return all Institutionen', () => {
                let institutionenRestArray: Array<any> = [mockInstitutionRest, mockInstitutionRest];
                $httpBackend.expectGET(institutionRS.serviceURL).respond(institutionenRestArray);

                let returnedInstitution: Array<TSInstitution>;
                institutionRS.getAllInstitutionen().then((result) => {
                    returnedInstitution = result;
                });
                $httpBackend.flush();
                expect(returnedInstitution).toBeDefined();
                expect(returnedInstitution.length).toEqual(2);
                expect(returnedInstitution[0].name).toEqual(institutionenRestArray[0].name);
                expect(returnedInstitution[1].name).toEqual(institutionenRestArray[1].name);
                expect(returnedInstitution[0].mandant.name).toEqual(institutionenRestArray[0].mandant.name);
                expect(returnedInstitution[1].mandant.name).toEqual(institutionenRestArray[1].mandant.name);
                expect(returnedInstitution[0].traegerschaft.name).toEqual(institutionenRestArray[0].traegerschaft.name);
                expect(returnedInstitution[1].traegerschaft.name).toEqual(institutionenRestArray[1].traegerschaft.name);
            });
        });

    });

    function saveInstitution() {
        let updatedInstitution: TSInstitution;
        $httpBackend.expectPUT(institutionRS.serviceURL, mockInstitutionRest).respond(mockInstitutionRest);

        institutionRS.updateInstitution(mockInstitution)
            .then((result) => {
                updatedInstitution = result;
            });
        $httpBackend.flush();
        expect(updatedInstitution).toBeDefined();
        expect(updatedInstitution.name).toEqual(mockInstitution.name);
        expect(updatedInstitution.id).toEqual(mockInstitution.id);
        expect(updatedInstitution.mandant.name).toEqual(mockInstitution.mandant.name);
        expect(updatedInstitution.traegerschaft.name).toEqual(mockInstitution.traegerschaft.name);
    }
});
