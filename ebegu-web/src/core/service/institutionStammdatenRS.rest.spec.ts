import {EbeguWebCore} from '../core.module';
import {IHttpBackendService} from 'angular';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import {TSInstitutionStammdaten} from '../../models/TSInstitutionStammdaten';
import {TSBetreuungsangebotTyp} from '../../models/enums/TSBetreuungsangebotTyp';
import {TSInstitution} from '../../models/TSInstitution';
import {InstitutionStammdatenRS} from './institutionStammdatenRS.rest';
import DateUtil from '../../utils/DateUtil';

describe('institutionStammdatenRS', function () {

    var institutionStammdatenRS: InstitutionStammdatenRS;
    let $httpBackend: IHttpBackendService;
    let ebeguRestUtil: EbeguRestUtil;
    let mockInstitutionStammdaten: TSInstitutionStammdaten;
    let mockInstitutionStammdatenRest: any;
    let mockInstitution: TSInstitution;

    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        institutionStammdatenRS = $injector.get('InstitutionStammdatenRS');
        $httpBackend = $injector.get('$httpBackend');
        ebeguRestUtil = $injector.get('EbeguRestUtil');
    }));

    beforeEach(() => {
        mockInstitution = new TSInstitution('Institution_Test');
        mockInstitutionStammdaten = new TSInstitutionStammdaten('InstStammDaten_Test', 250, 12,
            TSBetreuungsangebotTyp.KITA, mockInstitution, DateUtil.today(), DateUtil.today());
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
                expect(foundInstitutionStammdaten).toBeDefined();
                expect(foundInstitutionStammdaten.iban).toEqual(mockInstitutionStammdaten.iban);
                expect(foundInstitutionStammdaten.institution.name).toEqual(mockInstitutionStammdaten.institution.name);
            });

        });

        describe('createInstitutionStammdaten', () => {
            it('should create a InstitutionStammdaten', () => {
                saveInstitutionStammdaten();

            });
        });

        describe('updateInstitutionStammdaten', () => {
            it('should update a InstitutionStammdaten', () => {
                mockInstitutionStammdaten.iban = 'CH123456';
                mockInstitutionStammdatenRest = ebeguRestUtil.institutionStammdatenToRestObject({}, mockInstitutionStammdaten);
                saveInstitutionStammdaten();
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
                expect(returnedInstitutionStammdaten[0].iban).toEqual(institutionStammdatenRestArray[0].iban);
                expect(returnedInstitutionStammdaten[1].iban).toEqual(institutionStammdatenRestArray[1].iban);
                expect(returnedInstitutionStammdaten[0].institution.name).toEqual(institutionStammdatenRestArray[0].institution.name);
                expect(returnedInstitutionStammdaten[1].institution.name).toEqual(institutionStammdatenRestArray[1].institution.name);
            });
        });

        describe('getAllInstitutionStammdatenByDate', () => {
            it('should return all InstitutionStammdaten im gegebenen Datum', () => {
                let institutionStammdatenRestArray: Array<any> = [mockInstitutionStammdatenRest, mockInstitutionStammdatenRest];
                $httpBackend.expectGET(institutionStammdatenRS.serviceURL + '/date?date=' + DateUtil.momentToLocalDate(mockInstitutionStammdaten.gueltigAb))
                    .respond(institutionStammdatenRestArray);

                let returnedInstitutionStammdaten: Array<TSInstitutionStammdaten>;
                institutionStammdatenRS.getAllInstitutionStammdatenByDate(mockInstitutionStammdaten.gueltigAb).then((result) => {
                    returnedInstitutionStammdaten = result;
                });
                $httpBackend.flush();
                expect(returnedInstitutionStammdaten).toBeDefined();
                expect(returnedInstitutionStammdaten.length).toEqual(2);
                expect(returnedInstitutionStammdaten[0].iban).toEqual(institutionStammdatenRestArray[0].iban);
                expect(returnedInstitutionStammdaten[1].iban).toEqual(institutionStammdatenRestArray[1].iban);
                expect(returnedInstitutionStammdaten[0].institution.name).toEqual(institutionStammdatenRestArray[0].institution.name);
                expect(returnedInstitutionStammdaten[1].institution.name).toEqual(institutionStammdatenRestArray[1].institution.name);
            });
        });
    });

    function saveInstitutionStammdaten() {
        let updatedInstitutionStammdaten: TSInstitutionStammdaten;
        $httpBackend.expectPUT(institutionStammdatenRS.serviceURL, mockInstitutionStammdatenRest).respond(mockInstitutionStammdatenRest);

        institutionStammdatenRS.updateInstitutionStammdaten(mockInstitutionStammdaten)
            .then((result) => {
                updatedInstitutionStammdaten = result;
            });
        $httpBackend.flush();
        expect(updatedInstitutionStammdaten).toBeDefined();
        expect(updatedInstitutionStammdaten.iban).toEqual(mockInstitutionStammdaten.iban);
        expect(updatedInstitutionStammdaten.id).toEqual(mockInstitutionStammdaten.id);
        expect(updatedInstitutionStammdaten.institution.name).toEqual(mockInstitutionStammdaten.institution.name);
    }

});
