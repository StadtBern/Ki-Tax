import {EbeguWebCore} from '../core.module';
import {MandantRS} from './mandantRS.rest';
import {TSMandant} from '../../models/TSMandant';
import EbeguRestUtil from '../../utils/EbeguRestUtil';
import {IHttpBackendService} from 'angular';

describe('mandantRS', function () {

    var mandantRS: MandantRS;
    let $httpBackend: IHttpBackendService;
    let ebeguRestUtil: EbeguRestUtil;
    let mockMandant: TSMandant;
    let mockMandantRest: any;

    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        mandantRS = $injector.get('MandantRS');
        $httpBackend = $injector.get('$httpBackend');
        ebeguRestUtil = $injector.get('EbeguRestUtil');
    }));


    beforeEach(() => {
        mockMandant = new TSMandant('MandantTest');
        mockMandant.id = '2afc9d9a-957e-4550-9a22-97624a1d8fa1';
        mockMandantRest = ebeguRestUtil.mandantToRestObject({}, mockMandant);
    });

    describe('Public API', function () {
        it('check Service name', function () {
            expect(mandantRS.getServiceName()).toBe('MandantRS');

        });
        it('should include a findMandant() function', function () {
            expect(mandantRS.findMandant).toBeDefined();
        });
    });


    describe('API Usage', function () {
        describe('findMandant', () => {
            it('should return the mandant by id', () => {
                $httpBackend.expectGET(mandantRS.serviceURL + '/id/' + encodeURIComponent(mockMandant.id)).respond(mockMandantRest);

                let foundMandant: TSMandant;
                mandantRS.findMandant(mockMandant.id).then((result) => {
                    foundMandant = result;
                });
                $httpBackend.flush();
                expect(foundMandant).toBeDefined();
                expect(foundMandant.name).toEqual(mockMandant.name);
            });
        });
    });
});
