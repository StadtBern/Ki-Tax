import '../../bootstrap.ts';
import 'angular-mocks';
import PersonRS from './personRS.rest';
import {EbeguWebCore} from '../core.module';
import TSPerson from '../../models/TSPerson';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import IInjectorService = angular.auto.IInjectorService;
import IHttpBackendService = angular.IHttpBackendService;


describe('PersonRS', function () {

    let personRS: PersonRS;
    let $httpBackend: IHttpBackendService;
    let ebeguRestUtil: EbeguRestUtil;
    let mockPerson: TSPerson;
    let mockPersonRest: any;


    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        personRS = $injector.get('PersonRS');
        $httpBackend = $injector.get('$httpBackend');
        ebeguRestUtil = $injector.get('EbeguRestUtil');


    }));

    beforeEach(() => {
        mockPerson = new TSPerson('Tim', 'Tester');
        mockPerson.id = '2afc9d9a-957e-4550-9a22-97624a1d8feb';
        mockPersonRest = ebeguRestUtil.personToRestObject({}, mockPerson);

        $httpBackend.whenGET(personRS.serviceURL + '/' + encodeURIComponent(mockPerson.id)).respond(mockPersonRest);
    });


    describe('Public API', function () {
        it('check Service name', function () {
            expect(personRS.getServiceName()).toBe('PersonRS');

        });
        it('should include a findPerson() function', function () {
            expect(personRS.findPerson).toBeDefined();
        });
        it('should include a update() function', function () {
            expect(personRS.update).toBeDefined();
        });
    });

    describe('API Usage', function () {
        describe('update', () => {
            it('should update a person and her adresses', () => {
                    mockPerson.nachname = 'changedname';
                    let updatedPerson: TSPerson;
                    $httpBackend.expectPUT(personRS.serviceURL, ebeguRestUtil.personToRestObject({}, mockPerson)).respond(ebeguRestUtil.personToRestObject({}, mockPerson));

                    personRS.update(mockPerson).then((result) => {
                        updatedPerson = result;
                    });
                    $httpBackend.flush();
                    expect(updatedPerson).toBeDefined();
                    expect(updatedPerson.nachname).toEqual(mockPerson.nachname);
                    expect(updatedPerson.id).toEqual(mockPerson.id);
                }
            );
        });

        describe('findPerson', () => {
            it('should return the person by id', () => {
                    let foundPerson: TSPerson;
                    $httpBackend.expectGET(personRS.serviceURL + '/' + mockPerson.id);

                    personRS.findPerson(mockPerson.id).then((result) => {
                        foundPerson = result;
                    });
                    $httpBackend.flush();
                    expect(foundPerson).toBeDefined();
                    expect(foundPerson.nachname).toEqual(mockPerson.nachname);
                }
            );
        });
    });

});
