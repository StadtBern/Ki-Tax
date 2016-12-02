///<reference path="../../models/TSInstitution.ts"/>
import {EbeguWebCore} from '../core.module';
import {IHttpBackendService} from 'angular';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import TSInstitution from '../../models/TSInstitution';
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
                $httpBackend.expectGET(institutionRS.serviceURL + '/id/' + mockInstitution.id).respond(mockInstitutionRest);

                let foundInstitution: TSInstitution;
                institutionRS.findInstitution(mockInstitution.id).then((result) => {
                    foundInstitution = result;
                });
                $httpBackend.flush();
                checkFieldValues(foundInstitution, mockInstitution);
            });

        });

        describe('createInstitution', () => {
            it('should create an institution', () => {
                let createdInstitution: TSInstitution;
                $httpBackend.expectPOST(institutionRS.serviceURL, mockInstitutionRest).respond(mockInstitutionRest);

                institutionRS.createInstitution(mockInstitution)
                    .then((result) => {
                        createdInstitution = result;
                    });
                $httpBackend.flush();
                checkFieldValues(createdInstitution, mockInstitution);
            });
        });

        describe('updateInstitution', () => {
            it('should update an institution', () => {
                mockInstitution.name = 'changedname';
                mockInstitutionRest = ebeguRestUtil.institutionToRestObject({}, mockInstitution);
                let updatedInstitution: TSInstitution;
                $httpBackend.expectPUT(institutionRS.serviceURL, mockInstitutionRest).respond(mockInstitutionRest);

                institutionRS.updateInstitution(mockInstitution)
                    .then((result) => {
                        updatedInstitution = result;
                    });
                $httpBackend.flush();
                checkFieldValues(updatedInstitution, mockInstitution);
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
                checkFieldValues(returnedInstitution[0], institutionenRestArray[0]);
                checkFieldValues(returnedInstitution[1], institutionenRestArray[1]);
            });
        });

    });

    function checkFieldValues(institution1: TSInstitution, institution2: TSInstitution) {
        expect(institution1).toBeDefined();
        expect(institution1.name).toEqual(institution2.name);
        expect(institution1.id).toEqual(institution2.id);
        expect(institution1.mandant.name).toEqual(institution2.mandant.name);
        expect(institution1.traegerschaft.name).toEqual(institution2.traegerschaft.name);
    }

});
