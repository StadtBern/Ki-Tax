import {FachstelleRS} from './fachstelleRS.rest';
import {IHttpBackendService} from 'angular';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import {TSFachstelle} from '../../models/TSFachstelle';
import {EbeguWebCore} from '../core.module';

describe('fachstelleRS', function () {

    let fachstelleRS: FachstelleRS;
    let $httpBackend: IHttpBackendService;
    let ebeguRestUtil: EbeguRestUtil;
    let mockFachstelle: TSFachstelle;
    let mockFachstelleRest: any;

    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        fachstelleRS = $injector.get('FachstelleRS');
        $httpBackend = $injector.get('$httpBackend');
        ebeguRestUtil = $injector.get('EbeguRestUtil');
    }));

    beforeEach(() => {
        mockFachstelle = new TSFachstelle('Fachstelle_Test', 'Ein Test', true);
        mockFachstelle.id = '2afc9d9a-957e-4550-9a22-9762422d8f05';
        mockFachstelleRest = ebeguRestUtil.fachstelleToRestObject({}, mockFachstelle);
    });

    describe('Public API', function () {
        it('check URI', function () {
            expect(fachstelleRS.serviceURL).toContain('fachstellen');
        });
        it('check Service name', function () {
            expect(fachstelleRS.getServiceName()).toBe('FachstelleRS');
        });
        it('should include a findFachstelle() function', function () {
            expect(fachstelleRS.findFachstelle).toBeDefined();
        });
        it('should include a createFachstelle() function', function () {
            expect(fachstelleRS.createFachstelle).toBeDefined();
        });
        it('should include a updateFachstelle() function', function () {
            expect(fachstelleRS.updateFachstelle).toBeDefined();
        });
        it('should include a removeFachstelle() function', function () {
            expect(fachstelleRS.removeFachstelle).toBeDefined();
        });
        it('should include a getAllFachstellen() function', function () {
            expect(fachstelleRS.getAllFachstellen).toBeDefined();
        });
    });

    describe('API Usage', function () {
        describe('findFachstelle', () => {
            it('should return the Fachstelle by id', () => {
                $httpBackend.expectGET(fachstelleRS.serviceURL + '/' + mockFachstelle.id).respond(mockFachstelleRest);

                let foundFachstelle: TSFachstelle;
                fachstelleRS.findFachstelle(mockFachstelle.id).then((result) => {
                    foundFachstelle = result;
                });
                $httpBackend.flush();
                checkFieldValues(foundFachstelle, mockFachstelle);
            });

        });

        describe('createFachstelle', () => {
            it('should create an fachstelle', () => {
                let savedFachstelle: TSFachstelle;
                $httpBackend.expectPUT(fachstelleRS.serviceURL, mockFachstelleRest).respond(mockFachstelleRest);
                fachstelleRS.createFachstelle(mockFachstelle).then((result) => {
                    savedFachstelle = result;
                });
                $httpBackend.flush();
                checkFieldValues(savedFachstelle, mockFachstelle);
            });
        });

        describe('updateFachstelle', () => {
            it('should update an fachstelle', () => {
                mockFachstelle.name = 'changedname';
                mockFachstelleRest = ebeguRestUtil.fachstelleToRestObject({}, mockFachstelle);
                let updatedFachstelle: TSFachstelle;
                $httpBackend.expectPUT(fachstelleRS.serviceURL, mockFachstelleRest).respond(mockFachstelleRest);
                fachstelleRS.updateFachstelle(mockFachstelle).then((result) => {
                    updatedFachstelle = result;
                });
                $httpBackend.flush();
                checkFieldValues(updatedFachstelle, mockFachstelle);
            });
        });

        describe('removeFachstelle', () => {
            it('should remove an fachstelle', () => {
                $httpBackend.expectDELETE(fachstelleRS.serviceURL + '/' + encodeURIComponent(mockFachstelle.id))
                    .respond(200);

                let deleteResult: any;
                fachstelleRS.removeFachstelle(mockFachstelle.id)
                    .then((result) => {
                        deleteResult = result;
                    });
                $httpBackend.flush();
                expect(deleteResult).toBeDefined();
                expect(deleteResult.status).toEqual(200);
            });
        });

        describe('getAllFachstellen', () => {
            it('should return all Fachstellen', () => {
                let fachstellenRestArray: Array<any> = [mockFachstelleRest, mockFachstelleRest];
                $httpBackend.expectGET(fachstelleRS.serviceURL).respond(fachstellenRestArray);

                let returnedFachstellen: Array<TSFachstelle>;
                fachstelleRS.getAllFachstellen().then((result) => {
                    returnedFachstellen = result;
                });
                $httpBackend.flush();
                expect(returnedFachstellen).toBeDefined();
                expect(returnedFachstellen.length).toEqual(2);
                checkFieldValues(returnedFachstellen[0], fachstellenRestArray[0]);
                checkFieldValues(returnedFachstellen[1], fachstellenRestArray[1]);
            });
        });

    });

    function checkFieldValues(fachstelle1: TSFachstelle, fachstelle2: TSFachstelle) {
        expect(fachstelle1).toBeDefined();
        expect(fachstelle1.name).toEqual(fachstelle2.name);
        expect(fachstelle1.id).toEqual(fachstelle2.id);
        expect(fachstelle1.beschreibung).toEqual(fachstelle2.beschreibung);
        expect(fachstelle1.behinderungsbestaetigung).toEqual(fachstelle2.behinderungsbestaetigung);
    }

});
