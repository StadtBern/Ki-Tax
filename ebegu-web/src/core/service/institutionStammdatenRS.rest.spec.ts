import {EbeguWebCore} from '../core.module';
import {IHttpBackendService} from 'angular';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import {TSInstitutionStammdaten} from '../../models/TSInstitutionStammdaten';
import {TSBetreuungsangebotTyp} from '../../models/enums/TSBetreuungsangebotTyp';
import {TSInstitution} from '../../models/TSInstitution';
import {InstitutionStammdatenRS} from './institutionStammdatenRS.rest';
import DateUtil from '../../utils/DateUtil';
import {TSDateRange} from '../../models/types/TSDateRange';

describe('institutionStammdatenRS', function () {

    var institutionStammdatenRS: InstitutionStammdatenRS;
    let $httpBackend: IHttpBackendService;
    let ebeguRestUtil: EbeguRestUtil;
    let mockInstitutionStammdaten: TSInstitutionStammdaten;
    let mockInstitutionStammdatenRest: any;
    let mockInstitution: TSInstitution;
    let today: moment.Moment;

    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        institutionStammdatenRS = $injector.get('InstitutionStammdatenRS');
        $httpBackend = $injector.get('$httpBackend');
        ebeguRestUtil = $injector.get('EbeguRestUtil');
    }));

    beforeEach(() => {
        today = DateUtil.today();
        mockInstitution = new TSInstitution('Institution_Test');
        mockInstitutionStammdaten = new TSInstitutionStammdaten('InstStammDaten_Test', 250, 12,
            TSBetreuungsangebotTyp.KITA, mockInstitution, new TSDateRange(today, today));
        mockInstitutionStammdaten.id = '2afc9d9a-957e-4550-9a22-97624a1d8f05';
        mockInstitutionStammdatenRest = ebeguRestUtil.institutionStammdatenToRestObject({}, mockInstitutionStammdaten);
    });

    describe('Public API', function () {
        it('check Service name', function () {
            expect(institutionStammdatenRS.getServiceName()).toBe('InstitutionStammdatenRS');
        });
        it('should include a findInstitutionStammdaten() function', function () {
            expect(institutionStammdatenRS.findInstitutionStammdaten).toBeDefined();
        });
        it('should include a createInstitutionStammdaten() function', function () {
            expect(institutionStammdatenRS.createInstitutionStammdaten).toBeDefined();
        });
        it('should include a updateInstitutionStammdaten() function', function () {
            expect(institutionStammdatenRS.updateInstitutionStammdaten).toBeDefined();
        });
        it('should include a removeInstitutionStammdaten() function', function () {
            expect(institutionStammdatenRS.removeInstitutionStammdaten).toBeDefined();
        });
        it('should include a getAllInstitutionStammdaten() function', function () {
            expect(institutionStammdatenRS.getAllInstitutionStammdaten).toBeDefined();
        });
        it('should include a getAllInstitutionStammdatenByDate() function', function () {
            expect(institutionStammdatenRS.getAllInstitutionStammdatenByDate).toBeDefined();
        });
    });

    describe('API Usage', function () {
        describe('findInstitutionStammdaten', () => {
            it('should return the InstitutionStammdaten by id', () => {
                $httpBackend.expectGET(institutionStammdatenRS.serviceURL + '/' + encodeURIComponent(mockInstitutionStammdaten.id))
                    .respond(mockInstitutionStammdatenRest);

                let foundInstitutionStammdaten: TSInstitutionStammdaten;
                institutionStammdatenRS.findInstitutionStammdaten(mockInstitutionStammdaten.id).then((result) => {
                    foundInstitutionStammdaten = result;
                });
                $httpBackend.flush();
                checkFieldValues(foundInstitutionStammdaten, mockInstitutionStammdaten);
            });

        });

        describe('createInstitutionStammdaten', () => {
            it('should create a InstitutionStammdaten', () => {
                let createdInstitutionStammdaten: TSInstitutionStammdaten;
                $httpBackend.expectPUT(institutionStammdatenRS.serviceURL, mockInstitutionStammdatenRest).respond(mockInstitutionStammdatenRest);

                institutionStammdatenRS.createInstitutionStammdaten(mockInstitutionStammdaten)
                    .then((result) => {
                        createdInstitutionStammdaten = result;
                    });
                $httpBackend.flush();
                checkFieldValues(createdInstitutionStammdaten, mockInstitutionStammdaten);
            });
        });

        describe('updateInstitutionStammdaten', () => {
            it('should update a InstitutionStammdaten', () => {
                mockInstitutionStammdaten.iban = 'CH123456';
                mockInstitutionStammdatenRest = ebeguRestUtil.institutionStammdatenToRestObject({}, mockInstitutionStammdaten);
                let updatedInstitutionStammdaten: TSInstitutionStammdaten;
                $httpBackend.expectPUT(institutionStammdatenRS.serviceURL, mockInstitutionStammdatenRest).respond(mockInstitutionStammdatenRest);

                institutionStammdatenRS.updateInstitutionStammdaten(mockInstitutionStammdaten)
                    .then((result) => {
                        updatedInstitutionStammdaten = result;
                    });
                $httpBackend.flush();
                checkFieldValues(updatedInstitutionStammdaten, mockInstitutionStammdaten);
            });
        });

        describe('removeInstitutionStammdaten', () => {
            it('should remove a InstitutionStammdaten', () => {
                $httpBackend.expectDELETE(institutionStammdatenRS.serviceURL + '/' + encodeURIComponent(mockInstitutionStammdaten.id))
                    .respond(200);

                let deleteResult: any;
                institutionStammdatenRS.removeInstitutionStammdaten(mockInstitutionStammdaten.id)
                    .then((result) => {
                        deleteResult = result;
                    });
                $httpBackend.flush();
                expect(deleteResult).toBeDefined();
                expect(deleteResult.status).toEqual(200);
            });
        });

        describe('getAllInstitutionStammdaten', () => {
            it('should return all InstitutionStammdaten', () => {
                let institutionStammdatenRestArray: Array<any> = [mockInstitutionStammdatenRest, mockInstitutionStammdatenRest];
                $httpBackend.expectGET(institutionStammdatenRS.serviceURL).respond(institutionStammdatenRestArray);

                let returnedInstitutionStammdaten: Array<TSInstitutionStammdaten>;
                institutionStammdatenRS.getAllInstitutionStammdaten().then((result) => {
                    returnedInstitutionStammdaten = result;
                });
                $httpBackend.flush();
                expect(returnedInstitutionStammdaten).toBeDefined();
                expect(returnedInstitutionStammdaten.length).toEqual(2);
                checkFieldValues(returnedInstitutionStammdaten[0], institutionStammdatenRestArray[0]);
                checkFieldValues(returnedInstitutionStammdaten[1], institutionStammdatenRestArray[1]);
            });
        });

        describe('getAllInstitutionStammdatenByDate', () => {
            it('should return all InstitutionStammdaten im gegebenen Datum', () => {
                let institutionStammdatenRestArray: Array<any> = [mockInstitutionStammdatenRest, mockInstitutionStammdatenRest];
                $httpBackend.expectGET(institutionStammdatenRS.serviceURL + '/date?date='
                    + DateUtil.momentToLocalDate(today))
                    .respond(institutionStammdatenRestArray);

                let returnedInstitutionStammdaten: Array<TSInstitutionStammdaten>;
                institutionStammdatenRS.getAllInstitutionStammdatenByDate(today).then((result) => {
                    returnedInstitutionStammdaten = result;
                });
                $httpBackend.flush();
                expect(returnedInstitutionStammdaten).toBeDefined();
                expect(returnedInstitutionStammdaten.length).toEqual(2);
                checkFieldValues(returnedInstitutionStammdaten[0], institutionStammdatenRestArray[0]);
                checkFieldValues(returnedInstitutionStammdaten[1], institutionStammdatenRestArray[1]);
            });
        });
    });

    function checkFieldValues(institutionStammdaten1: TSInstitutionStammdaten, institutionStammdaten2: TSInstitutionStammdaten) {
        expect(institutionStammdaten1).toBeDefined();
        expect(institutionStammdaten1.iban).toEqual(institutionStammdaten2.iban);
        expect(institutionStammdaten1.id).toEqual(institutionStammdaten2.id);
        expect(institutionStammdaten1.institution.name).toEqual(institutionStammdaten2.institution.name);
    }

});
